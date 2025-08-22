package dylan.devocionalesspring.controladores;

import dylan.devocionalesspring.entidades.MeGusta;
import dylan.devocionalesspring.entidades.Usuario;
import dylan.devocionalesspring.excepciones.UsuarioNoEncontradoExcepcion;
import dylan.devocionalesspring.servicios.MeGustaServicio;
import dylan.devocionalesspring.servicios.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MeGustaControlador {

    @Autowired
    private MeGustaServicio meGustaServicio;

    @Autowired
    UsuarioServicio usuarioServicio;

    @PostMapping("/devocionales/{devocionalId}/megusta")
    public ResponseEntity<MeGusta> toggleMeGusta(@PathVariable Long devocionalId, Long usuarioId, Long usuarioReceptorId) throws UsuarioNoEncontradoExcepcion {

        MeGusta meGusta = meGustaServicio.toggleMeGusta(usuarioId, Math.toIntExact(devocionalId), usuarioReceptorId);
        return new ResponseEntity<>(meGusta, meGusta == null ? HttpStatus.OK : HttpStatus.CREATED);
    }

    @GetMapping("/devocionales/{devocionalId}/megusta")
    public ResponseEntity<List<MeGusta>> obtenerMeGustasPorDevocional(@PathVariable int devocionalId) {
        List<MeGusta> meGustas = meGustaServicio.obtenerMeGustasPorDevocional(devocionalId);
        return new ResponseEntity<>(meGustas, HttpStatus.OK);
    }

    @GetMapping("/usuarios/{usuarioId}/megusta")
    public ResponseEntity<List<MeGusta>> obtenerMeGustasPorUsuario(@PathVariable Long usuarioId) {
        List<MeGusta> meGustas = meGustaServicio.obtenerMeGustasPorUsuario(usuarioId);
        return new ResponseEntity<>(meGustas, HttpStatus.OK);
    }
}
