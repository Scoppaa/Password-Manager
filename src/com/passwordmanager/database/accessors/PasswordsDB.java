package com.passwordmanager.database.accessors;

import com.passwordmanager.database.objects.Password;
import com.passwordmanager.utils.DB;
import com.passwordmanager.utils.EncryptPasswordAES;
import com.passwordmanager.utils.HashPassword;
import com.sun.xml.internal.ws.util.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Justin Scott
 * @author Nick Soltysiak
 */
public class PasswordsDB {

    public static void deletePassword(String attribute, Object value) {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = pool.getConnection();
        PreparedStatement ps = null;

        String query = "DELETE FROM PASSWORDS " +
                "WHERE " + attribute + " = ?;";
        try {
            ps = connection.prepareStatement(query);
            ps.setString(1, value.toString());
            ps.execute();
        }

        catch (SQLException e) {
            Logger.getLogger(PasswordsDB.class.getName()).log(Level.SEVERE, null, e);
        }

        finally {
            DBUtils.closePreparedStatement(ps);
            pool.freeConnection(connection);
        }
    }

    // GET

    /**
     * Gets a single password object populated from the database using the inputs.
     *
     * @param attribute Column name in the PASSWORDS table you'd like to
     *                  search by.
     * @param value Value of the column you'd like to filter by.
     * @return Single user object given the filtered inputs.
     */
    public static Password getPassword(String attribute, Object value) {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = pool.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        Password pwd = null;

        String query = "SELECT * FROM PASSWORDS " +
                "WHERE " + attribute + " = ?";
        try {
            ps = connection.prepareStatement(query);
            ps.setString(1, value.toString());
            pwd = getFromDB(ps);
        } catch (SQLException e) {
            Logger.getLogger(PasswordsDB.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            DBUtils.closePreparedStatement(ps);
            pool.freeConnection(connection);
        }
        return pwd;
    }

    /**
     * Gets an arraylist of password objects populated from the database using the inputs.
     *
     * @return
     */
    public static ArrayList<Password> getPasswords(String attribute, Object value) {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = pool.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<Password> pwds = null;

        String query = "SELECT * FROM PASSWORDS " +
                "WHERE " + attribute + " = ?;";
        try {
            ps = connection.prepareStatement(query);
            ps.setString(1, value.toString());
            pwds = getListFromDB(ps);
        } catch (SQLException e) {
            Logger.getLogger(PasswordsDB.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            DBUtils.closePreparedStatement(ps);
            pool.freeConnection(connection);
        }
        return pwds;
    }

    /**
     * Gets an arraylist of password objects populated from the database using the inputs.
     *
     * @return
     */
    public static ArrayList<Password> getPasswords() {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = pool.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<Password> pwds = null;

        String query = "SELECT * FROM PASSWORDS";
        try {
            ps = connection.prepareStatement(query);
            pwds = getListFromDB(ps);
        } catch (SQLException e) {
            Logger.getLogger(PasswordsDB.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            DBUtils.closePreparedStatement(ps);
            pool.freeConnection(connection);
        }
        return pwds;
    }

    // UPDATE

    /**
     * Updates passed password object in the database
     * @param pwd Password object with updated values
     * @return Bool on whether update was completed.
     */
    public static boolean updatePassword(Password pwd) {

        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = pool.getConnection();
        PreparedStatement ps = null;
        int retVal = 0;

        String query = "UPDATE PASSWORDS SET " + DB.PASSWORD +
                " = ?, " + DB.PASSWORD_TITLE +
                " = ?, " + DB.PASSWORD_TIMESTAMP +
                " = ?, " + DB.PASSWORD_NOTES +
                " = ?, " + DB.PASSWORD_OWNER +
                " = ?, " + DB.PASSWORD_USERNAME +
                " = ?, " + DB.PASSWORD_URL +
                " = ?, " + DB.FOLDER_ID +
                " = ? WHERE " + DB.PASSWORD_ID + " = ?;";
        try {
            ps = connection.prepareStatement(query);
            String salt = HashPassword.createSalt(16).get();
            ps.setString(1, salt + ":" + EncryptPasswordAES.passwordEncrypt(pwd.getPassword(), salt));
            ps.setString(2, pwd.getPassword_title());
            ps.setTimestamp(3, pwd.getPassword_timestamp());
            ps.setString(4, pwd.getPassword_notes());
            ps.setInt(5, pwd.getPassword_owner());
            ps.setString(6, pwd.getPassword_username());
            ps.setString(7, pwd.getPassword_url());
            ps.setInt(8,pwd.getFolder_ID());
            ps.setInt(9, pwd.getPassword_ID());
            System.out.println(ps.toString());
            retVal = ps.executeUpdate();
        } catch (SQLException e) {
            Logger.getLogger(PasswordsDB.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            DBUtils.closePreparedStatement(ps);
            pool.freeConnection(connection);
        }
        return retVal != 0;
    }


    // INSERT

    /**
     * Inserts passed password object into the database. Used only on new password
     * objects. Use updatePassword to update the information of a user object.
     * @param pwd New password to insert into the database.
     * @return Bool based on success of insert statement.
     */
    public static boolean insertPassword(Password pwd) {

        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = pool.getConnection();
        PreparedStatement ps = null;
        int retVal = 0;

        String query = "INSERT INTO PASSWORDS (" +
                DB.PASSWORD + ", " +
                DB.PASSWORD_TITLE + ", " +
                DB.FOLDER_ID + ", " +
                DB.PASSWORD_TIMESTAMP + ", " +
                DB.PASSWORD_USERNAME + ", " +
                DB.PASSWORD_URL + ", " +
                DB.PASSWORD_NOTES + ", " +
                DB.PASSWORD_OWNER + ") " +
                "VALUES (?,?,?,?,?,?,?,?);";
        try {
            ps = connection.prepareStatement(query);
            String salt = HashPassword.createSalt(16).get();
            ps.setString(1, salt + ":" + EncryptPasswordAES.passwordEncrypt(pwd.getPassword(), salt));
            ps.setString(2, StringUtils.capitalize(pwd.getPassword_title()));
            ps.setLong(3, pwd.getFolder_ID());
            ps.setTimestamp(4, pwd.getPassword_timestamp());
            ps.setString(5, pwd.getPassword_username());
            ps.setString(6, pwd.getPassword_url());
            ps.setString(7, pwd.getPassword_notes());
            ps.setInt(8, pwd.getPassword_owner());
            retVal = ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        } finally {
            DBUtils.closePreparedStatement(ps);
            pool.freeConnection(connection);
        }
        return retVal != 0;

    }


    // GENERIC
    /**
     * Gets a single password bean from the a DB based on a prepared statement
     * @param ps
     * @return
     */
    private static Password getFromDB(PreparedStatement ps) {

        ResultSet rs = null;
        Password pwd = null;
        try {
            rs = ps.executeQuery();
            if (rs.next()) {
                pwd = new Password();

                String pass[] = rs.getString(DB.PASSWORD).split(":");
                pwd.setPassword(EncryptPasswordAES.passwordDecrypt(pass[1],pass[0]));
                pwd.setFolder_ID(rs.getInt(DB.FOLDER_ID));
                pwd.setPassword_title(rs.getString(DB.PASSWORD_TITLE));
                pwd.setPassword_timestamp(rs.getTimestamp(DB.PASSWORD_TIMESTAMP));
                pwd.setPassword_notes(rs.getString(DB.PASSWORD_NOTES));
                pwd.setPassword_owner(rs.getInt(DB.PASSWORD_OWNER));
                pwd.setPassword_username(rs.getString(DB.PASSWORD_USERNAME));
                pwd.setPassword_url(rs.getString(DB.PASSWORD_URL));
                pwd.setPassword_ID(rs.getInt(DB.PASSWORD_ID));
            }
        } catch (SQLException e) {
            Logger.getLogger(PasswordsDB.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            DBUtils.closeResultSet(rs);
        }
        return pwd;
    }

    /**
     * Gets a list of password beans from a DB based on a prepared
     * statement.
     *
     * @param ps
     * @return
     */
    private static ArrayList<Password> getListFromDB(PreparedStatement ps) {
        ResultSet rs = null;
        ArrayList<Password> pwds = null;
        try {
            rs = ps.executeQuery();
            pwds = new ArrayList<Password>();
            while (rs.next()) {
                Password pwd = new Password();

                String pass[] = rs.getString(DB.PASSWORD).split(":");
                pwd.setPassword(EncryptPasswordAES.passwordDecrypt(pass[1],pass[0]));
                pwd.setFolder_ID(rs.getInt(DB.FOLDER_ID));
                pwd.setPassword_title(rs.getString(DB.PASSWORD_TITLE));
                pwd.setPassword_timestamp(rs.getTimestamp(DB.PASSWORD_TIMESTAMP));
                pwd.setPassword_notes(rs.getString(DB.PASSWORD_NOTES));
                pwd.setPassword_owner(rs.getInt(DB.PASSWORD_OWNER));
                pwd.setPassword_username(rs.getString(DB.PASSWORD_USERNAME));
                pwd.setPassword_url(rs.getString(DB.PASSWORD_URL));
                pwd.setPassword_ID(rs.getInt(DB.PASSWORD_ID));

                pwds.add(pwd);
            }
        } catch (SQLException e) {
            Logger.getLogger(PasswordsDB.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            DBUtils.closeResultSet(rs);
        }
        return pwds;
    }


}
