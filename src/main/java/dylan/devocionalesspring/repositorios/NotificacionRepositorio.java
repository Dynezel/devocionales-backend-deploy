package dylan.devocionalesspring.repositorios;

import dylan.devocionalesspring.entidades.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NotificacionRepositorio extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByUsuarioReceptorIdAndVistoFalse(List<Long> usuarioReceptorId);

    Optional<Notificacion> findByTipoAndUsuarioEmisorIdAndUrl(String tipo, Long usuarioEmisorId, String url);
    void deleteByTipoAndUsuarioEmisorIdAndUrl(String tipo, Long usuarioEmisorId, String url);

    @Query("SELECT n FROM Notificacion n WHERE n.usuarioReceptorId = :usuarioReceptorId ORDER BY n.timestamp DESC")
    List<Notificacion> findByUsuarioReceptorId(@Param("usuarioReceptorId") List<Long> usuarioReceptorId);

}