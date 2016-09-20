// **********************************************************************
//
// Copyright (c) 2003-2016 ZeroC, Inc. All rights reserved.
//
// **********************************************************************

import Filesystem.*;

public class Server extends Ice.Application
{
    public
    Server(String envName)
    {
        _envName = envName;
    }

    @Override
    public int
    run(String[] args)
    {
        //
        // Install object factories.
        //
        Ice.ValueFactory factory = new NodeFactory();
        communicator().getValueFactoryManager().add(factory, PersistentFile.ice_staticId());
        communicator().getValueFactoryManager().add(factory, PersistentDirectory.ice_staticId());

        //
        // Create an object adapter.
        //
        Ice.ObjectAdapter adapter = communicator().createObjectAdapter("EvictorFilesystem");

        //
        // Create the Freeze evictor (stored in the _evictor
        // static member).
        //
        Freeze.Evictor evictor = Freeze.Util.createTransactionalEvictor(adapter, _envName, "evictorfs",
                                                                        null, null, null, true);
        DirectoryI._evictor = evictor;
        FileI._evictor = evictor;

        adapter.addServantLocator(evictor, "");

        //
        // Create the root node if it doesn't exist.
        //
        Ice.Identity rootId = new Ice.Identity();
        rootId.name = "RootDir";
        if(!evictor.hasObject(rootId))
        {
            PersistentDirectory root = new DirectoryI();
            root.nodeName = "/";
            root.nodes = new java.util.HashMap<java.lang.String, NodeDesc>();
            evictor.add(root, rootId);
        }

        //
        // Ready to accept requests now.
        //
        adapter.activate();

        //
        // Wait until we are done.
        //
        communicator().waitForShutdown();

        return 0;
    }

    public static void
    main(String[] args)
    {
        Server app = new Server("db");
        int status = app.main("demo.book.evictor_filesystem.Server", args, "config.server");
        System.exit(status);
    }

    private String _envName;
}
