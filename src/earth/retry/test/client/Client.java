package earth.retry.test.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

public class Client implements Runnable{
    final Socket     socket;
    final byte       identification;
    DataOutputStream output;
    DataInputStream  input;
    Thread           thread;


    public static void main(String[] args) throws IOException {
        new Client("localhost", 6666);
    }

    public Client(String address, int port) throws IOException {
        this.socket = new Socket(address, port);
        output = new DataOutputStream(socket.getOutputStream());
        input = new DataInputStream(socket.getInputStream());
        byte[] randIdentification=new byte[10];
        new Random().nextBytes(randIdentification);
        this.identification = randIdentification[0];
        System.out.println("ID: " + identification);
        thread=new Thread(this);
        thread.start();
    }

    void imitateActivity() {
        while (!(socket.isClosed()))
        {
            try {
                output.writeUTF(" "+identification);
                Thread.sleep(1000);
                //System.out.println(identification+":"+input.readUTF());
                input.readUTF();
            } catch (IOException | InterruptedException e) {
                System.out.println("Client"+this.hashCode()+":"+e.getMessage());
                //break;
            }
        }
    }

    @Override
    public void run() {
        imitateActivity();
    }
}
