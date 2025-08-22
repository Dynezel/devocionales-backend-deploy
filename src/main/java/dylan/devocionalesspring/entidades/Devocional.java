package dylan.devocionalesspring.entidades;

import com.fasterxml.jackson.annotation.JsonBackReference;
import dylan.devocionalesspring.enumeraciones.Rol;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Devocional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String titulo;
    private String contenido;
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private int vistas = 0;

    @OneToMany(mappedBy = "devocionalId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MeGusta> meGustas = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "devocional_comentarios",
            joinColumns = @JoinColumn(name = "devocional_id"),
            inverseJoinColumns = @JoinColumn(name = "comentarios_id")
    )
    private List<Comentario> comentarios = new ArrayList<>();

    // Método para agregar un comentario a la lista
    public void agregarComentario(Comentario comentario) {
        comentarios.add(comentario);
    }
}

