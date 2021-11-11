/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverApplication;

import dataModel.DataEncapsulation;
import dataModel.MessageEnum;
import exceptions.UserNotExistException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import logic.ClientThread;

/**
 * This class if for the initialization of the server an the control of the
 * maximum connections permitted
 *
 * @author Ilia Consuegra, Alain Lozano
 */
public class ServerApplication {

    private static int accepted = 0;

    /**
     * The method that accepts the connections between the server and the client
     * and created the threads
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            // TODO code application logic here
            Socket sc = null;

            final int maxAccept = Integer.valueOf(ResourceBundle.getBundle("dataModel.ServerConfiguration").getString("maxServerConnections"));
            String port = (ResourceBundle.getBundle("dataModel.ServerConfiguration").getString("Port"));
            ServerSocket ss = new ServerSocket(Integer.valueOf(port));

            Logger.getLogger("serverApplication").info("Escuchando por el puerto: " + port);
            while (true) {
                sc = ss.accept();
                if (accepted < maxAccept) {
                    ClientThread clientThread = new ClientThread(sc);
                    clientThread.start();
                } else {
                    ObjectOutputStream oos = null;
                    oos = new ObjectOutputStream(sc.getOutputStream());
                    DataEncapsulation data = new DataEncapsulation();
                    data.setMessage(MessageEnum.CONNECTION_ERROR);
                    oos.writeObject(data);
                    oos.close();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ServerApplication.class.getName()).log(Level.SEVERE, null, ex.getMessage());

        }

    }

    /**
     * The method to increment the number of connections accepted
     */

    public static synchronized void increment() {
        accepted++;
    }

    /**
     * The method to decrement the number of connections accepted
     */
    public static synchronized void decrement() {
        accepted--;
    }

}
