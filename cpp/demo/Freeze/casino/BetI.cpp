// **********************************************************************
//
// Copyright (c) 2003-2016 ZeroC, Inc. All rights reserved.
//
// **********************************************************************

#include <BetI.h>

int
BetI::getAmount(const Ice::Current&) const
{
    return amount;
}

void
BetI::accept(const Casino::PlayerPrx& p, const Ice::Current&)
{
    if(!p)
    {
        throw Casino::OutOfChipsException();
    }

    CasinoStore::PersistentPlayerPrx player = CasinoStore::PersistentPlayerPrx::uncheckedCast(p);

    try
    {
        player->withdraw(amount);
        potentialWinners.push_back(player);
    }
    catch(const Ice::ObjectNotExistException&)
    {
        throw Casino::OutOfChipsException();
    }
}

int
BetI::getChipsInPlay(const Ice::Current&) const
{
    return amount * static_cast<int>(potentialWinners.size());
}

Ice::Long
BetI::getCloseTime(const Ice::Current&) const
{
    return closeTime;
}

void
BetI::complete(int random, const Ice::Current& current)
{
    if(random < 0)
    {
        random = -random;
    }

    int size = static_cast<int>(potentialWinners.size());

    //
    // Pick a winner using random
    //
    int winnerIndex = random % (size + (_bankEdge - 1));

    if(winnerIndex >= size)
    {
        winnerIndex = 0;
    }

    CasinoStore::WinnerPrx winner = potentialWinners[winnerIndex];

    try
    {
        winner->win(amount * size);
    }
    catch(const Ice::ObjectNotExistException&)
    {
        //
        // Goes to the bank
        //
        winner = potentialWinners[0];
        winner->win(amount * size);
    }

    //
    // Self-destroys
    //
    _evictor->remove(current.id);
}

BetI::BetI()
{
}

BetI::BetI(int amnt, Ice::Long clseTime, const CasinoStore::PersistentBankPrx& bank,
           const Freeze::TransactionalEvictorPtr& evictor, int bankEdge)
{
    amount = amnt;
    closeTime = clseTime;
    init(evictor, bankEdge);
    potentialWinners.push_back(bank);
}

void
BetI::init(const Freeze::TransactionalEvictorPtr& evictor, int bankEdge)
{
    _evictor = evictor;
    _bankEdge = bankEdge;
}
