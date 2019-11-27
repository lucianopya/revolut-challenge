package revolut.challenge.services;

import com.google.inject.Singleton;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Singleton
public class AccountLockImpl implements AccountLock {
    Map<Long, ReentrantLock> locks = new ConcurrentHashMap<>();

    @Override
    public void doInLock(Long accountId1, Long accountId2, Runnable action) {
        locks.putIfAbsent(accountId1, new ReentrantLock());
        locks.putIfAbsent(accountId2, new ReentrantLock());
        ReentrantLock lock1 = accountId1 < accountId2 ? locks.get(accountId1) : locks.get(accountId2);
        ReentrantLock lock2 = accountId1 < accountId2 ? locks.get(accountId2) : locks.get(accountId1);
        boolean gotTwoLocks = false;
        do {
            if (lock1.tryLock()) {
                if (lock2.tryLock()) {
                    gotTwoLocks = true;
                } else {
                    lock1.unlock();
                }
            }
        } while (!gotTwoLocks);
        try {
            action.run();
        } finally {
            lock2.unlock();
            lock1.unlock();
        }
    }
}
