// **********************************************************************
//
// Copyright (c) 2003-2016 ZeroC, Inc. All rights reserved.
//
// **********************************************************************

package Freeze.MapInternal;

interface IteratorModel<K, V>
{
    String dbName();
    TraceLevels traceLevels();

    com.sleepycat.db.Cursor openCursor()
        throws com.sleepycat.db.DatabaseException;

    EntryI<K, V> firstEntry(com.sleepycat.db.Cursor cursor)
        throws com.sleepycat.db.DatabaseException;

    EntryI<K, V> nextEntry(com.sleepycat.db.Cursor cursor)
        throws com.sleepycat.db.DatabaseException;
}
