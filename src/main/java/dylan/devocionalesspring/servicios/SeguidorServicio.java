package dylan.devocionalesspring.servicios;

import dylan.devocionalesspring.entidades.Seguidor;
import dylan.devocionalesspring.entidades.Usuario;
import dylan.devocionalesspring.repositorios.SeguidorRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class SeguidorServicio {

    @Autowired
    private SeguidorRepositorio seguidorRepositorio;
    @Autowired
    private NotificacionServicio notificacionServicio;

    public Seguidor seguir(Long usuarioId, Long seguidoId) {
        if (seguidorRepositorio.findByUsuarioIdUsuarioAndSeguidoIdUsuario(usuarioId, seguidoId).isPresent()) {
            throw new IllegalArgumentException("Ya sigues a este usuario.");
        }
        Seguidor seguidor = new Seguidor();
        seguidor.setUsuario(new Usuario(usuarioId));
        seguidor.setSeguido(new Usuario(seguidoId));
        notificacionServicio.crearNotificacion(
                "seguimiento",
                seguidor.getUsuario().getNombre() + " ha comenzado a seguirte",
                Collections.singletonList(seguidoId),
                usuarioId,
                "/perfil/" + usuarioId
        );
        return seguidorRepositorio.save(seguidor);
    }

    public void dejarDeSeguir(Long usuarioId, Long seguidoId) {
        Optional<Seguidor> seguidor = seguidorRepositorio.findByUsuarioIdUsuarioAndSeguidoIdUsuario(usuarioId, seguidoId);
        seguidor.ifPresent(seguidorRepositorio::delete);
    }

    public List<Seguidor> obtenerSeguidores(Long seguidoId) {
        return seguidorRepositorio.findBySeguidoIdUsuario(seguidoId);
    }

    public List<Seguidor> obtenerSeguidos(Long usuarioId) {
        return seguidorRepositorio.findByUsuarioIdUsuario(usuarioId);
    }
}
