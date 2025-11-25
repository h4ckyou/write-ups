// Author's Note: almost none of this is LLM code, its just a bit rushed
// Author's Note: Please don't make fun of my code 🥲
const std = @import("std");
const pyMem = @import("pyMem/pyMem.zig");
const pyZigTypes = @import("types.zig");

var allocator: pyMem.allocator.PyVarAllocator = undefined;
var variableSlabHead: ?*pyMem.types.VariableSlab = null;
const stdin = std.io.getStdIn().reader();
const stdout = std.io.getStdOut().writer();

fn isValidString(data: []u8) bool {
    // Naive way of testing for string
    // TODO: improve
    if (data.len < 2) return false;
    return data[0] == '"' and data[data.len - 1] == '"';
}

fn isValidVarName(name: []u8) bool {
    // It would be nice to match PEP-3131, buts its too complicated for now
    // https://peps.python.org/pep-3131/#specification-of-language-changes
    if (name.len == 0) return false;

    if (!std.ascii.isAlphabetic(name[0]) and name[0] != '_') return false;
    for (name) |c| {
        if (!std.ascii.isAlphabetic(c) and !std.ascii.isDigit(c) and c != '_') {
            return false;
        }
    }
    return true;
}

fn isValidLiteral(data: []u8) bool {
    _ = getDataType(data) catch {
        return false;
    };
    return true;
}

fn findVariableSlab() !*pyMem.types.VariableSlab {
    var currVariableSlabNullable = variableSlabHead;
    while (currVariableSlabNullable) |currVariableSlab| {
        if (currVariableSlab.count != pyMem.types.max_vars_per_variable_slab) {
            return currVariableSlab;
        }
        currVariableSlabNullable = currVariableSlab.next;
    }

    const newVariableSlab = try allocator.allocVariableSlab();
    if (variableSlabHead) |variableSlab| {
        newVariableSlab.next = variableSlab;
        variableSlab.prev = newVariableSlab;
        variableSlabHead = newVariableSlab;
    } else {
        variableSlabHead = newVariableSlab;
    }

    return newVariableSlab;
}

fn findVar(varName: []u8) !*pyMem.types.PyVar {
    var currVariableSlabNullable = variableSlabHead;
    while (currVariableSlabNullable) |currVariableSlab| {
        for (currVariableSlab.vars, 0..) |_, i| {
            if (currVariableSlab.vars[i]) |pyVar| {
                if (std.mem.eql(u8, currVariableSlab.var_names[i], varName)) {
                    return pyVar;
                }
            }
        }
        currVariableSlabNullable = currVariableSlab.next;
    }

    return pyZigTypes.PyZigError.NameError;
}

fn getDataType(data: []u8) !pyMem.types.PyVarType {
    testInt: {
        _ = std.fmt.parseInt(u64, data, 10) catch {
            break :testInt;
        };
        return pyMem.types.PyVarType.INT;
    }
    testFloat: {
        _ = std.fmt.parseFloat(f64, data) catch {
            break :testFloat;
        };
        return pyMem.types.PyVarType.FLOAT;
    }
    testStr: {
        if (!isValidString(data)) {
            break :testStr;
        }
        return pyMem.types.PyVarType.STRING;
    }
    return pyZigTypes.PyZigError.InvalidLiteralError;
}

fn allocVar(varName: []u8, varData: []u8) !void {
    var variableSlab = try findVariableSlab();

    var emptyIndex: usize = 0xffffffffffffffff;
    for (variableSlab.vars, 0..) |_, i| {
        if (variableSlab.vars[i] == null) {
            emptyIndex = i;
            break;
        }
    }
    if (emptyIndex == 0xffffffffffffffff) {
        // this should never happen
        return pyZigTypes.PyZigError.UnknownError;
    }

    // Case where RHS is var not literal
    // Literals are never valid var names so no collisions should happen
    testVar: {
        const oldPyVar = findVar(varData) catch {
            // not a valid var, break to see if RHS is literal
            break :testVar;
        };

        var pyVar: *pyMem.types.PyVar = try allocator.allocPyVar(oldPyVar.varType);
        switch (pyVar.varType) {
            pyMem.types.PyVarType.INT => {
                pyVar.value.int = oldPyVar.value.int;
            },
            pyMem.types.PyVarType.FLOAT => {
                pyVar.value.float = oldPyVar.value.float;
            },
            pyMem.types.PyVarType.STRING => {
                pyVar.value.string = try allocator.allocString(oldPyVar.value.string.len, oldPyVar.value.string);
            },
            else => {
                // Should never happen
                return pyZigTypes.PyZigError.UnknownError;
            },
        }
        variableSlab.var_names[emptyIndex] = try allocator.allocString(varName.len, varName);
        variableSlab.vars[emptyIndex] = pyVar;
        variableSlab.count += 1;
        return;
    }

    // Case for literals
    const varType = try getDataType(varData);
    const pyVar = try allocator.allocPyVar(varType);

    variableSlab.var_names[emptyIndex] = try allocator.allocString(varName.len, varName);
    variableSlab.vars[emptyIndex] = pyVar;
    variableSlab.count += 1;

    switch (varType) {
        pyMem.types.PyVarType.INT => {
            pyVar.value.int = try std.fmt.parseInt(u64, varData, 10);
        },
        pyMem.types.PyVarType.FLOAT => {
            pyVar.value.float = try std.fmt.parseFloat(f64, varData);
        },
        pyMem.types.PyVarType.STRING => {
            pyVar.value.string = try allocator.allocString(varData.len - 2, varData[1 .. varData.len - 1]);
        },
        // TODO: Implement lists
        else => {
            // Should never happen
            return pyZigTypes.PyZigError.UnknownError;
        },
    }
    return;
}

// To save memory, we can edit variables in-memory instead of creating new ones each time
// Better than python 😎
fn editVar(varName: []u8, varData: []u8) !void {
    if (isValidVarName(varData)) {
        // In this case, we should check both types to see if their values match etc etc
        // Im lazy, so for now we just destroy and recreate the var
        try freeVar(varName);
        return allocVar(varName, varData);
    }

    const varType = try getDataType(varData);
    const pyVar = try findVar(varName);
    switch (varType) {
        pyMem.types.PyVarType.INT => {
            if (pyVar.varType == pyMem.types.PyVarType.STRING) {
                allocator.freeString(pyVar.value.string);
            }
            pyVar.varType = pyMem.types.PyVarType.INT;
            pyVar.value.int = try std.fmt.parseInt(u64, varData, 10);
        },
        pyMem.types.PyVarType.FLOAT => {
            if (pyVar.varType == pyMem.types.PyVarType.STRING) {
                allocator.freeString(pyVar.value.string);
            }
            pyVar.varType = pyMem.types.PyVarType.FLOAT;
            pyVar.value.float = try std.fmt.parseFloat(f64, varData);
        },
        pyMem.types.PyVarType.STRING => {
            if (pyVar.varType == pyMem.types.PyVarType.STRING) {
                if (varData.len - 2 > pyVar.value.string.len) {
                    // In this case, we need to recreate the string
                    allocator.freeString(pyVar.value.string);
                    pyVar.value.string = try allocator.allocString(varData.len - 2, varData[1 .. varData.len - 1]);
                } else {
                    // Else, in memory edit!
                    @memcpy(pyVar.value.string, varData[1 .. varData.len - 1]);
                }
            } else {
                pyVar.varType = pyMem.types.PyVarType.STRING;
                pyVar.value.string = try allocator.allocString(varData.len - 2, varData[1 .. varData.len - 1]);
            }
        },
        // TODO: Implement lists
        else => {
            // Should never happen
            return pyZigTypes.PyZigError.UnknownError;
        },
    }
    return;
}

fn freeVar(varName: []u8) !void {
    var currVariableSlabNullable = variableSlabHead;
    while (currVariableSlabNullable) |currVariableSlab| {
        for (currVariableSlab.vars, 0..) |_, i| {
            if (currVariableSlab.vars[i] != null and std.mem.eql(u8, currVariableSlab.var_names[i], varName)) {
                if (currVariableSlab.vars[i].?.varType == pyMem.types.PyVarType.STRING) {
                    allocator.freeString(currVariableSlab.vars[i].?.value.string);
                }
                allocator.freePyVar(currVariableSlab.vars[i]);
                allocator.freeString(currVariableSlab.var_names[i]);
                currVariableSlab.vars[i] = null;
                currVariableSlab.count -= 1;
                if (currVariableSlab.count == 0) {
                    if (currVariableSlab.next) |nextVariableSlab| {
                        nextVariableSlab.prev = currVariableSlab.prev;
                    }
                    if (currVariableSlab.prev) |prevVariableSlab| {
                        prevVariableSlab.next = currVariableSlab.next;
                    }
                    if (variableSlabHead == currVariableSlab) {
                        variableSlabHead = currVariableSlab.next;
                    }
                    allocator.freeVariableSlab(currVariableSlab);
                }
                return;
            }
        }
        currVariableSlabNullable = currVariableSlab.next;
    }

    return pyZigTypes.PyZigError.NameError;
}

fn printVar(varName: []u8) !void {
    const pyVar = try findVar(varName);
    switch (pyVar.varType) {
        pyMem.types.PyVarType.INT => {
            try stdout.print("{d}\n", .{pyVar.value.int});
        },
        pyMem.types.PyVarType.FLOAT => {
            try stdout.print("{d}\n", .{pyVar.value.float});
        },
        pyMem.types.PyVarType.STRING => {
            try stdout.print("{s}\n", .{pyVar.value.string});
        },
        else => unreachable,
    }
    return;
}

fn parse_cmd(cmd: []u8) !void {
    var tokens = std.mem.tokenizeAny(u8, cmd, " \t\n\r");

    const first = tokens.next();
    const second = tokens.next();
    var third: ?[]u8 = null;
    if (tokens.next()) |tok| {
        // For now, we only support 1-3 token commands
        // Author's Note: I am too lazy to make the proper grammar xD
        // Author's Note: Do YOU want to implement https://docs.python.org/3/reference/grammar.html? Thats what I thought. 🙄
        const start = tok.ptr - cmd.ptr;
        third = cmd[start..];
    }

    if (first) |_| {
        const lhs = @constCast(first.?);
        if (std.mem.eql(u8, lhs, "print")) {
            if (third) |_| {
                return pyZigTypes.PyZigError.SyntaxError;
            }
            if (second) |varName| {
                return printVar(@constCast(varName));
            } else {
                return stdout.print("\n", .{});
            }
        }
        if (std.mem.eql(u8, lhs, "del")) {
            if (third) |_| {
                return pyZigTypes.PyZigError.SyntaxError;
            }
            if (second) |varName| {
                return freeVar(@constCast(varName));
            } else {
                return pyZigTypes.PyZigError.SyntaxError;
            }
        }

        if (second == null) {
            if (isValidVarName(lhs)) return printVar(lhs);
            if (isValidLiteral(lhs)) return stdout.print("{s}\n", .{lhs});
            return pyZigTypes.PyZigError.SyntaxError;
        }

        if (third) |_| {
            const rhs = @constCast(third.?);
            if (std.mem.eql(u8, second.?, "=")) {
                if (!isValidVarName(lhs) and !isValidLiteral(lhs)) return pyZigTypes.PyZigError.SyntaxError;
                if (!isValidVarName(rhs) and !isValidLiteral(rhs)) return pyZigTypes.PyZigError.SyntaxError;

                _ = findVar(lhs) catch {
                    return allocVar(lhs, rhs);
                };
                return editVar(lhs, rhs);
            }
        }
        return pyZigTypes.PyZigError.SyntaxError;
    }
}

pub fn main() !void {
    allocator = pyMem.allocator.PyVarAllocator.init() catch |err| {
        std.debug.print("Allocator Init Error (contact admin if on remote), {}", .{err});
        return err;
    };

    while (true) {
        try stdout.print(">>> ", .{});
        var buffer: [1024]u8 = undefined;
        const line = try stdin.readUntilDelimiterOrEof(&buffer, '\n');
        if (line) |cmd| {
            parse_cmd(cmd) catch |err| {
                switch (err) {
                    pyZigTypes.PyZigError.SyntaxError => {
                        try stdout.print("SyntaxError: invalid syntax\n", .{});
                    },
                    pyZigTypes.PyZigError.NameError => {
                        // TODO: It would be nice to print the variable name
                        try stdout.print("NameError: variable is not defined\n", .{});
                    },
                    pyZigTypes.PyZigError.InvalidLiteralError => {
                        // Currently, InvalidLiteralError is return when RHS is an invalid literal,
                        // even in cases where RHS 'looks' like a variable
                        // TODO: Fix that.
                        try stdout.print("InvalidLiteralError: invalid literal in lhs or rhs\n", .{});
                    },
                    else => {
                        try stdout.print("Error: {}\n", .{err});
                    },
                }
            };
        } else {
            return;
        }
    }
}
