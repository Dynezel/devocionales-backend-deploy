package dylan.devocionalesspring.servicios;

import com.sun.jdi.IntegerValue;
import dylan.devocionalesspring.entidades.Comentario;
import dylan.devocionalesspring.entidades.Devocional;
import dylan.devocionalesspring.entidades.Notificacion;
import dylan.devocionalesspring.entidades.Usuario;
import dylan.devocionalesspring.repositorios.ComentarioRepositorio;
import dylan.devocionalesspring.repositorios.DevocionalRepositorio;
import dylan.devocionalesspring.repositorios.NotificacionRepositorio;
import dylan.devocionalesspring.repositorios.UsuarioRepositorio;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.valueOf;

@Service
public class ComentarioServicio {

    @Autowired
    private ComentarioRepositorio comentarioRepositorio;

    @Autowired
    private NotificacionServicio notificacionServicio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private DevocionalRepositorio devocionalRepositorio;

    @Autowired
    private NotificacionRepositorio notificacionRepositorio;

    @Transactional
    public List<Comentario> obtenerComentariosPorDevocional(int devocionalId) {
        // Obtener todos los comentarios asociados al devocional
        return devocionalRepositorio.findComentariosByDevocionalId(devocionalId);
    }

    @Transactional
    public Comentario crearComentario(String email, int devocionalId, Comentario comentario, Long usuarioReceptorId) throws Exception {
        Usuario usuario = usuarioServicio.obtenerPerfilUsuario(email);

        Devocional devocional = devocionalRepositorio.findById(devocionalId)
                .orElseThrow(() -> new Exception("Devocional no encontrado"));

        comentario.setFechaCreacion(LocalDate.now());
        comentario.setIdUsuario(usuario.getIdUsuario());
        comentario.setIdDevocional(devocional.getId());

        // Primero guardamos el comentario en la base de datos
        Comentario comentarioGuardado = comentarioRepositorio.save(comentario);

        // Luego añadimos el comentario a las colecciones de usuario y devocional
        usuario.getComentarios().add(comentarioGuardado);
        devocional.getComentarios().add(comentarioGuardado);

        // Guardamos las relaciones
        usuarioRepositorio.save(usuario);
        devocionalRepositorio.save(devocional);

        if (!usuario.getIdUsuario().equals(usuarioReceptorId)) { // Evitar notificación si el usuario comenta en su propio devocional
            // URL de notificación para dirigir al devocional comentado
            String urlNotificacion = "/devocional/" + devocionalId + "?autorId=" + usuarioReceptorId;

            // Buscar si ya existe una notificación de comentario en este devocional
            Optional<Notificacion> notificacionExistente = notificacionRepositorio.findByTipoAndUsuarioEmisorIdAndUrl(
                    "comentario", usuario.getIdUsuario(), urlNotificacion
            );

            if(notificacionExistente.isEmpty()) {
                // Crear una nueva notificación si no existe una previa
                notificacionServicio.crearNotificacion(
                        "comentario",
                        usuario.getNombre() + " ha comentado en tu devocional",
                        Collections.singletonList(usuarioReceptorId),
                        usuario.getIdUsuario(),
                        urlNotificacion
                );
            }
        }

        return comentarioGuardado;
    }


    @Transactional
    public Comentario actualizarComentario(Comentario comentario) {
        comentario = comentarioRepositorio.findById(comentario.getId()).orElseThrow(() -> new IllegalArgumentException("Comentario no encontrado"));
        comentario.setTexto(comentario.getTexto());
        comentario.setFechaCreacion(comentario.getFechaCreacion());
        return comentarioRepositorio.save(comentario);
    }


    @Transactional
    public void eliminarComentario(Long comentarioId) throws Exception {
        Comentario comentario = comentarioRepositorio.findById(comentarioId)
                .orElseThrow(() -> new Exception("Comentario no encontrado"));

        // Primero, eliminamos el comentario de la lista de comentarios en el usuario
        Usuario usuario = usuarioRepositorio.findById(comentario.getIdUsuario())
                .orElseThrow(() -> new Exception("Usuario no encontrado"));
        usuario.getComentarios().remove(comentario);
        usuarioRepositorio.save(usuario);

        // Luego, eliminamos el comentario de la lista de comentarios en el devocional
        Devocional devocional = devocionalRepositorio.findById(comentario.getIdDevocional())
                .orElseThrow(() -> new Exception("Devocional no encontrado"));
        devocional.getComentarios().remove(comentario);
        devocionalRepositorio.save(devocional);

        // Finalmente, eliminamos el comentario de la base de datos
        comentarioRepositorio.delete(comentario);
    }

    @Transactional
    public void eliminarComentariosDeDevocional(int devocionalId) throws Exception {
        Devocional devocional = devocionalRepositorio.findById(devocionalId)
                .orElseThrow(() -> new Exception("Devocional no encontrado"));

        // Iterar sobre todos los comentarios del devocional
        for (Comentario comentario : devocional.getComentarios()) {
            eliminarComentario(comentario.getId());
        }
    }

    @Transactional
    public void eliminarComentariosPorDevocional(int devocionalId) throws Exception {
        Devocional devocional = devocionalRepositorio.findById(devocionalId)
                .orElseThrow(() -> new Exception("Devocional no encontrado"));

        // Iterar sobre los comentarios y eliminar las referencias en usuarios y devocional
        for (Comentario comentario : devocional.getComentarios()) {
            Usuario usuario = usuarioRepositorio.findById(comentario.getIdUsuario())
                    .orElseThrow(() -> new Exception("Usuario no encontrado"));

            // Eliminar la referencia del comentario en la lista de comentarios del usuario
            usuario.getComentarios().remove(comentario);
            usuarioRepositorio.save(usuario);
        }

        // Ahora se pueden eliminar los comentarios de la base de datos
        comentarioRepositorio.deleteAll(devocional.getComentarios());

        // Limpiar la lista de comentarios en el devocional
        devocional.getComentarios().clear();
        devocionalRepositorio.save(devocional);
    }

    @Transactional
    public Comentario obtenerComentarioPorId(Long id) {
        return comentarioRepositorio.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comentario no encontrado"));
    }

}