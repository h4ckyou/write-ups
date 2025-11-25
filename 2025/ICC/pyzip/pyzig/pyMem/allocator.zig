const std = @import("std");
const linux = std.os.linux;
const pyTypes = @import("types.zig");

pub const PyVarAllocator = struct {
    cacheHead: ?*pyTypes.PyVarCache,
    page_allocator: std.mem.Allocator,
    string_allocator: std.heap.FixedBufferAllocator,

    pub const Self = @This();

    pub fn init() !Self {
        var self = Self{
            .cacheHead = undefined,
            .page_allocator = undefined,
            .string_allocator = undefined,
        };
        self.cacheHead = null;
        self.page_allocator = std.heap.page_allocator;
        self.cacheHead = self.allocCache() catch {
            return pyTypes.PyMemError.InitError;
        };
        const string_allocator_buffer = self.page_allocator.alloc(u8, 0x100000) catch {
            return pyTypes.PyMemError.InitError;
        };
        self.string_allocator = std.heap.FixedBufferAllocator.init(string_allocator_buffer);
        return self;
    }

    pub fn deinit(self: *Self) void {
        self.page_allocator.deinit();
        self.string_allocator.deinit();
    }

    pub fn allocVariableSlab(self: *Self) !*pyTypes.VariableSlab {
        const newVariableSlab = self.page_allocator.create(pyTypes.VariableSlab) catch {
            return pyTypes.PyMemError.OutOfMemory;
        };
        newVariableSlab.next = null;
        newVariableSlab.prev = null;
        newVariableSlab.count = 0;
        for (newVariableSlab.vars, 0..) |_, i| {
            newVariableSlab.vars[i] = null;
        }
        return newVariableSlab;
    }

    pub fn freeVariableSlab(self: *Self, variableSlab: *pyTypes.VariableSlab) void {
        self.page_allocator.destroy(variableSlab);
        return;
    }

    fn allocCache(self: *Self) !*pyTypes.PyVarCache {
        const newPyVarCache = self.page_allocator.create(pyTypes.PyVarCache) catch {
            return pyTypes.PyMemError.OutOfMemory;
        };
        newPyVarCache.next = null;
        newPyVarCache.prev = null;
        newPyVarCache.count = 0;
        for (newPyVarCache.vars, 0..) |_, i| {
            newPyVarCache.vars[i].varType = pyTypes.PyVarType.FREE;
        }
        return newPyVarCache;
    }

    pub fn allocPyVar(self: *Self, varType: pyTypes.PyVarType) !*pyTypes.PyVar {
        var currCache = self.cacheHead;
        while (true) {
            if (currCache) |cache| {
                for (cache.vars, 0..) |pyVar, i| {
                    if (pyVar.varType == pyTypes.PyVarType.FREE) {
                        cache.vars[i].varType = varType;
                        cache.count += 1;
                        return &cache.vars[i];
                    }
                }
                currCache = cache.next;
            } else {
                break;
            }
        }

        const newCache = try self.allocCache();
        if (self.cacheHead) |cacheHead| {
            newCache.next = cacheHead;
            cacheHead.prev = newCache;
            self.cacheHead = newCache;
        } else {
            self.cacheHead = newCache;
        }
        newCache.count += 1;
        newCache.vars[0].varType = varType;
        return &newCache.vars[0];
    }

    pub fn allocString(self: *Self, length: usize, oldStr: ?[]u8) ![]u8 {
        const newStr = self.string_allocator.allocator().alloc(u8, length) catch {
            return pyTypes.PyMemError.OutOfMemory;
        };
        if (oldStr) |str| {
            @memcpy(newStr, str);
        }
        return newStr;
    }

    pub fn freeString(self: *Self, mem: []u8) void {
        self.string_allocator.allocator().free(mem);
        return;
    }

    pub fn freePyVar(self: *Self, toFreeMem: ?*pyTypes.PyVar) void {
        if (toFreeMem) |mem| {
            mem.varType = pyTypes.PyVarType.FREE;

            // Each cache is 1 page in size, so its safe to do this
            const cache: *pyTypes.PyVarCache = @ptrFromInt(@intFromPtr(mem) & 0xfffffffffffff000);
            cache.count -= 1;
            if (cache.count == 0) {
                if (cache.next) |nextCache| {
                    nextCache.prev = cache.prev;
                }
                if (cache.prev) |prevCache| {
                    prevCache.next = cache.next;
                }
                if (self.cacheHead == cache) {
                    self.cacheHead = cache.next;
                }
                self.page_allocator.destroy(cache);
            }
        }
        return;
    }
};
