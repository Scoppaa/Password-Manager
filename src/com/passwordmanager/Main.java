package com.passwordmanager;


import java.sql.*;
import com.passwordmanager.database.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Application;
import com.passwordmanager.loginscreen.*;


public class Main extends Application {

    public static void main(String[] args) {

        /*
            Expandable connection service objects. ConnectionPool is a
            set of connection from this specific application. Each instance
            of this application will have its own connectionpool that allows
            multiple connections to be called from it. If web service created
            based on this application, the same system can be used on server side
            accross multiple connections from differing IPs.
         */
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = pool.getConnection();

        /*
            A prepared statement that is set using the Connection.prepareStatement
            function. Function takes a string in the form of an SQL statement.
         */
        PreparedStatement ps = null;

        /*
            Stores results from the DB. Is an object that contains information
            of datatypes based on datatype in DB. call getType("ATTRIBUTE NAME")
         */
        ResultSet rs = null;

        /*
            Test code to determine if DB connection has been esablished.
         */
        String query = "SELECT * FROM USERS";

        launch(args);

        try {

            ps = connection.prepareStatement(query);

            rs = ps.executeQuery();

            while (rs.next()) {
                System.out.println(rs.getString("USER_USERNAME"));

            }

        } catch (SQLException e) {
            System.out.println(e);
        } finally {
            DBUtils.closeResultSet(rs);
            DBUtils.closePreparedStatement(ps);
            pool.freeConnection(connection);
        }

    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("loginscreen/LoginScreen.fxml"));
        Parent rootPane = loader.load();
        Scene scene = new Scene(rootPane);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Welcome");
        primaryStage.show();
    }
}