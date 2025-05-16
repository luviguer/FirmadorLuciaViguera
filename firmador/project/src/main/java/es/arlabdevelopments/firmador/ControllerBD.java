package es.arlabdevelopments.firmador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
class ControllerDB {




/////////////////////////////  Inicio de sesion ///////////////////////////////////////////
    @GetMapping("/inicioSesion")
        public String handleInit() {

            return "inicioSesion";
            
    }

























}