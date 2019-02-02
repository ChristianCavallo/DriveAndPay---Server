/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Connection;

import SerializedObjects.Command;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.apache.commons.lang3.SerializationUtils;

/**
 *
 * @author Christian
 */
public class TestClient extends Thread {

    private final String ip;
    private final Integer port;
    private final AES encryption;

    private Socket sock;

    public TestClient(String ip, Integer port) {
        this.ip = ip;
        this.port = port;
        this.encryption = new AES("FB26E3BE8631A6A5");
        try {
            this.sock = new Socket(this.ip, this.port);
        } catch (IOException ex) {
            Logger.getLogger(TestClient.class.getName()).log(Level.SEVERE, null, ex);
        }

        new Thread(this).start();
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

                System.out.println("Ho ricevuto un messaggio: " + cmd.toString());

            }
        } catch (IOException e) {
            System.out.println("Errore nel test client socket: " + e.getMessage());
        } catch (Exception ex) {
            System.out.println("Errore nel test client socket: " + ex.getMessage());
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
            System.out.println("Ho inviato " + data.length + " bytes al server: " + cmd.toString());
        } catch (IOException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.WARNING, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(TestClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public byte[] extractBytes(String ImageName) throws IOException {
        // open image
        File imgPath = new File(ImageName);
        BufferedImage bufferedImage = ImageIO.read(imgPath);

        // get DataBufferBytes from Raster
        WritableRaster raster = bufferedImage.getRaster();
        DataBufferByte data = (DataBufferByte) raster.getDataBuffer();

        return (data.getData());
    }
}
