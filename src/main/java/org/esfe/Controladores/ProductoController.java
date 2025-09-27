package org.esfe.Controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.esfe.servicios.interfaces.IProductoService;
import org.esfe.dtos.producto.ProductoGuardar;
import org.esfe.dtos.producto.ProductoModificar;
import org.esfe.dtos.producto.ProductoSalida;
import org.esfe.modelos.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.esfe.repositorios.IProductoRepository;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {
    @Autowired
    private IProductoService productoService;
    @Autowired
    private IProductoRepository productoRepository;
    @Autowired
    private ModelMapper modelMapper;

    // ✅ OPERACIONES DE LECTURA - Públicas (mantienen el código original)

    @GetMapping
    public ResponseEntity<Page<ProductoSalida>> mostrarTodosPaginados(Pageable pageable){
        Page<ProductoSalida> productos = productoService.obtenerTodosPaginados(pageable);
        if(productos.hasContent()){
            return ResponseEntity.ok(productos);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/lista")
    public ResponseEntity<List<ProductoSalida>> mostrarTodos(){
        List<ProductoSalida> productos = productoService.obtenerTodos();
        if(!productos.isEmpty()){
            return ResponseEntity.ok(productos);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoSalida> mostrarPorId(@PathVariable Integer id){
        try{
            ProductoSalida producto = productoService.obtenerPorId(id);
            return ResponseEntity.ok(producto);
        }catch(RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

    // ✅ OPERACIONES DE ESCRITURA - Privadas (requieren autenticación y autorización)

    @PostMapping
    public ResponseEntity<ProductoSalida> crear(@Valid @RequestBody ProductoGuardar productoGuardar){
        ProductoSalida nuevoProducto = productoService.crear(productoGuardar);
        return new ResponseEntity<>(nuevoProducto, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<ProductoSalida> editar(@Valid @RequestBody ProductoModificar productoModificar){
        try{
            ProductoSalida productoEditado = productoService.editar(productoModificar);
            return ResponseEntity.ok(productoEditado);
        }catch(RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPorId(@PathVariable Integer id){
        try{
            productoService.eliminarPorId(id);
            return ResponseEntity.noContent().build();
        }catch(RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

    // ✅ OPERACIONES DE BÚSQUEDA - Públicas

    @GetMapping("/buscar")
    public ResponseEntity<List<ProductoSalida>> buscarProductos(@RequestParam(required = false) String nombre,
                                                               @RequestParam(required = false) String descripcion) {
        List<Producto> productos;

        if (nombre != null && descripcion != null) {
            // Buscar por nombre O descripción
            productos = productoRepository.findByNombreContainingIgnoreCaseOrDescripcionContainingIgnoreCase(nombre, descripcion);
        } else if (nombre != null) {
            // Buscar solo por nombre
            productos = productoRepository.findByNombreContainingIgnoreCase(nombre);
        } else if (descripcion != null) {
            // Buscar solo por descripción
            productos = productoRepository.findByDescripcionContainingIgnoreCase(descripcion);
        } else {
            // Si no se proporciona ningún parámetro, devolver todos los productos
            productos = productoRepository.findAll();
        }

        List<ProductoSalida> productosSalida = productos.stream()
                .map(producto -> modelMapper.map(producto, ProductoSalida.class))
                .collect(Collectors.toList());

        if (!productosSalida.isEmpty()) {
            return ResponseEntity.ok(productosSalida);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
