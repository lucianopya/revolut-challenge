package revolut.challenge.dao;

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.Timestamped;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.math.BigDecimal;
import java.util.Optional;

public interface AccountDAO {

    @SqlUpdate("insert into account (owner,balance,open_date) VALUES (:owner, :balance, :now)")
    @GetGeneratedKeys("id")
    @Timestamped
    long insert(@Bind("owner") String owner, @Bind("balance") BigDecimal balance);

    @SqlQuery("select * from account where id = :id")
    @RegisterBeanMapper(Account.class)
    Optional<Account> getAccountById(@Bind("id") long id);

    @SqlUpdate("update account set balance = :balance where id = :account")
    boolean updateAccount(@Bind("account") long account, @Bind("balance") BigDecimal balance);
}
