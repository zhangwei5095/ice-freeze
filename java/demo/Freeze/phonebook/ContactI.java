// **********************************************************************
//
// Copyright (c) 2003-2016 ZeroC, Inc. All rights reserved.
//
// **********************************************************************

import Demo.*;

class ContactI extends Contact
{
    //
    // No read/write mutexes in Java - hence use native
    // synchronization.
    //

    @Override
    synchronized public String
    getName(Ice.Current current)
    {
        return name;
    }

    @Override
    synchronized public void
    setName(String name, Ice.Current current)
        throws DatabaseException
    {
        this.name = name;
    }

    @Override
    synchronized public String
    getAddress(Ice.Current current)
    {
        return address;
    }

    @Override
    synchronized public void
    setAddress(String address, Ice.Current current)
    {
        this.address = address;
    }

    @Override
    synchronized public String
    getPhone(Ice.Current current)
    {
        return phone;
    }

    @Override
    synchronized public void
    setPhone(String phone, Ice.Current current)
    {
        this.phone = phone;
    }

    @Override
    public void
    destroy(Ice.Current current)
        throws DatabaseException
    {
        try
        {
            _factory.getEvictor().remove(current.id);
        }
        catch(Freeze.DatabaseException ex)
        {
            DatabaseException e = new DatabaseException();
            e.message = ex.message;
            throw e;
        }
    }

    ContactI(ContactFactory factory)
    {
        _factory = factory;
        //
        // It's possible to avoid this if there were two constructors
        // - one for original creation of the Contact and one for
        // loading of an existing Contact.
        //
        name = new String();
        address = new String();
        phone = new String();
    }

    private ContactFactory _factory;
 
}
