// **********************************************************************
//
// Copyright (c) 2003-2016 ZeroC, Inc. All rights reserved.
//
// **********************************************************************

package Freeze;

/**
 * A base interface for retrieving persistent objects from a backing store.
 **/

public interface Store
{
    /**
     * Instantiate an object, initializing its state from a backing store.
     *
     * @param key The database key for the object to instantiate.
     *
     **/
    Object load(Object key);
}
