package revolut.challenge.models.mapper;

import revolut.challenge.dao.Account;
import revolut.challenge.models.response.AccountResponse;

public class AccountMapper {
    public static AccountResponse mapAccountResponse(Account source) {
        return AccountResponse.builder()
                .accountNumber(source.getId())
                .owner(source.getOwner())
                .dateOpened(source.getOpenDate())
                .balance(source.getBalance()).build();
    }
}
