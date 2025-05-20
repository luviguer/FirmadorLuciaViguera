//importacion de el framework Express, que se usa para crear serbidores web en Node.js
import express from 'express';
import bodyParser from 'body-parser';
import { SignJWT, importPKCS8 } from 'jose';
import https from 'https';
import fs from 'fs';


//cargamos privkey y certificate
let privateKey  = process.env.PRIVATEKEY;
let certificate = process.env.CERTIFICATE;
console.log("CERTIFICATE:", certificate);
console.log("PRIVATE KEY:", privateKey);


var credentials = {key: privateKey, cert: certificate};
// Aquí monto el API REST para crear el JWS según la petición que reciba
const app = express();
app.use(express.json());
app.use(bodyParser.urlencoded({ extended: false }));
var httpsServer = https.createServer(credentials, app);



//Puerto donde escuchará el servidor
const PORT_HTTPS = 2002;



//Lanzamos el servidor HTTPS
httpsServer.listen(PORT_HTTPS,() => {
  console.log("Server HTTPS Listening on PORT:", PORT_HTTPS);
});


// Función que firma un VC como JWT (vc+jwt)
async function signVCasJWT(vcPayload, pem, issuer) {
    const privateKey = await importPKCS8(pem, 'PS256');
  
    return await new SignJWT(vcPayload)
      .setProtectedHeader({
        alg: 'PS256',
        typ: 'vc+jwt',
        cty: 'vc',
        iss: '${issuer}',
        kid: '${issuer}#X509-JWK2020'
      })
      .setIssuedAt()
      .setIssuer(issuer)
      .sign(privateKey);
  }


// Ruta POST para recibir VC y clave privada, y devolver la VC firmada como JWT
app.post('/jws', async (req, res) => {
    try {
      const verifiableCredential = JSON.parse(req.body.json);
      const pem = req.body.pem.trim();
      const issuer = 'did:web:arlabdevelopments.com';
  
      const signedJWT = await signVCasJWT(verifiableCredential, pem, issuer);
      res.send({ jwt: signedJWT });
    } catch (err) {
      console.error("Error firmando el VC:", err);
      res.status(500).send({ error: 'Error al firmar la credencial.' });
    }
  });





  