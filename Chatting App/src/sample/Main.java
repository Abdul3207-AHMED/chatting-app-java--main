package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Main extends Application {
    Database database = new Database();
    Socket server;
    String username;
    @Override
    public void start(Stage primaryStage) {
        try {
            ServerSocket serverSocket = new ServerSocket(8000);
            ArrayList<String> ipAddresses = new ArrayList<>();
            new Thread(()->{
                try {
                    while(true) {
                        server = serverSocket.accept();
//                        DataInputStream in = new DataInputStream(server.getInputStream());
                        DataOutputStream out = new DataOutputStream(server.getOutputStream());
//                        String[] array = in.readUTF().split("&");
//                        System.out.println(in.readUTF());
                        Platform.runLater(()->{
                            createChatScene(primaryStage);
                        });
                        out.writeUTF(" ");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        userPage(primaryStage,"login");
    }
    void userPage(Stage stage,String btnType){
        HBox root = new HBox();
        VBox leftBox = new VBox();
        leftBox.setStyle("-fx-background-color: #fff");
        leftBox.setPrefWidth((Screen.getPrimary().getBounds().getWidth()-20)/2);
        leftBox.setAlignment(Pos.CENTER);
        VBox rightBox = new VBox();
        rightBox.setPrefWidth((Screen.getPrimary().getBounds().getWidth()-20)/2);
        rightBox.setAlignment(Pos.CENTER);
        rightBox.setStyle("-fx-background-color: #00a8e8");
        Image image = new Image("/message.png");
        ImageView imageView = new ImageView(image);
        leftBox.getChildren().add(imageView);
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(7);
        gridPane.setVgap(5);
        Text welcomeText = new Text(btnType.equals("login")?"Welcome back, Login in to your Account !":"Hello, Create An Account !");
        welcomeText.setFont(Font.font("Arial", FontWeight.BOLD,20));
        welcomeText.setFill(Paint.valueOf("white"));
        Label userNameLB = new Label("Username");
        TextField usernameTF = new TextField();
        usernameTF.setPrefColumnCount(20);
        usernameTF.setPromptText("Enter Username");
        userNameLB.setLabelFor(usernameTF);
        userNameLB.setStyle("-fx-font-weight: bold;-fx-font-size: 20;-fx-text-fill: white");
        usernameTF.setFocusTraversable(false);
        Label passwordLB = new Label("Password");
        PasswordField passwordTF = new PasswordField();
        passwordTF.setPromptText("Enter Password");
        passwordLB.setStyle("-fx-font-weight: bold;-fx-font-size: 20;-fx-text-fill: white");
        passwordTF.setPrefColumnCount(20);
        passwordLB.setLabelFor(passwordTF);
        passwordTF.setFocusTraversable(false);
        Label confirmPasswordLB = new Label("Reenter password");
        PasswordField confirmPassword = new PasswordField();
        confirmPasswordLB.setLabelFor(confirmPassword);
        confirmPasswordLB.setStyle("-fx-font-weight: bold;-fx-font-size: 20;-fx-text-fill: white");
        confirmPassword.setPromptText("Confirm your Password");
        confirmPassword.setPrefColumnCount(20);
        confirmPassword.setFocusTraversable(false);
        Button loginBtn = new Button(btnType.equals("login")?"Login":"SignUp");
        loginBtn.setStyle("-fx-border-radius: 40;-fx-padding: 5 35 5;-fx-text-fill:#00a8e8 ");
        loginBtn.setAlignment(Pos.CENTER);
        loginBtn.setDisable(true);
        loginBtn.setOnAction(e->{
            if(!btnType.equals("login")){
                if(database.checkUser(usernameTF.getText().toLowerCase())){
                    Tooltip tooltip = new Tooltip();
                    tooltip.setText("Username has already been taken, Please enter another username");
                    tooltip.setStyle("-fx-background-color:white;-fx-text-fill:red;-fx-font-weight:bold;");
                    tooltip.setShowDelay(Duration.ZERO);
                    usernameTF.setTooltip(tooltip);
                    usernameTF.setStyle("-fx-border-color: red;-fx-border-width: 2");
                }
                if(!passwordTF.getText().equals(confirmPassword.getText())){
                    Tooltip tooltip = new Tooltip("The passwords don't match");
                    tooltip.setStyle("-fx-background-color:white;-fx-text-fill:red;-fx-font-weight:bold");
                    tooltip.setShowDelay(Duration.ZERO);
                    passwordTF.setTooltip(tooltip);
                    passwordTF.setStyle("-fx-border-width: 2;-fx-border-color: red");
                    confirmPassword.setTooltip(tooltip);
                    confirmPassword.setStyle("-fx-border-width: 2;-fx-border-color: red");
                }
                if(passwordTF.getText().equals(confirmPassword.getText())&&!database.checkUser(usernameTF.getText().toLowerCase()))
                    database.addUser(usernameTF.getText().toLowerCase(),passwordTF.getText());
                this.username = usernameTF.getText().toLowerCase();
                createChatScene(stage);
            }
            else{
                if(!database.verifyUser(usernameTF.getText().toLowerCase(),passwordTF.getText())){
                    Tooltip tooltip = new Tooltip();
                    tooltip.setText("This username is not registered");
                    tooltip.setStyle("-fx-background-color:white;-fx-text-fill:red;-fx-font-weight:bold;");
                    tooltip.setShowDelay(Duration.ZERO);
                    usernameTF.setTooltip(tooltip);
                    usernameTF.setStyle("-fx-border-color: red;-fx-border-width: 2");
                }
                else{
                    this.username = usernameTF.getText().toLowerCase();
                    createChatScene(stage);
                }
            }
        });
        passwordTF.setOnKeyTyped(e->{
            if(btnType.equals("login")){
                loginBtn.setDisable(passwordTF.getText().isEmpty() || usernameTF.getText().isEmpty());
            }
        });
        confirmPassword.setOnKeyTyped(e->{
            if(!btnType.equals("login")){
                loginBtn.setDisable(passwordTF.getText().isEmpty() || usernameTF.getText().isEmpty() || confirmPassword.getText().isEmpty());
            }
        });
        HBox createAccount = new HBox();
        Text createAnAccount = new Text(btnType.equals("login")?"Create a new Account":"Already have an Account");
        createAnAccount.setFill(Paint.valueOf("white"));
        createAnAccount.setOnMouseEntered(e-> createAnAccount.setStyle("-fx-underline: true"));
        createAnAccount.setOnMouseExited(e-> createAnAccount.setStyle("-fx-underline: false"));
        createAnAccount.setOnMouseClicked(e->{
            if (btnType.equals("login")) {
                userPage(stage, "createAccount");
            } else {
                userPage(stage, "login");
            }
        });
        createAccount.getChildren().add(createAnAccount);
        createAccount.setAlignment(Pos.CENTER);
        createAccount.setPadding(new Insets(0,0,0,300));
        gridPane.addRow(0,userNameLB,usernameTF);
        gridPane.addRow(1,passwordLB,passwordTF);
        if(!btnType.equals("login")){
            gridPane.addRow(2,confirmPasswordLB,confirmPassword);
        }
        VBox container = new VBox();
        container.setAlignment(Pos.CENTER);
        container.setSpacing(10);
        container.getChildren().addAll(welcomeText,gridPane,loginBtn);
        rightBox.getChildren().addAll(container,createAccount);
        root.getChildren().addAll(leftBox,rightBox);
        Scene sceneRoot = new Scene(root, Screen.getPrimary().getBounds().getWidth()-20,Screen.getPrimary().getBounds().getHeight()-60);
        stage.setScene(sceneRoot);
        stage.setTitle("Chit Chat");
        stage.show();
    }
    void createChatScene(Stage stage){
        ResultSet resultSet = database.getResults();
        ScrollPane scrollPane = new ScrollPane();
        BorderPane pane = new BorderPane();
        pane.setPrefWidth(790);
        pane.setPrefHeight(490);
        VBox container = new VBox();
        container.setSpacing(3);
        GridPane leftPane = new GridPane();
        GridPane rightPane = new GridPane();
        leftPane.setPadding(new Insets(0,0,0,20));
        rightPane.setPadding(new Insets(0,15,0,0));
        pane.setRight(rightPane);
        pane.setLeft(leftPane);
        HBox send = new HBox();
        TextField msgField = new TextField();
        msgField.setPromptText("Enter a message");
        msgField.setFocusTraversable(false);
        msgField.setPrefColumnCount(40);
        Button sendBTN = new Button("Send");
        send.getChildren().addAll(msgField,sendBTN);
        send.setSpacing(5);
        pane.setStyle("-fx-background-color: #282c34");
        send.setStyle("-fx-background-color: #282c34");
        rightPane.setVgap(2);
        leftPane.setVgap(2);
        send.setAlignment(Pos.CENTER);
        send.setPadding(new Insets(0,0,3,0));
        sendBTN.setOnAction(e->{
            if(!msgField.getText().isEmpty()){
                try {
                    Socket socket = new Socket("192.168.43.21",8000);
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    out.writeUTF(username+"&"+msgField.getText());
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        });
        //leftPane
        //pane.setHgap(20);
        scrollPane.setContent(pane);
        container.getChildren().addAll(scrollPane,send);
        try {
            int row = 0;
            while (resultSet.next()) {
                Label text;
                if(resultSet.getString(1).equals(this.username)){
                    text = new Label(resultSet.getString(2));
                    text.setStyle("-fx-background-color: white;-fx-border-radius: 10;-fx-border-width: 1; -fx-font-size: 15; -fx-padding: 2 4");
                    rightPane.addRow(row,text);
                    leftPane.addRow(row,new Text(""));
                    row++;
                    text = new Label("You");
                    text.setStyle("-fx-text-fill: white;-fx-border-radius: 10;-fx-border-width: 1; -fx-font-size: 10; -fx-font-weight: bold;");
                    rightPane.addRow(row,text);
                    leftPane.addRow(row,new Text(""));
                }
                else{
                    text = new Label(resultSet.getString(2));
                    text.setStyle("-fx-background-color: #9aff35;-fx-border-radius: 10;-fx-border-width: 1; -fx-font-size: 15; -fx-padding: 2 4");
                    leftPane.addRow(row,text);
                    rightPane.addRow(row,new Text(""));
                    row++;
                    text = new Label(resultSet.getString(1));
                    text.setStyle("-fx-text-fill: white;-fx-border-radius: 10;-fx-border-width: 1; -fx-font-size: 10; -fx-font-weight: bold");
                    leftPane.addRow(row,text);
                    rightPane.addRow(row,new Text(""));
                }
                row++;
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        stage.setScene(new Scene(container,810,500));
        stage.centerOnScreen();
        stage.setTitle("Chit Chat");
        stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
