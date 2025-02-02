package nflx.rozhnov.transactionservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nflx.rozhnov.transactionservice.model.Transaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.topic}")
    private String TOPIC_NAME;

    ObjectMapper mapper = new ObjectMapper();

    public void sendMessage(Transaction transaction) {
        sendMessage(transaction, TOPIC_NAME);
    }

    public void sendMessage(Transaction transaction, String topicName) {
        try {
            String message = mapper.writeValueAsString(transaction);

            log.info("|---| Sending : {}", message);
            kafkaTemplate.send(topicName, message);
            log.info("|---| Kafka sending: Success |---|");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }    }


}