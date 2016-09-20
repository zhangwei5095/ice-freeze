// **********************************************************************
//
// Copyright (c) 2003-2016 ZeroC, Inc. All rights reserved.
//
// **********************************************************************

class Server extends Ice.Application
{
    @Override
    public int
    run(String[] args)
    {
        if(args.length > 0)
        {
            System.err.println(appName() + ": too many arguments");
            return 1;
        }

        Ice.Properties properties = communicator().getProperties();

        //
        // Create an object adapter
        //
        Ice.ObjectAdapter adapter = communicator().createObjectAdapter("Library");

        //
        // Create an evictor for books.
        //
        Freeze.Evictor evictor = Freeze.Util.createBackgroundSaveEvictor(adapter, _envName, "books", null, null, true);
        int evictorSize = properties.getPropertyAsInt("EvictorSize");
        if(evictorSize > 0)
        {
            evictor.setSize(evictorSize);
        }

        adapter.addServantLocator(evictor, "book");

        //
        // Create the library, and add it to the object adapter.
        //
        LibraryI library = new LibraryI(communicator(), _envName, "authors", evictor);
        adapter.add(library, Ice.Util.stringToIdentity("library"));

        //
        // Create and install a factory for books.
        //
        Ice.ValueFactory bookFactory = new BookFactory(library);
        communicator().getValueFactoryManager().add(bookFactory, Demo.Book.ice_staticId());

        //
        // Everything ok, let's go.
        //
        adapter.activate();

        shutdownOnInterrupt();
        communicator().waitForShutdown();
        defaultInterrupt();

        library.close();
        return 0;
    }

    Server(String envName)
    {
        _envName = envName;
    }

    static public void
    main(String[] args)
    {
        Server app = new Server("db");
        app.main("demo.Freeze.library.Server", args, "config.server");
    }

    private String _envName;
}
