/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import dataModel.DataEncapsulation;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ResourceBundle;

/**
 *
 * @author 2dam
 */
public class ServerApplication {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        Socket sc=null;
        DataEncapsulation data;
        int maxAccept = Integer.valueOf(ResourceBundle.getBundle("dataModel.ServerConfiguration").getString("Port"));
        
        ServerSocket ss= new ServerSocket(Integer.valueOf(ResourceBundle.getBundle("dataModel.ServerConfiguration").getString("Port")));
        while(maxAccept<10){
            sc=ss.accept();
            
            data= new DataEncapsulation();
            ClientThread clientThread = new ClientThread(sc);
            clientThread.start();
            
            
            
        }
        
    } 
    
}
