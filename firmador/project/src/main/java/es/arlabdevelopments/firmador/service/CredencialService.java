package es.arlabdevelopments.firmador.service;

import es.arlabdevelopments.firmador.model.Credencial;
import es.arlabdevelopments.firmador.model.Usuario;
import es.arlabdevelopments.firmador.model.TipoCredencial;
import es.arlabdevelopments.firmador.repository.CredencialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CredencialService {

    @Autowired
    private CredencialRepository credencialRepo;

    public Credencial guardarCredencial(Usuario usuario, String tipo, String contenidoJson) {
        Credencial credencial = new Credencial();
        try {
            TipoCredencial tipoEnum = TipoCredencial.valueOf(tipo.trim());
            credencial.setTipo(tipoEnum);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Tipo de credencial inválido: " + tipo);
        }

        credencial.setContenidoJson(contenidoJson);
        credencial.setUsuario(usuario);
        return credencialRepo.save(credencial);
    }

    public List<Credencial> obtenerCredencialesPorUsuario(Usuario usuario) {
        return credencialRepo.findByUsuario_Id(usuario.getId());
    }



}
