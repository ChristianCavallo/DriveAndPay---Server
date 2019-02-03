/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Connection.Interpreter;

import SerializedObjects.Command;
import Connection.Connection;
import Management.Core.ScontiCore;
import Management.Core.VeicoliCore;
import Management.Database.DB;
import Management.LoginSystem.LoginService;

import SerializedObjects.CommandsEnum.CommandType;
import SerializedObjects.UserObjects.Documento;
import SerializedObjects.UserObjects.Utente;
import SerializedObjects.coreObjects.Fattura;
import SerializedObjects.coreObjects.Noleggio;
import SerializedObjects.coreObjects.Sconto;
import SerializedObjects.coreObjects.Veicolo;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Christian
 */
public class CommandsEngine {

    private final Connection connection;

    public CommandsEngine(Connection sock) {
        this.connection = sock;

    }

    public void executeCommand(Command cmd) {

        switch (cmd.getType()) {
            case HANDSHAKE_REQUEST:
                sendResponse(new Command(CommandType.HANDSHAKE_RESPONSE));
                break;
            case LOGIN_REQUEST:

                Utente res = (Utente) cmd.getArg();

                if (res != null) {

                    res = LoginService.login(res.getEmail(), res.getPassword());

                }

                sendResponse(new Command(CommandType.LOGIN_RESPONSE, res));
                break;

            case REGISTER_REQUEST:

                try {
                    Utente u = (Utente) cmd.getArg();
                    Boolean registration = LoginService.registerUser(u);
                    if (registration) {
                        sendResponse(new Command(CommandType.REGISTER_RESPONSE, "OK"));
                    } else {
                        sendResponse(new Command(CommandType.REGISTER_RESPONSE, "Internal error"));
                    }

                } catch (Exception ex) {
                    sendResponse(new Command(CommandType.REGISTER_RESPONSE, ex.getMessage()));
                }
                break;

            case UPLOAD_DOCUMENT_REQUEST:
                Documento x = (Documento) cmd.getArg();
                if (x.getRaw() == null || x.getRaw().length == 0) {
                    return;
                } else {

                    try {
                        ByteArrayInputStream imageStream = new ByteArrayInputStream(x.getRaw());
                        String filename;
                        if (x.getTipo() == 1) {
                            filename = "identita_";
                        } else {
                            filename = "patente_";
                        }
                        filename = filename + (int) (Math.random() * 999999999) + ".png";
                        File dir = new File("uploaded_documents");
                        if (!dir.exists()) {
                            dir.mkdir();
                        }

                        FileOutputStream f = new FileOutputStream("uploaded_documents/" + filename);
                        if (IOUtils.copy(imageStream, f) > 0) {
                            sendResponse(new Command(CommandType.UPLOAD_DOCUMENT_RESPONSE, filename));
                        } else {
                            sendResponse(new Command(CommandType.UPLOAD_DOCUMENT_RESPONSE, "Errore"));
                        }
                        imageStream.close();
                        f.close();
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(CommandsEngine.class.getName()).log(Level.WARNING, null, ex);
                        sendResponse(new Command(CommandType.UPLOAD_DOCUMENT_RESPONSE, "Errore"));
                    } catch (IOException ex) {
                        Logger.getLogger(CommandsEngine.class.getName()).log(Level.WARNING, null, ex);
                        sendResponse(new Command(CommandType.UPLOAD_DOCUMENT_RESPONSE, "Errore"));
                    }
                }

                break;

            case GET_LISTA_VEICOLI_REQUEST:
                sendResponse(new Command(CommandType.GET_LISTA_VEICOLI_RESPONSE, VeicoliCore.veicoli));
                break;

            case VEICOLO_REQUEST:
                int codice = (int) cmd.getArg();
                Veicolo v = VeicoliCore.getVeicolo(codice);
                sendResponse(new Command(CommandType.VEICOLO_RESPONSE, v));
                break;

            case SCONTO_REQUEST:
                String cod_sconto = (String) cmd.getArg();
                Sconto s = ScontiCore.getSconto(cod_sconto);
                sendResponse(new Command(CommandType.SCONTO_RESPONSE, s));
                break;

            case EXIST_NOLEGGIO_REQUEST:
                sendResponse(new Command(CommandType.EXIST_NOLEGGIO_RESPONSE, VeicoliCore.richiediNoleggio((Utente) cmd.getArg())));
                break;

            case NOLEGGIO_REQUEST:
                Noleggio n = (Noleggio) cmd.getArg();
                try {
                    if (VeicoliCore.noleggiaVeicolo(n)) {
                        sendResponse(new Command(CommandType.NOLEGGIO_RESPONSE, "OK"));
                        VeicoliCore.updateVeicoli();
                    } else {
                        sendResponse(new Command(CommandType.NOLEGGIO_RESPONSE, "ERRORE GENERICO"));
                    }
                } catch (Exception ex) {
                    sendResponse(new Command(CommandType.NOLEGGIO_RESPONSE, ex.getMessage()));
                }
                break;
            case TERMINA_NOLEGGIO_REQUEST:
                Utente u = (Utente) cmd.getArg();

                try {
                    if (VeicoliCore.terminaNoleggio(u)) {
                       sendResponse(new Command(CommandType.TERMINA_NOLEGGIO_RESPONSE, "OK"));
                        VeicoliCore.updateVeicoli();
                    } else {
                        sendResponse(new Command(CommandType.TERMINA_NOLEGGIO_RESPONSE, "ERRORE GENERICO"));
                    }
                } catch (Exception ex) {
                    sendResponse(new Command(CommandType.TERMINA_NOLEGGIO_RESPONSE, ex.getMessage()));
                }
                break;
            case GET_FATTURA_REQUEST:
                if(cmd.getArg() != null){
                    Fattura f = DB.getFattura((Noleggio) cmd.getArg());
                    sendResponse(new Command(CommandType.GET_FATTURA_RESPONSE, f));
                }
                break;
                
            default:
                System.out.println("Received an unknown command: " + cmd.toString());

        }

    }

    public void sendResponse(Command cmd) {
        this.connection.SendCommand(cmd);
    }

}
