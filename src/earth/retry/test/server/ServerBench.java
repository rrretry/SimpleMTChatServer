package earth.retry.test.server;

import earth.retry.test.client.Client;

import java.io.IOException;

public class ServerBench {

    public static void main(String[] args) throws InterruptedException, IOException {
        for (int i = 0; i < 3000; i++) {
            new Client("localhost", 6666);
            Thread.sleep(10);
            if(i%100==0) Thread.sleep(10000);
        }
    }
}
