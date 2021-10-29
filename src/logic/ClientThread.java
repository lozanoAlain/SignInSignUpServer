/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import dataModel.DataEncapsulation;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author 2dam
 */
public class ClientThread extends Thread{
    
    private Socket sc = null;
    
    public ClientThread(Socket sc){
        this.sc = sc;
    }
    
    public void run(){
        ObjectInputStream ois;
        ObjectOutputStream oos;
        
        try {
            ois = new ObjectInputStream(sc.getInputStream());
          //  DatatEncapsulation data = new DataEncapsulation();
            while(true){
                
            }
           // oos = new ObjectOutputStream(sc.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
