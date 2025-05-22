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
    private FuncionesAuxiliares faux;


/////////////////////////////  MENU PRINCIPAL ///////////////////////////////////////////
    @GetMapping("/")
        public String handleInit() {

            return "menuPrincipal";
            
    }



/////////////////////////////  LEGAL PERSON  ///////////////////////////////////////////

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
            
                    model.addAttribute("jsonGenerado", jsonGenerado); 
                    model.addAttribute("typeJson","LegalPerson");
            
                    return "peticionDatos";
            
            } catch (JsonProcessingException e) {
                logger.log(Level.SEVERE, "Error generando el JSON-LD: ", e);
                // Puedes devolver una vista de error o redirigir:
                return "error"; // nombre de la vista de error
            } 

    }





/////////////////////////////  TEMINOS Y CONDICIONES  ///////////////////////////////////////////

    @GetMapping("/startTerminosCondiciones")
    public String handleGetStartTerminosCondiciones() {

        return "terminosYcondiciones";
        
    }



    @PostMapping("/startTerminosCondiciones")
    public String handlePostStartTerminosCondiciones(
        @RequestParam("verifiableId") String verifiableId,
        Model model){



            try {
                String jsonGenerado = faux.generarJsonLDTerminosCondiciones(verifiableId);
        
                logger.info("\n" + jsonGenerado);
        
                model.addAttribute("jsonGenerado", jsonGenerado); 
                model.addAttribute("typeJson","TyC");
                return "peticionDatos";
        
        } catch (JsonProcessingException e) {
            logger.log(Level.SEVERE, "Error generando el JSON-LD: ", e);
            return "error"; 
        } 




    }




/////////////////////////////  PRESENTACION VERIFICABLE   ///////////////////////////////////////////

    @GetMapping("/startRequisitos")
    public String handleGetStartRequisitos() {

        return "requisitosPresentacion";
        
    }


    @GetMapping("/startPresentacionVerificable")
    public String handleGetStartPresentacionVerificable() {

        return "presentacionVerificable";
        
    }


/////////////////////////////  PETICION DE DATOS Y LLAMAR A LA API  ///////////////////////////////////////////


    @PostMapping("/upload")
    public String handleUpload(
        @RequestParam("archivo") MultipartFile file,
        @RequestParam("seleccion") String alias,
        @RequestParam("contrasena") String contrasena,
        @RequestParam("jsonGenerado") String jsonGenerado,
        @RequestParam("typeJson") String typeJson,
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
        
    
            // Convertir clave a formato PEM
                               

            //mario
                String privateKey = """
                -----BEGIN PRIVATE KEY-----
                MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDRBL6jVcYoKb4g
                Ip+qC27Hst/Mo7kCvYhDKkN5QNMtYL2rsydRwqfM8nEjeCJ7dhZR4Q7PfGEkQ8K5
                YAhByxE5VjWYYQNVyX4VrUSUEn0IGm5DjEkGmrUPgLOyL3A0Jv/SSovYKxMx6bNO
                Dfvg8MABb7+AtrY3GV+EPCk+T5iDax7uHfxKfpnjz4D8fGMy5bVHW+b1Fu/81j3/
                TmeJPmWfqAocajmlRqrYi7ObxIv2smtk55TaepnvwEatkIExmcyUprUK2T9IudLy
                Bvtyc2/lYdzOqqkKr7XEhmbmoOvPi57L0oxeRpAz7Ydwmod+rUCDKSZbjFYF/9cM
                HNx2PggtAgMBAAECggEBALEpEFxoyzgniVq7fhEm95KT7lUJQDsuYlxrah1P8K45
                nQn3I5CNKKTxqSujG7cBdBGabG84wS13sYhl+RmrAMJUa8DoGWeRDSlaXxISSZ+g
                p2zhbtQGNQka0TRqOPQ7SgH35WgnunFH4A58k80owdV13h8+vlsdSnROebayyFY5
                32SnWahi4e6bvU3mkwe+gBA65bbVIm2JdDtZJLhUyMFsMsm0Y8YMw8jpYAQtSkiN
                c7p/wLCNAp2dOcJ4Y8qsjZxDIv+5d+23h6Zr8PmLfL+gtV+sos1hASaMJHetlAib
                Bx+jP1Xdxma9WJYYXXj7MUP2bmMam0pRv4dvCNZJRBECgYEA/ohrXhEaVL+RlNQk
                SvJFHvAJke/nxg4ZfNBQArufw72Jb8ec4bTa9dJDa9AHHx5xLUbV1YatcyqtmcET
                ZMx5AHOo4nv/oucEu71U71XUE1pe2VZAaOFdAj4ZUWIFvCtjlXTyXG2OngdHUiOj
                lNh5qaas92kIq4kAlrFiD1L/1esCgYEA0jkqboRTUW42PFcTfuKV+Eob3hwVz3++
                YxvMDH6/+bHyHpJXHb+j+q5KAk8d3PkuGzieDl+ElCEw38R+jou6d1ONzKVRzjIJ
                ynjxIgrB4PZP9F7pLeIvZ+1gerlpYJyViehjBY3i0svn95pZKzypMOwec5chQGS2
                y5W1UzSsHEcCgYBRlE29R4QF96RkbB35u261/L9Ee/zwOKKoo2eRiKsrJHuBTRwW
                J04qjaq4SmON8MbbeSGeH11GVT5w0jYyD2sU3v0ZIh8MCjk1JvirAPpI/aT6ya85
                LkoOJvMcZ2tpJQr04xeu0hpswe51ACE02rEb0+UKIyr5N57trYq9WJ/Q4wKBgDa6
                BQ7SSfJn85yPupaMnCgP+uM+gnsLMWARq3QRRx7UsUg+JomrCyBGYSPqvsZ45ATY
                H2V0fkolvdhzCdNIEtnfmYmN/Bbmtd/MzlFjZYeP986RKrj0Kg0vIa+xNvqcqN1G
                7whSIJtp09CEkPQNjaobve2viUt/LIshRRwNGUUfAoGBAMkzlBSJto78fsXL9b8o
                AiUsrSFsO/VnoKbjdxqMn1kn6yv86zTaYPdX6YzGAiSwbwByMuFF+e9k40iftYUD
                tZ29mECTVsO5Qrdxe5/72Zes7T2/cdo9HcvObKDPNgfV7JYRhBM9xF1LeuUXkS8b
                mvgLi0xWVYjiSLzu9a+M1QH0
                -----END PRIVATE KEY-----
                """;

                
            /*String privateKey = "-----BEGIN PRIVATE KEY-----" +
                    Base64.getEncoder().encodeToString(clavePrivada.getEncoded()) +
                    "-----END PRIVATE KEY-----";*/

            logger.info("Valor de la clave privada: " + privateKey);

            

            // Llamar a la API REST para firmar el JSON como JWT
            String jsonData = faux.httpPetitionAPI_REST(privateKey, jsonGenerado);
            logger.info("Respuesta del firmador (JWT): " + jsonData);
            

            model.addAttribute("jsonData", jsonData);
            model.addAttribute("typeJson", typeJson);

            return "muestraJws"; 


    

    }









}


