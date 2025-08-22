package dylan.devocionalesspring.controladores;

import dylan.devocionalesspring.entidades.Notificacion;
import dylan.devocionalesspring.servicios.NotificacionServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/notificaciones")
public class NotificacionController {

    @Autowired
    private NotificacionServicio notificacionServicio;

    @GetMapping("/{usuarioReceptorId}")
    public List<Notificacion> obtenerNotificaciones(@PathVariable Long usuarioReceptorId) {
        return notificacionServicio.obtenerNotificaciones(usuarioReceptorId);
    }

    @GetMapping("/no-leidas/{usuarioReceptorId}")
    public List<Notificacion> obtenerNotificacionesNoLeidas(@PathVariable Long usuarioReceptorId) {
        List<Notificacion> notificaciones = notificacionServicio.obtenerNotificaciones(usuarioReceptorId);
        return notificaciones.stream().filter(notificacion -> !notificacion.isVisto()).collect(Collectors.toList());
    }

    @PutMapping("/marcar-como-leida/{id}")
    public ResponseEntity<Notificacion> marcarComoLeida(@PathVariable Long id) {
        Notificacion notificacion = notificacionServicio.marcarComoLeida(id);
        return new ResponseEntity<>(notificacion, HttpStatus.OK);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminarNotificacion(@PathVariable Long id) {
        notificacionServicio.eliminarNotificacion(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
