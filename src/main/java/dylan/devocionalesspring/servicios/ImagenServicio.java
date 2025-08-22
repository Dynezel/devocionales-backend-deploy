package dylan.devocionalesspring.servicios;

import java.io.IOException;
import java.util.List;

import dylan.devocionalesspring.entidades.Imagen;
import dylan.devocionalesspring.excepciones.MiExcepcion;
import dylan.devocionalesspring.repositorios.ImagenRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ImagenServicio {

    @Autowired
    private ImagenRepositorio imagenRepositorio;

    @Transactional
    public Imagen guardarImagen(MultipartFile archivo) throws MiExcepcion, IOException {

        Imagen imagen = new Imagen();
        imagen.setMime(archivo.getContentType());
        imagen.setNombre(archivo.getOriginalFilename()); // Cambiado de getName() a getOriginalFilename()
        imagen.setContenido(archivo.getBytes());

        // La imagen aún no se asocia con el inmueble aquí
        // porque el inmueble no ha sido guardado en la base de datos todavía.
        // La asociación se hará después de que el inmueble haya sido guardado y se tenga su ID.
        // Guardar la imagen sin asociarla al inmueble
        return imagenRepositorio.save(imagen);
    }

    @Transactional
    public Imagen guardarImagenRuta(String rutaImagen) throws MiExcepcion {
        Imagen imagen = new Imagen();
        imagen.setRutaImagen(rutaImagen);
        // Omitir guardar el contenido de la imagen
        // Solo guarda la ruta de la imagen
        return imagenRepositorio.save(imagen);
    }

    public Imagen actualizar(MultipartFile archivo, String idImagen) {
        if (archivo != null) {
            try {
                Imagen imagen = new Imagen();

                if (idImagen != null) {
                    Optional<Imagen> respuesta = imagenRepositorio.findById(idImagen);
                    if (respuesta.isPresent()) {
                        imagen = respuesta.get();
                    }

                }
                imagen.setMime(archivo.getContentType());
                imagen.setNombre(archivo.getName());
                imagen.setContenido(archivo.getBytes());

                return imagenRepositorio.save(imagen);

            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
        return null;
    }

    @Transactional(readOnly = true)
    public List<Imagen> listarTodos() {
        return imagenRepositorio.findAll();

    }

    @Transactional
    public void eliminarImagen(String idImagen) {
        if (idImagen != null) {
            Optional<Imagen> respuesta = imagenRepositorio.findById(idImagen);
            if (respuesta.isPresent()) {
                Imagen imagen = respuesta.get();

                try {
                    // Elimina la imagen del repositorio
                    imagenRepositorio.delete(imagen);
                } catch (Exception e) {
                    System.err.println("Error al eliminar la imagen: " + e.getMessage());
                }
            }
        }
    }

}
