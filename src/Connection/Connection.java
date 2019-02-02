/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Connection;

import Connection.Interpreter.CommandsEngine;
import SerializedObjects.Command;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.SerializationUtils;



/**
 *
 * @author Christian
 */
public class Connection extends Thread {

    private final Socket sock;
    private final AES encryption;
    private final CommandsEngine engine;
    private Boolean isLogged;

    public Connection(Socket s) {
        sock = s;
        this.encryption = new AES("FB26E3BE8631A6A5");
        this.engine = new CommandsEngine(this);
        this.isLogged = false;

        new Thread(this).start();
    }

    public Socket getSock() {
        return sock;
    }

    public CommandsEngine getEngine() {
        return engine;
    }

    public Boolean getIsLogged() {
        return isLogged;
    }

    public void setIsLogged(Boolean isLogged) {
        this.isLogged = isLogged;
    }

    @Override
    public void run() {
        try {
            DataInputStream inData = new DataInputStream(this.sock.getInputStream());

            while (true) {
                byte[] data;

                int count = inData.readInt();

                data = new byte[count];
                inData.readFully(data);

                if (count == -1) {
                    this.sock.getInputStream().close();
                    this.sock.close();
                    break;

                }

                data = encryption.decryptAES(data);
                Command cmd = SerializationUtils.deserialize(data);
                // MenuSettimanale ts = (MenuSettimanale) cmd.getArg();

                System.out.println("Ricevo: " + cmd.toString());

                this.engine.executeCommand(cmd);
                //QUA INSERISCO IL PARSER DEI COMANDI RICEVUTI

                // System.out.println("Contenuto dell'oggetto serializzato: " + ts.getPasti().get(0).toString());
            }
            System.out.println(sock.getInetAddress().getHostAddress() + " disconnected!");
        } catch (IOException ioe) {
            System.out.println(sock.getInetAddress().getHostAddress() + " disconnected!");
        } catch (Exception ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.WARNING, null, ex);
        }
    }

    public void SendCommand(Command cmd) {
        try {
            DataOutputStream out = new DataOutputStream(sock.getOutputStream());
            byte[] data = SerializationUtils.serialize(cmd);
            data = encryption.encryptAES(data);
            out.writeInt(data.length);
            out.write(data);
            out.flush();
            System.out.println("Invio: " + cmd.toString());
        } catch (IOException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.WARNING, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(TestClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
