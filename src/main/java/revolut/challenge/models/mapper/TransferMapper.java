package revolut.challenge.models.mapper;

import revolut.challenge.dao.Transfer;
import revolut.challenge.models.response.TransferResponse;

public class TransferMapper {

    public static TransferResponse mapTransferResponse(Transfer source) {
        return TransferResponse.builder()
                .fromAccount(source.getFromAccount())
                .toAccount(source.getToAccount())
                .amount(source.getAmount())
                .transferDate(source.getTransferDate().substring(0,19)).build();
    }
}
