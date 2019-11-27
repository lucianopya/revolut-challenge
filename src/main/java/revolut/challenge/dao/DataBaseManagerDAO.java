package revolut.challenge.dao;

import java.math.BigDecimal;
import java.util.List;

public interface DataBaseManagerDAO {
    long insertAccount(String owner, BigDecimal balance);

    Account getAccount(long id);

    Transfer transfer(long fromAccount, long toAccount, BigDecimal amount);

    List<Transfer> transfersByAccount(long accountId);

    List<Transfer> transfersHistory();
}
