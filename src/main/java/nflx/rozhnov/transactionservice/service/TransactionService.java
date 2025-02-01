package nflx.rozhnov.transactionservice.service;

import lombok.RequiredArgsConstructor;
import nflx.rozhnov.transactionservice.dto.request.TransactionGetRq;
import nflx.rozhnov.transactionservice.dto.request.TransactionNewRq;
import nflx.rozhnov.transactionservice.dto.response.TransactionHistoryRs;
import nflx.rozhnov.transactionservice.dto.response.TransactionNewRs;
import nflx.rozhnov.transactionservice.dto.response.TransactionRs;
import nflx.rozhnov.transactionservice.exception.AccountNotEnoughBalanceException;
import nflx.rozhnov.transactionservice.exception.AccountNotFoundException;
import nflx.rozhnov.transactionservice.exception.TransactionNotFoundException;
import nflx.rozhnov.transactionservice.exception.TransactionSaveException;
import nflx.rozhnov.transactionservice.kafka.KafkaProducer;
import nflx.rozhnov.transactionservice.model.Account;
import nflx.rozhnov.transactionservice.model.Transaction;
import nflx.rozhnov.transactionservice.repository.AccountRepository;
import nflx.rozhnov.transactionservice.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final KafkaProducer kafkaProducer;

    public TransactionNewRs createNewTransaction(TransactionNewRq rq) throws AccountNotFoundException{
        // 1) проверяем аккаунты
        Account from = accountRepository.findById(rq.getFromAccount())
                .orElseThrow(AccountNotFoundException::new);
        Account to = accountRepository.findById(rq.getToAccount())
                .orElseThrow(AccountNotFoundException::new);

        if (from.getBalance().compareTo(rq.getAmount()) < 0) {
            throw new AccountNotEnoughBalanceException();
        }

        Transaction transaction = new Transaction(
                UUID.randomUUID(),
                new Date(),
                rq.getFromAccount(),
                rq.getToAccount(),
                rq.getAmount()
        );

        // сохраняем результат

        from.setBalance(from.getBalance().add(rq.getAmount().multiply(new BigDecimal("-1"))));
        to.setBalance(to.getBalance().add(rq.getAmount()));

        transaction = saveTransaction(transaction, from, to);

        kafkaProducer.sendMessage(transaction);

        // создаем ответ и возвращаем
        return new TransactionNewRs(transaction.getId(), transaction.getTimestamp());
    }

    public TransactionRs getTransactionById(TransactionGetRq rq) {
        Transaction transaction = transactionRepository.findById(rq.getTransactionId())
                .orElseThrow(TransactionNotFoundException::new);

        return new TransactionRs(
                transaction.getId(),
                transaction.getTimestamp(),
                transaction.getFromAccount(),
                transaction.getToAccount(),
                transaction.getAmount());
    }

    public TransactionHistoryRs getAccountTransactions(long accountId) {
        // проверяем существование аккаунта
        accountRepository.findById(accountId).orElseThrow(AccountNotFoundException::new);

        // ищем список всех транзакций и собираем response
        List<TransactionRs> rsList = transactionRepository.findAllByAccountId(accountId).stream()
                .map(tr -> new TransactionRs(
                        tr.getId(),
                        tr.getTimestamp(),
                        tr.getFromAccount(),
                        tr.getToAccount(),
                        tr.getAmount()
                ))
                .toList();

        return new TransactionHistoryRs(accountId, rsList);
    }

    @Transactional
    private Transaction saveTransaction(Transaction transaction, Account from, Account to) {
        try {
            transaction = transactionRepository.save(transaction);
        } catch (Exception ex) {
            throw new TransactionSaveException();
        }

        try {
            accountRepository.save(from);
        } catch (Exception ex) {
            transactionRepository.deleteById(transaction.getId());
            throw new TransactionSaveException();
        }

        try {
            accountRepository.save(to);
        } catch (Exception ex) {
            transactionRepository.deleteById(transaction.getId());
            accountRepository.deleteById(from.getId());
            throw new TransactionSaveException();
        }

        return transaction;
    }
}
