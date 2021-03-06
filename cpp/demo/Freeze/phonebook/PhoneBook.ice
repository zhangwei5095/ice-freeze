// **********************************************************************
//
// Copyright (c) 2003-2016 ZeroC, Inc. All rights reserved.
//
// **********************************************************************

#pragma once

#include <Ice/Identity.ice>

module Demo
{

exception DatabaseException
{
    string message;
};

class Contact
{
    ["cpp:const"] idempotent string getName();
    ["freeze:write"] idempotent void setName(string name) throws DatabaseException;

    ["cpp:const"] idempotent string getAddress();
    ["freeze:write"] idempotent void setAddress(string address);

    ["cpp:const"] idempotent string getPhone();
    ["freeze:write"] idempotent void setPhone(string phone);

    ["freeze:write"] void destroy() throws DatabaseException;

    string name;
    string address;
    string phone;
};

sequence<Contact*> Contacts;

interface PhoneBook
{
    Contact* createContact() throws DatabaseException;
    ["cpp:const"] idempotent Contacts findContacts(string name) throws DatabaseException;
    void setEvictorSize(int size) throws DatabaseException;
    void shutdown();
};

};
