// TODO: these numbers should be calculated automatically
// currently I just calculated a number that allows Slab and Cache to fit in at most 1 page
pub const max_pyvars_per_cache = 100;
pub const max_vars_per_variable_slab = 150;

pub const VariableSlab = struct {
    next: ?*VariableSlab,
    prev: ?*VariableSlab,
    count: u32,
    vars: [max_vars_per_variable_slab]?*PyVar,
    var_names: [max_vars_per_variable_slab][]u8,
};

pub const PyVarCache = struct {
    next: ?*PyVarCache,
    prev: ?*PyVarCache,
    count: u32,
    vars: [max_pyvars_per_cache]PyVar,
};

pub const PyVar = struct {
    varType: PyVarType,
    value: union {
        int: u64,
        float: f64,
        string: []u8,
        list_head: *PyVar,
    },
    next: ?*PyVar,
};

pub const PyVarType = enum(u32) {
    FREE = 0,
    INT = 1,
    FLOAT = 2,
    STRING = 3,
    LIST = 4,
};

pub const PyMemError = error{
    InitError,
    OutOfMemory,
    UnknownError,
};
