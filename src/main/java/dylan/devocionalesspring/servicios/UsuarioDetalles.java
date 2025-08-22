package dylan.devocionalesspring.servicios;

import dylan.devocionalesspring.entidades.Usuario;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class UsuarioDetalles extends User{

    // Getters y setters adicionales
    private Long idUsuario;

    public UsuarioDetalles(String nombre, String contrasenia, Collection<? extends GrantedAuthority> authorities, Long idUsuario) {
        super(nombre, contrasenia, authorities);
        this.idUsuario = idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }
}
