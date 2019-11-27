package revolut.challenge.controllers;

import com.google.gson.Gson;
import revolut.challenge.exceptions.BadRequestException;
import revolut.challenge.models.request.TransferRequest;
import revolut.challenge.models.response.TransferResponseList;
import revolut.challenge.services.TransferService;
import spark.Request;
import spark.Response;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

public class TransferController {

    @Inject
    private TransferService transferService;

    public Object makeTransfer(Request request, Response response) {
        response.status(HttpServletResponse.SC_NO_CONTENT);
        TransferRequest transfer;
        try {
            transfer = new Gson().fromJson(request.body(), TransferRequest.class);
        } catch (Exception e) {
            throw new BadRequestException("Wrong body format", e);
        }
        transferService.makeTransfer(transfer);

        return null;
    }

    public TransferResponseList transferHistory(Request request, Response response) {
        response.status(HttpServletResponse.SC_OK);
        return transferService.listTransfers();
    }
}