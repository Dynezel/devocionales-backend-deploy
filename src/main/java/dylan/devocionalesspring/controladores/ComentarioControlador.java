package dylan.devocionalesspring.controladores;

import dylan.devocionalesspring.entidades.Comentario;
import dylan.devocionalesspring.entidades.Devocional;
import dylan.devocionalesspring.entidades.Usuario;
import dylan.devocionalesspring.excepciones.UsuarioNoEncontradoExcepcion;
import dylan.devocionalesspring.repositorios.ComentarioRepositorio;
import dylan.devocionalesspring.repositorios.UsuarioRepositorio;
import dylan.devocionalesspring.servicios.ComentarioServicio;
import dylan.devocionalesspring.servicios.DevocionalServicio;
import dylan.devocionalesspring.servicios.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
public class ComentarioControlador {

    @Autowired
    private ComentarioServicio comentarioServicio;

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private DevocionalServicio devocionalServicio;

    @Autowired
    private ComentarioRepositorio comentarioRepositorio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;


    @PostMapping("/devocionales/{devocionalId}/comentarios")
    public ResponseEntity<Comentario> crearComentario(@PathVariable int devocionalId,
                                                      @RequestBody Comentario comentario,
                                                      @RequestParam Long usuarioId,
                                                      Authentication authentication) {
        try {
            System.out.println("Id del usuario receptor: " + usuarioId);
            String email = authentication.getName();
            Comentario nuevoComentario = comentarioServicio.crearComentario(email, devocionalId, comentario, usuarioId);
            return new ResponseEntity<>(nuevoComentario, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/devocionales/{devocionalId}/comentarios")
    public ResponseEntity<List<Comentario>> obtenerComentariosPorDevocionalYUsuario(
            @PathVariable int devocionalId,
            @RequestParam Long usuarioId) {
        try {
            List<Comentario> comentarios = comentarioServicio.obtenerComentariosPorDevocional(devocionalId);
            return new ResponseEntity<>(comentarios, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/comentarios/{devocionalId}")
    public ResponseEntity<List<Comentario>> obtenerComentarios(@PathVariable int devocionalId) {
        try {
            List<Comentario> comentarios = comentarioServicio.obtenerComentariosPorDevocional(devocionalId);
            return new ResponseEntity<>(comentarios, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/comentarios/{id}")
    public ResponseEntity<Comentario> actualizarComentario(@PathVariable Long id,
                                                           @RequestBody Comentario comentarioActualizado,
                                                           Authentication authentication) {
        try {
            // Verifica que el usuario actual sea el propietario del comentario
            String email = authentication.getName();
            Usuario usuario = usuarioServicio.obtenerPerfilUsuario(email);

            Comentario comentarioExistente = comentarioServicio.obtenerComentarioPorId(id);

            if (!comentarioExistente.getIdUsuario().equals(usuario.getIdUsuario())) {
                return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
            }

            // Actualiza el texto del comentario
            comentarioExistente.setTexto(comentarioActualizado.getTexto());
            Comentario comentarioGuardado = comentarioServicio.actualizarComentario(comentarioExistente);
            return new ResponseEntity<>(comentarioGuardado, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/comentarios/{id}")
    public ResponseEntity<Void> eliminarComentario(@PathVariable Long id) {
        try {
            comentarioServicio.eliminarComentario(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/devocionales/{devocionalId}/comentarios")
    public ResponseEntity<Void> eliminarComentariosPorDevocional(@PathVariable int devocionalId) {
        try {
            comentarioServicio.eliminarComentariosPorDevocional(devocionalId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}