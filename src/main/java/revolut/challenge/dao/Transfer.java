package revolut.challenge.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transfer {
    private long id;
    private long fromAccount;
    private long toAccount;
    private String transferDate;
    private BigDecimal amount;
}
