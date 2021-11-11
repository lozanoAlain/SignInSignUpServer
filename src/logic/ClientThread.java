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
 * This class is used to create a thread and it is used for each connection
 *
 * @author Alain Lozano, Ilia Consuegra
 */
public class ClientThread extends Thread {

    private final static Logger logger = Logger.getLogger(ClientThread.class.getName());
    private Socket sc = null;
    private DataEncapsulation data;

    /**
     *
     * @param sc
     */
    public ClientThread(Socket sc) {
        this.sc = sc;
    }

    /**
     * This method is used to to the Sign In and Sign Up and it completes the
     * dataEncapsulation with the message received from the exceptions and the
     * user received from the SiganbleImplementation
     */
    public void run() {

        ObjectInputStream ois = null;
        ObjectOutputStream oos = null;
        try {
            ois = new ObjectInputStream(sc.getInputStream());
            SignableFactory signableFactory = new SignableFactory();
            Signable signable = signableFactory.getSignable();
            //  DatatEncapsulation data = new DataEncapsulation();

            data = (DataEncapsulation) ois.readObject();

            increment();
            User user = null;           
            switch (data.getMessage()) {
                case SIGN_IN:
                    user = signable.signIn(data.getUser());
                    //Se queda esperando                  
                    if (user == null) {
                        throw new UserNotExistException();
                    } else if (!user.getPassword().equals(data.getUser().getPassword())) {
                        throw new IncorrectPasswordException();
                    } else {
                        data.setUser(user);
                        data.setMessage(MessageEnum.SIGN_IN_OK);
                    }
                    break;
                case SIGN_UP:
                    signable.signUp(data.getUser());
                    data.setUser(user);
                    data.setMessage(MessageEnum.SIGN_UP_OK);
                    break;
                default:
                    break;
            }

        } catch (IOException ex) {
            data.setMessage(MessageEnum.CONNECTION_ERROR);
            logger.severe(ex.getMessage());
        } catch (ClassNotFoundException ex) {
            data.setMessage(MessageEnum.CONNECTION_ERROR);
            logger.severe(ex.getMessage());
        } catch (IncorrectPasswordException ex) {
            data.setMessage(MessageEnum.SIGN_IN_ERROR_PASSWORD);
            logger.severe(ex.getMessage());
        } catch (ConnectionErrorException ex) {
            data.setMessage(MessageEnum.CONNECTION_ERROR);
            logger.severe(ex.getMessage());
        } catch (ExistUserException ex) {
            data.setMessage(MessageEnum.SIGN_UP_ERROR_USER);
            logger.severe(ex.getMessage());
        } catch (UserNotExistException ex) {
            data.setMessage(MessageEnum.SIGN_IN_ERROR_USER);
            logger.severe(ex.getMessage());
        } catch (Exception ex) {
            data.setMessage(MessageEnum.CONNECTION_ERROR);
            logger.severe(ex.getMessage());
        }
        try {
            oos = new ObjectOutputStream(sc.getOutputStream());
            oos.writeObject(data);
            decrement();
            ois.close();
            oos.close();
            sc.close();
            this.interrupt();
        } catch (IOException ex) {
            logger.severe(ex.getMessage());
        }

    }

}
