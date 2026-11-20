package co.edu.unbosque.eventos.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unbosque.eventos.model.Evento;
import co.edu.unbosque.eventos.model.TipoBoleta;
import co.edu.unbosque.eventos.repository.EventoRepository;
import co.edu.unbosque.eventos.repository.TipoBoletaRepository;

/**
 * Crea tipos de boleta por defecto (General y VIP) para los eventos que
 * no tienen ninguno, por ejemplo los importados desde Ticketmaster.
 * Los precios son de ejemplo (en pesos colombianos) y se pueden cambiar despues.
 */
@Service
public class BoletaPorDefectoService {

    private static final double PRECIO_GENERAL = 120000.0;
    private static final double PRECIO_VIP = 250000.0;
    private static final int CUPOS_GENERAL = 500;
    private static final int CUPOS_VIP = 100;

    @Autowired
    private EventoRepository eventoRepo;

    @Autowired
    private TipoBoletaRepository tipoBoletaRepo;

    public BoletaPorDefectoService() {}

    /**
     * Agrega General y VIP a todos los eventos que no tengan tipos de boleta.
     *
     * @return cantidad de eventos a los que se les crearon boletas
     */
    @Transactional
    public int generarFaltantes() {
        int eventosActualizados = 0;
        List<Evento> eventos = eventoRepo.findAll();

        for (Evento evento : eventos) {
            if (tipoBoletaRepo.findByEventoId(evento.getId()).isEmpty()) {
                tipoBoletaRepo.save(new TipoBoleta(
                        "General", PRECIO_GENERAL, CUPOS_GENERAL,
                        "Entrada general", evento));
                tipoBoletaRepo.save(new TipoBoleta(
                        "VIP", PRECIO_VIP, CUPOS_VIP,
                        "Acceso preferencial", evento));
                eventosActualizados++;
            }
        }
        return eventosActualizados;
    }
}
