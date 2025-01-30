package nflx.rozhnov.transactionservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TransactionHistoryRs {
    private long accountId;
    private List<TransactionRs> transactions;
}
