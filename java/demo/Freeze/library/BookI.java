// **********************************************************************
//
// Copyright (c) 2003-2016 ZeroC, Inc. All rights reserved.
//
// **********************************************************************

import Demo.*;

class BookI extends Book
{
    //
    // No read/write mutexes in Java - hence use native
    // synchronization.
    //

    @Override
    synchronized public BookDescription
    getBookDescription(Ice.Current current)
    {
        if(_destroyed)
        {
            throw new Ice.ObjectNotExistException();
        }

        //
        // Immutable.
        //
        return description;
    }

    @Override
    synchronized public String
    getRenterName(Ice.Current current)
        throws BookNotRentedException
    {
        if(_destroyed)
        {
            throw new Ice.ObjectNotExistException();
        }

        if(rentalCustomerName.length() == 0)
        {
            throw new BookNotRentedException();
        }
        return rentalCustomerName;
    }

    @Override
    synchronized public void
    rentBook(String name, Ice.Current current)
        throws BookRentedException
    {
        if(_destroyed)
        {
            throw new Ice.ObjectNotExistException();
        }

        if(rentalCustomerName.length() != 0)
        {
            throw new BookRentedException();
        }
        rentalCustomerName = name;
    }

    @Override
    synchronized public void
    returnBook(Ice.Current current)
        throws BookNotRentedException
    {
        if(_destroyed)
        {
            throw new Ice.ObjectNotExistException();
        }

        if(rentalCustomerName.length() == 0)
        {
            throw new BookNotRentedException();
        }
        rentalCustomerName = new String();;
    }

    @Override
    synchronized public void
    destroy(Ice.Current current)
        throws DatabaseException
    {
        if(_destroyed)
        {
            throw new Ice.ObjectNotExistException();
        }

        _destroyed = true;

        try
        {
            _library.remove(description);
        }
        catch(Freeze.DatabaseException ex)
        {
            DatabaseException e = new DatabaseException();
            e.message = ex.message;
            throw e;
        }
    }

    BookI(LibraryI library)
    {
        _library = library;
        _destroyed = false;

        //
        // This could be avoided by having two constructors (one for
        // new creation of a book, and the other for restoring a
        // previously saved book).
        //
        rentalCustomerName = new String();
    }

    private LibraryI _library;
    private boolean _destroyed;
}
