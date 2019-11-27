package revolut.challenge.injection;

import com.google.inject.AbstractModule;
import revolut.challenge.dao.DataBaseManagerDAO;
import revolut.challenge.dao.DataBaseManagerDAOImpl;
import revolut.challenge.services.*;

public class BeansBinder extends AbstractModule {

    @Override
    protected void configure() {
        bind(AccountService.class).to(AccountServiceImpl.class);
        bind(TransferService.class).to(TransferServiceImpl.class);
        bind(DataBaseManagerDAO.class).to(DataBaseManagerDAOImpl.class);
        bind(AccountLock.class).to(AccountLockImpl.class);
    }
}
