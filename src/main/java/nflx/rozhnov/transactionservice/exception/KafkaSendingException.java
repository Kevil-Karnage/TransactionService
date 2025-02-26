package nflx.rozhnov.transactionservice.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KafkaSendingException extends RuntimeException {
    public KafkaSendingException() {
        super("Kafka sending message was not completed");
        log.error("---| Failed |---");
        log.error("Caused by {}", this.getMessage());
    }
}
