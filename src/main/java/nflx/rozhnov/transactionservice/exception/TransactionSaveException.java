package nflx.rozhnov.transactionservice.exception;

public class TransactionSaveException extends RuntimeException {
    public TransactionSaveException() {
        super("Error saving the transaction");
    }
}
