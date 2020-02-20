package user;

import group.Group;

import java.io.*;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UserFunctionality {
    private static Map<String, User> registeredUser = new HashMap<>();
    private static Set<User> friendList = new HashSet<>();
    private static User currentUser;
    private static SocketChannel socketChannel;
    private static Map<User, SocketChannel> loggedIn = new HashMap<>();
    private static Map<SocketChannel, User> loggedInSocket = new HashMap<>();
    private static OutputStream stacktrace;

    public UserFunctionality() {

        setSocketChannel(socketChannel);
        loadFile();
    }

    private static OutputStream file;

    public static User getCurrentUser() {

        return currentUser;
    }

    public static OutputStream getFile() {

        return file;
    }

    public static Map<User, SocketChannel> getLoggedIn() {
        return loggedIn;
    }

    public void loadFile() {
        try {
            stacktrace = new FileOutputStream("resources/stacktrace.txt", true);
            file = new FileOutputStream("resources/registered.txt", true);
        } catch (FileNotFoundException e) {
            saveStackTrace(stacktrace, e);
        }
    }

    public static Set<User> getFriendList() {
        return friendList;
    }

    public static Map<SocketChannel, User> getLoggedInSocket() {
        return loggedInSocket;
    }

    public static OutputStream getStacktrace() {
        return stacktrace;
    }

    public static Map<String, User> getRegisteredUser() {
        return registeredUser;
    }

    public static SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public static void saveStackTrace(OutputStream stream, Exception e) {
        try (PrintStream ps = new PrintStream(stream)) {
            e.printStackTrace(ps);
            stream.write("\n".getBytes());
        } catch (IOException ex) {
            ex.getMessage();
        }
    }

    public void setSocketChannel(SocketChannel socketChannel) {
        UserFunctionality.socketChannel = socketChannel;
        currentUser = loggedInSocket.get(socketChannel);

    }

    public static void addFriend(String[] commands) {
        User user = registeredUser.get(commands[1]);
        try {
            friendList.add(user);
            friendList.add(currentUser);
            currentUser.addToFriendGroup(commands[1]);
            user.addToFriendGroup(currentUser.username);
            socketChannel.write(StandardCharsets.UTF_8.encode("Friend added"));
        } catch (IOException e) {
            writeMessage("Unable to add friend.Try again or contact " +
                            "administrator by providing the logs in stacktrace.txt",
                    currentUser.username + "tried to split money");
            saveStackTrace(stacktrace, e);
        }
    }

    public static void createGroup(String[] commands) {
        HashSet<User> currGroups = new HashSet<>();
        for (int i = 2; i < commands.length; i++) {
            if (Registration.checkRegistered(commands[i])) {
                User user = registeredUser.get(commands[i]);
                currGroups.add(user);
            }
        }
        currGroups.add(currentUser);
        for (User u : currGroups) {
            u.createUserGroup(commands[1], currGroups);
        }
        try {
            socketChannel.write(StandardCharsets.UTF_8.encode("Successfully created a group"));
        } catch (IOException e) {
            writeMessage("Unable to create group.Try again or contact " +
                            "administrator by providing the logs in stacktrace.txt",
                    currentUser.username + "tried to create a group");

            saveStackTrace(stacktrace, e);
        }
    }

    public static void split(String[] commands) {
        User friendUser = registeredUser.get(commands[2]);
        currentUser.getFriendGroup().splitWithFriend(friendUser, currentUser, Double.parseDouble(commands[1]));
        try {
            socketChannel.write(StandardCharsets.UTF_8.encode("Successful split"));
        } catch (IOException e) {
            writeMessage("Unable to split money.Try again or contact " +
                            "administrator by providing the logs in stacktrace.txt",
                    currentUser.username + "tried to split money");
            saveStackTrace(stacktrace, e);
        }
    }


    public static void splitGroup(String[] commands) {
        currentUser.getUserGroups(commands[2])
                .splitWithGroup(currentUser, Double.parseDouble(commands[1]), currentUser.getUserGroups(commands[2]).getGroups());
        try {
            socketChannel.write(StandardCharsets.UTF_8.encode("Successful split"));
        } catch (IOException e) {
            writeMessage("Unable to split money in group.Try again or contact " +
                            "administrator by providing the logs in stacktrace.txt",
                    currentUser.username + "tried to split money");

            saveStackTrace(stacktrace, e);
        }
    }

    //pay amount group username
    public static void pay(String[] commands) {
        User user = registeredUser.get(commands[3]);
        double amount = Double.parseDouble(commands[1]);
        try {
            if (commands[2].equals("friends")) {
                currentUser.getFriendGroup().pay(currentUser, amount, user);
                loggedIn.get(user).write(StandardCharsets.UTF_8.encode("Successful payment!"));
                socketChannel.write(StandardCharsets.UTF_8.encode(user.username + " payed you " + amount));

            } else {
                currentUser.getUserGroups(commands[2]).pay(currentUser, amount, user);
                loggedIn.get(user).write(StandardCharsets.UTF_8.encode("Successful payment!"));
                socketChannel.write(StandardCharsets.UTF_8.encode(user.username + " payed you " + amount));
            }
        } catch (IOException e) {
            writeMessage("Unable to finish payment.Try again or contact " +
                            "administrator by providing the logs in stacktrace.txt",
                    currentUser.username + "tried to tried to pay");

            saveStackTrace(stacktrace, e);
        }
    }

    public static void printStatus() {
        try {
            socketChannel.write(StandardCharsets.UTF_8.encode("Friends"));
            if (currentUser.getFriendGroup().getDebt().size() > 0) {
                for (Map.Entry<String, HashMap<Group.PaymentType, Double>> entry :
                        currentUser.getFriendGroup().getDebt().entrySet()) {
                    if (entry.getValue().get(Group.PaymentType.OWES_ME) > 0 || entry.getValue().get(Group.PaymentType.YOU_OWE) > 0) {
                        socketChannel.write(StandardCharsets.UTF_8.encode
                                (entry.getKey() + ": You owe " + entry.getValue().get(Group.PaymentType.YOU_OWE)));
                        socketChannel.write(StandardCharsets.UTF_8.encode
                                (entry.getKey() + ": Owes me " + entry.getValue().get(Group.PaymentType.OWES_ME)));
                    }
                }
            }
            for (Group group : currentUser.userGroups) {
                socketChannel.write(StandardCharsets.UTF_8.encode(group.getGroupName()));
                for (Map.Entry<String, HashMap<Group.PaymentType, Double>> entry :
                        currentUser.getUserGroups(group.getGroupName()).getDebt().entrySet()) {
                    if (entry.getValue().get(Group.PaymentType.YOU_OWE) > 0 || entry.getValue().get(Group.PaymentType.OWES_ME) > 0) {
                        socketChannel.write(StandardCharsets.UTF_8.encode
                                (entry.getKey() + ": You owe " + entry.getValue().get(Group.PaymentType.YOU_OWE)));
                        socketChannel.write(StandardCharsets.UTF_8.encode
                                (entry.getKey() + ": Owes me " + entry.getValue().get(Group.PaymentType.OWES_ME)));
                    }
                }
            }
        } catch (IOException e) {
            writeMessage("Unable to print status.Try again or contact " +
                            "administrator by providing the logs in stacktrace.txt",
                    currentUser.username + "tried to check status");
            saveStackTrace(stacktrace, e);
        }
    }

    public static void printPaymentHistory() {
        try (BufferedReader reader = currentUser.getReader()) {
            String line = reader.readLine();
            while (line != null) {
                socketChannel.write(StandardCharsets.UTF_8.encode(line));
                line = reader.readLine();
            }
        } catch (IOException e) {
            writeMessage("Unable to print payment.Try again or contact " +
                            "administrator by providing the logs in stacktrace.txt",
                    currentUser.username + "tried to check payment history");
            saveStackTrace(stacktrace, e);
        }
    }

    public static void getNotified() {
        try {

            if (loggedIn.containsKey(getLoggedInSocket().get(socketChannel))
                    && getLoggedInSocket().get(socketChannel).searchDebt()) {
                User curr = getLoggedInSocket().get(socketChannel);

                BufferedReader reader = new BufferedReader(new FileReader("resources/" + curr.username + "groupNotify.txt"));
                String line = reader.readLine();
                StringBuilder sb = new StringBuilder();
                while (line != null) {
                    sb.append(line).append("\n");
                    line = reader.readLine();
                }
                BufferedReader reader1 = new BufferedReader(new FileReader("resources/" + curr.username + "friendNotify.txt"));
                String line1 = reader1.readLine();
                StringBuilder sb1 = new StringBuilder();
                while (line1 != null) {
                    sb.append(line1).append("\n");
                    line1 = reader.readLine();
                }

                String textGroup = sb.toString();
                String textFriend = sb1.toString();

                String[] text1 = textFriend.split("\n");
                String[] text2 = textGroup.split("\n");
                if (text1.length >= 3) {
                    for (int i = text1.length - 1; i >= text1.length - 3; i--) {
                        socketChannel.write(StandardCharsets.UTF_8.encode(text1[i]));
                    }
                } else {
                    for (int i = text1.length - 1; i >= 0; i--) {
                        socketChannel.write(StandardCharsets.UTF_8.encode(text1[i]));
                    }
                }
                if (text2.length >= 3) {
                    for (int i = text2.length - 1; i >= text2.length - 3; i--) {
                        socketChannel.write(StandardCharsets.UTF_8.encode(text2[i]));
                    }
                } else {
                    for (int i = text2.length - 1; i >= 0; i--) {
                        socketChannel.write(StandardCharsets.UTF_8.encode(text2[i]));
                    }
                }
            } else {
                socketChannel.write(StandardCharsets.UTF_8.encode("No new notifications"));
            }
        } catch (
                IOException e) {
            saveStackTrace(stacktrace, e);
        }
    }

    static void writeMessage(String message, String messageFile) {

        try {
            stacktrace.write(messageFile.getBytes());
            stacktrace.write("\n".getBytes());
            socketChannel.write(StandardCharsets.UTF_8.encode(message));

        } catch (IOException e) {
            saveStackTrace(stacktrace, e);
        }
    }
}

