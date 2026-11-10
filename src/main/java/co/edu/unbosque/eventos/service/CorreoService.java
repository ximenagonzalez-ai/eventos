package co.edu.unbosque.eventos.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import co.edu.unbosque.eventos.model.Correo;
import co.edu.unbosque.eventos.model.EstadoCorreo;
import co.edu.unbosque.eventos.model.TipoCorreo;
import co.edu.unbosque.eventos.model.Usuario;
import co.edu.unbosque.eventos.repository.CorreoRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CorreoService {

    private final JavaMailSender mailSender;
    private final CorreoRepository correoRepository;

    @Value("${app.mail.from}")
    private String remitente;

    /**
     * Envía un correo real (HTML) al usuario y guarda el registro en la BD.
     * Si el envío falla, NO lanza excepción: guarda el correo como FALLIDO
     * para que, por ejemplo, el registro de un usuario no se caiga por el SMTP.
     */
    public void enviarCorreo(Usuario destinatario, TipoCorreo tipo, String asunto, String cuerpoHtml) {
        Correo correo = Correo.builder()
                .usuario(destinatario)
                .destinatario(destinatario.getEmail())
                .asunto(asunto)
                .cuerpo(cuerpoHtml)
                .tipo(tipo)
                .estado(EstadoCorreo.ENVIADO)
                .build();

        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, "UTF-8");
            helper.setFrom(remitente);
            helper.setTo(destinatario.getEmail());
            helper.setSubject(asunto);
            helper.setText(cuerpoHtml, true); // true = el cuerpo es HTML
            mailSender.send(mensaje);
        } catch (Exception e) {
            log.error("No se pudo enviar el correo a {}", destinatario.getEmail(), e);
            correo.setEstado(EstadoCorreo.FALLIDO);
            String error = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
            correo.setMensajeError(error.length() > 500 ? error.substring(0, 500) : error);
        }

        correoRepository.save(correo);
    }

    /**
     * Correos de un usuario, del más reciente al más antiguo ("ver mis correos").
     */
    public List<Correo> listarPorUsuario(Long usuarioId) {
        return correoRepository.findByUsuarioIdOrderByFechaEnvioDesc(usuarioId);
    }
}