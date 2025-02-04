package nflx.rozhnov.transactionservice.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TransactionNotFoundException extends RuntimeException {
    public TransactionNotFoundException() {
        super("Transaction not found");
        log.error("---| Failed |---");
        log.error("Caused by {}", this.getMessage());
    }
}
