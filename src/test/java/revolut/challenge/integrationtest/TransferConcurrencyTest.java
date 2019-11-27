package revolut.challenge.integrationtest;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import revolut.challenge.Main;
import revolut.challenge.models.request.AccountRequest;
import revolut.challenge.models.request.TransferRequest;
import revolut.challenge.models.response.AccountResponse;
import spark.Spark;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class TransferConcurrencyTest {
    @BeforeClass
    public static void beforeClass() throws Exception {
        Main.main(null);
        Spark.awaitInitialization();
    }

    @Test
    public void verifyBalanceAfterConcurrencyTransfer() throws UnirestException, InterruptedException {
        HttpResponse<String> responseOne = Unirest.post("http://localhost:8080/v1/accounts")
                .body(new Gson().toJson(accountOne()))
                .asString();
        AccountResponse accountResponseOne = new Gson().fromJson(responseOne.getBody(), AccountResponse.class);

        HttpResponse<String> responseTwo = Unirest.post("http://localhost:8080/v1/accounts")
                .body(new Gson().toJson(accountTwo()))
                .asString();
        AccountResponse accountResponseTwo = new Gson().fromJson(responseTwo.getBody(), AccountResponse.class);

        final int threads = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads; i++) {
            executorService.submit(() -> {
                TransferRequest transferRequest =
                        transferFromAccountOneToAccountTwo(accountResponseOne.getAccountNumber(),
                                accountResponseTwo.getAccountNumber());
                try {
                    Unirest.post("http://localhost:8080/v1/transfers")
                            .body(new Gson().toJson(transferRequest))
                            .asString();
                } catch (UnirestException e) {
                    e.printStackTrace();
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        final long accountOne = accountResponseOne.getAccountNumber();
        HttpResponse<String> responseOneAfter = Unirest.get("http://localhost:8080/v1/accounts/" + accountOne)
                .asString();
        AccountResponse accountOneAfter =
                new Gson().fromJson(responseOneAfter.getBody(), AccountResponse.class);

        final long accountTwo = accountResponseTwo.getAccountNumber();
        HttpResponse<String> responseTwoAfter = Unirest.get("http://localhost:8080/v1/accounts/" + accountTwo)
                .asString();
        AccountResponse accountTwoAfter =
                new Gson().fromJson(responseTwoAfter.getBody(), AccountResponse.class);

        assertThat(accountOneAfter.getBalance()).isEqualByComparingTo(new BigDecimal(13000));
        assertThat(accountTwoAfter.getBalance()).isEqualByComparingTo(new BigDecimal(2000));
    }

    @Test
    public void verifyBalanceAfterConcurrencyTransferThreeAccount() throws UnirestException, InterruptedException {
        HttpResponse<String> responseOne = Unirest.post("http://localhost:8080/v1/accounts")
                .body(new Gson().toJson(accountOne()))
                .asString();
        AccountResponse accountResponseOne = new Gson().fromJson(responseOne.getBody(), AccountResponse.class);

        HttpResponse<String> responseTwo = Unirest.post("http://localhost:8080/v1/accounts")
                .body(new Gson().toJson(accountTwo()))
                .asString();
        AccountResponse accountResponseTwo = new Gson().fromJson(responseTwo.getBody(), AccountResponse.class);

        HttpResponse<String> responseThree = Unirest.post("http://localhost:8080/v1/accounts")
                .body(new Gson().toJson(accountThree()))
                .asString();
        AccountResponse accountResponseThree = new Gson().fromJson(responseThree.getBody(), AccountResponse.class);


        final int threads = 500;
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads; i++) {
            executorService.submit(() -> {
                TransferRequest transferRequestOne =
                        transferFromAccountOneToAccountTwo(accountResponseOne.getAccountNumber(),
                                accountResponseTwo.getAccountNumber());
                try {
                    Unirest.post("http://localhost:8080/v1/transfers")
                            .body(new Gson().toJson(transferRequestOne))
                            .asString();
                } catch (UnirestException e) {
                    e.printStackTrace();
                }

                TransferRequest transferRequestThree =
                        transferFromAccountTwoToAccountThree(accountResponseTwo.getAccountNumber(),
                                accountResponseThree.getAccountNumber());
                try {
                    Unirest.post("http://localhost:8080/v1/transfers")
                            .header("accept", "application/json")
                            .body(new Gson().toJson(transferRequestThree))
                            .asString();
                } catch (UnirestException e) {
                    e.printStackTrace();
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        final long accountOne = accountResponseOne.getAccountNumber();
        HttpResponse<String> responseOneAfter = Unirest.get("http://localhost:8080/v1/accounts/" + accountOne)
                .asString();
        AccountResponse accountOneAfter =
                new Gson().fromJson(responseOneAfter.getBody(), AccountResponse.class);

        final long accountTwo = accountResponseTwo.getAccountNumber();
        HttpResponse<String> responseTwoAfter = Unirest.get("http://localhost:8080/v1/accounts/" + accountTwo)
                .asString();
        AccountResponse accountTwoAfter =
                new Gson().fromJson(responseTwoAfter.getBody(), AccountResponse.class);

        final long accountThree = accountResponseThree.getAccountNumber();
        HttpResponse<String> responseThreeAfter = Unirest.get("http://localhost:8080/v1/accounts/" + accountThree)
                .asString();
        AccountResponse accountThreeAfter =
                new Gson().fromJson(responseThreeAfter.getBody(), AccountResponse.class);
        assertThat(accountOneAfter.getBalance()).isEqualByComparingTo(new BigDecimal(14000));
        assertThat(accountTwoAfter.getBalance()).isEqualByComparingTo(new BigDecimal(500));
        assertThat(accountThreeAfter.getBalance()).isEqualByComparingTo(new BigDecimal(700));
    }

    @AfterClass
    public static void afterClass() throws InterruptedException {
        Spark.stop();
        Thread.sleep(2000);
    }

    private AccountRequest accountOne() {
        return AccountRequest.builder()
                .owner("Account 1")
                .amount(new BigDecimal(15000))
                .build();
    }

    private AccountRequest accountTwo() {
        return AccountRequest.builder()
                .owner("Account 2")
                .amount(new BigDecimal(0))
                .build();
    }

    private AccountRequest accountThree() {
        return AccountRequest.builder()
                .owner("Account 3")
                .amount(new BigDecimal(200))
                .build();
    }

    private TransferRequest transferFromAccountOneToAccountTwo(long fromAcct, long toAcct) {
        return TransferRequest.builder()
                .fromAccount(fromAcct)
                .toAccount(toAcct)
                .amount(BigDecimal.valueOf(2))
                .build();
    }

    private TransferRequest transferFromAccountTwoToAccountThree(long fromAcct, long toAcct) {
        return TransferRequest.builder()
                .fromAccount(fromAcct)
                .toAccount(toAcct)
                .amount(BigDecimal.valueOf(1))
                .build();
    }
}
