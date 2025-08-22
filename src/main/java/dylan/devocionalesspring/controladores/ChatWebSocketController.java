package dylan.devocionalesspring.controladores;

import dylan.devocionalesspring.RedisConfig.RedisPublisher;
import dylan.devocionalesspring.dto.MensajeDTO;
import dylan.devocionalesspring.dto.UsuarioDTO;
import dylan.devocionalesspring.entidades.Mensaje;
import dylan.devocionalesspring.servicios.MensajeServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

import java.security.Principal;
import java.util.Map;

@Controller
public class ChatWebSocketController {

    @Autowired
    private MensajeServicio mensajeServicio;

    @Autowired
    private RedisPublisher redisPublisher;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        Principal user = event.getUser();
        System.out.println("🔗 Usuario conectado via WS: " + (user != null ? user.getName() : "desconocido"));
    }

    @MessageMapping("/chat.send")
    public void enviarMensajeWebSocket(@Payload Map<String, String> payload) {
        Long emisorId = Long.parseLong(payload.get("emisorId"));
        Long receptorId = Long.parseLong(payload.get("receptorId"));
        String contenido = payload.get("contenido");

        Mensaje mensaje = mensajeServicio.enviarMensaje(emisorId, receptorId, contenido);

        MensajeDTO mensajeDTO = new MensajeDTO(
                mensaje.getId(),
                mensaje.getContenido(),
                mensaje.getFechaEnvio(),
                new UsuarioDTO(mensaje.getEmisor()),
                new UsuarioDTO(mensaje.getReceptor())
        );
        redisPublisher.publicar(mensajeDTO);
    }
}
