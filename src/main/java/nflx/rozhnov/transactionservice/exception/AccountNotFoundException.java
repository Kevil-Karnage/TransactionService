package nflx.rozhnov.transactionservice.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException() {
        super("this account not found");
        log.info("---| Failed |---");
        log.info("Caused by {}", this.getMessage());
    }
}
