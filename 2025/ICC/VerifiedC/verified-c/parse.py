from lark import Lark, Transformer, v_args
import vast as ast_mod

# Lark grammar for the language
grammar = r"""
?start: func
func: "func" "(" [params] ")" "->" type "{" stmt "}"
params: param ("," param)*
param: IDENT ":" type
?stmt: return_stmt
     | let_stmt
     | array_assign_stmt
     | if_stmt
return_stmt: "return" expr ";"
let_stmt: "let" IDENT "=" expr ";" stmt
        | "let" IDENT "=" IDENT "[" expr "]" ";" stmt -> array_access_let
array_assign_stmt: IDENT "[" expr "]" "=" expr ";" stmt
if_stmt: "if" "(" expr ")" "{" stmt "}" "else" "{" stmt "}"
?expr: comp
?comp: add (comp_op add)?
?comp_op: "<" -> lt_op
       | "=" -> eq_op
?add: mul (add_op mul)?
?add_op: "+" -> add_op
       | "-" -> sub_op
?mul: atom (mul_op atom)?
?mul_op: "*" -> mul_op
?atom: NUMBER             -> int_lit
     | "func" "(" [args] ")"  -> call
     | IDENT             -> var
     | "len" "(" IDENT ")"   -> len
     | "(" expr ")"
args: expr ("," expr)*
type: "int"  -> int_ty
     | "array" -> array_ty

%import common.CNAME -> IDENT
%import common.INT    -> NUMBER
%import common.WS
%ignore WS
"""

# Initialize Lark parser
parser = Lark(grammar, start="start", parser="lalr")


@v_args(inline=True)
class AstBuilder(Transformer):
    """
    Transformer that builds the AST without performing type checking.
    This first phase only constructs the AST structure without validating types.
    """

    def __init__(self):
        super().__init__()

    def func(self, *args):
        assert len(args) == 3, f"Expected 3 arguments, got {len(args)}"
        return ast_mod.FunctionDef(args[0], args[1], args[2])

    def param(self, name, ty):
        if hasattr(name, 'value'):
            name = name.value
        return ast_mod.Param(name, ty)

    def params(self, *ps):
        return list(ps)

    def int_ty(self):
        return ast_mod.IntType()

    def array_ty(self):
        return ast_mod.ArrayType()

    def int_lit(self, tok):
        val = int(tok)
        assert -2147483648 <= val <= 2147483647
        return ast_mod.IntLit(int(tok))

    def var(self, name):
        if hasattr(name, 'value'):
            name = name.value
        return ast_mod.Var(name)

    def len(self, arr):
        return ast_mod.Len(arr)

    def call(self, args):
        return ast_mod.Call(list(args.children))

    def return_stmt(self, expr):
        return ast_mod.Return(expr)

    def let_stmt(self, name, expr, stmt):
        if hasattr(name, 'value'):
            name = name.value
        return ast_mod.Let(name, None, expr, stmt)

    def array_access_let(self, name, array_name, index, stmt):
        if hasattr(name, 'value'):
            name = name.value
        if hasattr(array_name, 'value'):
            array_name = array_name.value
        return ast_mod.ArrayAccess(name, array_name, index, stmt)

    def array_assign_stmt(self, name, idx, val, stmt):
        if hasattr(name, 'value'):
            name = name.value
        return ast_mod.ArrayAssign(name, idx, val, stmt)

    def if_stmt(self, cond, then_branch, else_branch):
        return ast_mod.IfElse(cond, then_branch, else_branch)

    def lt_op(self):
        return ast_mod.BinOpType.LT

    def eq_op(self):
        return ast_mod.BinOpType.EQ

    def add_op(self):
        return ast_mod.BinOpType.ADD

    def sub_op(self):
        return ast_mod.BinOpType.SUB

    def mul_op(self):
        return ast_mod.BinOpType.MUL

    def comp(self, left, op=None, right=None):
        if op is None or right is None:
            return left
        return ast_mod.BinOp(left, op, right)

    def add(self, left, op=None, right=None):
        if op is None or right is None:
            return left
        return ast_mod.BinOp(left, op, right)

    def mul(self, left, op=None, right=None):
        if op is None or right is None:
            return left
        return ast_mod.BinOp(left, op, right)

    def __default__(self, data, children, meta):
        return Transformer.__default__(self, data, children, meta)


class TypeChecker:
    def __init__(self):
        self.env = {}
        self.func = None

    def check_function(self, func):
        self.func = func

        arr_cnt = 0
        for param in func.params:
            assert param.name not in self.env, f"Parameter '{param.name}' redeclared"
            self.env[param.name] = param.type
            if param.type == ast_mod.ArrayType():
                arr_cnt += 1
                if arr_cnt > 1:
                    raise AssertionError("I don't like many arrays...")

        self.check_stmt(func.body)

        return func

    def check_stmt(self, stmt):
        match stmt:
            case ast_mod.Return(value):
                expr_ty = self.infer_type(value)
                assert isinstance(
                    expr_ty, ast_mod.IntType), f'Return type must be int but got {type(expr_ty).__name__}'

            case ast_mod.Let(var_name, ty, init, body):
                init_ty = self.infer_type(init)
                assert var_name not in self.env, f"Variable '{var_name}' redeclared"

                self.env[var_name] = init_ty
                stmt.type = init_ty
                self.check_stmt(body)

            case ast_mod.ArrayAccess(var_name, array_name, index, body):
                self._check_array_access(array_name, index)
                assert var_name not in self.env, f"Variable '{var_name}' redeclared"
                self.env[var_name] = ast_mod.IntType()
                self.check_stmt(body)

            case ast_mod.ArrayAssign(array_name, index, value, body):
                self._check_array_access(array_name, index)
                val_ty = self.infer_type(value)
                assert isinstance(
                    val_ty, ast_mod.IntType), 'Array element must be int'

                self.check_stmt(body)

            case ast_mod.IfElse(cond, then_branch, else_branch):
                cond_ty = self.infer_type(cond)
                assert isinstance(
                    cond_ty, ast_mod.BoolType), 'Condition must be bool'

                self.check_stmt(then_branch)
                self.check_stmt(else_branch)

            case _:
                assert False, f"Unknown statement type: {type(stmt)}"

    def _check_array_access(self, array_name, index):
        array_ty = self.env[array_name]
        assert isinstance(
            array_ty, ast_mod.ArrayType), f"Variable '{array_name}' is not an array"

        idx_ty = self.infer_type(index)
        assert isinstance(idx_ty, ast_mod.IntType), 'Array index must be int'

        return ast_mod.IntType()

    def infer_type(self, expr):
        match expr:
            case ast_mod.IntLit():
                return ast_mod.IntType()

            case ast_mod.Var(name):
                assert name in self.env, f"Undefined variable '{name}'"
                return self.env[name]

            case ast_mod.BinOp(left, ast_mod.BinOpType.EQ, right):
                left_ty = self.infer_type(left)
                right_ty = self.infer_type(right)
                assert type(left_ty) is type(
                    right_ty), 'Eq operands must be of the same type'
                return ast_mod.BoolType()

            case ast_mod.BinOp(left, op, right):
                left_ty = self.infer_type(left)
                right_ty = self.infer_type(right)

                assert isinstance(left_ty, ast_mod.IntType) and isinstance(
                    right_ty, ast_mod.IntType), 'Binary operands must be int'

                match op:
                    case ast_mod.BinOpType.LT:
                        return ast_mod.BoolType()
                    case ast_mod.BinOpType.ADD | ast_mod.BinOpType.SUB | ast_mod.BinOpType.MUL:
                        return ast_mod.IntType()

            case ast_mod.Len(var_name):
                arr_ty = self.env[var_name]
                assert isinstance(
                    arr_ty, ast_mod.ArrayType), 'len applied to non-array'
                return ast_mod.IntType()

            case ast_mod.Call(args):
                assert len(args) == len(
                    self.func.params), f"Expected {len(self.func.params)} arguments, got {len(args)}"

                for arg, param in zip(args, self.func.params):
                    arg_ty = self.infer_type(arg)
                    assert type(arg_ty) is type(
                        param.type), f"Argument type mismatch for '{param.name}'"

                return ast_mod.IntType()

            case _:
                assert False, f'Cannot infer type of {expr}'


def parse(text: str) -> ast_mod.FunctionDef:
    tree = parser.parse(text)
    ast_builder = AstBuilder()
    ast = ast_builder.transform(tree)

    type_checker = TypeChecker()
    return type_checker.check_function(ast)
