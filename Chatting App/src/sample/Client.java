package sample;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
    public static void main(String[] args){
        try {
            Socket client = new Socket("localhost", 8000);
            DataInputStream in = new DataInputStream(client.getInputStream());
            DataOutputStream out = new DataOutputStream(client.getOutputStream());
            out.writeUTF("hello from the client");
            System.out.println(in.readUTF());
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
