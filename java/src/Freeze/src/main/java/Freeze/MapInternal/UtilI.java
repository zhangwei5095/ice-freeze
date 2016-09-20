// **********************************************************************
//
// Copyright (c) 2003-2016 ZeroC, Inc. All rights reserved.
//
// **********************************************************************

package Freeze.MapInternal;

import java.nio.ByteBuffer;

class UtilI
{
    //
    // A DatabaseEntry object won't always have a ByteBuffer. In fact, the entry only
    // keeps a ByteBuffer if it's a direct buffer, otherwise the entry keeps a reference
    // to the buffer's backing array and discards the buffer.
    //
    public static ByteBuffer getBuffer(com.sleepycat.db.DatabaseEntry entry)
    {
        ByteBuffer b = entry.getDataNIO();
        if(b == null)
        {
            byte[] arr = entry.getData();
            if(arr != null)
            {
                b = ByteBuffer.wrap(arr, entry.getOffset(), entry.getSize());
            }
        }

        return b;
    }
}
