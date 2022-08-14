package sample;

import java.io.IOException;
import java.sql.*;

public class Database {
    private Connection connection;
    public Database(){
        try {
            this.connection = DriverManager.getConnection("jdbc:mysql://192.168.43.21/chat?useSSL=true", "root", "pass");
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }
    public void saveMessage(String username,String message){
        String sql = "insert into messages values (? , ?)";
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setString(1,username);
            preparedStatement.setString(2,message);
            preparedStatement.executeUpdate();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }
    public boolean checkUser(String username){
        String sql = "Select username from messages where username = ?";
        boolean returnBool = false;
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setString(1,username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                returnBool = resultSet.getString(1).equals(username);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return returnBool;
    }
    public boolean verifyUser(String username,String password){
        String sql = "Select * from login where username=?";
        boolean Bool = false;
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setString(1,username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                Bool = resultSet.getString(1).equals(username)&&resultSet.getString(2).equals(password);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Bool;
    }
    public void addUser(String username,String password){
        String sql = "Insert into login values ( ?,?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,username);
            preparedStatement.setString(2,password);
            preparedStatement.executeUpdate();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }
    public ResultSet getResults(){
        String sql = "Select * from messages";
        ResultSet resultSet = null;
        try {
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }
    public static void main(String[] args){
        Database database = new Database();
        database.saveMessage("Dave","Hello");
        database.addUser("dave","1,2,3,4");
        System.out.println(database.verifyUser("Dave","123"));
    }
}
