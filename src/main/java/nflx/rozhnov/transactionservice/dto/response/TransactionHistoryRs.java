package nflx.rozhnov.transactionservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nflx.rozhnov.transactionservice.model.Transaction;

import java.util.List;

@Getter
@AllArgsConstructor
public class TransactionHistoryRs {
    private long accountId;
    private List<Transaction> transactions;
}
