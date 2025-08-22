package dylan.devocionalesspring.repositorios;

import dylan.devocionalesspring.entidades.Seguidor;
import dylan.devocionalesspring.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeguidorRepositorio extends JpaRepository<Seguidor, Long> {
    List<Seguidor> findByUsuarioIdUsuario(Long usuarioId);
    List<Seguidor> findBySeguidoIdUsuario(Long seguidoId);
    Optional<Seguidor> findByUsuarioIdUsuarioAndSeguidoIdUsuario(Long usuarioId, Long seguidoId);

    void deleteByUsuarioOrSeguido(Usuario usuario, Usuario seguido);
}
