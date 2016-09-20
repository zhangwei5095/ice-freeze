// **********************************************************************
//
// Copyright (c) 2003-2016 ZeroC, Inc. All rights reserved.
//
// **********************************************************************

class PlayerI extends CasinoStore.PersistentPlayer
{
    @Override
    public int
    getChips(Ice.Current current)
    {
        return chips;
    }

    @Override
    public void
    destroy(Ice.Current current)
    {
        _bank.returnAllChips(_myPrx);
        _evictor.remove(current.id);
    }

    @Override
    public void
    withdraw(int count, Ice.Current current)
        throws Casino.OutOfChipsException
    {
        int newBalance = chips - count;
        if(newBalance < 0)
        {
            throw new Casino.OutOfChipsException();
        }
        chips = newBalance;
    }

    @Override
    public void
    win(int count, Ice.Current current)
    {
        assert count >= 0;
        chips += count;
    }

    void
    init(CasinoStore.PersistentPlayerPrx myPrx, Freeze.TransactionalEvictor evictor,
         CasinoStore.PersistentBankPrx bank)
    {
        _myPrx = myPrx;
        _evictor = evictor;
        _bank = bank;
    }

    private CasinoStore.PersistentPlayerPrx _myPrx;
    private Freeze.TransactionalEvictor _evictor;
    private CasinoStore.PersistentBankPrx _bank;
}
