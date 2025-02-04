package nflx.rozhnov.transactionservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TransactionNewRq {
    private Long fromAccount;
    private Long toAccount;
    private BigDecimal amount;
}
