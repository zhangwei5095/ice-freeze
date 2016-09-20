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
        // Create and install a factory for contacts.
        //
        ContactFactory contactFactory = new ContactFactory();
        communicator().getValueFactoryManager().add(contactFactory, Demo.Contact.ice_staticId());

        //
        // Create an object adapter
        //
        Ice.ObjectAdapter adapter = communicator().createObjectAdapter("PhoneBook");

        //
        // Create the Name index
        //
        NameIndex index = new NameIndex("name");
        Freeze.Index[] indices = new Freeze.Index[1];
        indices[0] = index;

        //
        // Create an evictor for contacts.
        // When Freeze.Evictor.db.contacts.PopulateEmptyIndices is not 0 and the
        // Name index is empty, Freeze will traverse the database to recreate
        // the index during createEvictor(). Therefore the factories for the objects
        // stored in evictor (contacts here) must be registered before the call
        // to createEvictor().
        //
        Freeze.Evictor evictor = Freeze.Util.createBackgroundSaveEvictor(adapter, _envName, "contacts", null, indices, true);
        int evictorSize = properties.getPropertyAsInt("EvictorSize");
        if(evictorSize > 0)
        {
            evictor.setSize(evictorSize);
        }

        //
        // Completes the initialization of the contact factory. Note that ContactI/
        // ContactFactoryI uses this evictor only when a Contact is destroyed,
        // which cannot happen during createEvictor().
        //
        contactFactory.setEvictor(evictor);

        //
        // Register the evictor with the adapter
        //
        adapter.addServantLocator(evictor, "contact");

        //
        // Create the phonebook, and add it to the Object Adapter.
        //
        PhoneBookI phoneBook = new PhoneBookI(evictor, contactFactory, index);
        adapter.add(phoneBook, Ice.Util.stringToIdentity("phonebook"));

        //
        // Everything ok, let's go.
        //
        int status = RunParser.runParser(appName(), args, communicator());
        adapter.destroy();

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
        app.main("demo.Freeze.phonebook.Collocated", args, "config.collocated");
    }

    private String _envName;
}
