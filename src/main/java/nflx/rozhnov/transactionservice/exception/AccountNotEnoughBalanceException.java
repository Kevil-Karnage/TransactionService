package nflx.rozhnov.transactionservice.exception;

public class AccountNotEnoughBalanceException extends RuntimeException {
    public AccountNotEnoughBalanceException() {
        super("Account has not enough balance for transaction");
    }
}
