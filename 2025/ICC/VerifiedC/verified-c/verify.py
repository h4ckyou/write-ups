from z3 import Solver, BitVec, And, Not, sat, ULT, UGE, Implies, BitVecVal
import vast as ast_mod

LEN_SUFFIX = "_size!"  # Suffix for array size variables
FRESH_ID = 0
solver = Solver()


class TypeError(Exception):
    pass


class Verifier:
    def __init__(self):
        self.env = {}
        self.solver = Solver()
        self.fresh_id = 0

    def fresh_var(self, name: str) -> str:
        var_name = f"{name}_nondet_{self.fresh_id}!"
        self.fresh_id += 1
        return var_name

    def verify(self, func: ast_mod.FunctionDef) -> bool:
        for param in func.params:
            match param.type:
                case ast_mod.IntType():
                    self.env[param.name] = BitVec(param.name, 32)
                case ast_mod.ArrayType():
                    var_name = f"{param.name}{LEN_SUFFIX}"
                    self.env[param.name] = BitVec(var_name, 32)
                case _:
                    assert False, f"Unsupported parameter type: {param.type}"
        try:
            self.check(func.body, [])
        except TypeError:
            return False
        return True

    def check_access(self, array_name, index, ctx):
        size_var = self.env[array_name]
        index_z3 = self.ast_to_z3(index)
        safety_condition = Implies(
            And(ctx), And(UGE(index_z3, 0), ULT(index_z3, size_var)))
        solver.push()
        solver.add(Not(safety_condition))
        if solver.check() == sat:
            model = solver.model()
            raise TypeError()
        solver.pop()

    def check(self, node, ctx):
        match node:
            case ast_mod.ArrayAccess(var_name=var_name, array_name=array_name, index=index, body=body):
                assert var_name not in self.env
                self.check_access(array_name, index, ctx)
                v = self.fresh_var(array_name)
                self.env[var_name] = BitVec(v, 32)
                self.check(body, ctx)

            case ast_mod.Let(var_name=var_name, type=ty, init=init, body=body):
                match ty:
                    case ast_mod.ArrayType():
                        self.env[var_name] = self.env[init.name]
                    case _:
                        res = self.ast_to_z3(init)
                        assert var_name not in self.env
                        self.env[var_name] = res
                self.check(body, ctx)

            case ast_mod.ArrayAssign(array_name=array_name, index=index, value=_value, body=body):
                self.check_access(array_name, index, ctx)
                self.check(body, ctx)

            case ast_mod.IfElse(cond=cond, then_branch=then_branch, else_branch=else_branch):
                ctx1, ctx2 = ctx[:], ctx[:]
                cond_z3 = self.ast_to_z3(cond)
                ctx1.append(cond_z3)
                ctx2.append(Not(cond_z3))
                self.check(then_branch, ctx1)
                self.check(else_branch, ctx2)

            case ast_mod.Return(value=_value):
                pass

            case _:
                assert False

    def ast_to_z3(self, expr):
        """Convert an AST expression to a Z3 expression."""
        match expr:
            case ast_mod.IntLit(value):
                return BitVecVal(value, 32)

            case ast_mod.Var(name):
                return self.env.get(name)

            case ast_mod.BinOp(left=left, op=op, right=right):
                left_z3 = self.ast_to_z3(left)
                right_z3 = self.ast_to_z3(right)

                match op:
                    case ast_mod.BinOpType.ADD:
                        return left_z3 + right_z3
                    case ast_mod.BinOpType.SUB:
                        return left_z3 - right_z3
                    case ast_mod.BinOpType.MUL:
                        return left_z3 * right_z3
                    case ast_mod.BinOpType.LT:
                        return ULT(left_z3, right_z3)
                    case ast_mod.BinOpType.EQ:
                        return left_z3 == right_z3
            case ast_mod.Len(var_name):
                return self.env.get(var_name)

            case ast_mod.Call(args=_):
                var_name = self.fresh_var("call")
                return BitVec(var_name, 32)
            case _:
                assert False, f"Unsupported expression type: {expr}"


def verify(func: ast_mod.FunctionDef) -> bool:
    """Verify that all array accesses in the function are safe."""
    verifier = Verifier()
    return verifier.verify(func)
