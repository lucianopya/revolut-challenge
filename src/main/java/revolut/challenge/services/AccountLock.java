package revolut.challenge.services;

public interface AccountLock {
    void doInLock(Long accountId1, Long accountId2, Runnable action);
}
