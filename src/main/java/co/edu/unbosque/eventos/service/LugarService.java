package co.edu.unbosque.eventos.service;

import co.edu.unbosque.eventos.DTO.LugarDTO;
import co.edu.unbosque.eventos.model.Lugar;
import co.edu.unbosque.eventos.repository.LugarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LugarService {

    private final LugarRepository lugarRepository;

    public List<LugarDTO> getAll() {
        return lugarRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<LugarDTO> getById(Long id) {
        return lugarRepository.findById(id).map(this::toDTO);
    }

    public List<LugarDTO> getByCiudad(String ciudad) {
        return lugarRepository.findByCiudadIgnoreCase(ciudad).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public LugarDTO crear(LugarDTO dto) {
        Lugar lugar = toEntity(dto);
        return toDTO(lugarRepository.save(lugar));
    }

    @Transactional
    public Optional<LugarDTO> actualizar(Long id, LugarDTO dto) {
        return lugarRepository.findById(id).map(l -> {
            l.setNombre(dto.getNombre());
            l.setDireccion(dto.getDireccion());
            l.setCiudad(dto.getCiudad());
            l.setLatitud(dto.getLatitud());
            l.setLongitud(dto.getLongitud());
            l.setDescripcion(dto.getDescripcion());
            return toDTO(lugarRepository.save(l));
        });
    }

    @Transactional
    public boolean eliminar(Long id) {
        if (!lugarRepository.existsById(id)) return false;
        lugarRepository.deleteById(id);
        return true;
    }

    public long count() {
        return lugarRepository.count();
    }

    public LugarDTO toDTO(Lugar l) {
        return LugarDTO.builder()
                .id(l.getId())
                .nombre(l.getNombre())
                .direccion(l.getDireccion())
                .ciudad(l.getCiudad())
                .latitud(l.getLatitud())
                .longitud(l.getLongitud())
                .descripcion(l.getDescripcion())
                .build();
    }

    public Lugar toEntity(LugarDTO dto) {
        return Lugar.builder()
                .nombre(dto.getNombre())
                .direccion(dto.getDireccion())
                .ciudad(dto.getCiudad())
                .latitud(dto.getLatitud() != null ? dto.getLatitud() : 0.0)
                .longitud(dto.getLongitud() != null ? dto.getLongitud() : 0.0)
                .descripcion(dto.getDescripcion())
                .build();
    }
}
