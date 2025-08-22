package dylan.devocionalesspring.RedisConfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dylan.devocionalesspring.dto.MensajeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class RedisSubscriber implements MessageListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper mapper;

    public RedisSubscriber(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        this.mapper = new ObjectMapper()
                .findAndRegisterModules()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String json = new String(message.getBody(), StandardCharsets.UTF_8);
            MensajeDTO dto = mapper.readValue(json, MensajeDTO.class);

            String emisor = dto.getEmisor().getIdUsuario().toString();
            String receptor = dto.getReceptor().getIdUsuario().toString();

            // 🚀 enviamos a ambos:
            messagingTemplate.convertAndSendToUser(emisor,  "/queue/messages", dto);
            messagingTemplate.convertAndSendToUser(receptor, "/queue/messages", dto);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
