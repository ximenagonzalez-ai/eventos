package co.edu.unbosque.eventos.service;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unbosque.eventos.model.Evento;
import co.edu.unbosque.eventos.model.Itinerario;
import co.edu.unbosque.eventos.model.ItinerarioItem;
import co.edu.unbosque.eventos.repository.ItinerarioItemRepository;
import co.edu.unbosque.eventos.repository.ItinerarioRepository;

@Service
public class PdfService {

    private final ItinerarioRepository itinerarioRepository;
    private final ItinerarioItemRepository itinerarioItemRepository;

    public PdfService(ItinerarioRepository itinerarioRepository,
            ItinerarioItemRepository itinerarioItemRepository) {
        this.itinerarioRepository = itinerarioRepository;
        this.itinerarioItemRepository = itinerarioItemRepository;
    }

    @Transactional(readOnly = true)
    public byte[] generarItinerario(Long itinerarioId) {
        Itinerario itinerario = itinerarioRepository.findById(itinerarioId)
                .orElseThrow(() -> new IllegalArgumentException("Itinerario no encontrado: " + itinerarioId));
        List<ItinerarioItem> items = itinerarioItemRepository.findByItinerarioId(itinerarioId);

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        List<String> lineas = new ArrayList<>();

        lineas.add("ITINERARIO: " + itinerario.getNombre());
        lineas.add("Usuario: " + itinerario.getUsuario().getNombre());
        lineas.add(" ");

        if (items.isEmpty()) {
            lineas.add("Este itinerario todavia no tiene eventos.");
        }

        int numero = 1;
        for (ItinerarioItem item : items) {
            Evento evento = item.getEvento();
            lineas.add(numero + ". " + evento.getNombre());
            if (evento.getFechaInicio() != null) {
                lineas.add("   Fecha: " + evento.getFechaInicio().format(formato));
            }
            if (evento.getDescripcion() != null) {
                String descripcion = evento.getDescripcion().replace("\n", " ").replace("\r", " ");
                if (descripcion.length() > 400) {
                    descripcion = descripcion.substring(0, 400) + "...";
                }
                agregarTexto(lineas, "   " + descripcion, 85);
            }
            lineas.add(" ");
            numero++;
        }

        return crearPdf(lineas);
    }

    // Parte un texto largo en varias lineas de maximo "ancho" caracteres
    private void agregarTexto(List<String> lineas, String texto, int ancho) {
        String[] palabras = texto.split(" ");
        StringBuilder actual = new StringBuilder();
        for (String palabra : palabras) {
            if (actual.length() + palabra.length() + 1 > ancho) {
                lineas.add(actual.toString());
                actual = new StringBuilder("   ");
            }
            actual.append(palabra).append(" ");
        }
        if (actual.length() > 0) {
            lineas.add(actual.toString());
        }
    }

    // Quita caracteres que rompen el PDF y los que no existen en Latin-1
    private String escapar(String texto) {
        StringBuilder limpio = new StringBuilder();
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            if (c == '\\' || c == '(' || c == ')') {
                limpio.append('\\').append(c);
            } else if (c > 255) {
                limpio.append('?');
            } else {
                limpio.append(c);
            }
        }
        return limpio.toString();
    }

    private byte[] crearPdf(List<String> lineas) {
        int lineasPorPagina = 45;
        int paginas = (lineas.size() + lineasPorPagina - 1) / lineasPorPagina;
        if (paginas == 0) {
            paginas = 1;
        }

        int total = 3 + paginas * 2;
        int[] posiciones = new int[total + 1];
        StringBuilder pdf = new StringBuilder("%PDF-1.4\n");

        posiciones[1] = pdf.length();
        pdf.append("1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n");

        StringBuilder hijos = new StringBuilder();
        for (int i = 0; i < paginas; i++) {
            hijos.append(4 + 2 * i).append(" 0 R ");
        }
        posiciones[2] = pdf.length();
        pdf.append("2 0 obj\n<< /Type /Pages /Kids [").append(hijos).append("] /Count ")
                .append(paginas).append(" >>\nendobj\n");

        posiciones[3] = pdf.length();
        pdf.append("3 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica /Encoding /WinAnsiEncoding >>\nendobj\n");

        for (int i = 0; i < paginas; i++) {
            int numPagina = 4 + 2 * i;
            int numContenido = 5 + 2 * i;

            posiciones[numPagina] = pdf.length();
            pdf.append(numPagina).append(" 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] ")
                    .append("/Resources << /Font << /F1 3 0 R >> >> /Contents ")
                    .append(numContenido).append(" 0 R >>\nendobj\n");

            StringBuilder contenido = new StringBuilder("BT\n/F1 11 Tf\n14 TL\n50 790 Td\n");
            int inicio = i * lineasPorPagina;
            int fin = Math.min(inicio + lineasPorPagina, lineas.size());
            for (int j = inicio; j < fin; j++) {
                contenido.append("(").append(escapar(lineas.get(j))).append(") Tj\nT*\n");
            }
            contenido.append("ET");

            posiciones[numContenido] = pdf.length();
            pdf.append(numContenido).append(" 0 obj\n<< /Length ").append(contenido.length())
                    .append(" >>\nstream\n").append(contenido).append("\nendstream\nendobj\n");
        }

        int inicioXref = pdf.length();
        pdf.append("xref\n0 ").append(total + 1).append("\n");
        pdf.append("0000000000 65535 f \n");
        for (int k = 1; k <= total; k++) {
            pdf.append(String.format("%010d 00000 n \n", posiciones[k]));
        }
        pdf.append("trailer\n<< /Size ").append(total + 1).append(" /Root 1 0 R >>\n");
        pdf.append("startxref\n").append(inicioXref).append("\n%%EOF");

        return pdf.toString().getBytes(StandardCharsets.ISO_8859_1);
    }
}