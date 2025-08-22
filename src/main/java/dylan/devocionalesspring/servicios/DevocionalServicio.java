package dylan.devocionalesspring.servicios;

import dylan.devocionalesspring.entidades.*;
import dylan.devocionalesspring.enumeraciones.Rol;
import dylan.devocionalesspring.repositorios.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DevocionalServicio {

    @Autowired
    private DevocionalRepositorio devocionalRepositorio;
    @Autowired
    private NotificacionRepositorio notificacionRepositorio;
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private ComentarioRepositorio comentarioRepositorio;

    @Autowired
    private AmistadRepositorio amistadRepositorio;
    @Autowired
    private ComentarioServicio comentarioServicio;

    @Transactional
    public Usuario crearDevocional(String titulo, String contenido, LocalDateTime fechaCreacion, Usuario usuario) {
        Devocional devocional = new Devocional();
        devocional.setTitulo(titulo);
        devocional.setContenido(contenido);
        devocional.setFechaCreacion(fechaCreacion);
        usuario.setDevocionales(Collections.singletonList(devocional));
        return usuarioRepositorio.save(usuario);
    }

    @Transactional
    public void incrementarVistas(Long devocionalId) {
        devocionalRepositorio.incrementarVistas(devocionalId);
    }

    @Transactional
    public boolean modificarDevocional(int id, String titulo, String contenido) {
        Optional<Devocional> respuesta = devocionalRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Devocional devocional = respuesta.get();
            devocional.setTitulo(titulo);
            devocional.setContenido(contenido);

            devocionalRepositorio.save(devocional);
            return true;
        } else {
            return false;
        }
    }

    public List<Devocional> obtenerTodosDevocionales() {
        return devocionalRepositorio.findAll();
    }

    public Optional<Devocional> obtenerDevocionalPorId(int id) {
        return devocionalRepositorio.findById(id);
    }

    public Usuario obtenerUsuarioPorId(Long idUsuario) {
        return usuarioRepositorio.findById(idUsuario).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public List<Devocional> obtenerDevocionalesPorFecha() {
        return devocionalRepositorio.findAllByOrderByFechaCreacionDesc();
    }

    @Transactional
    public void eliminarDevocional(int id) throws Exception {
        Optional<Devocional> devocionalOpt = devocionalRepositorio.findById(id);
        if (devocionalOpt.isPresent()) {
            Devocional devocional = devocionalOpt.get();

            // Elimina los comentarios asociados utilizando el servicio de Comentario
            for (Comentario comentario : devocional.getComentarios()) {
                comentarioServicio.eliminarComentario(comentario.getId());
            }

            // Elimina el devocional
            devocionalRepositorio.delete(devocional);
        } else {
            throw new Exception("Devocional no encontrado");
        }
    }
}