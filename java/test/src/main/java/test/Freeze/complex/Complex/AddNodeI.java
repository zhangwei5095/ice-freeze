// **********************************************************************
//
// Copyright (c) 2003-2016 ZeroC, Inc. All rights reserved.
//
// **********************************************************************

package test.Freeze.complex.Complex;

public class AddNodeI extends AddNode
{
    public
    AddNodeI()
    {
    }

    public
    AddNodeI(Node left, Node right)
    {
        this.left = left;
        this.right = right;
    }

    public int
    calc(Ice.Current current)
    {
        return left.calc(current) + right.calc(current);
    }
}
