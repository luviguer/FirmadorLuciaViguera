package es.arlabdevelopments.firmador.repository;

import es.arlabdevelopments.firmador.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
 
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByIdentificador(String identificador);
}
