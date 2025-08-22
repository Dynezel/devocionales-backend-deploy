package dylan.devocionalesspring.controladores;

import dylan.devocionalesspring.excepciones.MiExcepcion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("http://localhost:5173")
public class PortalControlador {

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Servidor funcionando");
    }



}
