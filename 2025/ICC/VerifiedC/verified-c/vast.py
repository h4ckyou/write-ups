from dataclasses import dataclass
from enum import Enum
from typing import List


class Type:
    """ Type Enum.
    Type = BoolType | IntType | ArrayType
    """
    pass


@dataclass(frozen=True)
class BoolType(Type):
    def to_cty(self):
        return "unsigned"


@dataclass(frozen=True)
class IntType(Type):
    def to_cty(self):
        return "unsigned"


@dataclass(frozen=True)
class ArrayType(Type):
    def to_cty(self):
        return "struct IArray*"


class BinOpType(Enum):
    LT = '<'
    EQ = '='
    ADD = '+'
    SUB = '-'
    MUL = '*'


@dataclass(frozen=True)
class Param:
    """Function parameter."""
    name: str
    type: Type


class Expr:
    """Expr Enum.
    Expr =
        IntLit(value: int)
        | Var(name: str)
        | BinOp(left: Expr, op: BinOpType, right: Expr)
        | Len(array: Expr)
        | Call(args: List[Expr])
    """
    pass


@dataclass(frozen=True)
class IntLit(Expr):
    """Integer literal."""
    value: int


@dataclass(frozen=True)
class Var(Expr):
    """Variable."""
    name: str


@dataclass(frozen=True)
class BinOp(Expr):
    """Binary operation."""
    left: Expr
    op: BinOpType
    right: Expr


@dataclass(frozen=True)
class Len(Expr):
    """Length of an array."""
    var_name: str


@dataclass(frozen=True)
class Call(Expr):
    """Function call."""
    args: List[Expr]


class Stmt:
    """Statement Enum.
    stmt =
        Return(Expr)
        | Let(Param, Expr, Stmt)
        | ArrayAssign(str, Expr, Expr, Stmt)
        | ArrayAccess(str, Expr, Stmt)
        | IfElse(Expr, Stmt, Stmt)
    """
    pass


@dataclass(frozen=True)
class Return(Stmt):
    """Return statement."""
    value: Expr


@dataclass
class Let(Stmt):
    """Variable binding."""
    var_name: str
    type: Type
    init: Expr
    body: Stmt


@dataclass(frozen=True)
class ArrayAssign(Stmt):
    """Array assignment."""
    array_name: str
    index: Expr
    value: Expr
    body: Stmt


@dataclass(frozen=True)
class ArrayAccess(Stmt):
    """Array access expression (only to be used in Let statements, not in expressions)."""
    var_name: str
    array_name: str
    index: Expr
    body: Stmt


@dataclass(frozen=True)
class IfElse(Stmt):
    """If-else statement."""
    cond: Expr
    then_branch: Stmt
    else_branch: Stmt


@dataclass(frozen=True)
class FunctionDef:
    """Function definition."""
    params: List[Param]
    return_type: Type
    body: Stmt
