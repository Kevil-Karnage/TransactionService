package nflx.rozhnov.transactionservice.repository;

import nflx.rozhnov.transactionservice.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    @Query("FROM Transaction t WHERE :accountId IN (t.fromAccount, t.toAccount)")
    List<Transaction> findAllByAccountId(long accountId);
}
