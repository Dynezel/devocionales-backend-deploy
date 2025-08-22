package dylan.devocionalesspring.servicios;

import dylan.devocionalesspring.entidades.MeGusta;
import dylan.devocionalesspring.entidades.Notificacion;
import dylan.devocionalesspring.entidades.Usuario;
import dylan.devocionalesspring.repositorios.DevocionalRepositorio;
import dylan.devocionalesspring.repositorios.MeGustaRepositorio;
import dylan.devocionalesspring.repositorios.NotificacionRepositorio;
import dylan.devocionalesspring.repositorios.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class MeGustaServicio {

    @Autowired
    private NotificacionServicio notificacionServicio;

    @Autowired
    private MeGustaRepositorio meGustaRepositorio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private NotificacionRepositorio notificacionRepositorio;

    @Autowired
    private DevocionalRepositorio devocionalRepositorio;


    @Transactional
    public MeGusta toggleMeGusta(Long usuarioId, int devocionalId, long usuarioReceptorId) {
        Optional<MeGusta> meGustaExistente = meGustaRepositorio.findByUsuarioIdAndDevocionalId(usuarioId, (long) devocionalId);

        if (meGustaExistente.isPresent()) {
            meGustaRepositorio.delete(meGustaExistente.get());



            return null; // Indica que se quitó el "Me Gusta"
        } else {
            MeGusta meGusta = new MeGusta();
            meGusta.setUsuarioId(usuarioId);
            meGusta.setDevocionalId((long) devocionalId);

            // URL única para la notificación del "Me Gusta" en el devocional
            String urlNotificacion = "/devocional/" + devocionalId + "?autorId=" + usuarioReceptorId;

            // Buscar si existe una notificación de "Me Gusta" para este devocional y usuario
            Optional<Notificacion> notificacionExistente = notificacionRepositorio.findByTipoAndUsuarioEmisorIdAndUrl(
                    "megusta", usuarioId, urlNotificacion
            );

            if (notificacionExistente.isPresent()) {
                // Si existe, actualiza el timestamp para que aparezca reciente
                Notificacion notificacion = notificacionExistente.get();
                notificacion.setTimestamp(LocalDateTime.now());
                notificacionRepositorio.save(notificacion);
            } else {
                // Si no existe, crea una nueva notificación
                Usuario emisor = usuarioRepositorio.buscarPorIdUsuario(usuarioId);
                Notificacion notificacion = notificacionServicio.crearNotificacion(
                        "megusta",
                        emisor.getNombre() + " le ha dado me gusta a tu publicación",
                        Collections.singletonList(usuarioReceptorId),
                        usuarioId,
                        urlNotificacion
                );
                notificacionRepositorio.save(notificacion);
            }

            return meGustaRepositorio.save(meGusta); // Se agregó un nuevo "Me Gusta"
        }
    }

    public List<MeGusta> obtenerMeGustasPorDevocional(int devocionalId) {
        return meGustaRepositorio.findByDevocionalId(devocionalId);
    }

    public List<MeGusta> obtenerMeGustasPorUsuario(Long usuarioId) {
        return meGustaRepositorio.findByUsuarioId(usuarioId);
    }
}
