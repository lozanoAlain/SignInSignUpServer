/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataModel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * This class gets the connections and sends the connection to the
 * SignableImplementation and its maximun size is stablised on 10 connections at
 * the same time
 *
 * @author Alain Lozano, Ilia Consuegra
 */
public class DAOPool {
//The parameters for the connection are taken from the configuration file
    private static String url = ResourceBundle.getBundle("dataModel.ServerConfiguration").getString("URL");
    private static String user = ResourceBundle.getBundle("dataModel.ServerConfiguration").getString("DBUser");
    private static String pass = ResourceBundle.getBundle("dataModel.ServerConfiguration").getString("DBPass");
//An ArrayList to manage the connections 
    private static ArrayList<Connection> pool = new ArrayList<>(Integer.valueOf(ResourceBundle.getBundle("dataModel.ServerConfiguration").getString("maxServerConnections")));

    /**
     * The method that creates the connections and sends them to the
     * SignableImplementation
     *
     * @return The connection opened
     * @throws SQLException Is thrown in case there is an error opening the
     * connection
     */
    public static synchronized Connection getConnection() throws SQLException {
        Connection connection = null;
        //If there are no connection available the first connection is created
        if (pool.size() == 0) {
            pool.add(DriverManager.getConnection(url, user, pass));
        }
        //It gets the last connection available
        connection = pool.get(pool.size() - 1);
        //The connection is sended to the thread and is removed from the pool
        pool.remove(connection);

        return connection;
    }

    /**
     * The method receives the connections
     *
     * @param connection The connection that was used by the
     * SignableImplementation
     */

    public static synchronized void returnConnection(Connection connection) {
        //The retuned connection is added to the pool
        pool.add(connection);
    }

}
