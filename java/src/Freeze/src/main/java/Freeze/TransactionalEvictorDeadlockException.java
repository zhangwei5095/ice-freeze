// **********************************************************************
//
// Copyright (c) 2003-2016 ZeroC, Inc. All rights reserved.
//
// **********************************************************************

package Freeze;

public class TransactionalEvictorDeadlockException extends Ice.SystemException
{
    public TransactionalEvictorDeadlockException(Transaction transaction)
    {
        tx = transaction;
    }

    @Override
    public String
    ice_id()
    {
        return "::Freeze::TransactionalEvictorDeadlockException";
    }

    public Transaction tx;
}
