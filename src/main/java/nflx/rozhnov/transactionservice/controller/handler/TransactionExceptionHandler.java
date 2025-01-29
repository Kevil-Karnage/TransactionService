package nflx.rozhnov.transactionservice.controller.handler;

import nflx.rozhnov.transactionservice.dto.response.ExceptionResponse;
import nflx.rozhnov.transactionservice.exception.AccountNotEnoughBalanceException;
import nflx.rozhnov.transactionservice.exception.AccountNotFoundException;
import nflx.rozhnov.transactionservice.exception.TransactionNotFoundException;
import nflx.rozhnov.transactionservice.exception.TransactionSaveException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class TransactionExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AccountNotEnoughBalanceException.class)
    private ResponseEntity<Object> handleAccountNotEnoughBalanceException(RuntimeException ex, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(400, ex.getMessage());
        return handleExceptionInternal(ex, response,
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {AccountNotFoundException.class, TransactionNotFoundException.class})
    private ResponseEntity<Object> handleTransactionNotFoundException(RuntimeException ex, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(404, ex.getMessage());
        return handleExceptionInternal(ex, response,
                new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = TransactionSaveException.class)
    private ResponseEntity<Object> handleTransactionNotSavedException(RuntimeException ex, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(500, ex.getMessage());
        return handleExceptionInternal(ex, response,
                new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
