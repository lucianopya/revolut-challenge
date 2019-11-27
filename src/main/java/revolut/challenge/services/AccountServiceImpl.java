package revolut.challenge.services;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import revolut.challenge.dao.Account;
import revolut.challenge.dao.DataBaseManagerDAO;
import revolut.challenge.dao.Transfer;
import revolut.challenge.exceptions.BadRequestException;
import revolut.challenge.exceptions.errors.ErrorEnum;
import revolut.challenge.models.mapper.AccountMapper;
import revolut.challenge.models.mapper.TransferMapper;
import revolut.challenge.models.request.AccountRequest;
import revolut.challenge.models.response.AccountResponse;
import revolut.challenge.models.response.TransferResponse;
import revolut.challenge.models.response.TransferResponseList;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class AccountServiceImpl implements AccountService {
    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    private final DataBaseManagerDAO dataBaseManagerDAO;

    @Inject
    public AccountServiceImpl(DataBaseManagerDAO dataBaseManagerDAO) {
        this.dataBaseManagerDAO = dataBaseManagerDAO;
    }

    @Override
    public AccountResponse addAccount(AccountRequest account) {
        if (account.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException(ErrorEnum.ACCOUNT_CANT_BE_NEGATIVE.getMessage());
        }

        long accountNumber = this.dataBaseManagerDAO
                .insertAccount(account.getOwner(), account.getAmount());
        logger.info("Account created successfully. Account Number : {} ", accountNumber);
        return AccountResponse.builder()
                .accountNumber(accountNumber).build();
    }

    @Override
    public AccountResponse getAccount(long accountNumber) {
        Account account = this.dataBaseManagerDAO.getAccount(accountNumber);
        return AccountMapper.mapAccountResponse(account);
    }

    @Override
    public TransferResponseList listTransfersByAccount(long accountId) {
        List<Transfer> transfers = dataBaseManagerDAO.transfersByAccount(accountId);
        List<TransferResponse> responses =
                transfers.stream().map(transfer -> TransferMapper.mapTransferResponse(transfer)).collect(Collectors.toList());
        return TransferResponseList.builder().data(responses).build();
    }

}
