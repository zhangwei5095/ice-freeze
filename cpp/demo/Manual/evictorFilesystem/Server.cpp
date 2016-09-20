// **********************************************************************
//
// Copyright (c) 2003-2016 ZeroC, Inc. All rights reserved.
//
// **********************************************************************

#include <PersistentFilesystemI.h>

using namespace std;
using namespace Filesystem;

class FilesystemApp : public Ice::Application
{
public:

    FilesystemApp(const string& envName) :
        _envName(envName)
    {
    }

    virtual int run(int, char*[])
    {
        //
        // Install object factories.
        //
        Ice::ValueFactoryPtr factory = new NodeFactory;
        communicator()->getValueFactoryManager()->add(factory, PersistentFile::ice_staticId());
        communicator()->getValueFactoryManager()->add(factory, PersistentDirectory::ice_staticId());

        //
        // Create an object adapter.
        //
        Ice::ObjectAdapterPtr adapter = communicator()->createObjectAdapter("EvictorFilesystem");

        //
        // Create the Freeze evictor.
        //
        Freeze::EvictorPtr evictor = Freeze::createTransactionalEvictor(adapter, _envName, "evictorfs");
        FileI::_evictor = evictor;
        DirectoryI::_evictor = evictor;

        adapter->addServantLocator(evictor, "");

        //
        // Create the root node if it doesn't exist.
        //
        Ice::Identity rootId;
        rootId.name = "RootDir";
        if(!evictor->hasObject(rootId))
        {
            PersistentDirectoryPtr root = new DirectoryI;
            root->nodeName = "/";
            evictor->add(root, rootId);
        }

        //
        // Ready to accept requests now.
        //
        adapter->activate();

        //
        // Wait until we are done.
        //
        communicator()->waitForShutdown();
        if(interrupted())
        {
            cerr << appName() << ": received signal, shutting down" << endl;
        }

        return 0;
    }

private:

    string _envName;
};

int
main(int argc, char* argv[])
{
    FilesystemApp app("db");
    return app.main(argc, argv, "config.server");
}
