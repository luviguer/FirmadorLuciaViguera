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
    


    /**
     * Metodo que recibe un fichero, obtiene el certificado y devuelve los alias
     * contenidos en el mismo
     *
     * @param cert Fichero que contiene un certificado PKCS12
     * @return Un array list
     */
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

    /**
     * Metodo que devuelve la clave de un fichero dadas las credenciales
     *
     * @param alias      El alias del certificado
     * @param contrasena La contraseña del certificado
     * @param cert       El fichero que referencia al certificado
     * @return Un objeto tipo Key en caso de que el fichero y las credenciales
     * referenciasen a una clave privada, en caso de que la clave no fuese
     * correcta o no referenciase a una clave privada devolvera <b>null</b>
     * LUCIA: AÑADIR QUE HE COMPROBADO QUE LA CONTRASEÑA SEA INCORRECTA 
     */
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

    

    /**
     * Metodo para obtener una KeyStore recibiendo un fichero
     *
     * @param cert El fichero que contiene el certificado
     * @return La KeyStore contenida en el certificado
     */
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



