package dylan.devocionalesspring.servicios;

import dylan.devocionalesspring.entidades.Amistad;
import dylan.devocionalesspring.entidades.Notificacion;
import dylan.devocionalesspring.entidades.Usuario;
import dylan.devocionalesspring.excepciones.UsuarioNoEncontradoExcepcion;
import dylan.devocionalesspring.repositorios.AmistadRepositorio;
import dylan.devocionalesspring.repositorios.NotificacionRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class AmistadServicio {

    @Autowired
    private AmistadRepositorio amistadRepositorio;

    @Autowired
    private NotificacionRepositorio notificacionRepositorio;

    @Autowired
    private NotificacionServicio notificacionServicio;

    @Autowired
    private UsuarioServicio usuarioServicio;

    public Amistad enviarSolicitudAmistad(Long usuarioId, Long amigoId) throws UsuarioNoEncontradoExcepcion {
        if (amistadRepositorio.findByUsuarioSolicitanteAndUsuarioAmigoAndEstado(new Usuario(usuarioId), new Usuario(amigoId), Amistad.EstadoAmistad.PENDIENTE).isPresent()) {
            throw new IllegalArgumentException("Solicitud de amistad ya enviada.");
        }
        Amistad amistad = new Amistad();
        amistad.setUsuarioSolicitante(new Usuario(usuarioId));
        amistad.setUsuarioAmigo(new Usuario(amigoId));
        amistad.setEstado(Amistad.EstadoAmistad.PENDIENTE);

        Usuario usuarioSolicitante = usuarioServicio.obtenerUsuarioPorId(usuarioId);

        notificacionServicio.crearNotificacion(
                "solicitudamistad",
                usuarioSolicitante.getNombre() + " Te ha enviado una solicitud de amistad",
                Collections.singletonList(amigoId),
                usuarioId,
                ""
        );

        return amistadRepositorio.save(amistad);
        }


    public void aceptarSolicitudAmistad(Long usuarioId, Long amigoId) {
        Amistad amistad = amistadRepositorio.findByUsuarioSolicitanteAndUsuarioAmigoAndEstado(new Usuario(usuarioId), new Usuario(amigoId), Amistad.EstadoAmistad.PENDIENTE)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud de amistad no encontrada."));
        amistad.setEstado(Amistad.EstadoAmistad.ACEPTADA);
        amistadRepositorio.save(amistad);
    }

    public void rechazarSolicitudAmistad(Long usuarioId, Long amigoId) {
        amistadRepositorio.findByUsuarioSolicitanteAndUsuarioAmigoAndEstado(new Usuario(usuarioId), new Usuario(amigoId), Amistad.EstadoAmistad.PENDIENTE)
                .ifPresent(amistadRepositorio::delete);
    }

    public List<Amistad> obtenerAmigos(Long usuarioId) {
        Usuario usuario = new Usuario(usuarioId);
        return amistadRepositorio.findByUsuarioSolicitanteOrUsuarioAmigoAndEstado(usuario, usuario, Amistad.EstadoAmistad.ACEPTADA);
    }
}
