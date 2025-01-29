package nflx.rozhnov.transactionservice.dto.response;

import lombok.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRs {
    private UUID transactionId;
    private ZonedDateTime timestamp;
}
