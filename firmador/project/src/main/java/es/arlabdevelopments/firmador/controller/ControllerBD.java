package es.arlabdevelopments.firmador.controller;

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
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import es.arlabdevelopments.firmador.model.Credencial;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.ArrayList;
import java.util.List;


import java.util.logging.Logger;

@Controller
class ControllerDB {


    Logger logger = Logger.getLogger("Pruebas SpringBoot controllerFirmador");

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private CredencialService credencialService;



////////////////////////////////  Inicio de sesion  ////////////////////////////////////////////////
    @GetMapping("/inicioSesion")
        public String handleInitDB() {

        
            return "inicioSesion";
            
    }


////////////////////////////////  Inicio de sesion  ////////////////////////////////////////////////
    @GetMapping("/logout")
        public String handleInitlogout(HttpSession session, Model model,RedirectAttributes redirectAttributes) {


            session.setAttribute("usuario", null); 
            redirectAttributes.addFlashAttribute("mensaje", "Sesión cerrada correctamente.");     
            return "redirect:/";
            
    }



/////////////////////////////  Auth ///////////////////////////////////////////

    @GetMapping("/auth")
        public String handleAuth() {       
            return "auth";
            
    }

    @PostMapping("/auth")
    public String handleAuth(@RequestParam("dni") String dni,
                            @RequestParam("password") String password,
                            @RequestParam("confirmPassword") String confirmPassword,
                            HttpSession session,
                            Model model,RedirectAttributes redirectAttributes) {

        logger.info("Datos recibidos en /auth:");
        logger.info("DNI: " + dni);
        logger.info("Password: " + password);
        logger.info("Confirm Password: " + confirmPassword);


        if (!dni.matches("\\d{8}[A-HJ-NP-TV-Z]")) {
        model.addAttribute("error", "El formato del DNI no es válido. Debe tener 8 números seguidos de una letra.");
        return "auth";
    }

        // Validar que las contraseñas coincidan
        if (!password.equals(confirmPassword)) {
            logger.info("Las contraseñas no coinciden.");
            model.addAttribute("error", "Las contraseñas no coinciden.");
            return "auth";
        }

        // Verificar si el usuario ya existe
        Optional<Usuario> existente = usuarioService.buscarPorIdentificador(dni);
        if (existente.isPresent()) {
            logger.info("El usuario ya está registrado.");
            model.addAttribute("error", "El usuario ya está registrado.");
            return "auth";
        }

        // Registrar nuevo usuario
        Usuario nuevoUsuario = usuarioService.registrar(dni, password);
        logger.info("Registro exitoso para el DNI: " + dni);

        // Obtener datos de sesión
        String jsonData = (String) session.getAttribute("jsonData");
        String typeJson = (String) session.getAttribute("typeJson");
        logger.info("Los datos de sesion:" + jsonData + "," +typeJson);

        //guardo al usuario en sesion
        session.setAttribute("usuario", nuevoUsuario); 

                    // Caso 1: guardar credenciales

        if (typeJson != null && jsonData != null) {


                return "redirect:/guardarCredencial";

        } else if("verCredenical".equals(session.getAttribute("vista"))){

                        session.setAttribute("vista",null); 
                        return "redirect:/verCredenciales";

        } else if(!"verCredenical".equals(session.getAttribute("vista"))){
                        
            session.setAttribute("vista",null); 
            redirectAttributes.addFlashAttribute("mensaje", "Registro exitoso.");
             return "redirect:/";
            }

        return "cargandoCredencial";
    }

            

    /////////////////////////////  Login ///////////////////////////////////////////


        @GetMapping("/login")
            public String handleLogin() {

                return "login";
                
        }



        @PostMapping("/login")
             public String handleLoginSubmit(@RequestParam String dni,
                                    @RequestParam String password,
                                    HttpSession session,
                                    Model model,RedirectAttributes redirectAttributes) {

                var usuarioOpt = usuarioService.autenticar(dni, password);
                String jsonData = (String) session.getAttribute("jsonData");
                String typeJson = (String) session.getAttribute("typeJson"); 

                logger.info("Datos de sesión: jsonData=" + jsonData + ", typeJson=" + typeJson);

                if (usuarioOpt.isPresent()) {

                    logger.info("Login exitoso para el DNI: " + dni);
                    Usuario usuario = usuarioOpt.get();

                    //guardo al usuario en sesion
                    session.setAttribute("usuario", usuario); 

                    // Caso 1: guardar credenciales

                    if (typeJson != null && jsonData != null) {


                        return "redirect:/guardarCredencial";

                    } else if("verCredenical".equals(session.getAttribute("vista"))){

                        session.setAttribute("vista",null); 
                        return "redirect:/verCredenciales";

                    } else if(!"verCredenical".equals(session.getAttribute("vista"))){
                        
                        session.setAttribute("vista",null);
                        redirectAttributes.addFlashAttribute("mensaje", "Inicio de sesión exitosa.");
                        return "redirect:/";

        }

                } else {
                    logger.info("No se ha encontrado el usuario: " + dni);
                    model.addAttribute("error", "DNI o contraseña incorrectos.");
                    return "login";
        }
        return "login";
 }




///////////////////////////// GUARADR CREDENCIAL ////////////////////////////////////////////


    @PostMapping("/guardarCredencial")
        public String guardarCredencial(HttpSession session, Model model,@RequestParam("typeJson") String typeJson,
                    @RequestParam("jsonData") String jsonData) {

             
            session.setAttribute("jsonData", jsonData);
            session.setAttribute("typeJson", typeJson);
            
        return "redirect:/guardarCredencial";            
    }




    @GetMapping("/guardarCredencial")
    public String guardarCredencial(HttpSession session, Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        //compruebo si el usuario esta en sesion
        if (usuario == null) {
            return "redirect:/inicioSesion";
        }

       String jsonData = (String) session.getAttribute("jsonData");
       String typeJson = (String) session.getAttribute("typeJson"); 

        logger.info("Datos de sesión: jsonData=" + jsonData + ", typeJson=" + typeJson);


            try {
                credencialService.guardarCredencial(usuario, typeJson, jsonData);
                session.setAttribute("jsonData", null);
                session.setAttribute("typeJson", null);

                return "cargandoCredencial";

            } catch (RuntimeException e) {
                logger.info("Error al guardar la credencial: " + e.getMessage());
                model.addAttribute("error", "Error al guardar la credencial, ya la tienes guardada.");
                return "login";
            }
        
    }


//////////////////////////   VER CREDENCIALES  /////////////////////////////////////////////
    @GetMapping("/verCredenciales")
    public String verCredenciales(HttpSession session, Model model) {

            // Obtener el usuario de la sesión
            Usuario usuario = (Usuario) session.getAttribute("usuario");

            

            //compruebo si el usuario esta en sesion

            if (usuario == null) {

                //cambiar el tipo para redirigir posteriormente
                session.setAttribute("vista","verCredenical");
                return "redirect:/inicioSesion";
            }

          

            // Obtener las credenciales del servicio
            List<Credencial> credenciales = credencialService.obtenerCredencialesPorUsuario(usuario);
            
            // bool para saber si no hay ninguna credencial
            boolean sinCredenciales = credenciales == null || credenciales.isEmpty();

            String legalPerson = null;
            String terms = null;
            String lrn = null;

            for (Credencial c : credenciales) {
                System.out.println("Encontrada credencial tipo: " + c.getTipo().name());

                if (c.getTipo().name().equalsIgnoreCase("LegalPerson")) {
                    legalPerson = c.getContenidoJson();
                    System.out.println("Asignada a legalPerson");
                } else if (c.getTipo().name().equalsIgnoreCase("TyC")) {
                    terms = c.getContenidoJson();
                    System.out.println("Asignada a terms");
                } else if (c.getTipo().name().equalsIgnoreCase("LRN")) {
                    lrn = c.getContenidoJson();
                    System.out.println("Asignada a lrn");
                }
            }

            // Pasar datos al modelo para mostrarlos en la vista
            model.addAttribute("legalPerson", legalPerson);
            model.addAttribute("terms", terms);
            model.addAttribute("lrn", lrn);
            model.addAttribute("sinCredenciales", sinCredenciales);


            return "muestraCredenciales";
        }











////////////////// BUSCAR CREDENCIALES PARA PRESENTACION   ///////////////////////////////////


    @PostMapping("/buscarCredenciales")
    public String buscarCredenciales(@RequestParam String dni,
                                    @RequestParam String password,
                                    HttpSession session,
                                    Model model) {

         var usuarioOpt = usuarioService.autenticar(dni, password);

        if (usuarioOpt.isEmpty()) {
            model.addAttribute("error", "DNI o contraseña incorrectos.");
            return "presentacionVerificable";
        }

        Usuario usuario = usuarioOpt.get();
        List<Credencial> credenciales = credencialService.obtenerCredencialesPorUsuario(usuario);

        String legalPerson = null;
        String terms = null;
        String lrn = null;

        for (Credencial c : credenciales) {
            System.out.println("Encontrada credencial tipo: " + c.getTipo().name());

            if (c.getTipo().name().equalsIgnoreCase("LegalPerson")) {
                legalPerson = c.getContenidoJson();
                System.out.println("Asignada a legalPerson");
            } else if (c.getTipo().name().equalsIgnoreCase("TyC")) {
                terms = c.getContenidoJson();
                System.out.println("Asignada a terms");
            } else if (c.getTipo().name().equalsIgnoreCase("LRN")) {
                lrn = c.getContenidoJson();
                System.out.println("Asignada a lrn");
            }
        }

        // Lista para llevar seguimiento de faltantes
        List<String> faltantes = new ArrayList<>();

        if (legalPerson != null) {
            session.setAttribute("credencial_legalPerson", legalPerson);
        } else {
            faltantes.add("LegalPerson");
        }

        if (terms != null) {
            session.setAttribute("credencial_terms", terms);
        } else {
            faltantes.add("Términos y Condiciones");
        }

        if (lrn != null) {
            session.setAttribute("credencial_lrn", lrn);
        } else {
            faltantes.add("Número de Registro Legal");
        }

        if (faltantes.isEmpty()) {
            model.addAttribute("mensaje", "Credenciales encontradas, listo para continuar");
        } else {
            String mensajeError = "Faltan las siguientes credenciales: " + String.join(", ", faltantes) + ".";
            model.addAttribute("error", mensajeError);
        }

        return "presentacionVerificable";
    }












}
