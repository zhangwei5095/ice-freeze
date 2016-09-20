// **********************************************************************
//
// Copyright (c) 2003-2016 ZeroC, Inc. All rights reserved.
//
// **********************************************************************

package test.Freeze.fileLock;

public class ClientFail extends test.Util.Application
{
    private static void
    test(boolean b)
    {
        if(!b)
        {
            throw new RuntimeException();
        }
    }

    @Override
    public int run(String[] args)
    {
        String envName = "db";
        try
        {
            if(args.length > 0)
            {
                envName = args[0];
                envName += "/";
                envName += "db";
            }

            try
            {
                System.out.println("create createConnection");
                Freeze.Util.createConnection(communicator(), envName);
                test(false);
            }
            catch(Freeze.FileLockException ex)
            {
                System.out.println("File lock not acquired.");
                test(true);
            }
        }
        catch(Exception ex)
        {
            System.out.println("exception:\n" + ex.toString());
            test(false);
        }
        return 0;
    }
    
    public static void main(String[] args)
    {
        ClientFail c = new ClientFail();
        int status = c.main("Client", args);
        System.gc();
        System.exit(status);
    }
}
