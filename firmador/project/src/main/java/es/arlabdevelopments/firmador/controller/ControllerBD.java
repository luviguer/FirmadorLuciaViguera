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


import java.util.logging.Logger;

@Controller
class ControllerDB {


    Logger logger = Logger.getLogger("Pruebas SpringBoot controllerFirmador");

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private CredencialService credencialService;





////////////////////////////////  Inicio de sesion  ////////////////////////////////////////////////
    @PostMapping("/inicioSesion")
        public String handleInitDB(@RequestParam("typeJson") String typeJson,
                    @RequestParam("jsonData") String jsonData,
                    HttpSession session,
                    Model model) {

          

            //guardar en sesion
            session.setAttribute("jsonData", jsonData);
            session.setAttribute("typeJson", typeJson);

            return "inicioSesion";
            
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
                            Model model) {

        logger.info("Datos recibidos en /auth:");
        logger.info("DNI: " + dni);
        logger.info("Password: " + password);
        logger.info("Confirm Password: " + confirmPassword);

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



        try {
            credencialService.guardarCredencial(nuevoUsuario, typeJson, jsonData);
            logger.info("Credencial guardada correctamente para el nuevo usuario " + dni);
            session.setAttribute("jsonData", null);
            session.setAttribute("typeJson", null);

        } catch (RuntimeException e) {
            logger.warning("Error al guardar la credencial: " + e.getMessage());
            model.addAttribute("error", "Error al guardar la credencial.");
            return "auth";
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
                                    Model model) {

                var usuarioOpt = usuarioService.autenticar(dni, password);
                String jsonData = (String) session.getAttribute("jsonData");
                String typeJson = (String) session.getAttribute("typeJson"); // Usamos esto para decidir el flujo

                logger.info("Datos de sesión: jsonData=" + jsonData + ", typeJson=" + typeJson);

                if (usuarioOpt.isPresent()) {
                    Usuario usuario = usuarioOpt.get();
                    logger.info("Login exitoso para el DNI: " + dni);

                    if (typeJson != null && jsonData != null) {
                        //  Caso 1: crear credencial
                        try {
                            credencialService.guardarCredencial(usuario, typeJson, jsonData);
                            logger.info("Credencial guardada correctamente para el usuario " + dni);
                            session.setAttribute("jsonData", null);
                            session.setAttribute("typeJson", null);

                            return "cargandoCredencial";
                        } catch (RuntimeException e) {
                            logger.info("Error al guardar la credencial: " + e.getMessage());
                            model.addAttribute("error", "Error al guardar la credencial.");
                            return "login";
                        }
                    } else {
                        // Caso 2: ver credenciales
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


                        model.addAttribute("legalPerson", legalPerson);
                        model.addAttribute("terms", terms);
                        model.addAttribute("lrn", lrn);
                        return "muestraCredenciales";
                    }

                } else {
                    logger.info("No se ha encontrado el usuario: " + dni);
                    model.addAttribute("error", "DNI o contraseña incorrectos.");
                    return "login";
                }
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

        int count = 0;
        if (legalPerson != null) {
            session.setAttribute("credencial_legalPerson", legalPerson);
            count++;
        }
        if (terms != null) {
            session.setAttribute("credencial_terms", terms);
            count++;
        }

        if (lrn != null) {
            session.setAttribute("credencial_lrn", terms);
            count++;
        }

        if (count == 3) {
            model.addAttribute("mensaje", "Credenciales encontradas, listo para continuar");
        } else if (count == 2) {
            model.addAttribute("error", "Faltan una de las credenciales.");
        } else if (count == 2) {
            model.addAttribute("error", "Faltan dos de las credenciales.");
        }else{
            model.addAttribute("error", "Faltan todas las credenciales.");
        }

        return "presentacionVerificable";
    }












}
