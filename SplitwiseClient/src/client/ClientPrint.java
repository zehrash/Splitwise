package client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ClientPrint implements Runnable {


    private SocketChannel socketChannel;
    private static ByteBuffer buffer = ByteBuffer.allocate(1024);

    ClientPrint(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    @Override
    public void run() {
        while (true) {
            try {
                buffer.clear();
                socketChannel.read(buffer);
                buffer.flip();
                String message = new String(buffer.array(), 0, buffer.limit());
                System.out.println(message);

            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

    }
}