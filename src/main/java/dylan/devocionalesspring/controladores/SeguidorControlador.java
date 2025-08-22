package dylan.devocionalesspring.controladores;

import dylan.devocionalesspring.entidades.Seguidor;
import dylan.devocionalesspring.servicios.SeguidorServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/seguidores")
public class SeguidorControlador {

    @Autowired
    private SeguidorServicio seguidorServicio;

    @PostMapping("/{usuarioId}/seguir/{seguidoId}")
    public ResponseEntity<Seguidor> seguir(@PathVariable Long usuarioId, @PathVariable Long seguidoId) {
        Seguidor seguidor = seguidorServicio.seguir(usuarioId, seguidoId);
        return new ResponseEntity<>(seguidor, HttpStatus.CREATED);
    }

    @DeleteMapping("/{usuarioId}/dejar-de-seguir/{seguidoId}")
    public ResponseEntity<Void> dejarDeSeguir(@PathVariable Long usuarioId, @PathVariable Long seguidoId) {
        seguidorServicio.dejarDeSeguir(usuarioId, seguidoId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{seguidoId}/seguidores")
    public ResponseEntity<List<Seguidor>> obtenerSeguidores(@PathVariable Long seguidoId) {
        List<Seguidor> seguidores = seguidorServicio.obtenerSeguidores(seguidoId);
        return new ResponseEntity<>(seguidores, HttpStatus.OK);
    }

    @GetMapping("/{usuarioId}/seguidos")
    public ResponseEntity<List<Seguidor>> obtenerSeguidos(@PathVariable Long usuarioId) {
        List<Seguidor> seguidos = seguidorServicio.obtenerSeguidos(usuarioId);
        return new ResponseEntity<>(seguidos, HttpStatus.OK);
    }
}
