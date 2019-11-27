package revolut.challenge.unit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import revolut.challenge.services.AccountLockImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class AccountLockTest {


    @InjectMocks
    private AccountLockImpl accountLock;

    @Test
    public void doInLock() throws InterruptedException {
        List<Integer> listOne = new ArrayList<>();
        List<Integer> listTwo = new ArrayList<>();
        long accountOne = 1;
        long accountTwo = 2;
        long accountThree = 3;
        final int threads = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads; i++) {
            executorService.submit(() -> {
                accountLock.doInLock(accountTwo, accountThree, () -> add(listOne));
                accountLock.doInLock(accountOne, accountTwo, () -> add(listTwo));
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        assertThat(listOne.size()).isEqualTo(listTwo.size());
    }

    private void add(List<Integer> list) {
        list.add(1);
    }
}
