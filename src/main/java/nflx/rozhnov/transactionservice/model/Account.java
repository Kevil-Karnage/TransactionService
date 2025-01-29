package nflx.rozhnov.transactionservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Entity
@AllArgsConstructor
public class Account {
    @Id
    private long id;
    private double balance;
}
