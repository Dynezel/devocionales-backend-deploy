package dylan.devocionalesspring.entidades;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Amistad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_idUsuario")
    private Usuario usuarioSolicitante;

    @ManyToOne
    @JoinColumn(name = "amigo_idUsuario")
    private Usuario usuarioAmigo;

    @Enumerated(EnumType.STRING)
    private EstadoAmistad estado;

    public enum EstadoAmistad {
        PENDIENTE,
        ACEPTADA,
        RECHAZADA
    }
}