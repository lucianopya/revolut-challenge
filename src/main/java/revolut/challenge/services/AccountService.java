package revolut.challenge.services;

import revolut.challenge.models.request.AccountRequest;
import revolut.challenge.models.response.AccountResponse;
import revolut.challenge.models.response.TransferResponseList;

public interface AccountService {
    AccountResponse addAccount(AccountRequest account);

    AccountResponse getAccount(long accountNumber);

    TransferResponseList listTransfersByAccount(long accountId);
}
