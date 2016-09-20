// **********************************************************************
//
// Copyright (c) 2003-2016 ZeroC, Inc. All rights reserved.
//
// **********************************************************************

#include <BetResolver.h>
#include <stdlib.h>

BetResolver::BetResolver() :
    _betCount(0)
{
}

void
BetResolver::start()
{
    //
    // Initialize the timers
    //
    for(int i = 0; i < 3; ++i)
    {
        _timers.push_back(new IceUtil::Timer);
    }
}

void
BetResolver::add(const CasinoStore::PersistentBetPrx& bet, Ice::Long closeTime)
{
    class Task : public IceUtil::TimerTask
    {
    public:

        Task(BetResolver& resolver, const CasinoStore::PersistentBetPrx& b) :
            _resolver(resolver),
            _bet(b)
        {
        }

        virtual void
        runTimerTask()
        {
            try
            {
                _bet->complete(rand());
            }
            catch(const Ice::ObjectNotExistException&)
            {
                //
                // Looks like this bet was never saved (committed)
                //
            }
            catch(...)
            {
                _resolver.decrementBetCount();
                throw;
            }
            _resolver.decrementBetCount();
        }

    private:

        Task& operator=(const Task&) { return *this; }

        BetResolver& _resolver;
        CasinoStore::PersistentBetPrx _bet;

    };

    IceUtil::Time timeLeft = IceUtil::Time::milliSeconds(closeTime) - IceUtil::Time::now();
    _timers[rand() % _timers.size()]->schedule(new Task(*this, bet), timeLeft);

    IceUtil::Mutex::Lock sync(_mutex);
    _betCount++;
}

void
BetResolver::destroy()
{
    for(size_t i = 0; i < _timers.size(); ++i)
    {
        _timers[i]->destroy();
    }
}

int
BetResolver::getBetCount() const
{
    IceUtil::Mutex::Lock sync(_mutex);
    return _betCount;
}

void
BetResolver::decrementBetCount()
{
    IceUtil::Mutex::Lock sync(_mutex);
    _betCount--;
}
