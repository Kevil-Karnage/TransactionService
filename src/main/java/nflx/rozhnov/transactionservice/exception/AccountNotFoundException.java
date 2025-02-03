package nflx.rozhnov.transactionservice.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException() {
        super("this account not found");
        log.error("---| Failed |---");
        log.error("Caused by {}", this.getMessage());
    }
}
