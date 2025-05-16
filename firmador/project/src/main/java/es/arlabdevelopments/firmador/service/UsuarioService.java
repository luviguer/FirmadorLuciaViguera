package es.arlabdevelopments.firmador.service;


import es.arlabdevelopments.firmador.model.Usuario;
import es.arlabdevelopments.firmador.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario registrar(String identificador, String rawPassword) {
        Usuario usuario = new Usuario();
        usuario.setIdentificador(identificador);
        usuario.setPassword(passwordEncoder.encode(rawPassword));
        return usuarioRepo.save(usuario);
    }

    public Optional<Usuario> autenticar(String identificador, String rawPassword) {
        return usuarioRepo.findByIdentificador(identificador)
                .filter(u -> passwordEncoder.matches(rawPassword, u.getPassword()));
    }
    public Usuario guardarUsuario(Usuario usuario) {
        return usuarioRepo.save(usuario); 
    }
}
