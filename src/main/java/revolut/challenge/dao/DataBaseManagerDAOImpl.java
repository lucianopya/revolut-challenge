package revolut.challenge.dao;

import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import revolut.challenge.configuration.JDBIConfiguration;
import revolut.challenge.exceptions.BadRequestException;
import revolut.challenge.exceptions.NotFoundException;
import revolut.challenge.exceptions.errors.ErrorEnum;

import java.math.BigDecimal;
import java.util.List;

public class DataBaseManagerDAOImpl implements DataBaseManagerDAO {
    private static final Logger logger = LoggerFactory.getLogger(DataBaseManagerDAOImpl.class);
    private Jdbi jdbi = JDBIConfiguration.getJdbi();

    @Override
    public long insertAccount(String owner, BigDecimal balance) {
        return jdbi.withExtension(AccountDAO.class,
                dao -> dao.insert(owner, balance));
    }

    @Override
    public Account getAccount(long id) {
        return jdbi.withExtension(AccountDAO.class,
                dao -> dao.getAccountById(id)
                        .orElseThrow(() -> new NotFoundException(ErrorEnum.ACCOUNT_NOT_EXISTS.getMessage())));
    }

    @Override
    public Transfer transfer(long fromAccount, long toAccount, BigDecimal amount) {
        try {
            return jdbi.inTransaction(handle -> {
                AccountDAO accountDAO = handle.attach(AccountDAO.class);

                BigDecimal balFrom = getAccountBalance(fromAccount).subtract(amount);
                if (balFrom.compareTo(BigDecimal.ZERO) < 0) {
                    throw new BadRequestException(ErrorEnum.ACCOUNT_INSUFFICIENT_FUNDS.getMessage());
                }
                accountDAO.updateAccount(fromAccount, balFrom);

                BigDecimal balTo = getAccountBalance(toAccount).add(amount);
                accountDAO.updateAccount(toAccount, balTo);

                TransferDAO transferDAO = handle.attach(TransferDAO.class);

                long transId = transferDAO.insert(fromAccount,
                        toAccount, amount);
                logger.info("Transfer successful. amount {} has been transferred from {} to {} account." +
                                "From account balance : {}, To account balance : {}",
                        amount, fromAccount, toAccount, balFrom, balTo);
                return Transfer.builder()
                        .id(transId).build();
            });
        } catch (Exception e) {
            logger.info("Transfer has been failed due to exception. amount {} hasn't been transferred " +
                            "  from {} to {} account. Exception is : " + e.getMessage(),
                    amount, fromAccount, toAccount);
            throw e;
        }
    }

    @Override
    public List<Transfer> transfersByAccount(long accountId) {
        return jdbi.withExtension(TransferDAO.class,
                dao -> dao.getTransfersByAccount(accountId));
    }

    @Override
    public List<Transfer> transfersHistory() {
        return jdbi.withExtension(TransferDAO.class, TransferDAO::listTransfers);
    }

    private BigDecimal getAccountBalance(long accountNumber) {
        return getAccount(accountNumber).getBalance();
    }
}
