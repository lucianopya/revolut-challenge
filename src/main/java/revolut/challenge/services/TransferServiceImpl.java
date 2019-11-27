package revolut.challenge.services;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import revolut.challenge.dao.Account;
import revolut.challenge.dao.DataBaseManagerDAO;
import revolut.challenge.dao.Transfer;
import revolut.challenge.exceptions.BadRequestException;
import revolut.challenge.exceptions.errors.ErrorEnum;
import revolut.challenge.models.mapper.TransferMapper;
import revolut.challenge.models.request.TransferRequest;
import revolut.challenge.models.response.TransferResponse;
import revolut.challenge.models.response.TransferResponseList;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class TransferServiceImpl implements TransferService {
    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    private final DataBaseManagerDAO dataBaseManagerDAO;
    private AccountLock accountLock;

    @Inject
    public TransferServiceImpl(DataBaseManagerDAO dataBaseManagerDAO, AccountLock accountLock) {
        this.dataBaseManagerDAO = dataBaseManagerDAO;
        this.accountLock = accountLock;
    }

    @Override
    public void makeTransfer(TransferRequest transfer) {
        validateTransferRequest(transfer);
        accountLock.doInLock(transfer.getFromAccount(), transfer.getToAccount(), () -> {
                    this.dataBaseManagerDAO.transfer(transfer.getFromAccount(),
                            transfer.getToAccount(), transfer.getAmount());
                }
        );
    }

    @Override
    public TransferResponseList listTransfers() {
        List<Transfer> transfers = dataBaseManagerDAO.transfersHistory();
        List<TransferResponse> responses =
                transfers.stream().map(transfer -> TransferMapper.mapTransferResponse(transfer)).collect(Collectors.toList());
        return TransferResponseList.builder().data(responses).build();
    }

    private void validateTransferRequest(TransferRequest request) {

        if (request.getFromAccount() == request.getToAccount()) {
            throw new BadRequestException(ErrorEnum.TRANSFER_SAME_ACCOUNT.getMessage());
        }
        if (request.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException(ErrorEnum.TRANSFER_AMOUNT_ZERO_OR_NEGATIVE.getMessage());
        }

        Account fromAccount = dataBaseManagerDAO.getAccount(request.getFromAccount());
        if (fromAccount == null) {
            throw new BadRequestException(ErrorEnum.ACCOUNT_NOT_EXISTS.getMessage());
        }
        Account toAccount = dataBaseManagerDAO.getAccount(request.getToAccount());
        if (toAccount == null) {
            throw new BadRequestException(ErrorEnum.ACCOUNT_NOT_EXISTS.getMessage());
        }
    }
}
