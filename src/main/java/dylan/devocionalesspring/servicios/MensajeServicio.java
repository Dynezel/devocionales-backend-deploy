package dylan.devocionalesspring.servicios;

import dylan.devocionalesspring.entidades.Mensaje;
import dylan.devocionalesspring.entidades.Notificacion;
import dylan.devocionalesspring.entidades.Usuario;
import dylan.devocionalesspring.repositorios.MensajeRepositorio;
import dylan.devocionalesspring.repositorios.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class MensajeServicio {

    @Autowired
    private MensajeRepositorio mensajeRepositorio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private NotificacionServicio notificacionServicio;

    public Mensaje enviarMensaje(Long emisorId, Long receptorId, String contenido) {
        Usuario emisor = usuarioRepositorio.findById(emisorId).orElseThrow(() -> new RuntimeException("Emisor no encontrado"));
        Usuario receptor = usuarioRepositorio.findById(receptorId).orElseThrow(() -> new RuntimeException("Receptor no encontrado"));

        Mensaje mensaje = new Mensaje();
        mensaje.setEmisor(emisor);
        mensaje.setReceptor(receptor);
        mensaje.setContenido(contenido);
        mensaje.setFechaEnvio(LocalDateTime.now());
        Notificacion notificacion = notificacionServicio.crearNotificacion(
                "mensaje",
                mensaje.getEmisor().getNombre() + " Te ha enviado un mensaje",
                Collections.singletonList(mensaje.getReceptor().getIdUsuario()),
                mensaje.getEmisor().getIdUsuario(),
                "/asdas"
        );

        return mensajeRepositorio.save(mensaje);
    }

    public List<Mensaje> obtenerMensajes(Long receptorId) {
        Usuario receptor = usuarioRepositorio.findById(receptorId).orElseThrow(() -> new RuntimeException("Receptor no encontrado"));
        return mensajeRepositorio.findByReceptor(receptor);
    }

    public List<Mensaje> obtenerConversacion(Long emisorId, Long receptorId) {
        Usuario emisor = usuarioRepositorio.findById(emisorId).orElseThrow(() -> new RuntimeException("Emisor no encontrado"));
        Usuario receptor = usuarioRepositorio.findById(receptorId).orElseThrow(() -> new RuntimeException("Receptor no encontrado"));
        return mensajeRepositorio.findByUsuarios(emisorId, receptorId);
    }
}