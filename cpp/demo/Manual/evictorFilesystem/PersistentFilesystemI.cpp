// **********************************************************************
//
// Copyright (c) 2003-2016 ZeroC, Inc. All rights reserved.
//
// **********************************************************************

#include <PersistentFilesystemI.h>
#include <Ice/Ice.h>

using namespace std;

//
// Filesystem::FileI
//

Freeze::EvictorPtr Filesystem::FileI::_evictor;

Filesystem::FileI::FileI() : _destroyed(false)
{
}

string
Filesystem::FileI::name(const Ice::Current& c)
{
    IceUtil::Mutex::Lock lock(_mutex);

    if(_destroyed)
    {
        throw Ice::ObjectNotExistException(__FILE__, __LINE__, c.id, c.facet, c.operation);
    }

    return nodeName;
}

void
Filesystem::FileI::destroy(const Ice::Current& c)
{
    {
        IceUtil::Mutex::Lock lock(_mutex);

        if(_destroyed)
        {
            throw Ice::ObjectNotExistException(__FILE__, __LINE__, c.id, c.facet, c.operation);
        }
        _destroyed = true;
    }

    //
    // Because we use a transactional evictor, these updates are guaranteed to be atomic.
    //
    parent->removeNode(nodeName);
    _evictor->remove(c.id);
}

Filesystem::Lines
Filesystem::FileI::read(const Ice::Current& c)
{
    IceUtil::Mutex::Lock lock(_mutex);

    if(_destroyed)
    {
        throw Ice::ObjectNotExistException(__FILE__, __LINE__, c.id, c.facet, c.operation);
    }

    return text;
}

void
Filesystem::FileI::write(const Filesystem::Lines& txt, const Ice::Current& c)
{
    IceUtil::Mutex::Lock lock(_mutex);

    if(_destroyed)
    {
        throw Ice::ObjectNotExistException(__FILE__, __LINE__, c.id, c.facet, c.operation);
    }

    this->text = txt;
}

//
// Filesystem::DirectoryI
//

Freeze::EvictorPtr Filesystem::DirectoryI::_evictor;

Filesystem::DirectoryI::DirectoryI() : _destroyed(false)
{
}

string
Filesystem::DirectoryI::name(const Ice::Current& c)
{
    IceUtil::Mutex::Lock lock(_mutex);

    if(_destroyed)
    {
        throw Ice::ObjectNotExistException(__FILE__, __LINE__, c.id, c.facet, c.operation);
    }

    return nodeName;
}

void
Filesystem::DirectoryI::destroy(const Ice::Current& c)
{
    if(!parent)
    {
        throw Filesystem::PermissionDenied("Cannot destroy root directory");
    }

    {
        IceUtil::Mutex::Lock lock(_mutex);

        if(_destroyed)
        {
            throw Ice::ObjectNotExistException(__FILE__, __LINE__, c.id, c.facet, c.operation);
        }
        if(!nodes.empty())
        {
            throw Filesystem::PermissionDenied("Cannot destroy non-empty directory");
        }
        _destroyed = true;
    }

    //
    // Because we use a transactional evictor, these updates are guaranteed to be atomic.
    //
    parent->removeNode(nodeName);
    _evictor->remove(c.id);
}

Filesystem::NodeDescSeq
Filesystem::DirectoryI::list(const Ice::Current& c)
{
    IceUtil::Mutex::Lock lock(_mutex);

    if(_destroyed)
    {
        throw Ice::ObjectNotExistException(__FILE__, __LINE__, c.id, c.facet, c.operation);
    }

    NodeDict::const_iterator p;
    NodeDescSeq result;
    for(p = nodes.begin(); p != nodes.end(); ++p)
    {
        result.push_back(p->second);
    }
    return result;
}

Filesystem::NodeDesc
Filesystem::DirectoryI::find(const string& nm, const Ice::Current& c)
{
    IceUtil::Mutex::Lock lock(_mutex);

    if(_destroyed)
    {
        throw Ice::ObjectNotExistException(__FILE__, __LINE__, c.id, c.facet, c.operation);
    }

    NodeDict::iterator p = nodes.find(nm);
    if(p == nodes.end())
    {
        throw NoSuchName(nm);
    }
    return p->second;
}

Filesystem::DirectoryPrx
Filesystem::DirectoryI::createDirectory(const string& nm, const Ice::Current& c)
{
    IceUtil::Mutex::Lock lock(_mutex);

    if(_destroyed)
    {
        throw Ice::ObjectNotExistException(__FILE__, __LINE__, c.id, c.facet, c.operation);
    }

    if(nm.empty() || nodes.find(nm) != nodes.end())
    {
        throw NameInUse(nm);
    }

    Ice::Identity id;
    id.name = Ice::generateUUID();
    PersistentDirectoryPtr dir = new DirectoryI;
    dir->nodeName = nm;
    dir->parent = PersistentDirectoryPrx::uncheckedCast(c.adapter->createProxy(c.id));
    DirectoryPrx proxy = DirectoryPrx::uncheckedCast(_evictor->add(dir, id));

    NodeDesc nd;
    nd.name = nm;
    nd.type = DirType;
    nd.proxy = proxy;
    nodes[nm] = nd;

    return proxy;
}

Filesystem::FilePrx
Filesystem::DirectoryI::createFile(const string& nm, const Ice::Current& c)
{
    IceUtil::Mutex::Lock lock(_mutex);

    if(_destroyed)
    {
        throw Ice::ObjectNotExistException(__FILE__, __LINE__, c.id, c.facet, c.operation);
    }

    if(nm.empty() || nodes.find(nm) != nodes.end())
    {
        throw NameInUse(nm);
    }

    Ice::Identity id;
    id.name = Ice::generateUUID();
    PersistentFilePtr file = new FileI;
    file->nodeName = nm;
    file->parent = PersistentDirectoryPrx::uncheckedCast(c.adapter->createProxy(c.id));
    FilePrx proxy = FilePrx::uncheckedCast(_evictor->add(file, id));

    NodeDesc nd;
    nd.name = nm;
    nd.type = FileType;
    nd.proxy = proxy;
    nodes[nm] = nd;

    return proxy;
}

void
Filesystem::DirectoryI::removeNode(const string& nm, const Ice::Current&)
{
    IceUtil::Mutex::Lock lock(_mutex);

    NodeDict::iterator p = nodes.find(nm);
    assert(p != nodes.end());
    nodes.erase(p);
}

//
// Filesystem::NodeFactory
//
Ice::ObjectPtr
Filesystem::NodeFactory::create(const string& type)
{
    if(type == PersistentFile::ice_staticId())
    {
        return new FileI;
    }
    else if(type == PersistentDirectory::ice_staticId())
    {
        return new DirectoryI;
    }
    else
    {
        assert(false);
        return 0;
    }
}
