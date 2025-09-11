package org.esfe.servicios.implementaciones;

import jakarta.persistence.EntityNotFoundException;
import org.esfe.dtos.blog.BlogGuardar;
import org.esfe.dtos.blog.BlogModificar;
import org.esfe.dtos.blog.BlogSalida;
import org.esfe.modelos.Blog;
import org.esfe.repositorios.IBlogRepository;
import org.esfe.servicios.interfaces.IBlogService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BlogService implements IBlogService {

    @Autowired
    private IBlogRepository blogRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<BlogSalida> obtenerTodos() {
        List<Blog> blogs = blogRepository.findAll();
        return blogs.stream()
                .map(blog -> modelMapper.map(blog, BlogSalida.class))
                .collect(Collectors.toList());
    }

    @Override
    public Page<BlogSalida> obtenerTodosPaginados(Pageable pageable) {
        Page<Blog> page = blogRepository.findAll(pageable);
        List<BlogSalida> blogsDto = page.stream()
                .map(blog -> modelMapper.map(blog, BlogSalida.class))
                .collect(Collectors.toList());
        return new PageImpl<>(blogsDto, page.getPageable(), page.getTotalElements());
    }

    @Override
    public BlogSalida obtenerPorId(Integer id) {
        Optional<Blog> blog = blogRepository.findById(id);
        if (blog.isPresent()) {
            return modelMapper.map(blog.get(), BlogSalida.class);
        }
        return null;
    }

    @Override
    public BlogSalida crear(BlogGuardar blogGuardar, Integer usuarioId) {
        Blog blog = modelMapper.map(blogGuardar, Blog.class);
        blog.setIdUsuario(usuarioId);
        blog.setFechaPublicacion(LocalDateTime.now());
        Blog blogGuardado = blogRepository.save(blog);
        return modelMapper.map(blogGuardado, BlogSalida.class);
    }

    // 🎯 El método 'editar' ahora usa el ID directamente del DTO
    @Override
    public BlogSalida editar(Integer id, BlogModificar blogModificar, Integer usuarioId) {
        // Busca el blog usando el ID del parámetro, no el del DTO
        Blog blogExistente = blogRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Blog no encontrado con ID: " + id));

        // Validación de seguridad
        if (!blogExistente.getIdUsuario().equals(usuarioId)) {
            throw new AccessDeniedException("No tienes permiso para editar este blog");
        }

        // Usa ModelMapper para actualizar solo los campos del DTO
        modelMapper.map(blogModificar, blogExistente);

        Blog blogActualizado = blogRepository.save(blogExistente);
        return modelMapper.map(blogActualizado, BlogSalida.class);
    }

    @Override
    public void eliminarPorId(Integer id, Integer usuarioId) {
        Blog blogExistente = blogRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Blog no encontrado con ID: " + id));

        // Validación de seguridad
        if (!blogExistente.getIdUsuario().equals(usuarioId)) {
            throw new AccessDeniedException("No tienes permiso para eliminar este blog");
        }

        blogRepository.deleteById(id);
    }

    @Override
    public List<Blog> findByTituloContainingIgnoreCaseOrDescripcionContainingIgnoreCase(String titulo, String descripcion) {
        return List.of();
    }
}