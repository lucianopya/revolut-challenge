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
import revolut.challenge.models.request.TransferRequest;
import revolut.challenge.models.response.TransferResponseList;
import revolut.challenge.services.AccountLock;
import revolut.challenge.services.TransferServiceImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TransferServiceTest {

    @Mock
    private DataBaseManagerDAO moneyTransfer;

    @Mock
    private AccountLock accountLock;

    @InjectMocks
    private TransferServiceImpl transferService;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void makeTransfer() {
        when(moneyTransfer.getAccount(1))
                .thenReturn(mockAccountOne());
        when(moneyTransfer.getAccount(2))
                .thenReturn(mockAccountTwo());
        doNothing().when(accountLock).doInLock(any(), any(), any());
        transferService.makeTransfer(mockTransferRequest());
    }

    @Test
    public void makeTransferFromAccountNotValidError() {
        when(moneyTransfer.getAccount(1))
                .thenReturn(null);

        expectedEx.expect(BadRequestException.class);
        expectedEx.expectMessage(ErrorEnum.ACCOUNT_NOT_EXISTS.getMessage());

        transferService.makeTransfer(mockTransferRequest());
    }

    @Test
    public void makeTransferToAccountNotValidError() {
        when(moneyTransfer.getAccount(1))
                .thenReturn(mockAccountOne());
        when(moneyTransfer.getAccount(2))
                .thenReturn(null);

        expectedEx.expect(BadRequestException.class);
        expectedEx.expectMessage(ErrorEnum.ACCOUNT_NOT_EXISTS.getMessage());

        transferService.makeTransfer(mockTransferRequest());
    }

    @Test
    public void makeTransferSameAccountError() {
        expectedEx.expect(BadRequestException.class);
        expectedEx.expectMessage(ErrorEnum.TRANSFER_SAME_ACCOUNT.getMessage());

        transferService.makeTransfer(mockSameAccount());
    }

    @Test
    public void makeTransferZeroOrNegativeError() {
        expectedEx.expect(BadRequestException.class);
        expectedEx.expectMessage(ErrorEnum.TRANSFER_AMOUNT_ZERO_OR_NEGATIVE.getMessage());

        transferService.makeTransfer(mockTransferNegative());
    }

    @Test
    public void testListTransfers() {
        when(moneyTransfer.transfersHistory())
                .thenReturn(mockTransferList());
        TransferResponseList response = transferService.listTransfers();

        assertThat(response.getData().size()).isEqualTo(3);
    }

    private TransferRequest mockTransferRequest() {
        return TransferRequest.builder()
                .fromAccount(1)
                .toAccount(2)
                .amount(BigDecimal.valueOf(123)).build();
    }

    private TransferRequest mockSameAccount() {
        return TransferRequest.builder()
                .fromAccount(1)
                .toAccount(1)
                .amount(BigDecimal.valueOf(123)).build();
    }

    private TransferRequest mockTransferNegative() {
        return TransferRequest.builder()
                .fromAccount(1)
                .toAccount(2)
                .amount(BigDecimal.valueOf(-12)).build();
    }

    private Account mockAccountOne() {
        return Account.builder()
                .id(1)
                .owner("luciano")
                .openDate("2019-12-12 00:00:00")
                .balance(BigDecimal.valueOf(123)).build();
    }

    private Account mockAccountTwo() {
        return Account.builder()
                .id(2)
                .owner("jhon")
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
