package dylan.devocionalesspring.dto;

import dylan.devocionalesspring.entidades.Usuario;

public class UsuarioDTO {
    private Long idUsuario;
    private String nombre;

    public UsuarioDTO() {}

    public UsuarioDTO(Long idUsuario, String nombre) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
    }

    // Constructor auxiliar para mapear desde la entidad
    public UsuarioDTO(Usuario usuario) {
        this.idUsuario = usuario.getIdUsuario();
        this.nombre = usuario.getNombre();
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}