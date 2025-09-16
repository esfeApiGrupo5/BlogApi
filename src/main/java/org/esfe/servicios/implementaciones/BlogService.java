package org.esfe.servicios.implementaciones;

import org.esfe.Seguridad.JwtAuthFilter;
import org.esfe.dtos.blog.BlogGuardar;
import org.esfe.dtos.blog.BlogModificar;
import org.esfe.dtos.blog.BlogSalida;
import org.esfe.dtos.blog.UsuarioInfoDto;
import org.esfe.modelos.Blog;
import org.esfe.repositorios.IBlogRepository;
import org.esfe.servicios.externos.UsuarioApiService;
import org.esfe.servicios.interfaces.IBlogService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlogService implements IBlogService {

    @Autowired
    private IBlogRepository blogRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UsuarioApiService usuarioApiService;

    @Override
    public BlogSalida crear(BlogGuardar blogGuardar) {
        // Obtener el userId del contexto de autenticación
        Long usuarioId = obtenerUsuarioIdDelContexto();

        if (usuarioId == null) {
            throw new RuntimeException("Usuario no autenticado");
        }

        Blog blog = modelMapper.map(blogGuardar, Blog.class);
        blog.setId(null);
        blog.setUsuarioId(usuarioId);
        blog.setFechaPublicacion(LocalDateTime.now());

        blog = blogRepository.save(blog);

        BlogSalida blogSalida = modelMapper.map(blog, BlogSalida.class);

        // Obtener información del usuario
        String jwtToken = obtenerJwtToken();
        if (jwtToken != null) {
            UsuarioInfoDto usuarioInfo = usuarioApiService.obtenerUsuario(usuarioId, jwtToken);
            blogSalida.setUsuarioInfo(usuarioInfo);
        }

        return blogSalida;
    }

    @Override
    public BlogSalida editar(BlogModificar blogModificar) {
        // Obtener el userId del contexto de autenticación
        Long usuarioIdActual = obtenerUsuarioIdDelContexto();

        if (usuarioIdActual == null) {
            throw new RuntimeException("Usuario no autenticado");
        }

        Blog blogExistente = blogRepository.findById(blogModificar.getId())
                .orElseThrow(() -> new RuntimeException("Blog no encontrado con ID: " + blogModificar.getId()));

        // Verificar que el usuario sea el propietario del blog
        if (!blogExistente.getUsuarioId().equals(usuarioIdActual)) {
            throw new RuntimeException("No tienes permisos para editar este blog");
        }

        modelMapper.map(blogModificar, blogExistente);
        blogExistente = blogRepository.save(blogExistente);

        BlogSalida blogSalida = modelMapper.map(blogExistente, BlogSalida.class);

        // Obtener información del usuario
        String jwtToken = obtenerJwtToken();
        if (jwtToken != null) {
            UsuarioInfoDto usuarioInfo = usuarioApiService.obtenerUsuario(usuarioIdActual, jwtToken);
            blogSalida.setUsuarioInfo(usuarioInfo);
        }

        return blogSalida;
    }

    @Override
    public void eliminarPorId(Integer id) {
        Long usuarioIdActual = obtenerUsuarioIdDelContexto();

        if (usuarioIdActual == null) {
            throw new RuntimeException("Usuario no autenticado");
        }

        Blog blogExistente = blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog no encontrado con ID: " + id));

        // Verificar que el usuario sea el propietario del blog
        if (!blogExistente.getUsuarioId().equals(usuarioIdActual)) {
            throw new RuntimeException("No tienes permisos para eliminar este blog");
        }

        blogRepository.deleteById(id);
    }

    @Override
    public BlogSalida obtenerPorId(Integer id) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog no encontrado"));

        BlogSalida blogSalida = modelMapper.map(blog, BlogSalida.class);

        // Obtener información del usuario
        String jwtToken = obtenerJwtToken();
        if (jwtToken != null) {
            UsuarioInfoDto usuarioInfo = usuarioApiService.obtenerUsuario(blog.getUsuarioId(), jwtToken);
            blogSalida.setUsuarioInfo(usuarioInfo);
        }

        return blogSalida;
    }

    @Override
    public List<BlogSalida> obtenerTodos() {
        List<Blog> blogs = blogRepository.findAll();
        String jwtToken = obtenerJwtToken();

        return blogs.stream()
                .map(blog -> {
                    BlogSalida blogSalida = modelMapper.map(blog, BlogSalida.class);
                    if (jwtToken != null) {
                        UsuarioInfoDto usuarioInfo = usuarioApiService.obtenerUsuario(blog.getUsuarioId(), jwtToken);
                        blogSalida.setUsuarioInfo(usuarioInfo);
                    }
                    return blogSalida;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Page<BlogSalida> obtenerTodosPaginados(Pageable pageable) {
        Page<Blog> page = blogRepository.findAll(pageable);
        String jwtToken = obtenerJwtToken();

        List<BlogSalida> blogsDto = page.stream()
                .map(blog -> {
                    BlogSalida blogSalida = modelMapper.map(blog, BlogSalida.class);
                    if (jwtToken != null) {
                        UsuarioInfoDto usuarioInfo = usuarioApiService.obtenerUsuario(blog.getUsuarioId(), jwtToken);
                        blogSalida.setUsuarioInfo(usuarioInfo);
                    }
                    return blogSalida;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(blogsDto, page.getPageable(), page.getTotalElements());
    }

    @Override
    public List<Blog> findByTituloContainingIgnoreCaseOrDescripcionContainingIgnoreCase(String titulo, String descripcion) {
        return blogRepository.findByTituloContainingIgnoreCaseOrDescripcionContainingIgnoreCase(titulo, descripcion);
    }

    // Métodos auxiliares
    private Long obtenerUsuarioIdDelContexto() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthFilter.BlogUserAuthentication) {
            return ((JwtAuthFilter.BlogUserAuthentication) authentication).getUserId();
        }
        return null;
    }

    private String obtenerJwtToken() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                return authHeader.substring(7);
            }
        }
        return null;
    }
}