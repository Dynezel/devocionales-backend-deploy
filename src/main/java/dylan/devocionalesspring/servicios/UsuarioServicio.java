package dylan.devocionalesspring.servicios;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import dylan.devocionalesspring.entidades.*;
import dylan.devocionalesspring.enumeraciones.Rol;
import dylan.devocionalesspring.excepciones.MiExcepcion;
import dylan.devocionalesspring.excepciones.UsuarioNoEncontradoExcepcion;
import dylan.devocionalesspring.repositorios.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class UsuarioServicio implements UserDetailsService {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private ComentarioRepositorio comentarioRepositorio;

    @Autowired
    private DevocionalRepositorio devocionalRepositorio;

    @Autowired
    private NotificacionRepositorio notificacionRepositorio;

    @Autowired
    private MensajeRepositorio mensajeRepositorio;

    @Autowired
    private SeguidorRepositorio seguidorRepositorio;

    @Autowired
    private AmistadRepositorio amistadRepositorio;

    @Autowired
    private NotificacionServicio notificacionServicio;

    private HttpServletRequest request;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    //@Autowired
    //JavaMailSender javaMailSender;

    @Autowired
    private ImagenServicio imagenServicio;

    @Transactional
    public void registrarUsuario(String nombre,
                                 String email,
                                 String nombreUsuario,
                                 String biografia,
                                 String celular,
                                 String contrasenia,
                                 String contrasenia2,
                                 MultipartFile fotoArchivo,
                                 MultipartFile bannerArchivo) throws MiExcepcion, IOException, UsuarioNoEncontradoExcepcion {
        validarDatosRegistro(nombre, email, celular, contrasenia, contrasenia2);
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setNombreUsuario(nombreUsuario);
        usuario.setBiografia(biografia);
        usuario.setCelular(celular);
        usuario.setContrasenia(new BCryptPasswordEncoder().encode(contrasenia));
        usuario.setRol(Rol.USUARIO);

        if (fotoArchivo != null && !fotoArchivo.isEmpty()) {
            Imagen fotoPerfil = imagenServicio.guardarImagen(fotoArchivo);
            usuario.setFotoPerfil(fotoPerfil);
        }

        if (bannerArchivo != null && !bannerArchivo.isEmpty()) {
            Imagen bannerPerfil = imagenServicio.guardarImagen(bannerArchivo);
            usuario.setBannerPerfil(bannerPerfil);
        }

        Usuario usuario2 = usuarioRepositorio.buscarPorEmail(email);
        if(usuario2 == null) {
            usuarioRepositorio.save(usuario);
        }
        else {
            throw new UsuarioNoEncontradoExcepcion("El usuario con el email: " + email + " ya existe" );
        }
    }

    @Transactional
    public List<Devocional> obtenerDevocionalesDeUsuario(Long idUsuario) throws UsuarioNoEncontradoExcepcion {
        Usuario usuario = usuarioRepositorio.findById(idUsuario)
                .orElseThrow(() -> new UsuarioNoEncontradoExcepcion("Usuario no encontrado con ID: " + idUsuario));

        return usuario.getDevocionales();
    }

    @Transactional
    public Devocional agregarDevocionalAUsuario(String email, Devocional devocional) throws UsuarioNoEncontradoExcepcion {
        // Obtener el usuario que está creando el devocional
        Usuario usuario = obtenerPerfilUsuario(email);

        // Configurar la fecha de creación del devocional
        devocional.setFechaCreacion(LocalDateTime.now());

        // Guardar el devocional para obtener el ID asignado por la base de datos
        Devocional devocionalGuardado = devocionalRepositorio.save(devocional);

        // Asociar el devocional guardado al usuario
        usuario.getDevocionales().add(devocionalGuardado);
        usuarioRepositorio.save(usuario);

        // Obtener amistades aceptadas del usuario
        List<Amistad> amistades = amistadRepositorio.findByUsuarioSolicitanteOrUsuarioAmigoAndEstado(
                usuario, usuario, Amistad.EstadoAmistad.ACEPTADA);

        // Crear una notificación para cada amigo
        amistades.forEach(amistad -> {
            // Obtener el usuario amigo correspondiente en cada amistad
            Usuario amigo = amistad.getUsuarioAmigo().equals(usuario) ? amistad.getUsuarioSolicitante() : amistad.getUsuarioAmigo();

            // Generar URL de notificación para el devocional creado, ahora con ID asignado
            String urlNotificacion = "/devocional/" + devocionalGuardado.getId() + "?autorId=" + usuario.getIdUsuario();

            // Verificar si ya existe una notificación similar
            Optional<Notificacion> notificacionExistente = notificacionRepositorio.findByTipoAndUsuarioEmisorIdAndUrl(
                    "devocionalcreado", usuario.getIdUsuario(), urlNotificacion);

            if (notificacionExistente.isEmpty()) {
                // Crear y guardar una nueva notificación si no existe
                Notificacion notificacion = notificacionServicio.crearNotificacion(
                        "devocionalcreado",
                        usuario.getNombre() + " ha publicado un nuevo devocional: " + devocionalGuardado.getTitulo(),
                        Collections.singletonList(amigo.getIdUsuario()),
                        usuario.getIdUsuario(),
                        urlNotificacion
                );
                notificacionRepositorio.save(notificacion);
            }
        });

        return devocionalGuardado;
    }

    @Transactional
    public void modificarUsuario(Long idUsuario, String nombre, String celular, String biografia,
                                 MultipartFile imagenPerfil, MultipartFile banner) throws MiExcepcion, IOException {
        // Validar los datos modificados
        validarCadena(nombre, "El nombre no puede ser vacío o nulo.");
        validarCadena(celular, "El celular no puede ser vacío o nulo.");

        // Obtener el usuario actual desde el repositorio
        Usuario usuarioActual = getOne(idUsuario);

        // Actualizar nombre y celular
        usuarioActual.setNombre(nombre);
        usuarioActual.setCelular(celular);
        usuarioActual.setBiografia(biografia);

        // Si se proporcionó una imagen de perfil, actualizarla
        if (imagenPerfil != null && !imagenPerfil.isEmpty()) {
            Imagen imagen = imagenServicio.guardarImagen(imagenPerfil);
            usuarioActual.setFotoPerfil(imagen);
        }

        // Si se proporcionó un banner, actualizarlo
        if (banner != null && !banner.isEmpty()) {
            Imagen bannerImagen = imagenServicio.guardarImagen(banner);
            usuarioActual.setBannerPerfil(bannerImagen);
        }

        // Guardar los cambios en el repositorio
        usuarioRepositorio.save(usuarioActual);
    }

    @Transactional
    public void setImagenUsuario(MultipartFile archivo, Long idUsuario) throws Exception {
        Usuario usuario = usuarioRepositorio.buscarPorIdUsuario(idUsuario);

        if (usuario == null) {
            System.out.println("error"); // Manejar el caso en el que no se encuentre el usuario con el código tributario dado.
            return;
        }

        Imagen imagen = imagenServicio.guardarImagen(archivo);// Manejar errores de lectura de bytes de la imagen

        usuario.setFotoPerfil(imagen);
        // Guardar el usuario actualizado
        usuarioRepositorio.save(usuario);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepositorio.buscarPorEmail(email);

        if (usuario == null) {
            throw new UsernameNotFoundException("Usuario no encontrado con el email: " + email);
        }

        List<GrantedAuthority> permisos = new ArrayList<>();
        GrantedAuthority p = new SimpleGrantedAuthority("ROLE_" + usuario.getRol().toString());
        permisos.add(p);

        // Obtener la sesión y agregar el usuario a la sesión
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(true);
        session.setAttribute("usuariosession", usuario);

        return new UsuarioDetalles(
                usuario.getEmail(),
                usuario.getContrasenia(),
                permisos,
                usuario.getIdUsuario()
        );
    }

    // Nuevo método para obtener el perfil completo del usuario
    public Usuario obtenerPerfilUsuario(String email) throws UsuarioNoEncontradoExcepcion {
        Usuario usuario = usuarioRepositorio.buscarPorEmail(email);
        if (usuario == null) {
            throw new UsuarioNoEncontradoExcepcion("No se encontró un usuario con el email: " + email);
        }
        usuario.setFotoPerfil(usuario.getFotoPerfil());
        return usuario;
    }

    public Usuario obtenerUsuarioPorId(Long idUsuario) throws UsuarioNoEncontradoExcepcion {
        Usuario usuario = usuarioRepositorio.buscarPorIdUsuario(idUsuario);
        if (usuario == null) {
            throw new UsuarioNoEncontradoExcepcion("No se encontró un usuario con el id: " + idUsuario);
        }
        usuario.setFotoPerfil(usuario.getFotoPerfil());
        return usuario;
    }

    public Usuario obtenerUsuarioPorNombre(String nombre) throws UsuarioNoEncontradoExcepcion {
        Usuario usuario = usuarioRepositorio.buscarPorNombre(nombre);
        if (usuario == null) {
            throw new UsuarioNoEncontradoExcepcion("No se encontró un usuario con el nombre: " + nombre);
        }
        usuario.setFotoPerfil(usuario.getFotoPerfil());
        return usuario;
    }

    @Transactional(readOnly = true)
    public Usuario getOne(Long idUsuario) {
        return usuarioRepositorio.getOne(idUsuario);
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarUsuarios() {

        return usuarioRepositorio.findAll();
    }

    //@Transactional
    //public void darBajaUsuario(String idCodigoTributario) {
    //    Optional<Usuario> respuesta = usuarioRepositorio.findById(idCodigoTributario);
    //    if (respuesta.isPresent()) {
    //        Usuario usuario = respuesta.get();
    //        usuario.setAlta(false);
    //
    //        usuarioRepositorio.save(usuario);
    //    }
    //}

    //@Transactional
    //public void darAltaUsuario(String idCodigoTributario) {
    //    Optional<Usuario> respuesta = usuarioRepositorio.findById(idCodigoTributario);
    //    if (respuesta.isPresent()) {
    //        Usuario usuario = respuesta.get();
    //        usuario.setAlta(true);
    //
    //        usuarioRepositorio.save(usuario);
    //    }
    //}

    @Transactional
    public void eliminarUsuario(Long usuarioId) throws Exception {
        Usuario usuario = usuarioRepositorio.findById(usuarioId)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        // Eliminar mensajes donde el usuario es el emisor
        mensajeRepositorio.deleteByEmisor(usuario);

        // Eliminar mensajes donde el usuario es el receptor
        mensajeRepositorio.deleteByReceptor(usuario);

        // Eliminar relaciones de seguidores y seguidos
        seguidorRepositorio.deleteByUsuarioOrSeguido(usuario, usuario);

        // Recorrer y eliminar todos los comentarios asociados al usuario
        for (Comentario comentario : usuario.getComentarios()) {
            Devocional devocional = devocionalRepositorio.findById(comentario.getIdDevocional())
                    .orElseThrow(() -> new Exception("Devocional no encontrado"));
            devocional.getComentarios().remove(comentario);
            comentarioRepositorio.delete(comentario);
            devocionalRepositorio.save(devocional);
        }

        // Finalmente, eliminar al usuario de la base de datos
        usuarioRepositorio.delete(usuario);
    }

    private void eliminarComentario(Long comentarioId) throws Exception {
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

    private void eliminarDevocional(int devocionalId) throws Exception {
        Devocional devocional = devocionalRepositorio.findById(devocionalId)
                .orElseThrow(() -> new Exception("Devocional no encontrado"));

        // Primero, eliminamos los comentarios asociados al devocional
        for (Comentario comentario : devocional.getComentarios()) {
            eliminarComentario(comentario.getId());
        }

        // Finalmente, eliminamos el devocional
        devocionalRepositorio.delete(devocional);
    }
    @Transactional(readOnly = true)
    public Usuario obtenerUsuarioPorUsername(String username) throws MiExcepcion {
        Usuario usuario = usuarioRepositorio.buscarPorEmail(username);
        if (usuario == null) {
            throw new MiExcepcion("No se encontró un usuario con el username: " + username);
        }
        return usuario;
    }

    //public void updateResetPwToken(String token, String email) throws UsuarioNoEncontradoExcepcion {
    //
    //    Usuario usuario = usuarioRepositorio.buscarPorEmail(email);
    //
    //    if (usuario != null) {
    //        usuario.setResetPwToken(token);
    //        usuarioRepositorio.save(usuario);
    //    } else {
    //        throw new UsuarioNoEncontradoExcepcion("No pudimos encontrar ningún usuario con el email" + email);
    //    }
    //}

    //public Usuario getResetPwToken(String token) {

    //    return usuarioRepositorio.buscarPorResetPwToken(token);
    //}

    //public void updatePassword(Usuario usuario, String newPassword) throws MiExcepcion {
    //    validarContrasenia(newPassword);
    //    System.out.println(usuario);
    //    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    //    String encodePassword = passwordEncoder.encode(newPassword);
    //    usuario.setContrasenia(encodePassword);
    //
    //    usuario.setResetPwToken(null);
    //    usuarioRepositorio.save(usuario);
    //}

    public void validarDatosRegistro(String nombre,
                                     String email,
                                     String celular,
                                     String contrasenia,
                                     String contrasenia2) throws MiExcepcion {

        validarCadena(nombre, "El nombre no puede ser vacío o nulo.");
        validarCadena(email, "El email no puede ser vacío o nulo.");
        validarCadena(celular, "El numero de celular no puede ser vacío o nulo.");
        validarContrasenia(contrasenia);
        if (!contrasenia.equals(contrasenia2)) {
            throw new MiExcepcion("Las contraseñas ingresadas deben ser iguales");
        }
    }
    void validarCadena(String valor, String mensajeError) throws MiExcepcion {
        if (valor == null || valor.trim().isEmpty()) {
            throw new MiExcepcion(mensajeError);
        }
    }

    public void validarDatosModificar(String email, String celular) throws MiExcepcion {

        if (email == null || email.isEmpty()) {
            throw new MiExcepcion("El email no puede estar vacío, ni haber sido registrado anteriormente");
        }
        if (celular == null || celular.isEmpty()) {
            throw new MiExcepcion("El celular no puede estar vacío o ser nulo");
        }
    }

    public void validarContrasenia(String contrasenia) throws MiExcepcion {
        if (contrasenia == null || contrasenia.length() <= 5) {
            throw new MiExcepcion("La contraseña no puede estar vacía, y debe tener más de 5 dígitos");
        }

    }

}
