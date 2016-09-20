// **********************************************************************
//
// Copyright (c) 2003-2016 ZeroC, Inc. All rights reserved.
//
// **********************************************************************

package Freeze;

public class FileLockException extends RuntimeException implements Cloneable
{
    public FileLockException()
    {
    }

    public FileLockException(String path)
    {
        this.path = path;
    }

    public FileLockException(Throwable cause)
    {
        super(cause);
    }

    public FileLockException(String path, Throwable cause)
    {
        super(cause);
        this.path = path;
    }

    @Override
    public FileLockException clone()
    {
	FileLockException c = null;
	try
	{
	    c = (FileLockException)super.clone();
	}
	catch(CloneNotSupportedException ex)
	{
	    assert false;
	}
	return c; 
    }

    public String
    ice_name()
    {
        return "Freeze::FileLockException";
    }

    @Override
    public String
    toString()
    {
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
        IceUtilInternal.OutputBase out = new IceUtilInternal.OutputBase(pw);
        out.setUseTab(false);
        out.print(getClass().getName());
        out.inc();
        IceInternal.ValueWriter.write(this, out);
        pw.flush();
        return sw.toString();
    }

    public String path;
}
