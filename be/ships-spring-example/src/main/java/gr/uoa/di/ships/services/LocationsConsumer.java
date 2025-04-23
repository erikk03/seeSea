package gr.uoa.di.ships.services;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;


@Service
public class LocationsConsumer {

    @Autowired
    SimpMessagingTemplate template;

    //@Value("{websocket.topic}")
    //public String websocketTopic;

    //@Value("{websocket.destinationPrefix}")
    //public String websocketDestinationPrefix;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @KafkaListener(topics = "${kafka.topic}")
    public void consume(String message) {
        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            template.convertAndSend("/topic/locations", jsonNode.toPrettyString());
            System.out.println("Sent message: " + jsonNode.toPrettyString()); //debugging
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }

    }
}
