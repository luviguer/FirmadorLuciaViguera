package es.arlabdevelopments.firmador.repository;

import es.arlabdevelopments.firmador.model.Credencial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CredencialRepository extends JpaRepository<Credencial, Long> {
    List<Credencial> findByUsuario_Id(Long usuarioId);
}




 