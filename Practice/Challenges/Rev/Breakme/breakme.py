#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import modules.banner as banner
from modules.main import check

if __name__ == '__main__':
    
    banner.banner()

    check(input('Enter the key : '))