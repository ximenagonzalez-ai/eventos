package co.edu.unbosque.eventos.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unbosque.eventos.dto.BoletaDTO;
import co.edu.unbosque.eventos.dto.CompraDTO;
import co.edu.unbosque.eventos.model.Boleta;
import co.edu.unbosque.eventos.model.Compra;
import co.edu.unbosque.eventos.model.TipoBoleta;
import co.edu.unbosque.eventos.model.TipoCorreo;
import co.edu.unbosque.eventos.model.Usuario;
import co.edu.unbosque.eventos.repository.BoletaRepository;
import co.edu.unbosque.eventos.repository.TipoBoletaRepository;
import co.edu.unbosque.eventos.repository.UsuarioRepository;

@Service
public class BoletaService {

    private final BoletaRepository boletaRepository;
    private final TipoBoletaRepository tipoBoletaRepository;
    private final UsuarioRepository usuarioRepository;
    private final CompraService compraService;
    private final AuditoriaService auditoriaService;
    private final CorreoService correoService;

    public BoletaService(BoletaRepository boletaRepository, TipoBoletaRepository tipoBoletaRepository,
            UsuarioRepository usuarioRepository, CompraService compraService, AuditoriaService auditoriaService,
            CorreoService correoService) {
        this.boletaRepository = boletaRepository;
        this.tipoBoletaRepository = tipoBoletaRepository;
        this.usuarioRepository = usuarioRepository;
        this.compraService = compraService;
        this.auditoriaService = auditoriaService;
        this.correoService = correoService;
    }

    @Transactional
    public List<BoletaDTO> comprar(Long usuarioId, Long tipoBoletaId, Integer cantidad) {
        if (cantidad == null || cantidad < 1) {
            throw new IllegalArgumentException("La cantidad debe ser al menos 1");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + usuarioId));
        TipoBoleta tipo = tipoBoletaRepository.findById(tipoBoletaId)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de boleta no encontrado: " + tipoBoletaId));

        if (tipo.getCantidadDisponible() < cantidad) {
            throw new IllegalArgumentException("No hay suficientes boletas disponibles");
        }

        BigDecimal precio = BigDecimal.valueOf(tipo.getPrecio());
        BigDecimal total = precio.multiply(BigDecimal.valueOf(cantidad));

        Compra compra = compraService.crear(usuario, total);

        tipo.setCantidadDisponible(tipo.getCantidadDisponible() - cantidad);
        tipoBoletaRepository.save(tipo);

        List<BoletaDTO> respuesta = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            Boleta boleta = new Boleta();
            boleta.setCompra(compra);
            boleta.setTipoBoleta(tipo);
            boleta.setPrecioPagado(precio);
            boleta = boletaRepository.save(boleta);
            respuesta.add(BoletaDTO.convertirABoletaDTO(boleta));
        }

        compraService.completar(compra.getId());
        auditoriaService.registrar(usuario, "COMPRAR", "Boleta", compra.getId(),
                cantidad + " boleta(s) de tipo " + tipo.getNombre());

        enviarCorreoConfirmacion(usuario, tipo, total, respuesta);

        System.out.println("Boleta comprada: compra " + compra.getId() + ", " + cantidad + " boleta(s)");
        return respuesta;
    }

    @Transactional(readOnly = true)
    public List<BoletaDTO> listarPorCompra(Long compraId) {
        List<BoletaDTO> respuesta = new ArrayList<>();
        for (Boleta boleta : boletaRepository.findByCompraId(compraId)) {
            respuesta.add(BoletaDTO.convertirABoletaDTO(boleta));
        }
        return respuesta;
    }

    @Transactional
    public CompraDTO cancelarCompra(Long compraId) {
        List<Boleta> boletas = boletaRepository.findByCompraId(compraId);
        Compra compra = compraService.cancelar(compraId);
        for (Boleta boleta : boletas) {
            TipoBoleta tipo = boleta.getTipoBoleta();
            tipo.setCantidadDisponible(tipo.getCantidadDisponible() + 1);
            tipoBoletaRepository.save(tipo);
        }
        return CompraDTO.convertirACompraDTO(compra);
    }

    private void enviarCorreoConfirmacion(Usuario usuario, TipoBoleta tipo, BigDecimal total,
            List<BoletaDTO> boletas) {
        StringBuilder html = new StringBuilder();
        html.append("<h2>Gracias por tu compra</h2>");
        html.append("<p>Evento: ").append(tipo.getEvento().getNombre()).append("</p>");
        html.append("<p>Tipo de boleta: ").append(tipo.getNombre()).append("</p>");
        html.append("<p>Total pagado: ").append(total).append("</p>");
        html.append("<p>Tus códigos de boleta:</p><ul>");
        for (BoletaDTO boleta : boletas) {
            html.append("<li>").append(boleta.getCodigo()).append("</li>");
        }
        html.append("</ul>");

        correoService.enviarCorreo(usuario, TipoCorreo.CONFIRMACION_COMPRA,
                "Confirmación de tu compra - " + tipo.getEvento().getNombre(), html.toString());
    }
}