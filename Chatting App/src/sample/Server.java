package sample;

import java.io.IOException;
import java.net.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class Server {
    public static void main(String[] args){
        ServerSocket serverSocket;
        try {
             serverSocket = new ServerSocket(8000);
             while(true) {
                 Socket server = serverSocket.accept();
                 DataInputStream in = new DataInputStream(server.getInputStream());
                 DataOutputStream out = new DataOutputStream(server.getOutputStream());
                 System.out.println(in.readUTF());
                 out.writeUTF("The connection succeeded");
             }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
