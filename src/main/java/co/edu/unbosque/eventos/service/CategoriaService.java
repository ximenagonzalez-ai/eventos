package co.edu.unbosque.eventos.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unbosque.eventos.dto.CategoriaDTO;
import co.edu.unbosque.eventos.model.Categoria;
import co.edu.unbosque.eventos.repository.CategoriaRepository;

/**
 * Servicio para operaciones CRUD sobre la entidad Categoria.
 * Solo ADMIN puede crear, actualizar o eliminar categorias.
 * Cualquier usuario autenticado puede consultarlas.
 */
@Service
public class CategoriaService implements CRUDOperation<CategoriaDTO> {

    /** Repositorio de categorias para acceso a la base de datos. */
    @Autowired
    private CategoriaRepository categoriaRepo;

    /** Mapeador para convertir entre Categoria y CategoriaDTO. */
    @Autowired
    private ModelMapper modelMapper;

    /** Constructor por defecto. */
    public CategoriaService() {}

    @Override
    public long count() {
        return categoriaRepo.count();
    }

    @Override
    public boolean exist(Long id) {
        return categoriaRepo.existsById(id);
    }

    /**
     * Crea una nueva categoria.
     * Retorna 1 si el nombre ya esta en uso.
     *
     * @param data DTO con los datos de la nueva categoria
     * @return 0 si se creo correctamente, 1 si el nombre ya existe
     */
    @Override
    public int create(CategoriaDTO data) {
        if (categoriaRepo.existsByNombreIgnoreCase(data.getNombre())) {
            return 1; // nombre ya existe
        }
        Categoria entity = modelMapper.map(data, Categoria.class);
        categoriaRepo.save(entity);
        return 0;
    }

    /**
     * Retorna todas las categorias disponibles.
     *
     * @return Lista de DTOs de categorias
     */
    @Override
    public List<CategoriaDTO> getAll() {
        List<Categoria> lista = categoriaRepo.findAll();
        List<CategoriaDTO> dtos = new ArrayList<>();
        lista.forEach(c -> dtos.add(modelMapper.map(c, CategoriaDTO.class)));
        return dtos;
    }

    /**
     * Elimina una categoria por su ID.
     *
     * @param id ID de la categoria a eliminar
     * @return 0 si se elimino, 1 si no existe
     */
    @Override
    public int deleteById(Long id) {
        if (!categoriaRepo.existsById(id)) return 1;
        categoriaRepo.deleteById(id);
        return 0;
    }

    /**
     * Actualiza el nombre y/o descripcion de una categoria.
     *
     * @param id      ID de la categoria a actualizar
     * @param newData DTO con los nuevos datos
     * @return 0 exito, 1 nombre ya en uso por otra categoria, 2 no existe
     */
    @Override
    public int updateById(Long id, CategoriaDTO newData) {
        Optional<Categoria> found = categoriaRepo.findById(id);
        if (found.isEmpty()) return 2;

        // Verifica que el nuevo nombre no lo use otra categoria
        Optional<Categoria> byName = categoriaRepo.findByNombreIgnoreCase(newData.getNombre());
        if (byName.isPresent() && !byName.get().getId().equals(id)) return 1;

        Categoria entity = found.get();
        entity.setNombre(newData.getNombre());
        entity.setDescripcion(newData.getDescripcion());
        categoriaRepo.save(entity);
        return 0;
    }

    /**
     * Busca una categoria por su nombre exacto.
     *
     * @param nombre Nombre a buscar
     * @return DTO de la categoria o null si no existe
     */
    public CategoriaDTO getByNombre(String nombre) {
        Optional<Categoria> found = categoriaRepo.findByNombreIgnoreCase(nombre);
        return found.map(c -> modelMapper.map(c, CategoriaDTO.class)).orElse(null);
    }

    /**
     * Busca una categoria por su ID.
     *
     * @param id ID a buscar
     * @return DTO de la categoria o null si no existe
     */
    public CategoriaDTO getById(Long id) {
        Optional<Categoria> found = categoriaRepo.findById(id);
        return found.map(c -> modelMapper.map(c, CategoriaDTO.class)).orElse(null);
    }
}
