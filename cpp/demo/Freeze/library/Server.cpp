// **********************************************************************
//
// Copyright (c) 2003-2016 ZeroC, Inc. All rights reserved.
//
// **********************************************************************

#include <Ice/Ice.h>
#include <Freeze/Freeze.h>
#include <BookFactory.h>

using namespace std;

class LibraryServer : public Ice::Application
{
public:

    LibraryServer(const string& envName) :
        _envName(envName)
    {
    }

    virtual int run(int argc, char* argv[]);

private:

    const string _envName;
};

int
main(int argc, char* argv[])
{
    LibraryServer app("db");
    return app.main(argc, argv, "config.server");
}

int
LibraryServer::run(int argc, char*[])
{
    if(argc > 1)
    {
        cerr << appName() << ": too many arguments" << endl;
        return EXIT_FAILURE;
    }

    Ice::PropertiesPtr properties = communicator()->getProperties();

    //
    // Create an object adapter
    //
    Ice::ObjectAdapterPtr adapter = communicator()->createObjectAdapter("Library");

    //
    // Create an evictor for books.
    //
    Freeze::EvictorPtr evictor = Freeze::createBackgroundSaveEvictor(adapter, _envName, "books");
    Ice::Int evictorSize = properties->getPropertyAsInt("EvictorSize");
    if(evictorSize > 0)
    {
        evictor->setSize(evictorSize);
    }

    //
    // Use the evictor as servant Locator.
    //
    adapter->addServantLocator(evictor, "book");


    //
    // Create the library, and add it to the object adapter.
    //
    LibraryIPtr library = new LibraryI(communicator(), _envName, "authors", evictor);
    adapter->add(library, Ice::stringToIdentity("library"));

    //
    // Create and install a factory for books.
    //
    Ice::ValueFactoryPtr bookFactory = new BookFactory(library);
    communicator()->getValueFactoryManager()->add(bookFactory, Demo::Book::ice_staticId());

    //
    // Everything ok, let's go.
    //
    shutdownOnInterrupt();
    adapter->activate();
    communicator()->waitForShutdown();
    ignoreInterrupt();

    return EXIT_SUCCESS;
}
