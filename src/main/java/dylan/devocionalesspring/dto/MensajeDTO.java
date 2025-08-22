package dylan.devocionalesspring.dto;

import java.time.LocalDateTime;

public class MensajeDTO {
    private Long id;
    private String contenido;
    private LocalDateTime fechaEnvio;
    private UsuarioDTO emisor;
    private UsuarioDTO receptor;

    public MensajeDTO() {}

    public MensajeDTO(Long id, String contenido, LocalDateTime fechaEnvio,
                      UsuarioDTO emisor, UsuarioDTO receptor) {
        this.id = id;
        this.contenido = contenido;
        this.fechaEnvio = fechaEnvio;
        this.emisor = emisor;
        this.receptor = receptor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(LocalDateTime fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public UsuarioDTO getEmisor() {
        return emisor;
    }

    public void setEmisor(UsuarioDTO emisor) {
        this.emisor = emisor;
    }

    public UsuarioDTO getReceptor() {
        return receptor;
    }

    public void setReceptor(UsuarioDTO receptor) {
        this.receptor = receptor;
    }
}
