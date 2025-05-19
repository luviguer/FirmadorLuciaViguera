package es.arlabdevelopments.firmador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;
import es.arlabdevelopments.firmador.model.Usuario; 
import es.arlabdevelopments.firmador.service.UsuarioService; 
import es.arlabdevelopments.firmador.service.CredencialService; 
import org.springframework.ui.Model;

import java.util.logging.Logger;

@Controller
class ControllerDB {


    Logger logger = Logger.getLogger("Pruebas SpringBoot controllerFirmador");

   @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private CredencialService credencialService;


/////////////////////////////  Inicio de sesion ///////////////////////////////////////////
    @GetMapping("/inicioSesion")
        public String handleInitDB(Model model) {
            
            //esto se tendra que imprementar pero actualemte es una prueba 
            String jsonData="prueba";
            String tipo="LegalPerson";

            logger.info("jsonData recibido de mmuestraJWS: " + jsonData);
            logger.info("tipo recibido de mmuestraJWS: " + jsonData);

            model.addAttribute("jsonData",jsonData);
            model.addAttribute("tipo",tipo);

            return "inicioSesion";
            
    }


/////////////////////////////  Auth ///////////////////////////////////////////

    @GetMapping("/auth")
        public String handleAuth(@RequestParam String jsonData,@RequestParam String tipo, Model model) {

            model.addAttribute("jsonData",jsonData);
            model.addAttribute("tipo",tipo);

            return "auth";
            
    }

    @PostMapping("/auth")
        public String handlAuth(
            @RequestParam("dni") String dni,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            @RequestParam String tipo,
            @RequestParam String jsonData, Model model) {

            logger.info("Datos recibidos en /auth:");
            logger.info("DNI: " + dni);
            logger.info("Password: " + password);
            logger.info("Confirm Password: " + confirmPassword);
            logger.info("valor del json: " + jsonData);
            logger.info("valor del tipo: " + tipo);



            if (!password.equals(confirmPassword)) {
                logger.warning("Las contraseñas no coinciden.");
                return "auth"; // Página de error o formulario
            }

            // Verificamos si el usuario ya existe
            Optional<Usuario> existente = usuarioService.autenticar(dni, password);
            if (existente.isPresent()) {
                logger.warning("El usuario ya está registrado.");
                return "auth"; 
            }

            // Registrar usuario
            usuarioService.registrar(dni, password);
            logger.info("Registro exitoso para el DNI: " + dni);
            

            return "menuPrincipal"; 
    
        }

/////////////////////////////  Login ///////////////////////////////////////////


     @GetMapping("/login")
        public String handleLogin(@RequestParam String jsonData,@RequestParam String tipo, Model model) {

            logger.info("jsonData recibido de inicioSesion: " + jsonData);
            logger.info("valor del tipo recibido de inicioSesion: " + tipo);

            model.addAttribute("jsonData",jsonData);
            model.addAttribute("tipo",tipo);

            return "login";
            
    }



    @PostMapping("/login")
    public String handleLoginSubmit(@RequestParam String dni,
                                    @RequestParam String password,
                                    @RequestParam String jsonData,
                                    @RequestParam String tipo,
                                    Model model) {

        logger.info("valor del json para ya guardar: " + jsonData);
        logger.info("valor del tipo para ya guardar: " + tipo);

        var usuarioOpt = usuarioService.autenticar(dni, password);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            logger.info("Loggin exitoso para el DNI: " + dni);

            try {
                credencialService.guardarCredencial(usuario, tipo, jsonData);
                logger.info("Credencial guardada correctamente para el usuario " + dni);
            } catch (RuntimeException e) {
                logger.info("Error al guardar la credencial: " + e.getMessage());
                return "login";
            }

            return "cargandoCredencial";
        } else {
            logger.info("No se ha encontrado: " + dni);
            return "login";
        }
    }


















}
