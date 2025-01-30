package nflx.rozhnov.transactionservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionNewRq {
    private Long fromAccount;
    private Long toAccount;
    private double amount;
}
