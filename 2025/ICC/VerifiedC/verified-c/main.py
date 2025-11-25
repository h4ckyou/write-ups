#!/usr/local/bin/python -u
import json
import sys
from parse import parse
from verify import verify
from executor import execute

print("Enter your code below. End input with \"EOF\"")
code = ""
while True:
    tmp = input()
    if tmp == "EOF":
        break
    code += tmp + "\n"

print("Please send your input as a single-line JSON:")
inputs = json.loads(input())

f = parse(code)
if verify(f):
    c_code = execute(f, inputs)
else:
    print("[Check failed] Some array accesses may be unsafe.")
    sys.exit(1)
