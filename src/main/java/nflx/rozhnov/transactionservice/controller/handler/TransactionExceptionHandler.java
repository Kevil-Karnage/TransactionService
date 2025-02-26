package nflx.rozhnov.transactionservice.controller.handler;

import nflx.rozhnov.transactionservice.dto.response.ExceptionResponse;
import nflx.rozhnov.transactionservice.exception.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
public class TransactionExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(AccountNotEnoughBalanceException.class)
    private ResponseEntity<Object> handleAccountNotEnoughBalanceException(RuntimeException ex, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(BAD_REQUEST.value(), ex.getMessage());
        return handleExceptionInternal(ex, response,
                new HttpHeaders(), BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {AccountNotFoundException.class, TransactionNotFoundException.class})
    private ResponseEntity<Object> handleTransactionNotFoundException(RuntimeException ex, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(NOT_FOUND.value(), ex.getMessage());
        return handleExceptionInternal(ex, response,
                new HttpHeaders(), NOT_FOUND, request);
    }

    @ExceptionHandler(value = {TransactionSaveException.class, KafkaSendingException.class})
    private ResponseEntity<Object> handleTransactionNotSavedException(RuntimeException ex, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(BAD_GATEWAY.value(), ex.getMessage());
        return handleExceptionInternal(ex, response,
                new HttpHeaders(), BAD_GATEWAY, request);
    }
}
