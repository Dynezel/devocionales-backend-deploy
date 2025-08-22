package dylan.devocionalesspring.controladores;

import dylan.devocionalesspring.entidades.Amistad;
import dylan.devocionalesspring.excepciones.UsuarioNoEncontradoExcepcion;
import dylan.devocionalesspring.servicios.AmistadServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/amistades")
public class AmistadControlador {

    @Autowired
    private AmistadServicio amistadServicio;

    @PostMapping("/{usuarioId}/enviar-solicitud/{amigoId}")
    public ResponseEntity<Amistad> enviarSolicitudAmistad(@PathVariable Long usuarioId, @PathVariable Long amigoId) throws UsuarioNoEncontradoExcepcion {
        Amistad amistad = amistadServicio.enviarSolicitudAmistad(usuarioId, amigoId);
        return new ResponseEntity<>(amistad, HttpStatus.CREATED);
    }

    @PostMapping("/{usuarioId}/aceptar-solicitud/{amigoId}")
    public ResponseEntity<Void> aceptarSolicitudAmistad(@PathVariable Long usuarioId, @PathVariable Long amigoId) {
        amistadServicio.aceptarSolicitudAmistad(usuarioId, amigoId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{usuarioId}/rechazar-solicitud/{amigoId}")
    public ResponseEntity<Void> rechazarSolicitudAmistad(@PathVariable Long usuarioId, @PathVariable Long amigoId) {
        amistadServicio.rechazarSolicitudAmistad(usuarioId, amigoId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{usuarioId}/amigos")
    public ResponseEntity<List<Amistad>> obtenerAmigos(@PathVariable Long usuarioId) {
        List<Amistad> amigos = amistadServicio.obtenerAmigos(usuarioId);
        return new ResponseEntity<>(amigos, HttpStatus.OK);
    }
}
