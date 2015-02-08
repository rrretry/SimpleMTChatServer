package earth.retry.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    final ServerSocket serverSocket;
    final CopyOnWriteArrayList<Client> clients = new CopyOnWriteArrayList<>();
    final StringBuffer buffer = new StringBuffer();

    public Server(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        serveClients();
    }

    private void serveClients() {
        new Thread(() -> {
            try {
                while (true) {
                    clients.add(new Client(serverSocket.accept()));
                    System.out.println(clients.size()+" : " + Thread.activeCount());
                }
            } catch (IOException e) {
                System.out.println("New client error: " + e);
            }
        }).start();
    }

    class Client implements Runnable {
        final Socket socket;
        final DataInputStream input;
        final DataOutputStream output;
        final Thread thread;

        public Client(Socket accept) throws IOException {
            socket = accept;
            input = new DataInputStream(accept.getInputStream());
            output = new DataOutputStream(accept.getOutputStream());
            thread = new Thread(this);
            thread.start();
        }

        @Override
        public void run() {
            while (true) {
                try {
                    buffer.append(input.readUTF());
                    if(buffer.length()>0)
                        new Thread(new BroadCaster()).start();
                } catch (IOException d){
                    this.thread.interrupt();
                    clients.remove(this);
                    break;
                }
            }
        }
    }
    class BroadCaster implements Runnable {
        public static final int PACKET_SIZE = 65535;

        @Override
        public void run() {
            String localBuffer = new String(buffer);
            for (Client current : clients) {
                try {
                    if (current.thread.isAlive())
                        if (localBuffer.length() > PACKET_SIZE) {
                            int count = 0;
                            do {
                                current.output.writeUTF(localBuffer.substring(count++ * PACKET_SIZE, count * PACKET_SIZE));
                            } while (count * PACKET_SIZE < localBuffer.length());
                        } else
                            current.output.writeUTF(localBuffer);
                } catch (IOException ignored) {

                }
            }
            buffer.delete(0, localBuffer.length());
        }
    }
}
