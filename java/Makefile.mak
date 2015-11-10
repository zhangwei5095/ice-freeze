# **********************************************************************
#
# Copyright (c) 2003-2015 ZeroC, Inc. All rights reserved.
#
# This copy of Ice is licensed to you under the terms described in the
# ICE_LICENSE file included in this distribution.
#
# **********************************************************************

!if "$(GRADLE)" == ""
GRADLE = gradlew.bat
!endif

all:
	$(GRADLE) build

dist:
	$(GRADLE) :Freeze:assemble

clean:
	$(GRADLE) clean

install::
!if "$(PREFIX)" != ""
	$(GRADLE) -Dorg.gradle.project.prefix="$(PREFIX)" install
!else
	$(GRADLE) install
!endif

test:
	@python .\allTests.py