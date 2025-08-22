package dylan.devocionalesspring.repositorios;

import dylan.devocionalesspring.entidades.Comentario;
import dylan.devocionalesspring.entidades.Devocional;
import dylan.devocionalesspring.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DevocionalRepositorio extends JpaRepository<Devocional, Integer> {

    @Query("SELECT d.comentarios FROM Devocional d WHERE d.id = :devocionalId")
    List<Comentario> findComentariosByDevocionalId(@Param("devocionalId") int devocionalId);

    // Método para buscar devocionales por nombre
    List<Devocional> findByTituloContainingIgnoreCase(String nombre);

    List<Devocional> findAllByOrderByFechaCreacionDesc();

    //Incrementar Vistas y Likes
    @Modifying
    @Query("UPDATE Devocional d SET d.vistas = d.vistas + 1 WHERE d.id = :id")
    void incrementarVistas(@Param("id") Long id);


}
