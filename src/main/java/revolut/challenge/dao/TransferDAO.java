package revolut.challenge.dao;

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.Timestamped;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDAO {
    @SqlUpdate("insert into transfer(from_account,to_account,amount,transfer_date)" +
            " values (:from_account, :to_account, :amount, :now)")
    @GetGeneratedKeys("id")
    @Timestamped
    long insert(@Bind("from_account") long fromAccount, @Bind("to_account") long toAccount,
                @Bind("amount") BigDecimal amount);

    @SqlQuery("select * from transfer where from_account = :accountId or to_account = :accountId")
    @RegisterBeanMapper(Transfer.class)
    List<Transfer> getTransfersByAccount(@Bind("accountId") long accountId);

    @SqlQuery("select * from transfer order by id")
    @RegisterBeanMapper(Transfer.class)
    List<Transfer> listTransfers();
}
