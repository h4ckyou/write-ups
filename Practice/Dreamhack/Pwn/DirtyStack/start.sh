#!/bin/bash
socat TCP-LISTEN:8080,reuseaddr,fork EXEC:'su dirty_stack -c /home/dirty_stack/dirtystack'
