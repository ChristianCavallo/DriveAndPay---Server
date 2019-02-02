/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package drivepay.server;

import Connection.Server;
import Connection.TestClient;
import Management.Core.ScontiCore;
import Management.Core.VeicoliCore;
import Management.Database.DB;
import SerializedObjects.Command;
import SerializedObjects.CommandsEnum.CommandType;
import SerializedObjects.UserObjects.CartaCredito;
import SerializedObjects.UserObjects.Documento;
import SerializedObjects.UserObjects.InformazioniUtente;
import SerializedObjects.UserObjects.Utente;
import SerializedObjects.coreObjects.Noleggio;
import SerializedObjects.coreObjects.Veicolo;
import static drivepay.server.DrivePayServer.main;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.Month;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import SerializedObjects.coreObjects.Sconto;
/**
 *
 * @author Christian
 */
public class DrivePayServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Inizializzazione server...");
        try {
            Server server = new Server(9696);
            Thread x = new Thread(server);
            x.start();
            System.out.println("Server pronto nella porta 9696!");
            VeicoliCore.Initialize();
            /*VeicoliCore.addVeicolo(new Veicolo(1234,100, 37.526049, 15.071718));
            VeicoliCore.addVeicolo(new Veicolo(2134, 30, 37.522875, 15.069183));
            VeicoliCore.addVeicolo(new Veicolo(1111, 20, 37.521806, 15.070042));
            VeicoliCore.addVeicolo(new Veicolo(1231, 80, 37.520539, 15.069301));
            VeicoliCore.addVeicolo(new Veicolo(9832, 70, 37.521252, 15.067478));*/
            /*
            Utente u = new Utente(798379828, "chrychry96@gmail.com", "12345");
            Noleggio n;
            try {
               n = VeicoliCore.richiediNoleggio(u);
               Veico v = new Veicolo(1111);
               n.setUtenteCorrente(u);
               n.setVeicoloCorrente(v);
               
               System.out.println("Noleggi effettuato ->" + VeicoliCore.noleggiaVeicolo(n));
             } catch (Exception ex) {
                Logger.getLogger(DrivePayServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            */
            
            ScontiCore.Initialize();
            //ScontiCore.addSconto(new Sconto("ABCD", 15));



            /*
                TestClient testClient = new TestClient("127.0.0.1", 9696);
                testClient.SendCommand(new Command(CommandType.HANDSHAKE_REQUEST, ""));
                Documento d = new Documento(1, "jpg");
                File fi = new File("test.jpg");
                byte[] fileContent = Files.readAllBytes(fi.toPath());
                d.setRaw(fileContent);
                testClient.SendCommand(new Command(CommandType.UPLOAD_DOCUMENT_REQUEST, d));*/
                
                /*
                Utente u = new Utente("chry@gmail.com", "12345");
                InformazioniUtente iu = new InformazioniUtente("Christian", "Cavallo", LocalDate.of(1996, Month.OCTOBER, 2), "CVLCRS96R02C351I");
                Documento carta = new Documento(1, "file.png");
                Documento patente = new Documento(2, "file2.png");
                CartaCredito cr = new CartaCredito("123456789012", "123");
                iu.setCartaIdentita(carta);
                iu.setPatente(patente);
                iu.setCarta(cr);
                u.setInfoUtente(iu);
                
                System.out.println(DB.addUserToDB(u));
            */        
        } catch (IOException ex) {
            System.out.println(ex);
        }

    }

  

}
