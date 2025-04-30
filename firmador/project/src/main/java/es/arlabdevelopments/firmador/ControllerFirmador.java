package es.arlabdevelopments.firmador;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.logging.Level;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.File;
import java.util.Base64;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;
import okhttp3.OkHttpClient;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;

import es.arlabdevelopments.firmador.Libreria;
import java.security.Key;


import java.util.logging.Logger;

@Controller
class ControllerFirmador {


    Logger logger = Logger.getLogger("Pruebas SpringBoot controllerFirmador");

    @Autowired
    private FuncionesAuxiliares faux=new FuncionesAuxiliares();


    //inicio 
    @GetMapping("/")
        public String handleInit() {

            return "menuPrincipal";
            
    }


    @GetMapping("/startLegalPerson")
        public String handleGetStartLegalPerson() {

            return "legalPerson";
            
    }

    @PostMapping("/startLegalPerson")
    public String handlePostStartLegalPerson(
            @RequestParam("verifiableId") String verifiableId,
            @RequestParam("hqCountryCode") String hqCountryCode,
            @RequestParam("hqCountryName") String hqCountryName,
            @RequestParam("lrn") String lrn,
            @RequestParam("lrnValue") String lrnValue,
            @RequestParam("legalCountryCode") String legalCountryCode,
            @RequestParam("legalCountryName") String legalCountryName,
            Model model) {


            try {
                    String jsonGenerado = faux.generarJsonLDLegalPerson(
                        verifiableId, hqCountryCode, hqCountryName, lrn, lrnValue, legalCountryCode, legalCountryName
                    );
            
                    logger.info("\n" + jsonGenerado);
            
                    //model.addAttribute("jsonGenerado", jsonGenerado); // si quieres mandarlo a la vista
            
                    return "peticionDatos";
            
            } catch (JsonProcessingException e) {
                logger.log(Level.SEVERE, "Error generando el JSON-LD: ", e);
                // Puedes devolver una vista de error o redirigir:
                return "error"; // nombre de la vista de error
            } 

    }






    @PostMapping("/upload")
    public String handleUpload(
        @RequestParam("archivo") MultipartFile file,
        @RequestParam("seleccion") String alias,
        @RequestParam("contrasena") String contrasena,
        @RequestParam("json") String json,
        Model model) throws IOException {

            
            if (file.isEmpty()) {
                logger.info("El archivo no se ha subido correctamente.");     
                
            }

            File f = faux.creaFichero(file);  
            model.addAttribute("aliases", Libreria.comprobarAlias(f));
            logger.info("Nombre del fichero: " + f.getName());
            logger.info("Valor del alias: " + alias);
            logger.info("Valor de la contraseña: " + contrasena);
            logger.info("Contenido del fichero: " + Base64.getEncoder().encodeToString(Files.readAllBytes(f.toPath())));

        
              
            Key clavePrivada = Libreria.clave(alias, contrasena, f);
                  /*if (clavePrivada == null) {
                    logger.info("No se pudo obtener la clave privada. Verifica el alias y la contraseña.");

                    model.addAttribute("jsonResponse", json);
                    model.addAttribute("lrn", credecialNumeroDeRegitro);
                    model.addAttribute("tYc", tYc);
                    model.addAttribute("verifiableIdTerminos", verifiableIdTerminos);
                    model.addAttribute("verifiableIdParticipant", verifiableIdParticipant);
                    model.addAttribute("errorMessage", "Contraseña incorrecta");
                    
                
                    return "peticionDatos"; 
                }
                */
        

            // Convertir clave a formato PEM
            String privateKey = "-----BEGIN PRIVATE KEY-----" +
                    Base64.getEncoder().encodeToString(clavePrivada.getEncoded()) +
                    "-----END PRIVATE KEY-----";                   
            logger.info("Valor de la clave privada: " + privateKey);



            

            // Llamar a la API REST para firmar el JSON como JWT
            String jwtResponse = faux.httpPetition(privateKey, json);
            logger.info("Respuesta del firmador (JWT): " + jwtResponse);

            model.addAttribute("jwtResponse", jwtResponse);
            return "muestraJws"; 


    

    }









}


