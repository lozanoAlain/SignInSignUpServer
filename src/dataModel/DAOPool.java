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
 *
 * @author Usuario
 */
public class DAOPool {
    private static String url = ResourceBundle.getBundle("dataModel.ServerConfiguration").getString("URL");
    private static String user = ResourceBundle.getBundle("dataModel.ServerConfiguration").getString("DBUser");
    private static String pass = ResourceBundle.getBundle("dataModel.ServerConfiguration").getString("DBPass");
    
   
    
    private static ArrayList<Connection> pool= new ArrayList<>(Integer.valueOf(ResourceBundle.getBundle("dataModel.ServerConfiguration").getString("maxServerConnections")));
    
    public static synchronized Connection getConnection() throws SQLException{
        Connection connection=null;
        if(pool.size()==0){
            pool.add(DriverManager.getConnection(url,user,pass));
        }
        connection=pool.get(pool.size()-1);
        pool.remove(connection);
        
        
        return connection;
    }
    
    public static synchronized void returnConnection(Connection connection){
        pool.add(connection);       
    }
    
    
    
    
}
