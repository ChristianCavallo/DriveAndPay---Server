/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Christian
 */
public class Server extends Thread {

    private final ServerSocket sock;
    private final Integer port;

    private final ArrayList<Connection> connections;

    public Server(Integer port) throws IOException {
        this.port = port;
        this.sock = new ServerSocket(this.port);
        this.connections = new ArrayList<>();
        
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket client = this.sock.accept();
                this.connections.add(new Connection(client));

                 System.out.println("New connection from " + client.getInetAddress().getHostAddress());
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.WARNING, null, ex);
            }
        }
    }

}
