package org.esfe.repositorios;

import org.esfe.modelos.Blog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IBlogRepository  extends JpaRepository<Blog, Integer> {
    List<Blog> findByTituloContainingIgnoreCaseOrDescripcionContainingIgnoreCase(String titulo, String descripcion);
}
