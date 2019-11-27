package revolut.challenge.integrationtest;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import revolut.challenge.models.request.TransferRequest;
import revolut.challenge.models.response.AccountResponse;
import revolut.challenge.models.response.TransferResponseList;
import spark.Spark;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class TransferApiTest {
    @BeforeClass
    public static void beforeClass() throws Exception {
        Main.main(null);
        Spark.awaitInitialization();
    }


    @Test
    public void getTransfers() throws UnirestException {
        HttpResponse<String> response = Unirest.get("http://localhost:8080/v1/transfers")
                .asString();

        TransferResponseList transfers =
                new Gson().fromJson(response.getBody(), TransferResponseList.class);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK_200);
        assertThat(transfers.getData().size()).isEqualTo(4);
        assertThat(transfers.getData().get(0).getFromAccount()).isEqualTo(1003);
        assertThat(transfers.getData().get(0).getToAccount()).isEqualTo(1004);
        assertThat(transfers.getData().get(1).getFromAccount()).isEqualTo(1004);
        assertThat(transfers.getData().get(1).getToAccount()).isEqualTo(1001);
        assertThat(transfers.getData().get(2).getFromAccount()).isEqualTo(1002);
        assertThat(transfers.getData().get(2).getToAccount()).isEqualTo(1000);
    }

    @Test
    public void makeTransferSuccessfully() throws UnirestException {
        HttpResponse<String> response = Unirest.get("http://localhost:8080/v1/accounts/1000")
                .asString();
        AccountResponse accountOneBefore =
                new Gson().fromJson(response.getBody(), AccountResponse.class);
        response = Unirest.get("http://localhost:8080/v1/accounts/1001")
                .asString();
        AccountResponse accountTwoBefore =
                new Gson().fromJson(response.getBody(), AccountResponse.class);

        HttpResponse<String> responseTransfer = Unirest.post("http://localhost:8080/v1/transfers")
                .header("accept", "application/json")
                .body(new Gson().toJson(mockTransferRequest()))
                .asString();

        response = Unirest.get("http://localhost:8080/v1/accounts/1000")
                .asString();
        AccountResponse accountOneAfter =
                new Gson().fromJson(response.getBody(), AccountResponse.class);
        response = Unirest.get("http://localhost:8080/v1/accounts/1001")
                .asString();
        AccountResponse accountTwoAfter =
                new Gson().fromJson(response.getBody(), AccountResponse.class);

        assertThat(responseTransfer.getStatus()).isEqualTo(HttpStatus.NO_CONTENT_204);
        assertThat(accountOneBefore.getBalance()).isEqualTo(accountOneAfter.getBalance().add(mockTransferRequest().getAmount()));
        assertThat(accountTwoBefore.getBalance()).isEqualTo(accountTwoAfter.getBalance().subtract(mockTransferRequest().getAmount()));
    }

    @Test
    public void makeTransferErrorSameAccount() throws UnirestException, JsonProcessingException {

        HttpResponse<String> response = Unirest.post("http://localhost:8080/v1/transfers")
                .header("accept", "application/json")
                .body(new Gson().toJson(mockTransferSameAccountRequest()))
                .asString();

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = mapper.readValue(response.getBody().toString(), Map.class);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST_400);
        assertThat(map.get("message")).isEqualTo(ErrorEnum.TRANSFER_SAME_ACCOUNT.getMessage());
    }

    @Test
    public void makeTransferErrorInsufficientFunds() throws UnirestException, JsonProcessingException {

        HttpResponse<String> response = Unirest.post("http://localhost:8080/v1/transfers")
                .header("accept", "application/json")
                .body(new Gson().toJson(mockTransferInsufficientFundsRequest()))
                .asString();

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = mapper.readValue(response.getBody().toString(), Map.class);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST_400);
        assertThat(map.get("message")).isEqualTo(ErrorEnum.ACCOUNT_INSUFFICIENT_FUNDS.getMessage());
    }

    @Test
    public void makeTransferErrorNegativeAmount() throws UnirestException, JsonProcessingException {

        HttpResponse<String> response = Unirest.post("http://localhost:8080/v1/transfers")
                .header("accept", "application/json")
                .body(new Gson().toJson(mockTransferNegativeAmountRequest()))
                .asString();

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = mapper.readValue(response.getBody().toString(), Map.class);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST_400);
        assertThat(map.get("message")).isEqualTo(ErrorEnum.TRANSFER_AMOUNT_ZERO_OR_NEGATIVE.getMessage());
    }

    @AfterClass
    public static void afterClass() throws InterruptedException {
        Spark.stop();
        Thread.sleep(2000);
    }

    private TransferRequest mockTransferRequest() {
        return TransferRequest.builder()
                .fromAccount(1000)
                .toAccount(1001)
                .amount(BigDecimal.valueOf(100))
                .build();
    }

    private TransferRequest mockTransferSameAccountRequest() {
        return TransferRequest.builder()
                .fromAccount(1000)
                .toAccount(1000)
                .amount(BigDecimal.valueOf(100))
                .build();
    }

    private TransferRequest mockTransferInsufficientFundsRequest() {
        return TransferRequest.builder()
                .fromAccount(1000)
                .toAccount(1001)
                .amount(BigDecimal.valueOf(400000))
                .build();
    }

    private TransferRequest mockTransferNegativeAmountRequest() {
        return TransferRequest.builder()
                .fromAccount(1000)
                .toAccount(1001)
                .amount(BigDecimal.valueOf(-10))
                .build();
    }
}
