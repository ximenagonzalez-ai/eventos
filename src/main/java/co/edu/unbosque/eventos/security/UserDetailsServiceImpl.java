package co.edu.unbosque.eventos.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import co.edu.unbosque.eventos.repository.UsuarioRepository;

/**
 * Implementación de UserDetailsService para nuestro proyecto.
 *
 * Spring Security llama a este servicio cuando alguien intenta autenticarse.
 * Le pasa el "username" (en nuestro caso el email) y nosotros buscamos
 * al usuario en la base de datos.
 *
 * También se llama desde JwtAuthenticationFilter en cada petición
 * para cargar los datos del usuario a partir del email guardado en el token.
 *
 * Si el usuario no existe, lanzamos UsernameNotFoundException y Spring Security
 * devuelve automáticamente un 401 Unauthorized.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UserDetailsServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Carga el usuario por su email. Spring Security llama a este método
     * pasando lo que el usuario escribió en el campo "email" del login.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Usuario no encontrado con email: " + email));
    }
}
