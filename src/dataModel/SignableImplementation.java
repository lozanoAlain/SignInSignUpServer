/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataModel;

import static dataModel.DAOPool.getConnection;
import static dataModel.DAOPool.returnConnection;
import exceptions.ConnectionErrorException;
import exceptions.ExistUserException;
import exceptions.IncorrectPasswordException;
import exceptions.UserNotExistException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

/**
 * This class is the implementation class of the Signable interface
 *
 * @author Alain Lozano, Ilia Consuegra
 */
public class SignableImplementation implements Signable {

    //Statement to select all date from a user specified
    final String SIGNIN = "SELECT * FROM user WHERE login = ?";
    //Statement to insert a user in the user table
    final String SIGNUP = "INSERT INTO user (id,login,email,fullName,enumStatus,enumPrivilege,userPassword,lastPasswordChange) values (default,?,?,?,?,?,?,now())";
    //Statement to insert a sign in in signIn table
    final String SIGNININSERT = "INSERT INTO signin (lastSignIn,userId,signinId) values (now(),?,default)";
    //Statement to count the specified user´s connections
    final String SIGNINCOUNT = "SELECT COUNT(*) FROM signin WHERE userId=?";
    //Statement to select the first connection from the signIn table 
    final String SIGNINSELECTMINDATE = "SELECT * FROM signin WHERE lastSignIn=(Select min(lastSignIn) from signin WHERE userId=?)";
    private Connection con;
    private PreparedStatement stmt;

    /**
     * This method is for the Sign In case, gets the connection and inserts the
     * user data in the userAux
     *
     * @param user The user that is received from the Sing In window
     * @return The user we return completed from the database
     * @throws UserNotExistException Is thrown in case that the user do not
     * exist in the database
     * @throws IncorrectPasswordException Is thrown in case that the password
     * for the user is incorrect
     * @throws ConnectionErrorException Is thrown in case there is an error in
     * the connection
     */
    @Override
    public User signIn(User user) throws UserNotExistException, IncorrectPasswordException, ConnectionErrorException, Exception {
        //final String SIGNIN = "SELECT * FROM user WHERE login = ?";
        ResultSet rs = null;
        con = getConnection();

        stmt = con.prepareStatement(SIGNIN);
        stmt.setString(1, user.getLogin());

        rs = stmt.executeQuery();
        User userAux = null;
        // All fields received from the database are entered into an auxiliary user.
        while (rs.next()) {
            userAux = new User();
            userAux.setId(rs.getInt("id"));
            userAux.setLogin(rs.getString("login"));
            userAux.setEmail(rs.getString("email"));
            userAux.setFullName(rs.getString("fullName"));
            int auxStatus = rs.getInt("enumStatus");
            userAux.setStatus(UserStatus.values()[auxStatus]);
            int auxPrivilege = rs.getInt("enumPrivilege");
            userAux.setPrivilege(UserPrivilege.values()[auxPrivilege]);
            userAux.setPassword(rs.getString("userPassword"));
            userAux.setLastPasswordChange(rs.getTimestamp("lastPasswordChange").toLocalDateTime());
            signInCountAndInsert(userAux, rs, stmt);
        }
        rs.close();

        if (stmt != null) {
            stmt.close();
        }

        returnConnection(con);
        return userAux;

    }

    /**
     * This method is for the Sign Up case, gets the connection and inserts the
     * user data in the database
     *
     * @param user The user that is received from the Sing Up window
     * @throws ExistUserException Is thrown in case that the user exists in the
     * database
     * @throws ConnectionErrorException Is thrown in case there is an error in
     * the connection
     */
    @Override
    public void signUp(User user) throws ExistUserException, ConnectionErrorException {
        //final String SIGNUP = "INSERT INTO user (id,login,email,fullName,enumStatus,enumPrivilege,userPassword,lastPasswordChange) values (default,?,?,?,?,?,?,now())";
        try {
            con = getConnection();
            // All fields received from the client side are entered in an preparedStatement.

            stmt = con.prepareStatement(SIGNUP);
            stmt.setString(1, user.getLogin());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getFullName());
            stmt.setInt(4, 1);
            stmt.setInt(5, 1);
            stmt.setString(6, user.getPassword());

            stmt.executeUpdate();

            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException ex) {
            throw new ExistUserException();
        }
        returnConnection(con);

    }

    /**
     * This method is used to control the last sign in connection in the
     * database
     *
     * @param userAux The user is received completed from the database
     * @param rs The Resulset is used to received data from the database
     * @param stmt The PreparedStatement is used to prepare the consult to the
     * database
     * @throws SQLException Is thrown in case that there is an error in the
     * connection with the database
     */
    private void signInCountAndInsert(User userAux, ResultSet rs, PreparedStatement stmt) throws SQLException {
        //final String SIGNINCOUNT = "SELECT COUNT(*) FROM signin WHERE userId=?";
        //final String SIGNINSELECTMINDATE = "SELECT * FROM signin WHERE lastSignIn=(Select min(lastSignIn) from signin WHERE userId=?)";
        //final String SIGNININSERT = "INSERT INTO signin (lastSignIn,userId,signinId) values (now(),?,default)";

        stmt = con.prepareStatement(SIGNINCOUNT);
        stmt.setInt(1, userAux.getId());
        rs = stmt.executeQuery();
        //Counts the connections of a user and if more than 10, deletes the last connection.
        if (rs.next()) {
            if (rs.getInt("count(*)") >= 10) {
                stmt = con.prepareStatement(SIGNINSELECTMINDATE, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                stmt.setInt(1, userAux.getId());
                rs = stmt.executeQuery();
                rs.first();
                rs.updateTimestamp("lastSignIn", Timestamp.valueOf(LocalDateTime.now()));
                rs.updateRow();
            } else {
                stmt = con.prepareStatement(SIGNININSERT);
                stmt.setInt(1, userAux.getId());
                stmt.executeUpdate();
            }
        } else {
            stmt = con.prepareStatement(SIGNININSERT);
            stmt.setInt(1, userAux.getId());
            stmt.executeUpdate();
        }
    }

}
