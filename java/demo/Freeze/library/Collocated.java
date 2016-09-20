// **********************************************************************
//
// Copyright (c) 2003-2016 ZeroC, Inc. All rights reserved.
//
// **********************************************************************

class Collocated extends Ice.Application
{
    class ShutdownHook extends Thread
    {
        @Override
        public void
        run()
        {
            try
            {
                communicator().destroy();
            }
            catch(Ice.LocalException ex)
            {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public int
    run(String[] args)
    {
        //
        // Since this is an interactive demo we want to clear the
        // Application installed interrupt callback and install our
        // own shutdown hook.
        //
        setInterruptHook(new ShutdownHook());

        Ice.Properties properties = communicator().getProperties();

        //
        // Create an Object Adapter
        //
        Ice.ObjectAdapter adapter = communicator().createObjectAdapter("Library");

        //
        // Create an Evictor for books.
        //
        Freeze.Evictor evictor = Freeze.Util.createBackgroundSaveEvictor(adapter, _envName, "books", null, null, true);
        int evictorSize = properties.getPropertyAsInt("EvictorSize");
        if(evictorSize > 0)
        {
            evictor.setSize(evictorSize);
        }

        adapter.addServantLocator(evictor, "book");

        //
        // Create the library, and add it to the Object Adapter.
        //
        LibraryI library = new LibraryI(communicator(), _envName, "authors", evictor);
        adapter.add(library, Ice.Util.stringToIdentity("library"));

        //
        // Create and install a factory and initializer for books.
        //
        Ice.ValueFactory bookFactory = new BookFactory(library);
        communicator().getValueFactoryManager().add(bookFactory, Demo.Book.ice_staticId());

        //
        // Everything ok, let's go.
        //
        int status = RunParser.runParser(appName(), args, communicator());
        adapter.destroy();

        library.close();
        return status;
    }

    Collocated(String envName)
    {
        _envName = envName;
    }

    static public void
    main(String[] args)
    {
        Collocated app = new Collocated("db");
        app.main("demo.Freeze.library.Collocated", args, "config.collocated");
    }

    private String _envName;
}
