#!/usr/bin/env python
# **********************************************************************
#
# Copyright (c) 2003-2016 ZeroC, Inc. All rights reserved.
#
# **********************************************************************

import os, sys

path = [ ".", "..", "../..", "../../..", "../../../.." ]
head = os.path.dirname(sys.argv[0])
if len(head) > 0:
    path = [os.path.join(head, p) for p in path]
path = [os.path.abspath(p) for p in path if os.path.exists(os.path.join(p, "scripts", "FreezeTestUtil.py")) ]
if len(path) == 0:
    raise RuntimeError("can't find toplevel directory!")
sys.path.append(os.path.join(path[0], "scripts"))
import FreezeTestUtil as TestUtil

dbdir = os.path.join(os.getcwd(), "db")
TestUtil.cleanDbDir(dbdir)

if TestUtil.appverifier:
    TestUtil.setAppVerifierSettings([client])

client = os.path.join(os.getcwd(), TestUtil.getTestExecutable("client"))
TestUtil.simpleTest(client, ' --Freeze.Warn.Rollback=0 "%s"' % os.getcwd())

if TestUtil.appverifier:
    TestUtil.appVerifierAfterTestEnd([client])
