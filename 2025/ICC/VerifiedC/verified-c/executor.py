import json
import os
import vast as ast_mod
from typing import Dict, Any, List, Optional


def execute(func: ast_mod.FunctionDef, inputs: Dict[str, Any]) -> str:
    validate_inputs(func, inputs)
    c_code = generate_c_code(func, inputs)
    # todo random number
    with open("/tmp/output.c", "w") as f:
        f.write(c_code)
    os.system(
        "clang -Wall -Wno-unused-variable -Wno-parentheses-equality -Werror -o /tmp/output /tmp/output.c 2> /dev/null && /tmp/output")
    print(c_code)
    return c_code


def validate_inputs(func: ast_mod.FunctionDef, inputs: Dict[str, Any]) -> None:
    for param in func.params:
        assert param.name in inputs, f"Missing input for parameter '{param.name}'"

        value = inputs[param.name]

        match param.type:
            case ast_mod.IntType():
                assert isinstance(
                    value, int)

            case ast_mod.ArrayType():
                assert isinstance(
                    value, list), f"Parameter '{param.name}' expected array, got {type(value).__name__}"

                for i, elem in enumerate(value):
                    assert isinstance(
                        elem, int), f"Array element {param.name}[{i}] expected int, got {type(elem).__name__}"


def generate_c_code(func: ast_mod.FunctionDef, inputs: Dict[str, Any]) -> str:
    c_code = []

    c_code.append("#include <stdio.h>")
    c_code.append("#include <stdlib.h>")
    c_code.append("")

    c_code.append("struct IArray {")
    c_code.append("  unsigned *data;")
    c_code.append("  unsigned size;")
    c_code.append("};")
    c_code.append("")
    c_code.append("char *veri_prompt = \">>\";")
    c_code.append("")

    signature = generate_function_signature(func)
    c_code.append(f"{signature};")
    c_code.append("")

    impl = generate_function_implementation(func)
    c_code.append(impl)
    c_code.append("")

    main_func = generate_main_function(func, inputs)
    c_code.append(main_func)

    return "\n".join(c_code)


def generate_function_signature(func: ast_mod.FunctionDef) -> str:
    params = []
    for param in func.params:
        match param.type:
            case ast_mod.IntType():
                params.append(f"unsigned veri_{param.name}")
            case ast_mod.ArrayType():
                params.append(f"struct IArray* veri_{param.name}")
            case _:
                assert False, f"Unsupported parameter type: {param.type}"

    return f"unsigned func({', '.join(params)})"


def collect_contants_expr(expr: ast_mod.Expr, consts):
    match expr:
        case ast_mod.IntLit(value):
            consts.add(value)
        case ast_mod.BinOp(left, _op, right):
            collect_contants_expr(left, consts)
            collect_contants_expr(right, consts)
        case ast_mod.Len(var_name=_):
            pass
        case ast_mod.Var(name=_):
            pass
        case ast_mod.Call(args=args):
            for arg in args:
                collect_contants_expr(arg, consts)
        case _:
            assert False, f"Unsupported expression type: {type(expr).__name__}"

def collect_constants(stmt: ast_mod.Stmt, consts):
    match stmt:
        case ast_mod.Return(value):
            collect_contants_expr(value, consts)

        case ast_mod.Let(var_name=_var_name, type=_type, init=init, body=body):
            collect_contants_expr(init, consts)
            collect_constants(body, consts)

        case ast_mod.ArrayAssign(array_name=_array_name, index=index, value=value, body=body):
            collect_contants_expr(index, consts)
            collect_contants_expr(value, consts)
            collect_constants(body, consts)

        case ast_mod.ArrayAccess(var_name=_var_name, array_name=_array_name, index=index, body=body):
            collect_contants_expr(index, consts)
            collect_constants(body, consts)

        case ast_mod.IfElse(cond, then_branch, else_branch):
            collect_contants_expr(cond, consts)
            collect_constants(then_branch, consts)
            collect_constants(else_branch, consts)


def gen_constant_definitions(consts: set) -> str:
    s = ""
    for c in consts:
        s += f"  unsigned CONST_{c} = {c}u;\n"
    return s


def generate_function_implementation(func: ast_mod.FunctionDef) -> str:
    """Generate C function implementation."""
    signature = generate_function_signature(func)
    body = generate_stmt_code(func.body, 1)
    consts = set()
    collect_constants(func.body, consts)
    consts_str = gen_constant_definitions(consts)

    return f"{signature} {{\n{consts_str}\n{body}\n}}"


def generate_stmt_code(stmt: ast_mod.Stmt, indent_level: int) -> str:
    """Generate C code for a statement."""
    indent = "  " * indent_level

    match stmt:
        case ast_mod.Return(value):
            expr_code = generate_expr_code(value)
            return f"{indent}return {expr_code};"

        case ast_mod.Let(var_name=var_name, type=type, init=init, body=body):
            init_code = generate_expr_code(init)
            body_code = generate_stmt_code(body, indent_level)
            return f"{indent}{type.to_cty()} veri_{var_name} = ({type.to_cty()})({init_code});\n{body_code}"

        case ast_mod.ArrayAssign(array_name=array_name, index=index, value=value, body=body):
            index_code = generate_expr_code(index)
            value_code = generate_expr_code(value)
            body_code = generate_stmt_code(body, indent_level)
            return f"{indent}veri_{array_name}->data[{index_code}] = {value_code};\n{body_code}"

        case ast_mod.ArrayAccess(var_name=var_name, array_name=array_name, index=index, body=body):
            index_code = generate_expr_code(index)
            body_code = generate_stmt_code(body, indent_level)
            return f"{indent}unsigned veri_{var_name} = veri_{array_name}->data[{index_code}];\n{body_code}"

        case ast_mod.IfElse(cond, then_branch, else_branch):
            cond_code = generate_expr_code(cond)
            then_code = generate_stmt_code(then_branch, indent_level + 1)
            else_code = generate_stmt_code(else_branch, indent_level + 1)
            return f"{indent}if ({cond_code}) {{\n{then_code}\n{indent}}} else {{\n{else_code}\n{indent}}}"

        case _:
            assert False, f"Unsupported statement type: {type(stmt).__name__}"


def generate_expr_code(expr: ast_mod.Expr) -> str:
    """Generate C code for an expression."""
    match expr:
        case ast_mod.IntLit(value):
            return f"CONST_{value}"

        case ast_mod.Var(name):
            return f"veri_{name}"

        case ast_mod.BinOp(left, op, right):
            left_code = generate_expr_code(left)
            right_code = generate_expr_code(right)

            op_map = {
                ast_mod.BinOpType.ADD: '+',
                ast_mod.BinOpType.SUB: '-',
                ast_mod.BinOpType.MUL: '*',
                ast_mod.BinOpType.LT: '<',
                ast_mod.BinOpType.EQ: '==',
            }
            op_str = op_map[op]
            return f"({left_code} {op_str} {right_code})"

        case ast_mod.Len(var_name=name):
            return f"veri_{name}->size"

        case ast_mod.Call(args):
            args_code = [generate_expr_code(arg) for arg in args]
            return f"func({', '.join(args_code)})"

        case _:
            assert False, f"Unsupported expression type: {type(expr).__name__}"


def generate_main_function(func: ast_mod.FunctionDef, inputs: Dict[str, Any]) -> str:
    """Generate C main function to call the implemented function with given inputs."""
    main_lines = ["int main() {"]

    for param in func.params:
        match param.type:
            case ast_mod.ArrayType():
                value = inputs[param.name]
                assert (len(value) < 65536)
                data_decl = ", ".join(str(elem) for elem in value)
                main_lines.append(
                    f"unsigned veri_{param.name}_data[{len(value)}] = {{{data_decl}}};")

                main_lines.append(
                    f"  struct IArray veri_{param.name}_struct = {{veri_{param.name}_data, {len(value)}}};")
                main_lines.append(
                    f"  struct IArray* veri_{param.name} = &veri_{param.name}_struct;")

    args = []
    for param in func.params:
        match param.type:
            case ast_mod.IntType():
                args.append(str(inputs[param.name]))
            case ast_mod.ArrayType():
                args.append(f"veri_{param.name}")
            case _:
                assert False, f"Unsupported parameter type: {param.type}"

    main_lines.append("")
    main_lines.append(f"  unsigned result = func({', '.join(args)});")
    main_lines.append("  printf(\"%s %u\\n\", veri_prompt, result);")
    main_lines.append("  return 0;")
    main_lines.append("}")

    return "\n".join(main_lines)
