package es.arlabdevelopments.firmador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.logging.Logger;

@Controller
class ControllerDB {


    Logger logger = Logger.getLogger("Pruebas SpringBoot controllerFirmador");


/////////////////////////////  Inicio de sesion ///////////////////////////////////////////
    @GetMapping("/inicioSesion")
        public String handleInitDB() {

            return "inicioSesion";
            
    }


/////////////////////////////  Auth ///////////////////////////////////////////

    @GetMapping("/auth")
        public String handleAuth() {

            return "auth";
            
    }

    @PostMapping("/auth")
        public String handlAuth(
            @RequestParam("dni") String dni,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword) {
            Logger logger = Logger.getLogger("ControllerFirmador");

            logger.info("Datos recibidos en /auth:");
            logger.info("DNI: " + dni);
            logger.info("Password: " + password);
            logger.info("Confirm Password: " + confirmPassword);

            // Validación simple ejemplo
            if (!password.equals(confirmPassword)) {
                logger.warning("Las contraseñas no coinciden.");
                return "auth"; // volver al formulario si falla
            }

            // Aquí iría la lógica para guardar el usuario (registro)
            logger.info("Registro exitoso para el DNI: " + dni);

            return "menuPrincipal"; 
    
        }

/////////////////////////////  Login ///////////////////////////////////////////


     @GetMapping("/login")
        public String handleLogin() {

            return "login";
            
    }




















}
