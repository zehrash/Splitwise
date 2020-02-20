package server;

import comands.CommandExplain;
import comands.CommandManager;
import user.UserFunctionality;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Server {

    public static final int PORT = 7775;
    public static final int CAPACITY = 1024;

    public static void main(String[] args) {
        UserFunctionality userFunctionality = new UserFunctionality();
        CommandExplain commandExplain = new CommandExplain();
        commandExplain.help();
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {

            serverSocketChannel.bind(new InetSocketAddress("localhost", PORT));
            serverSocketChannel.configureBlocking(false);

            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            ByteBuffer buffer = ByteBuffer.allocate(CAPACITY);

            while (true) {
                int readyChannels = selector.select();
                if (readyChannels == 0) {
                    System.out.println("Still waiting for a ready channel...");
                }

                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();

                    if (key.isReadable()) {
                        SocketChannel socketChannel = (SocketChannel) key.channel();

                        userFunctionality.setSocketChannel(socketChannel);

                        buffer.clear();
                        int r = socketChannel.read(buffer);
                        if (r <= 0) {
                            System.out.println("nothing to read, will close channel");
                            socketChannel.close();
                            break;
                        }

                        buffer.flip();
                        String message = new String(buffer.array(), 0, buffer.limit());
                        String[] commands = message.split(" ");

                        CommandManager.checkCommand(commands);
                    } else if (key.isAcceptable()) {
                        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                        SocketChannel accept = ssc.accept();
                        accept.configureBlocking(false);
                        accept.register(selector, SelectionKey.OP_READ);
                    }
                    iterator.remove();
                }
            }
        } catch (IOException e) {
            UserFunctionality.saveStackTrace(UserFunctionality.getStacktrace(), e);
        }
    }
}

