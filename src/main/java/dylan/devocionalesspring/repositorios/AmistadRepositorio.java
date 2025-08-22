package dylan.devocionalesspring.repositorios;

import dylan.devocionalesspring.entidades.Amistad;
import dylan.devocionalesspring.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AmistadRepositorio extends JpaRepository<Amistad, Long> {
    List<Amistad> findByUsuarioSolicitanteOrUsuarioAmigoAndEstado(Usuario usuario, Usuario amigo, Amistad.EstadoAmistad estado);
    Optional<Amistad> findByUsuarioSolicitanteAndUsuarioAmigoAndEstado(Usuario usuario, Usuario amigo, Amistad.EstadoAmistad estado);

    void deleteByUsuarioSolicitanteOrUsuarioAmigo(Usuario usuarioSolicitante, Usuario usuarioAmigo);
}
