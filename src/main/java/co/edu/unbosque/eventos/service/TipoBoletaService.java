package co.edu.unbosque.eventos.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unbosque.eventos.dto.TipoBoletaDTO;
import co.edu.unbosque.eventos.model.Evento;
import co.edu.unbosque.eventos.model.TipoBoleta;
import co.edu.unbosque.eventos.repository.EventoRepository;
import co.edu.unbosque.eventos.repository.TipoBoletaRepository;

/**
 * Servicio para operaciones CRUD sobre la entidad TipoBoleta.
 * Gestiona los tipos de boleta (General, VIP, Palco, etc.) de cada evento.
 */
@Service
public class TipoBoletaService implements CRUDOperation<TipoBoletaDTO> {

    /** Repositorio de tipos de boleta. */
    @Autowired
    private TipoBoletaRepository tipoBoletaRepo;

    /** Repositorio de eventos, necesario para asociar el tipo de boleta al evento. */
    @Autowired
    private EventoRepository eventoRepo;

    /** Mapeador para convertir entre TipoBoleta y TipoBoletaDTO. */
    @Autowired
    private ModelMapper modelMapper;

    /** Constructor por defecto. */
    public TipoBoletaService() {}

    @Override
    public long count() {
        return tipoBoletaRepo.count();
    }

    @Override
    public boolean exist(Long id) {
        return tipoBoletaRepo.existsById(id);
    }

    /**
     * Crea un nuevo tipo de boleta asociado a un evento.
     *
     * @param data DTO con los datos del tipo (debe incluir eventoId valido)
     * @return 0 exito, 1 si el evento no existe
     */
    @Override
    public int create(TipoBoletaDTO data) {
        Optional<Evento> eventoFound = eventoRepo.findById(data.getEventoId());
        if (eventoFound.isEmpty()) return 1; // evento no existe

        TipoBoleta entity = modelMapper.map(data, TipoBoleta.class);
        entity.setEvento(eventoFound.get());
        tipoBoletaRepo.save(entity);
        return 0;
    }

    /**
     * Retorna todos los tipos de boleta del sistema.
     *
     * @return Lista de todos los tipos de boleta
     */
    @Override
    public List<TipoBoletaDTO> getAll() {
        List<TipoBoleta> lista = tipoBoletaRepo.findAll();
        List<TipoBoletaDTO> dtos = new ArrayList<>();
        lista.forEach(tb -> {
            TipoBoletaDTO dto = modelMapper.map(tb, TipoBoletaDTO.class);
            // Mapea el ID del evento manualmente porque ModelMapper no cruza la relacion
            if (tb.getEvento() != null) {
                dto.setEventoId(tb.getEvento().getId());
            }
            dtos.add(dto);
        });
        return dtos;
    }

    /**
     * Elimina un tipo de boleta por su ID.
     *
     * @param id ID del tipo de boleta
     * @return 0 exito, 1 si no existe
     */
    @Override
    public int deleteById(Long id) {
        if (!tipoBoletaRepo.existsById(id)) return 1;
        tipoBoletaRepo.deleteById(id);
        return 0;
    }

    /**
     * Actualiza los datos de un tipo de boleta.
     *
     * @param id      ID del tipo a actualizar
     * @param newData DTO con los nuevos valores
     * @return 0 exito, 2 si no existe el tipo, 1 si el nuevo evento no existe
     */
    @Override
    public int updateById(Long id, TipoBoletaDTO newData) {
        Optional<TipoBoleta> found = tipoBoletaRepo.findById(id);
        if (found.isEmpty()) return 2;

        TipoBoleta entity = found.get();
        entity.setNombre(newData.getNombre());
        entity.setPrecio(newData.getPrecio());
        entity.setCantidadDisponible(newData.getCantidadDisponible());
        entity.setDescripcion(newData.getDescripcion());

        // Si se manda un eventoId diferente, actualiza la relacion
        if (newData.getEventoId() != null) {
            Optional<Evento> eventoFound = eventoRepo.findById(newData.getEventoId());
            if (eventoFound.isEmpty()) return 1;
            entity.setEvento(eventoFound.get());
        }

        tipoBoletaRepo.save(entity);
        return 0;
    }

    /**
     * Obtiene todos los tipos de boleta de un evento especifico.
     *
     * @param eventoId ID del evento
     * @return Lista de tipos de boleta del evento
     */
    public List<TipoBoletaDTO> getByEventoId(Long eventoId) {
        List<TipoBoleta> lista = tipoBoletaRepo.findByEventoId(eventoId);
        List<TipoBoletaDTO> dtos = new ArrayList<>();
        lista.forEach(tb -> {
            TipoBoletaDTO dto = modelMapper.map(tb, TipoBoletaDTO.class);
            dto.setEventoId(eventoId);
            dtos.add(dto);
        });
        return dtos;
    }
}
