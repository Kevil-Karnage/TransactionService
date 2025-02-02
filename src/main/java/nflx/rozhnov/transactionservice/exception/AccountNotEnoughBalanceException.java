package nflx.rozhnov.transactionservice.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AccountNotEnoughBalanceException extends RuntimeException {
    public AccountNotEnoughBalanceException() {
        super("Account has not enough balance for transaction");
        log.info("---| Failed |---");
        log.info("Caused by {}", this.getMessage());
    }
}
