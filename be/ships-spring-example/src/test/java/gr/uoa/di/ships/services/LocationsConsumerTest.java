package gr.uoa.di.ships.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LocationsConsumerTest {
    @Mock
    SimpMessagingTemplate template;

    @InjectMocks
    LocationsConsumer locationsConsumer;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testConsume_validJson_sendsMessageToWebSocket() throws Exception {
        // given
        String kafkaMessage = "{\"ship\":\"Titanic\",\"location\":\"Atlantic\"}";
        String expectedWebSocketMessage = objectMapper
                .readTree(kafkaMessage)
                .toPrettyString();

        // when
        locationsConsumer.consume(kafkaMessage);

        // then
        verify(template, times(1))
                .convertAndSend("/topic/locations", expectedWebSocketMessage);
    }

    @Test
    void testConsume_invalidJson_handlesException() {
        // given
        String invalidJsonMessage = "{invalid json}";

        // when
        locationsConsumer.consume(invalidJsonMessage);

        // then
        // verify that template is never called
        verify(template, never())
                .convertAndSend(anyString(), Optional.ofNullable(any()));
    }
}
