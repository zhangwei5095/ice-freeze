#!/usr/bin/env python
# **********************************************************************
#
# Copyright (c) 2003-2016 ZeroC, Inc. All rights reserved.
#
# **********************************************************************

import os, sys, imp

sys.path.append(os.path.join(os.path.dirname(__file__), "scripts"))
import FreezeTestUtil as TestUtil

testGroups = []

languages = [ "cpp", "java" ]

for d in languages:

    filename = os.path.abspath(os.path.join(os.path.dirname(__file__), d, "allTests.py"))
    f = open(filename, "r")
    current_mod = imp.load_module("allTests", f, filename, (".py", "r", imp.PY_SOURCE))
    f.close()

    tests = []

    tests = [ (os.path.join(d, "test", x), y) for x, y in current_mod.tests ]
    if len(tests) > 0:
        testGroups.extend(tests)

TestUtil.run(testGroups, root=True)
