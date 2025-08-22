package dylan.devocionalesspring.entidades;

import  com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import dylan.devocionalesspring.enumeraciones.Rol;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.util.Lazy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Usuario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;
    private String nombre;
    private String email;
    private String nombreUsuario;
    private String biografia;
    private String celular;
    private String contrasenia;
    private String contrasenia2;

    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "imagen_id")
    private Imagen fotoPerfil;

    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "banner_id")
    private Imagen bannerPerfil;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Devocional> devocionales;

    @OneToMany(mappedBy = "usuarioId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MeGusta> meGustas = new ArrayList<>();


    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "usuario_comentarios",
            joinColumns = @JoinColumn(name = "usuario_id_usuario"),
            inverseJoinColumns = @JoinColumn(name = "comentarios_id")
    )
    private List<Comentario> comentarios = new ArrayList<>();

    public Usuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }
}


// private String resetPwToken;