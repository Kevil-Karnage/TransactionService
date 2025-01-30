package nflx.rozhnov.transactionservice.service;

import nflx.rozhnov.transactionservice.dto.request.TransactionGetRq;
import nflx.rozhnov.transactionservice.dto.request.TransactionNewRq;
import nflx.rozhnov.transactionservice.dto.response.TransactionHistoryRs;
import nflx.rozhnov.transactionservice.dto.response.TransactionNewRs;
import nflx.rozhnov.transactionservice.dto.response.TransactionRs;
import nflx.rozhnov.transactionservice.exception.AccountNotEnoughBalanceException;
import nflx.rozhnov.transactionservice.exception.AccountNotFoundException;
import nflx.rozhnov.transactionservice.exception.TransactionNotFoundException;
import nflx.rozhnov.transactionservice.exception.TransactionSaveException;
import nflx.rozhnov.transactionservice.model.Account;
import nflx.rozhnov.transactionservice.model.Transaction;
import nflx.rozhnov.transactionservice.repository.AccountRepository;
import nflx.rozhnov.transactionservice.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;


    public TransactionNewRs createNewTransaction(TransactionNewRq rq) throws AccountNotFoundException{
        // 1) проверяем аккаунты
        Account from = getAccountById(rq.getFromAccount());
        Account to = getAccountById(rq.getToAccount());

        if (from.getBalance() < rq.getAmount()) {
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
        try {
            transaction = transactionRepository.save(transaction);

            from.setBalance(from.getBalance() - rq.getAmount());
            accountRepository.save(from);

            to.setBalance(to.getBalance() + rq.getAmount());
            accountRepository.save(to);
        } catch (Exception ex) {
            throw new TransactionSaveException();
        }

        // создаем ответ и возвращаем
        return new TransactionNewRs(transaction.getId(), transaction.getTimestamp());
    }

    public TransactionRs getTransactionById(TransactionGetRq rq) {
        Transaction transaction = getTransactionById(rq.getTransactionId());

        return new TransactionRs(
                transaction.getId(),
                transaction.getTimestamp(),
                transaction.getFromAccount(),
                transaction.getToAccount(),
                transaction.getAmount());
    }

    public TransactionHistoryRs getAccountTransactions(long accountId) {
        // проверяем существование аккаунта
        getAccountById(accountId);

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



    private Account getAccountById(long accountId) throws AccountNotFoundException {
        Optional<Account> accountOptional = accountRepository.findById(accountId);

        if (accountOptional.isEmpty()) throw new AccountNotFoundException();

        return accountOptional.get();
    }

    private Transaction getTransactionById(UUID transactionId) throws TransactionNotFoundException {
        Optional<Transaction> transactionOptional = transactionRepository.findById(transactionId);

        if (transactionOptional.isEmpty()) throw new TransactionNotFoundException();

        return transactionOptional.get();
    }
}
