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
import SerializedObjects.coreObjects.Prenotazione;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

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
            ScontiCore.Initialize();

            if (args.length > 0) {
                if ("first".equals(args[0])) {
                    VeicoliCore.addVeicolo(new Veicolo(1234, 100, 37.526049, 15.071718));
                    VeicoliCore.addVeicolo(new Veicolo(2134, 30, 37.522875, 15.069183));
                    VeicoliCore.addVeicolo(new Veicolo(1111, 20, 37.521806, 15.070042));
                    VeicoliCore.addVeicolo(new Veicolo(1231, 80, 37.520539, 15.069301));
                    VeicoliCore.addVeicolo(new Veicolo(9832, 70, 37.521252, 15.067478));
                    ScontiCore.addSconto(new Sconto("ABCD", 10));
                }
            }

        } catch (IOException ex) {
            System.out.println(ex);
        }

    }

}
