package org.esfe.servicios.implementaciones;

import org.esfe.dtos.producto.ProductoGuardar;
import org.esfe.dtos.producto.ProductoModificar;
import org.esfe.dtos.producto.ProductoSalida;
import org.esfe.modelos.Producto;
import org.esfe.repositorios.IProductoRepository;
import org.esfe.servicios.interfaces.IProductoService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductoService implements IProductoService {

    @Autowired
    private IProductoRepository productoRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<ProductoSalida> obtenerTodos() {
        List<Producto> productos = productoRepository.findAll();
        return productos.stream()
                .map(producto -> modelMapper.map(producto, ProductoSalida.class))
                .collect(Collectors.toList());
    }

    @Override
    public Page<ProductoSalida> obtenerTodosPaginados(Pageable pageable) {
        Page<Producto> productosPage = productoRepository.findAll(pageable);
        List<ProductoSalida> productosSalida = productosPage.getContent().stream()
                .map(producto -> modelMapper.map(producto, ProductoSalida.class))
                .collect(Collectors.toList());
        return new PageImpl<>(productosSalida, pageable, productosPage.getTotalElements());
    }

    @Override
    public ProductoSalida obtenerPorId(Integer id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));
        return modelMapper.map(producto, ProductoSalida.class);
    }

    @Override
    public ProductoSalida crear(ProductoGuardar productoGuardar) {
        Producto producto = modelMapper.map(productoGuardar, Producto.class);
        producto.setId(null); // Asegurarse de que el ID sea nulo para crear un nuevo registro
        producto = productoRepository.save(producto);
        return modelMapper.map(producto, ProductoSalida.class);
    }

    @Override
    public ProductoSalida editar(ProductoModificar productoModificar) {
        Producto productoExistente = productoRepository.findById(productoModificar.getId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + productoModificar.getId()));

        modelMapper.map(productoModificar, productoExistente);
        productoExistente = productoRepository.save(productoExistente);
        return modelMapper.map(productoExistente, ProductoSalida.class);
    }

    @Override
    public void eliminarPorId(Integer id) {
        if (!productoRepository.existsById(id)) {
            throw new RuntimeException("Producto no encontrado con id: " + id);
        }
        productoRepository.deleteById(id);
    }

    @Override
    public List<Producto> findByNombreContainingIgnoreCaseOrDescripcionContainingIgnoreCase(String nombre, String descripcion) {
        return productoRepository.findByNombreContainingIgnoreCaseOrDescripcionContainingIgnoreCase(nombre, descripcion);  
    }
}
