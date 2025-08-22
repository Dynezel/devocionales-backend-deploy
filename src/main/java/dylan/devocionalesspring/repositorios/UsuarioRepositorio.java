package dylan.devocionalesspring.repositorios;

import dylan.devocionalesspring.dto.UsuarioDTO;
import dylan.devocionalesspring.entidades.Comentario;
import dylan.devocionalesspring.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {

    @Query("SELECT u FROM Usuario u WHERE u.email =  :email")
    public Usuario buscarPorEmail(@Param("email") String email);

    @Query("SELECT u FROM Usuario u WHERE u.nombre =  :nombre")
    public Usuario buscarPorNombre(@Param("nombre") String nombre);

    @Query("SELECT u FROM Usuario u WHERE u.idUsuario =  :idUsuario")
    public Usuario buscarPorIdUsuario(@Param("idUsuario") Long idUsuario);

    @Query("SELECT u FROM Usuario u WHERE u.idUsuario =  :idUsuario")
    public UsuarioDTO buscarPorIdUsuarioDTO(@Param("idUsuario") Long idUsuario);

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.devocionales WHERE u.idUsuario = :idUsuario")
    Usuario findUsuarioWithDevocionales(@Param("idUsuario") Long idUsuario);

    @Query("SELECT u.comentarios FROM Usuario u WHERE u.id = :idUsuario")
    List<Comentario> findComentariosByUsuarioId(@Param("idUsuario") Long idUsuario);

}
