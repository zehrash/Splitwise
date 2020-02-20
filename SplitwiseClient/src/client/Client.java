package client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Client {

    private static final int SERVER_PORT = 7775;
    public static final int CAPACITY = 1024;
    private static ByteBuffer buffer = ByteBuffer.allocate(CAPACITY);

    public static void main(String[] args) {

        try (SocketChannel socketChannel = SocketChannel.open();
             Scanner scanner = new Scanner(System.in)) {

            socketChannel.connect(new InetSocketAddress("localhost", SERVER_PORT));

            System.out.println("Connected to the server.");
            new Thread(new ClientPrint(socketChannel)).start();

            while (true) {
                String message = scanner.nextLine();
                buffer.clear();
                buffer.put(message.getBytes());
                buffer.flip();
                socketChannel.write(buffer);

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}