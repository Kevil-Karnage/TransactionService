package nflx.rozhnov.transactionservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@RequiredArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue
    private UUID id;
    private Date timestamp;
    private long fromAccount;
    private long toAccount;
    private double amount;
}
