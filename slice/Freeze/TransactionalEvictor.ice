// **********************************************************************
//
// Copyright (c) 2003-2016 ZeroC, Inc. All rights reserved.
//
// **********************************************************************

#pragma once

[["cpp:header-ext:h", "objc:header-dir:objc"]]

#include <Freeze/Evictor.ice>

module Freeze
{

local interface Transaction;

/**
 * 
 * A transactional evictor is an evictor that performs every single read-write
 * operation within its own transaction.
 *
 **/
local interface TransactionalEvictor extends Evictor
{
    /**
     *
     * Get the transaction associated with the calling thread.
     *
     * @return The transaction associated with the calling thread.
     *
     **/
    ["cpp:const"] Transaction getCurrentTransaction();

    /**
     *
     * Associate a transaction with the calling thread.
     *
     * @param tx The transaction to associate with the calling thread.
     *
     **/
    void setCurrentTransaction(Transaction tx);
};

};


