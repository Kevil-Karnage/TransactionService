package nflx.rozhnov.transactionservice.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TransactionSaveException extends RuntimeException {
    public TransactionSaveException() {
        super("Error saving the transaction");
        log.error("---| Failed |---");
        log.error("Caused by {}", this.getMessage());
    }
}
