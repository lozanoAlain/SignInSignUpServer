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
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 *
 * @author Usuario
 */
public class SignableImplementation implements Signable {

    final String SIGNIN = "SELECT * FROM user WHERE login = ?";
    final String SIGNUP = "INSERT INTO user (id,login,email,fullName,enumStatus,enumPrivilege,userPassword,lastPasswordChange) values (default,?,?,?,?,?,?,now())";
    final String SIGNININSERT = "INSERT INTO signin (lastSignIn,userId,signinId) values (now(),?,default)";
    final String SIGNINCOUNT = "SELECT COUNT(*) FROM signin WHERE userId=?";
    final String SIGNINSELECTMINDATE = "SELECT * FROM signin WHERE lastSignIn=(Select min(lastSignIn) from signin WHERE userId=?)";
    private Connection con;
    private PreparedStatement stmt;

    /**
     *
     * @param user
     * @throws UserNotExistException
     * @throws IncorrectPasswordException
     * @throws ConnectionErrorException
     */
    @Override
    public User signIn(User user) throws UserNotExistException, IncorrectPasswordException, ConnectionErrorException, Exception {
        //    final String SIGNIN = "SELECT * FROM user WHERE login = ?";
        ResultSet rs = null;
        con = getConnection();

        stmt = con.prepareStatement(SIGNIN);
        stmt.setString(1, user.getLogin());

        rs = stmt.executeQuery();
        User userAux = null;
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
        }
        rs.close();

        stmt = con.prepareStatement(SIGNINCOUNT);
        stmt.setInt(1, userAux.getId());
        rs = stmt.executeQuery();
        if (rs.next()) {
            if (rs.getInt("count(*)") >= 2) {
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

        if (stmt != null) {
            stmt.close();
        }

        returnConnection(con);
        return userAux;

    }

    /**
     *
     * @param user
     * @throws ExistUserException
     * @throws ConnectionErrorException
     */
    @Override
    public void signUp(User user) throws ExistUserException, ConnectionErrorException, Exception {
    //final String SIGNUP = "INSERT INTO user (id,login,email,fullName,enumStatus,enumPrivilege,userPassword,lastPasswordChange) values (default,?,?,?,?,?,?,now())";
        con = getConnection();

        stmt = con.prepareStatement(SIGNUP);
        stmt.setString(1, user.getLogin());
        stmt.setString(2, user.getEmail());
        stmt.setString(3, user.getFullName());
        stmt.setInt(4, 1);
        stmt.setInt(5, 1);
        stmt.setString(6, user.getPassword());

        try {
            stmt.executeUpdate();
        } catch (Exception ex) {
            throw new ExistUserException();
        }

        /*if( == 0){
            
        }
         */
        if (stmt != null) {
            stmt.close();
        }
        returnConnection(con);

    }

}
