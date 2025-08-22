package dylan.devocionalesspring.entidades;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Seguidor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_idUsuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "seguido_idUsuario")
    private Usuario seguido;

    // Getters y setters
}
