package nflx.rozhnov.transactionservice.dto.response;

import lombok.*;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRs {
    private UUID transactionId;
    private Date timestamp;
    private long fromAccount;
    private long toAccount;
    private double amount;
}
