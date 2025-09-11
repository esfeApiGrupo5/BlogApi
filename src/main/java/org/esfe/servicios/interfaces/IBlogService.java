package org.esfe.servicios.interfaces;

import org.esfe.dtos.blog.BlogGuardar;
import org.esfe.dtos.blog.BlogModificar;
import org.esfe.dtos.blog.BlogSalida;
import org.esfe.modelos.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IBlogService {
    List<BlogSalida> obtenerTodos();

    Page<BlogSalida> obtenerTodosPaginados(Pageable pageable);

    BlogSalida obtenerPorId(Integer id);

    // Método para crear un nuevo blog, requiere el ID del usuario para asociarlo.
    BlogSalida crear(BlogGuardar blogGuardar, Integer usuarioId);

    // Método para editar un blog existente, requiere el ID del blog y del usuario para validar permisos.
    BlogSalida editar(Integer id, BlogModificar blogModificar, Integer usuarioId);

    // Método para eliminar un blog, requiere el ID del blog y del usuario para validar permisos.
    void eliminarPorId(Integer id, Integer usuarioId);

    List<Blog> findByTituloContainingIgnoreCaseOrDescripcionContainingIgnoreCase(String titulo, String descripcion);
}
