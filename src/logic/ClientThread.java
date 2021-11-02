/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import dataModel.DataEncapsulation;
import dataModel.MessageEnum;
import dataModel.Signable;
import dataModel.SignableFactory;
import dataModel.User;
import exceptions.ConnectionErrorException;
import exceptions.ExistUserException;
import exceptions.IncorrectPasswordException;
import exceptions.UserNotExistException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import static serverApplication.ServerApplication.decrement;
import static serverApplication.ServerApplication.increment;

/**
 *
 * @author Alain Lozano, Ilia Consuegra
 */
public class ClientThread extends Thread {

    private final static Logger logger = Logger.getLogger(ClientThread.class.getName());
    private Socket sc = null;
    private DataEncapsulation data = null;

    public ClientThread(Socket sc) {
        this.sc = sc;
    }

    public void run() {
        ObjectInputStream ois;
        ObjectOutputStream oos;
        try {
            ois = new ObjectInputStream(sc.getInputStream());
            SignableFactory signableFactory = new SignableFactory();
            Signable signable = signableFactory.getSignable();
            //  DatatEncapsulation data = new DataEncapsulation();

            data = (DataEncapsulation) ois.readObject();
            increment();
            User user = null;
            switch (data.getMessage().ordinal()) {
                case 0:
                    user = signable.signIn(data.getUser());
                    //Se queda esperando
                    data.setUser(user);
                    break;
                case 1:
                    signable.signUp(data.getUser());
                    data.setUser(user);
                    break;
                default:
                    break;
            }
            ois.close();
            

        } catch (IOException ex) {
            data.setMessage(MessageEnum.CONNECTION_ERROR);
            logger.warning(ex.getMessage());
        } catch (ClassNotFoundException ex) {
            data.setMessage(MessageEnum.CONNECTION_ERROR);
            logger.warning(ex.getMessage());
        } catch (IncorrectPasswordException ex) {
            data.setMessage(MessageEnum.SIGN_IN_ERROR_PASSWORD);
            logger.warning(ex.getMessage());
        } catch (ConnectionErrorException ex) {
            data.setMessage(MessageEnum.CONNECTION_ERROR);
            logger.warning(ex.getMessage());
        } catch (ExistUserException ex) {
            data.setMessage(MessageEnum.SIGN_UP_ERROR_USER);
            logger.warning(ex.getMessage());
        } catch (UserNotExistException ex) {
            data.setMessage(MessageEnum.SIGN_IN_ERROR_USER);
            logger.warning(ex.getMessage());
        } catch (Exception ex) {
            data.setMessage(MessageEnum.CONNECTION_ERROR);
            logger.warning(ex.getMessage());
        }
        try {
            oos = new ObjectOutputStream(sc.getOutputStream());
            oos.writeObject(data);
            decrement();
            oos.close();
            sc.close();
            this.interrupt();
        } catch (IOException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
