package co.edu.unbosque.eventos.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unbosque.eventos.dto.LugarDTO;
import co.edu.unbosque.eventos.model.Lugar;
import co.edu.unbosque.eventos.repository.LugarRepository;

/**
 * Servicio para operaciones CRUD sobre la entidad Lugar.
 * Los lugares almacenan latitud y longitud para que el frontend
 * pueda mostrar la ubicacion y la ruta en un mapa.
 */
@Service
public class LugarService implements CRUDOperation<LugarDTO> {

    /** Repositorio de lugares para acceso a la base de datos. */
    @Autowired
    private LugarRepository lugarRepo;

    /** Mapeador para convertir entre Lugar y LugarDTO. */
    @Autowired
    private ModelMapper modelMapper;

    /** Constructor por defecto. */
    public LugarService() {}

    @Override
    public long count() {
        return lugarRepo.count();
    }

    @Override
    public boolean exist(Long id) {
        return lugarRepo.existsById(id);
    }

    /**
     * Crea un nuevo lugar con su ubicacion geografica.
     *
     * @param data DTO con los datos del lugar (incluye latitud y longitud)
     * @return 0 si se creo correctamente
     */
    @Override
    public int create(LugarDTO data) {
        Lugar entity = modelMapper.map(data, Lugar.class);
        lugarRepo.save(entity);
        return 0;
    }

    /**
     * Retorna todos los lugares disponibles.
     *
     * @return Lista de DTOs de lugares con sus coordenadas
     */
    @Override
    public List<LugarDTO> getAll() {
        List<Lugar> lista = lugarRepo.findAll();
        List<LugarDTO> dtos = new ArrayList<>();
        lista.forEach(l -> dtos.add(modelMapper.map(l, LugarDTO.class)));
        return dtos;
    }

    /**
     * Elimina un lugar por su ID.
     *
     * @param id ID del lugar a eliminar
     * @return 0 si se elimino, 1 si no existe
     */
    @Override
    public int deleteById(Long id) {
        if (!lugarRepo.existsById(id)) return 1;
        lugarRepo.deleteById(id);
        return 0;
    }

    /**
     * Actualiza los datos de un lugar, incluyendo sus coordenadas.
     *
     * @param id      ID del lugar a actualizar
     * @param newData DTO con los nuevos datos
     * @return 0 exito, 2 si no existe el lugar
     */
    @Override
    public int updateById(Long id, LugarDTO newData) {
        Optional<Lugar> found = lugarRepo.findById(id);
        if (found.isEmpty()) return 2;

        Lugar entity = found.get();
        entity.setNombre(newData.getNombre());
        entity.setDireccion(newData.getDireccion());
        entity.setCiudad(newData.getCiudad());
        entity.setLatitud(newData.getLatitud());
        entity.setLongitud(newData.getLongitud());
        entity.setDescripcion(newData.getDescripcion());
        lugarRepo.save(entity);
        return 0;
    }

    /**
     * Obtiene un lugar por su ID.
     *
     * @param id ID del lugar
     * @return DTO del lugar con coordenadas o null si no existe
     */
    public LugarDTO getById(Long id) {
        Optional<Lugar> found = lugarRepo.findById(id);
        return found.map(l -> modelMapper.map(l, LugarDTO.class)).orElse(null);
    }

    /**
     * Busca lugares por ciudad.
     * Util para filtrar donde ocurren los eventos.
     *
     * @param ciudad Ciudad a buscar
     * @return Lista de lugares en esa ciudad
     */
    public List<LugarDTO> getByCiudad(String ciudad) {
        List<Lugar> lista = lugarRepo.findByCiudadIgnoreCase(ciudad);
        List<LugarDTO> dtos = new ArrayList<>();
        lista.forEach(l -> dtos.add(modelMapper.map(l, LugarDTO.class)));
        return dtos;
    }
}
