package org.esfe.repositorios;

import org.esfe.modelos.Blog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IBlogRepository  extends JpaRepository<Blog, Integer> {
    List<Blog> findByUsuarioId(Integer usuarioId);
    List<Blog> findByTituloContainingIgnoreCaseOrDescripcionContainingIgnoreCase(String titulo, String descripcion);
}
//se añadio comentario para poder subir el commit y relacionarlo con jira ya que sino no deja vincularlo.