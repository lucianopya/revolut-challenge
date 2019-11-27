package revolut.challenge.integrationtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import revolut.challenge.Main;
import revolut.challenge.exceptions.errors.ErrorEnum;
import revolut.challenge.models.request.AccountRequest;
import revolut.challenge.models.response.AccountResponse;
import revolut.challenge.models.response.TransferResponseList;
import spark.Spark;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class AccountApiTest {

    @BeforeClass
    public static void beforeClass() {
        Main.main(null);
        Spark.awaitInitialization();
    }

    @AfterClass
    public static void afterClass() throws InterruptedException {
        Spark.stop();
        Thread.sleep(2000);
    }

    private AccountRequest mockAccountValid() {
        return AccountRequest.builder()
                .owner("Valid account")
                .amount(new BigDecimal(1100))
                .build();
    }

    private AccountRequest mockAccountBalanceInvalid() {
        return AccountRequest.builder()
                .owner("Invalid account - negative balance")
                .amount(new BigDecimal(-10))
                .build();
    }

    @Test
    public void createAccountSuccessfully() throws UnirestException {
        HttpResponse<String> response = Unirest.post("http://localhost:8080/v1/accounts")
                .header("accept", "application/json")
                .body(new Gson().toJson(mockAccountValid()))
                .asString();
        AccountResponse accountResponse = new Gson().fromJson(response.getBody(), AccountResponse.class);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED_201);
        assertThat(accountResponse.getAccountNumber()).isEqualTo(1005);
    }

    @Test
    public void createAccountErrorBalanceInvalid() throws UnirestException {
        HttpResponse<String> response = Unirest.post("http://localhost:8080/v1/accounts")
                .header("accept", "application/json")
                .body(new Gson().toJson(mockAccountBalanceInvalid()))
                .asString();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST_400);
    }

    @Test
    public void getAccountSuccessfully() throws UnirestException {
        HttpResponse<String> response = Unirest.get("http://localhost:8080/v1/accounts/1000")
                .asString();
        AccountResponse account =
                new Gson().fromJson(response.getBody(), AccountResponse.class);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK_200);
        assertThat(account.getAccountNumber()).isEqualTo(1000);
        assertThat(account.getBalance().compareTo(new BigDecimal(1030.21)));
    }

    @Test
    public void getAccountErrorAccountNotNumber() throws UnirestException, IOException {
        HttpResponse<String> response = Unirest.get("http://localhost:8080/v1/accounts/string")
                .asString();

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = mapper.readValue(response.getBody().toString(), Map.class);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST_400);
        assertThat(map.get("message")).isEqualTo(ErrorEnum.ACCOUNT_MUST_BE_NUMBER.getMessage());
    }

    @Test
    public void getAccountErrorAccountNotExist() throws UnirestException, IOException {
        HttpResponse<String> response = Unirest.get("http://localhost:8080/v1/accounts/1010")
                .asString();

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = mapper.readValue(response.getBody().toString(), Map.class);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND_404);
        assertThat(map.get("message")).isEqualTo(ErrorEnum.ACCOUNT_NOT_EXISTS.getMessage());
    }

    @Test
    public void getTransfersByAccountSuccessfully() throws UnirestException {
        HttpResponse<String> response = Unirest.get("http://localhost:8080/v1/accounts/1004/transfers")
                .asString();

        TransferResponseList transfers =
                new Gson().fromJson(response.getBody(), TransferResponseList.class);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK_200);
        assertThat(transfers.getData().size()).isEqualTo(2);
        assertThat(transfers.getData().get(0).getFromAccount()).isEqualTo(1003);
        assertThat(transfers.getData().get(0).getToAccount()).isEqualTo(1004);
        assertThat(transfers.getData().get(1).getFromAccount()).isEqualTo(1004);
        assertThat(transfers.getData().get(1).getToAccount()).isEqualTo(1001);
    }

}
