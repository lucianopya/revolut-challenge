package revolut.challenge.models.request;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TransferRequest {
    private long fromAccount;
    private long toAccount;
    private BigDecimal amount;
}
