package dylan.devocionalesspring.excepciones;

public class MiExcepcion extends Exception {

    public MiExcepcion(String msg) {
        super(msg);
    }


    public String usuarioEncontrado(String msg, String email) {
        return("El usuario con el email: " + email + " Ya existe");
    }

}
