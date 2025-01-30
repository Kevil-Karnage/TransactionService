package nflx.rozhnov.transactionservice.exception;

public class TransactionSaveException extends RuntimeException {
    public TransactionSaveException() {
        super("Ошибка сохранения транзакции");
    }
}
