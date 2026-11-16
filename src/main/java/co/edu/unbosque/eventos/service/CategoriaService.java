package co.edu.unbosque.eventos.service;

import co.edu.unbosque.eventos.dto.CategoriaDTO;
import co.edu.unbosque.eventos.model.Categoria;
import co.edu.unbosque.eventos.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public List<CategoriaDTO> getAll() {
        return categoriaRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<CategoriaDTO> getById(Long id) {
        return categoriaRepository.findById(id).map(this::toDTO);
    }

    @Transactional
    public CategoriaDTO crear(CategoriaDTO dto) {
        Categoria categoria = Categoria.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .build();
        return toDTO(categoriaRepository.save(categoria));
    }

    @Transactional
    public Optional<CategoriaDTO> actualizar(Long id, CategoriaDTO dto) {
        return categoriaRepository.findById(id).map(c -> {
            c.setNombre(dto.getNombre());
            c.setDescripcion(dto.getDescripcion());
            return toDTO(categoriaRepository.save(c));
        });
    }

    @Transactional
    public boolean eliminar(Long id) {
        if (!categoriaRepository.existsById(id)) return false;
        categoriaRepository.deleteById(id);
        return true;
    }

    public long count() {
        return categoriaRepository.count();
    }

    private CategoriaDTO toDTO(Categoria c) {
        return CategoriaDTO.builder()
                .id(c.getId())
                .nombre(c.getNombre())
                .descripcion(c.getDescripcion())
                .build();
    }
}
