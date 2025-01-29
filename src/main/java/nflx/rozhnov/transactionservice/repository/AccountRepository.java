package nflx.rozhnov.transactionservice.repository;

import nflx.rozhnov.transactionservice.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
