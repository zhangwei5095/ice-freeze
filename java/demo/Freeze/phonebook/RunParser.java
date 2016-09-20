// **********************************************************************
//
// Copyright (c) 2003-2016 ZeroC, Inc. All rights reserved.
//
// **********************************************************************

import Demo.*;

class RunParser
{
    static int
    runParser(String appName, String[] args, Ice.Communicator communicator)
    {
        PhoneBookPrx phoneBook = PhoneBookPrxHelper.checkedCast(communicator.propertyToProxy("PhoneBook.Proxy"));
        if(phoneBook == null)
        {
            System.err.println(appName + ": invalid object reference");
            return 1;
        }

        Parser parser = new Parser(phoneBook);
        return parser.parse();
    }
}
