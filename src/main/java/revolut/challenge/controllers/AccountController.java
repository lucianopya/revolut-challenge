package revolut.challenge.controllers;

import com.google.gson.Gson;
import revolut.challenge.exceptions.BadRequestException;
import revolut.challenge.exceptions.errors.ErrorEnum;
import revolut.challenge.models.request.AccountRequest;
import revolut.challenge.models.response.AccountResponse;
import revolut.challenge.models.response.TransferResponseList;
import revolut.challenge.services.AccountService;
import spark.Request;
import spark.Response;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

public class AccountController {

    @Inject
    private AccountService accountService;

    public AccountResponse createAccount(Request request, Response response) {
        response.status(HttpServletResponse.SC_CREATED);
        AccountRequest account;
        try {
            account = new Gson().fromJson(request.body(), AccountRequest.class);
        } catch (Exception e) {
            throw new BadRequestException("Wrong body format", e);
        }
        return accountService.addAccount(account);
    }

    public AccountResponse getAccount(Request request, Response response) {
        response.status(HttpServletResponse.SC_OK);
        try {
            long accountId = Long.parseLong(request.params("accountId"));
            return accountService.getAccount(accountId);
        } catch (NumberFormatException e) {
            throw new BadRequestException(ErrorEnum.ACCOUNT_MUST_BE_NUMBER.getMessage());
        }
    }

    public TransferResponseList listTransferByAccount(Request request, Response response) {
        response.status(HttpServletResponse.SC_OK);
        try {
            long accountId = Long.parseLong(request.params("accountId"));
            return accountService.listTransfersByAccount(accountId);
        } catch (NumberFormatException e) {
            throw new BadRequestException(ErrorEnum.ACCOUNT_MUST_BE_NUMBER.getMessage());
        }
    }
}