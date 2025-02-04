package nflx.rozhnov.transactionservice.service;

import nflx.rozhnov.transactionservice.dto.request.TransactionGetRq;
import nflx.rozhnov.transactionservice.dto.request.TransactionNewRq;
import nflx.rozhnov.transactionservice.dto.response.TransactionHistoryRs;
import nflx.rozhnov.transactionservice.dto.response.TransactionNewRs;
import nflx.rozhnov.transactionservice.dto.response.TransactionRs;
import nflx.rozhnov.transactionservice.exception.*;
import nflx.rozhnov.transactionservice.kafka.KafkaProducer;
import nflx.rozhnov.transactionservice.model.Account;
import nflx.rozhnov.transactionservice.model.Transaction;
import nflx.rozhnov.transactionservice.repository.AccountRepository;
import nflx.rozhnov.transactionservice.repository.TransactionRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    @Mock
    TransactionRepository transactionRepository;
    @Mock
    AccountRepository accountRepository;
    @Mock
    KafkaProducer kafkaProducer;

    @InjectMocks
    TransactionService service;

    private final long ACCOUNT_ID_1 = 111L;
    private final long ACCOUNT_ID_2 = 222L;
    private final BigDecimal ACCOUNT_BALANCE_1 = new BigDecimal("100.99");
    private final BigDecimal ACCOUNT_BALANCE_2 = new BigDecimal("200.99");
    private final BigDecimal SMALL_AMOUNT = new BigDecimal("50");
    private final BigDecimal BIG_AMOUNT = new BigDecimal("500");
    private final UUID TRANSACTION_ID_1 = UUID.randomUUID();
    private final Date DATE_1 = new Date();

    private final Account ACCOUNT_1 = new Account(ACCOUNT_ID_1, ACCOUNT_BALANCE_1);
    private final Account ACCOUNT_2 = new Account(ACCOUNT_ID_2, ACCOUNT_BALANCE_2);
    private final Transaction TRANSACTION_1 = new Transaction(TRANSACTION_ID_1, DATE_1, ACCOUNT_ID_1, ACCOUNT_ID_2, SMALL_AMOUNT);

    @Test
    @DisplayName("createNewTransaction - correct")
    void createNewTransaction_correct() {
        // Данные
        TransactionNewRq rq = new TransactionNewRq(ACCOUNT_ID_1, ACCOUNT_ID_2, SMALL_AMOUNT);

        // Моки
        when(accountRepository.findById(ACCOUNT_ID_1)).thenReturn(Optional.of(ACCOUNT_1));
        when(accountRepository.findById(ACCOUNT_ID_2)).thenReturn(Optional.of(ACCOUNT_2));
        when(transactionRepository.save(any())).thenReturn(TRANSACTION_1);

        // Запрос
        TransactionNewRs rs = service.createNewTransaction(rq);

        // Проверка
        assertThat(rs).isNotNull();
        assertThat(rs.getTransactionId()).isNotNull();
        assertThat(rs.getTimestamp()).isNotNull();
        verify(accountRepository, times(2)).save(any());
        verify(transactionRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("createNewTransaction - not found first account")
    void createNewTransaction_notFoundFirstAccount() {
        // Данные
        TransactionNewRq rq = new TransactionNewRq(ACCOUNT_ID_1, ACCOUNT_ID_2, SMALL_AMOUNT);

        // Моки
        when(accountRepository.findById(ACCOUNT_ID_1)).thenReturn(Optional.empty());

        // Запрос + Проверка
        Assertions.assertThatThrownBy(() -> service.createNewTransaction(rq))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    @DisplayName("createNewTransaction - not found second account")
    void createNewTransaction_notFoundSecondAccount() {
        // Данные
        TransactionNewRq rq = new TransactionNewRq(ACCOUNT_ID_1, ACCOUNT_ID_2, SMALL_AMOUNT);

        // Моки
        when(accountRepository.findById(ACCOUNT_ID_1)).thenReturn(Optional.of(ACCOUNT_1));
        when(accountRepository.findById(ACCOUNT_ID_2)).thenReturn(Optional.empty());

        // Запрос + Проверка
        Assertions.assertThatThrownBy(() -> service.createNewTransaction(rq))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    @DisplayName("createNewTransaction - firstAccountDoesNotHaveEnoughMoney")
    void createNewTransaction_firstAccountDoesNotHaveEnoughMoney() {
        // Данные
        TransactionNewRq rq = new TransactionNewRq(ACCOUNT_ID_1, ACCOUNT_ID_2, BIG_AMOUNT);

        // Моки
        when(accountRepository.findById(ACCOUNT_ID_1)).thenReturn(Optional.of(ACCOUNT_1));
        when(accountRepository.findById(ACCOUNT_ID_2)).thenReturn(Optional.of(ACCOUNT_2));

        // Запрос + Проверка
        Assertions.assertThatThrownBy(() -> service.createNewTransaction(rq))
                .isInstanceOf(AccountNotEnoughBalanceException.class);
    }

    @Test
    @DisplayName("createNewTransaction - incorrect - kafka exception")
    void createNewTransaction_kafkaException() {
        // Данные
        TransactionNewRq rq = new TransactionNewRq(ACCOUNT_ID_1, ACCOUNT_ID_2, SMALL_AMOUNT);

        // Моки
        when(accountRepository.findById(ACCOUNT_ID_1)).thenReturn(Optional.of(ACCOUNT_1));
        when(accountRepository.findById(ACCOUNT_ID_2)).thenReturn(Optional.of(ACCOUNT_2));
        when(transactionRepository.save(any())).thenReturn(TRANSACTION_1);
        doThrow(KafkaSendingException.class).when(kafkaProducer).sendMessage(any());

        // Запрос + Проверка
        Assertions.assertThatThrownBy(() -> service.createNewTransaction(rq))
                .isInstanceOf(KafkaSendingException.class);
    }

    @Test
    @DisplayName("createNewTransaction - incorrect - saveTransactionException transaction")
    void createNewTransaction_saveTransactionException_transaction() {
        // Данные
        TransactionNewRq rq = new TransactionNewRq(ACCOUNT_ID_1, ACCOUNT_ID_2, SMALL_AMOUNT);

        // Моки
        when(accountRepository.findById(ACCOUNT_ID_1)).thenReturn(Optional.of(ACCOUNT_1));
        when(accountRepository.findById(ACCOUNT_ID_2)).thenReturn(Optional.of(ACCOUNT_2));

        when(transactionRepository.save(TRANSACTION_1))
                .thenThrow(TransactionSaveException.class);

        // Запрос + Проверка
        Assertions.assertThatThrownBy(() -> service.createNewTransaction(rq))
                .isInstanceOf(TransactionSaveException.class);
    }

    @Test
    @DisplayName("createNewTransaction - incorrect - saveTransactionException firstAccount")
    void createNewTransaction_saveTransactionException_firstAccount() {
        // Данные
        TransactionNewRq rq = new TransactionNewRq(ACCOUNT_ID_1, ACCOUNT_ID_2, SMALL_AMOUNT);

        // Моки
        when(accountRepository.findById(ACCOUNT_ID_1)).thenReturn(Optional.of(ACCOUNT_1));
        when(accountRepository.findById(ACCOUNT_ID_2)).thenReturn(Optional.of(ACCOUNT_2));
        when(transactionRepository.save(any())).thenReturn(TRANSACTION_1);

        when(accountRepository.save(ACCOUNT_1))
                .thenThrow(TransactionSaveException.class);

        // Запрос + Проверка
        Assertions.assertThatThrownBy(() -> service.createNewTransaction(rq))
                .isInstanceOf(TransactionSaveException.class);
    }

    @Test
    @DisplayName("createNewTransaction - incorrect - saveTransactionException secondAccount")
    void createNewTransaction_saveTransactionException_secondAccount() {
        // Данные
        TransactionNewRq rq = new TransactionNewRq(ACCOUNT_ID_1, ACCOUNT_ID_2, SMALL_AMOUNT);

        // Моки
        when(accountRepository.findById(ACCOUNT_ID_1)).thenReturn(Optional.of(ACCOUNT_1));
        when(accountRepository.findById(ACCOUNT_ID_2)).thenReturn(Optional.of(ACCOUNT_2));
        when(transactionRepository.save(any())).thenReturn(TRANSACTION_1);

        when(accountRepository.save(ACCOUNT_1)).thenReturn(ACCOUNT_1);
        when(accountRepository.save(ACCOUNT_2))
                .thenThrow(TransactionSaveException.class);

        // Запрос + Проверка
        Assertions.assertThatThrownBy(() -> service.createNewTransaction(rq))
                .isInstanceOf(TransactionSaveException.class);
    }



    @Test
    @DisplayName("getTransactionById - correct")
    void getTransactionById() {
        // Данные
        TransactionGetRq rq = new TransactionGetRq(TRANSACTION_ID_1);

        // Моки
        when(transactionRepository.findById(TRANSACTION_ID_1)).thenReturn(Optional.of(TRANSACTION_1));

        // Запрос
        TransactionRs rs = service.getTransactionById(rq);

        assertThat(rs).isNotNull();
        assertThat(rs.getTransactionId()).isEqualTo(TRANSACTION_1.getId());
        assertThat(rs.getTimestamp()).isEqualTo(TRANSACTION_1.getTimestamp());
        assertThat(rs.getFromAccount()).isEqualTo(TRANSACTION_1.getFromAccount());
        assertThat(rs.getToAccount()).isEqualTo(TRANSACTION_1.getToAccount());
        assertThat(rs.getAmount()).isEqualTo(TRANSACTION_1.getAmount());
    }

    @Test
    @DisplayName("getTransactionById - not found transaction")
    void getTransactionById_notFound() {
        // Данные
        TransactionGetRq rq = new TransactionGetRq(TRANSACTION_ID_1);

        // Моки
        when(transactionRepository.findById(TRANSACTION_ID_1)).thenReturn(Optional.empty());

        // Запрос + Проверка
        Assertions.assertThatThrownBy(() -> service.getTransactionById(rq))
                .isInstanceOf(TransactionNotFoundException.class);
    }


    @Test
    @DisplayName("getAccountTransactions - correct")
    void getAccountTransactions() {
        // Данные
        final Transaction TRANSACTION_2 = new Transaction(
                UUID.randomUUID(),
                new Date(),
                ACCOUNT_ID_2,
                ACCOUNT_ID_1,
                BIG_AMOUNT
        );
        List<Transaction> expectedTransactions = List.of(TRANSACTION_1, TRANSACTION_2);

        // Моки
        when(accountRepository.findById(ACCOUNT_ID_1)).thenReturn(Optional.of(ACCOUNT_1));
        when(transactionRepository.findAllByAccountId(ACCOUNT_ID_1)).thenReturn(expectedTransactions);

        // Запрос
        TransactionHistoryRs rs = service.getAccountTransactions(ACCOUNT_ID_1);

        // Проверка
        assertThat(rs.getTransactions().size()).isEqualTo(expectedTransactions.size());
        for (int i = 0; i < expectedTransactions.size(); i++) {
            TransactionRs fromRs = rs.getTransactions().get(i);
            Transaction fromExpected = expectedTransactions.get(i);

            assertThat(fromRs.getTransactionId()).isEqualTo(fromExpected.getId());
            assertThat(fromRs.getTimestamp()).isEqualTo(fromExpected.getTimestamp());
            assertThat(fromRs.getFromAccount()).isEqualTo(fromExpected.getFromAccount());
            assertThat(fromRs.getToAccount()).isEqualTo(fromExpected.getToAccount());
            assertThat(fromRs.getAmount()).isEqualTo(fromExpected.getAmount());
        }
    }

    @Test
    @DisplayName("getAccountTransactions - not found accountId")
    void getAccountTransactions_notFoundAccountId() {
        // Моки
        when(accountRepository.findById(ACCOUNT_ID_1)).thenReturn(Optional.empty());

        // Запрос + Проверка
        Assertions.assertThatThrownBy(() -> service.getAccountTransactions(ACCOUNT_ID_1))
                .isInstanceOf(AccountNotFoundException.class);
    }
}