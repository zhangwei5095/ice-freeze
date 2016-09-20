// **********************************************************************
//
// Copyright (c) 2003-2016 ZeroC, Inc. All rights reserved.
//
// **********************************************************************

#include <Ice/Ice.h>
#include <Filesystem.h>
#include <Parser.h>

using namespace std;
using namespace Filesystem;

class FilesystemClient : public Ice::Application
{
public:
    FilesystemClient() :
        //
        // Since this is an interactive demo we don't want any signal
        // handling.
        //
        Ice::Application(Ice::NoSignalHandling)
    {
    }

    virtual int run(int, char*[])
    {
        //
        // Down-cast the proxy to a Directory proxy.
        //
        DirectoryPrx rootDir = DirectoryPrx::checkedCast(communicator()->propertyToProxy("RootDir.Proxy"));
        if(!rootDir)
        {
            throw "Invalid proxy";
        }

        ParserPtr p = new Parser(rootDir);
        return p->parse();
    }
};

int
main(int argc, char* argv[])
{
    FilesystemClient client;
    return client.main(argc, argv, "config.client");
}
