package dylan.devocionalesspring.controladores;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dylan.devocionalesspring.entidades.Devocional;
import dylan.devocionalesspring.entidades.Usuario;
import dylan.devocionalesspring.enumeraciones.Rol;
import dylan.devocionalesspring.excepciones.MiExcepcion;
import dylan.devocionalesspring.excepciones.UsuarioNoEncontradoExcepcion;
import dylan.devocionalesspring.repositorios.UsuarioRepositorio;
import dylan.devocionalesspring.servicios.UsuarioDetalles;
import dylan.devocionalesspring.servicios.UsuarioServicio;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;

@RestController
@CrossOrigin("http://localhost:5173")
@RequestMapping("/usuario")
public class UsuarioControlador {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    //registroControlador

    @GetMapping("/{idUsuario}/devocionales")
    public ResponseEntity<List<Devocional>> obtenerDevocionalesDeUsuario(@PathVariable Long idUsuario) {
        try {
            List<Devocional> devocionales = usuarioServicio.obtenerDevocionalesDeUsuario(idUsuario);
            return ResponseEntity.ok(devocionales);
        } catch (UsuarioNoEncontradoExcepcion e) {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping("/registro")
    public ResponseEntity<String> registro(
            @RequestParam(value = "archivo", required = false) MultipartFile archivo,
            @RequestParam(value = "banner", required = false) MultipartFile banner,
            @RequestParam("nombre") String nombre,
            @RequestParam("email") String email,
            @RequestParam("nombreUsuario") String nombreUsuario,
            @RequestParam(value = "biografia", required = false) String biografia,
            @RequestParam(value = "celular", required = false) String celular,
            @RequestParam("contrasenia") String contrasenia,
            @RequestParam("contrasenia2") String contrasenia2) {
        try {
            // Aquí puedes usar los datos del formulario junto con el archivo adjunto
            usuarioServicio.registrarUsuario(nombre, email, nombreUsuario, biografia, celular, contrasenia, contrasenia2, archivo, banner);

            return ResponseEntity.ok("Usuario registrado correctamente");
        } catch (MiExcepcion ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Hola: " + ex.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("El siguiente error ha sido arrojado: " + e.getMessage());
        }
    }

    // Endpoint para obtener el perfil del usuario
    @GetMapping("/perfil")
    public Usuario obtenerPerfilUsuario(Authentication authentication) throws UsuarioNoEncontradoExcepcion {
        if (authentication != null) {
            String email = authentication.getName();
            return usuarioServicio.obtenerPerfilUsuario(email);
        } else {
            return null;
        }
    }


    // Endpoint para subir la foto de perfil
    @PreAuthorize("hasAnyRole('ROLE_USUARIO','ROLE_ADMIN')")
    @PostMapping("/perfil/foto")
    public ResponseEntity<String> subirFotoPerfil(@RequestParam("archivo") MultipartFile archivo,
                                                  HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuariosession");
        if (usuario != null) {
            try {
                usuarioServicio.setImagenUsuario(archivo, usuario.getIdUsuario());
                return ResponseEntity.ok("Su imagen se subió correctamente!");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error al subir la imagen: " + e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/perfil/{idUsuario}")
    public Usuario obtenerCualquierPerfilUsuario(@PathVariable Long idUsuario) throws UsuarioNoEncontradoExcepcion {
        return usuarioServicio.obtenerUsuarioPorId(idUsuario);
    }

    // Endpoint para modificar los datos del usuario
    @PreAuthorize("hasAnyRole('ROLE_USUARIO','ROLE_ADMIN')")
    @PostMapping("/perfil/modificar/{idUsuario}")
    public ResponseEntity<String> modificar(@PathVariable("idUsuario") Long idUsuario,
                                            @RequestParam(value ="nombre", required = false) String nombre,
                                            @RequestParam(value ="celular", required = false) String celular,
                                            @RequestParam(value ="biografia", required = false) String biografia,
                                            @RequestParam(value ="imagenPerfil", required = false) MultipartFile imagenPerfil,
                                            @RequestParam(value ="banner", required = false) MultipartFile banner) {
        try {
            // Llamada al método del servicio para modificar el usuario
            usuarioServicio.modificarUsuario(idUsuario, nombre, celular, biografia, imagenPerfil, banner);
            return ResponseEntity.ok("Perfil actualizado correctamente");
        } catch (MiExcepcion ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");
        }
    }

    // Endpoint para listar todos los usuarios
    @GetMapping("/lista")
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        List<Usuario> usuarios = usuarioServicio.listarUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    @DeleteMapping("/eliminar/{idUsuario}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable("idUsuario") Long idUsuario) {
        try {
            usuarioServicio.eliminarUsuario(idUsuario);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    //@GetMapping("/dar-alta/{idUsuario}")
    //public ResponseEntity<String> darAltaUsuario(@PathVariable("idUsuario") String idUsuario) {
    //    try {
    //        usuarioServicio.darAltaUsuario(idUsuario);
    //        return ResponseEntity.ok("Usuario dado de alta correctamente");
    //    } catch (Exception e) {
    //        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al dar de alta al usuario");
    //    }
    //}

    //@GetMapping("/dar-baja/{idUsuario}")
    //public ResponseEntity<String> darBajaUsuario(@PathVariable("idUsuario") String idUsuario) {
    //    try {
    //        usuarioServicio.darBajaUsuario(idUsuario);
    //        return ResponseEntity.ok("Usuario dado de baja correctamente");
    //    } catch (Exception e) {
    //        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al dar de baja al usuario");
    //    }
    //}


    /*
    Evaluando la incorporacion de este metodo para optimizar el codigo de las validaciones. ->  Emi

    private ResponseEntity<?> validation(BindingResult result) {
                Map<String, String> errors = new HashMap<>();

        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }
update(@Valid @RequestBody UserRequest user, BindingResult result, @PathVariable Long id)
if(result.hasErrors()){
            return validation(result);
        }*/
}
