package revolut.challenge.services;

import revolut.challenge.models.request.TransferRequest;
import revolut.challenge.models.response.TransferResponseList;

public interface TransferService {
    void makeTransfer(TransferRequest transfer);

    TransferResponseList listTransfers();
}
