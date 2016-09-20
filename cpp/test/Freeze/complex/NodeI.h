// **********************************************************************
//
// Copyright (c) 2003-2016 ZeroC, Inc. All rights reserved.
//
// **********************************************************************

#ifndef NODE_I_H
#define NODE_I_H

#include <Complex.h>
#include <Ice/ValueFactory.h>

namespace Complex
{

class NumberNodeI : public NumberNode
{
public:

    NumberNodeI()
    {
    }

    NumberNodeI(int n)
    {
        number = n;
    }

    virtual int calc(const Ice::Current&)
    {
        return number;
    }
};

class AddNodeI : public AddNode
{
public:

    AddNodeI()
    {
    }

    AddNodeI(const NodePtr& l, const NodePtr& r)
    {
        left = l;
        right = r;
    }

    virtual int calc(const Ice::Current&)
    {
        return left->calc() + right->calc();
    }
};

class MultiplyNodeI : public MultiplyNode
{
public:

    MultiplyNodeI()
    {
    }

    MultiplyNodeI(const NodePtr& l, const NodePtr& r)
    {
        left = l;
        right = r;
    }

    virtual int calc(const Ice::Current&)
    {
        return left->calc() * right->calc();
    }
};

class ValueFactoryI : public Ice::ValueFactory
{
public:

    virtual Ice::ObjectPtr create(const std::string& type)
    {
        if(type == "::Complex::MultiplyNode")
        {
            return new MultiplyNodeI();
        }
        if(type == "::Complex::AddNode")
        {
            return new AddNodeI();
        }
        if(type == "::Complex::NumberNode")
        {
            return new NumberNodeI();
        }
        std::cout << "create: " << type << std::endl;
        assert(false);
        return 0;
    }
};

} // End namespace Complex

#endif
