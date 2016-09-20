// **********************************************************************
//
// Copyright (c) 2003-2016 ZeroC, Inc. All rights reserved.
//
// **********************************************************************

#pragma once

module Demo
{

//
// A new, improved version of ContactData
//
// Typically we would keep the same type name (ContactData).
// For this Java demo we pick a different names to be able to
// build both old and new versions in the same directory.
//

struct NewContactData
{
    string phoneNumber;
    string emailAddress;
};

};
