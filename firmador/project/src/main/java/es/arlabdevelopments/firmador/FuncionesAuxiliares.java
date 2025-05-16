package es.arlabdevelopments.firmador;

import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class FuncionesAuxiliares {
    

    Logger logger = Logger.getLogger("Pruebas SpringBoot controllerFirmador");


    public String generarJsonLDLegalPerson(
        String verifiableId,
        String hqCountryCode,
        String hqCountryName,
        String lrn,
        String lrnValue,
        String legalCountryCode,
        String legalCountryName) throws JsonProcessingException {

    logger.info("Iniciando generación de JSON-LD para LegalPerson...");

    // Obtener la fecha actual en formato ISO 8601
    String validFrom = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(new Date());

    // Construcción del JSON-LD
    Map<String, Object> jsonLd = new LinkedHashMap<>();
    jsonLd.put("@context", Arrays.asList("https://www.w3.org/ns/credentials/v2", "https://w3id.org/gaia-x/development#"));
    jsonLd.put("id", verifiableId);  
    jsonLd.put("type", Arrays.asList("VerifiableCredential", "gx:LegalPerson"));
    jsonLd.put("issuer", System.getenv("ISSUER"));
    jsonLd.put("validFrom", validFrom);

    // Construcción del credentialSubject
    Map<String, Object> credentialSubject = new LinkedHashMap<>();
    credentialSubject.put("id", verifiableId);

    // Dirección de la sede
    credentialSubject.put("gx:headquartersAddress", Map.of(
            "type", "gx:Address",
            "gx:countryName", legalCountryName,
            "gx:countryCode", legalCountryCode
    ));

    // Construcción dinámica del gx:registrationNumber
    Map<String, Object> registrationEntry = new LinkedHashMap<>();

    // Según el tipo de LRN, ajustamos el campo correspondiente
    switch (lrnValue) {
        case "EORI":
            registrationEntry.put("type", "gx:EORI");
            registrationEntry.put("gx:EORI", lrn);
            break;
        case "VAT_ID":
            registrationEntry.put("type", "gx:VatID");
            registrationEntry.put("gx:vatID", lrn);
            break;
        case "LEI_Code":
            registrationEntry.put("type", "gx:LEI_Code");
            registrationEntry.put("gx:leiCode", lrn);
            break;
        default:
            throw new IllegalArgumentException("Tipo de LRN no soportado: " + lrnValue);
    }

    registrationEntry.put("gx:countryCode", hqCountryCode);  // Agregar el código de país


    // Agregar el número de registro al credentialSubject
    credentialSubject.put("gx:registrationNumber", List.of(registrationEntry));

    // Dirección legal
    credentialSubject.put("gx:legalAddress", Map.of(
            "type", "gx:Address",
            "gx:countryName", legalCountryName,
            "gx:countryCode", legalCountryCode
           
    ));

    jsonLd.put("credentialSubject", credentialSubject);

    // Convertir el mapa a un String JSON usando ObjectMapper
    ObjectMapper objectMapper = new ObjectMapper();
    String jsonResult = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonLd);

    logger.info("JSON-LD generado exitosamente:");
    logger.info("\n" + jsonResult);

    return jsonResult;
}


public String generarJsonLDTerminosCondiciones(String verifiableId) throws JsonProcessingException {
    logger.info("Iniciando generación de JSON-LD para Términos y Condiciones...");

    // Obtener la fecha actual en formato ISO 8601
    String validFrom = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(new Date());

    // Construcción del JSON-LD
    Map<String, Object> jsonLd = new LinkedHashMap<>();
    jsonLd.put("@context", Arrays.asList(
        "https://www.w3.org/ns/credentials/v2",
        "https://w3id.org/gaia-x/development#"
    ));
    jsonLd.put("type", Arrays.asList("VerifiableCredential", "gx:Issuer"));
    jsonLd.put("id", verifiableId);
    jsonLd.put("validFrom", validFrom);
    jsonLd.put("issuer", System.getenv("ISSUER"));

    // Construcción del credentialSubject
    Map<String, Object> credentialSubject = new LinkedHashMap<>();
    credentialSubject.put("id", verifiableId);
    credentialSubject.put("gx:gaiaxTermsAndConditions", System.getenv("TERMS"));

    jsonLd.put("credentialSubject", credentialSubject);

    // Convertir el mapa a un String JSON usando ObjectMapper
    ObjectMapper objectMapper = new ObjectMapper();
    String jsonResult = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonLd);

    logger.info("JSON-LD de Términos y Condiciones generado exitosamente:");
    logger.info("\n" + jsonResult);

    return jsonResult;
}







    public File creaFichero(String file) {
        File f = null;
        try {
            f = File.createTempFile("cert", "p12");
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(Base64.getDecoder().decode(file));
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return f;
    }

    public File creaFichero(MultipartFile file) {
        File f = null;
        try {
            f = File.createTempFile("cert", "p12");
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(file.getBytes());
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return f;
    }


    public String httpPetitionAPI_REST(String pem, String json) {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
    
        RequestBody body = RequestBody.create(mediaType,
            "pem=" + URLEncoder.encode(pem, StandardCharsets.UTF_8) +
            "&json=" + URLEncoder.encode(json, StandardCharsets.UTF_8));
    
        String url = System.getenv("API_PROTOCOL") + "://" +
                     System.getenv("API_HOST") + ":" +
                     System.getenv("API_PORT") + "/" +
                     System.getenv("API_URI");
    
        logger.info("URL API: " + url);
    
        Request request = new Request.Builder()
            .url(url)
            .method("POST", body)
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .build();
    
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Error en la llamada al API: " + response);
            }
            return response.body().string();
        } catch (IOException e) {
            logger.severe("Error al ejecutar la llamada al API: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }



    
    



















}


