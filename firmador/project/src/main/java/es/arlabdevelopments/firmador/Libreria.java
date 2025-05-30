package es.arlabdevelopments.firmador;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Enumeration;

public class Libreria {
    


  
    public static ArrayList<String> comprobarAlias(File cert) {
        KeyStore ks = obtenerKeyStore(cert);

        Enumeration<String> enumer;
        ArrayList<String> lista = new ArrayList<String>();

        try {
            enumer = ks.aliases();
            while (enumer.hasMoreElements()) {
                String s = enumer.nextElement();
                lista.add(s);
            }

        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return lista;
    }

   
    public static Key clave(String alias, String contrasena, File cert) {
        Key k = null;
        try (FileInputStream fis = new FileInputStream(cert)) {
            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(fis, contrasena.toCharArray()); 
            k = ks.getKey(alias, contrasena.toCharArray());
            if (!(k instanceof PrivateKey)) {
                System.err.println("El alias no referencia a una clave privada.");
                return null;
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo del certificado: " + e.getMessage());
        } catch (KeyStoreException e) {
            System.err.println("Error con el almacén de claves: " + e.getMessage());
        } catch (NoSuchAlgorithmException | UnrecoverableKeyException e) {
            System.err.println("Error al recuperar la clave: Verifica la contraseña o el alias.");
        } catch (CertificateException e) {
            System.err.println("Certificado inválido: " + e.getMessage());
        }
        return k;
    }

    

   
    private static KeyStore obtenerKeyStore(File cert) {
        KeyStore ks = null;
        try (FileInputStream fis = new FileInputStream(cert)) {
            ks = KeyStore.getInstance("PKCS12");
            ks.load(fis, null);
        } catch (FileNotFoundException ex) {

        } catch (IOException ex) {

        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException ex) {
            System.err.println("Problemas con el certificado");
        }
        return ks;
    }
}



