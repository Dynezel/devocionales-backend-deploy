package dylan.devocionalesspring.repositorios;

import dylan.devocionalesspring.entidades.Mensaje;
import dylan.devocionalesspring.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MensajeRepositorio extends JpaRepository<Mensaje, Long> {
    List<Mensaje> findByEmisorAndReceptor(Usuario emisor, Usuario receptor);

    List<Mensaje> findByReceptor(Usuario receptor);

    //Query para buscar las conversaciones de un Usuario
    @Query("SELECT DISTINCT m.receptor FROM Mensaje m WHERE m.emisor.id = :userId " +
            "UNION " +
            "SELECT DISTINCT m.emisor FROM Mensaje m WHERE m.receptor.id = :userId")
    List<Usuario> findConversacionesPorUsuario(@Param("userId") Long userId);

    //Query para buscar la conversacion entre ambos usuarios
    @Query("SELECT m FROM Mensaje m WHERE (m.emisor.id = :emisorId AND m.receptor.id = :receptorId) OR (m.emisor.id = :receptorId AND m.receptor.id = :emisorId) ORDER BY m.fechaEnvio ASC")
    List<Mensaje> findByUsuarios(@Param("emisorId") Long emisorId, @Param("receptorId") Long receptorId);

    void deleteByEmisor(Usuario emisor);

    void deleteByReceptor(Usuario receptor);

}