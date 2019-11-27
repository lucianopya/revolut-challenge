package revolut.challenge.unit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import revolut.challenge.dao.Account;
import revolut.challenge.dao.DataBaseManagerDAO;
import revolut.challenge.dao.Transfer;
import revolut.challenge.exceptions.BadRequestException;
import revolut.challenge.exceptions.errors.ErrorEnum;
import revolut.challenge.models.request.AccountRequest;
import revolut.challenge.models.response.AccountResponse;
import revolut.challenge.models.response.TransferResponseList;
import revolut.challenge.services.AccountServiceImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTest {

    @Mock
    private DataBaseManagerDAO moneyTransfer;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void testAddAccountSuccessfully() {
        when(moneyTransfer.insertAccount("luciano", BigDecimal.valueOf(123)))
                .thenReturn((long) 120);
        AccountResponse response = accountService.addAccount(mockAccountRequest());

        assertThat(response.getAccountNumber()).isEqualTo(120);
    }

    @Test
    public void testAddAccountAmountNegativeError() {
        expectedEx.expect(BadRequestException.class);
        expectedEx.expectMessage(ErrorEnum.ACCOUNT_CANT_BE_NEGATIVE.getMessage());

        accountService.addAccount(mockAccountAmountNegativeRequest());
    }

    @Test
    public void testGetAccountSuccessfully() {
        long accountId = 12;
        when(moneyTransfer.getAccount(accountId))
                .thenReturn(mockAccount());
        AccountResponse response = accountService.getAccount(accountId);

        assertThat(response.getOwner()).isEqualTo("luciano");
        assertThat(response.getBalance()).isEqualTo(BigDecimal.valueOf(123));
    }

    @Test
    public void testListTransferByAccountSuccessfully() {
        long accountId = 12;
        when(moneyTransfer.transfersByAccount(accountId))
                .thenReturn(mockTransferList());
        TransferResponseList response = accountService.listTransfersByAccount(accountId);

        assertThat(response.getData().size()).isEqualTo(3);
        assertThat(response.getData().get(0).getFromAccount()).isEqualTo(accountId);
        assertThat(response.getData().get(1).getFromAccount()).isEqualTo(accountId);
        assertThat(response.getData().get(2).getToAccount()).isEqualTo(accountId);
    }

    private AccountRequest mockAccountRequest() {
        return AccountRequest.builder()
                .owner("luciano")
                .amount(BigDecimal.valueOf(123)).build();
    }

    private AccountRequest mockAccountAmountNegativeRequest() {
        return AccountRequest.builder()
                .owner("luciano")
                .amount(BigDecimal.valueOf(-123)).build();
    }

    private Account mockAccount() {
        return Account.builder()
                .owner("luciano")
                .openDate("2019-12-12 00:00:00")
                .balance(BigDecimal.valueOf(123)).build();
    }

    private List<Transfer> mockTransferList() {
        List<Transfer> transfers = new ArrayList<>();
        transfers.add(Transfer.builder()
                .fromAccount(12)
                .toAccount(13)
                .amount(BigDecimal.valueOf(50))
                .transferDate("2019-12-12 00:00:00")
                .build()
        );
        transfers.add(Transfer.builder()
                .fromAccount(12)
                .toAccount(14)
                .amount(BigDecimal.valueOf(10))
                .transferDate("2019-12-12 00:00:00")
                .build()
        );
        transfers.add(Transfer.builder()
                .fromAccount(3)
                .toAccount(12)
                .amount(BigDecimal.valueOf(60))
                .transferDate("2019-12-12 00:00:00")
                .build()
        );
        return transfers;
    }
}
