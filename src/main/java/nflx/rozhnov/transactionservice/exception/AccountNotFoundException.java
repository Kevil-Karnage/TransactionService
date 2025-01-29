package nflx.rozhnov.transactionservice.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException() {
        super("this account not found");
    }
}
