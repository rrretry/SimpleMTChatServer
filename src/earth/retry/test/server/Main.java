package earth.retry.test.server;

import earth.retry.server.Server;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        new Server(6666);
    }
}
