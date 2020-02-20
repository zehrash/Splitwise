package user;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Registration {
    public static boolean checkRegistered(String username) {

        try (BufferedReader reader = new BufferedReader(new FileReader("resources/registered.txt"))) {
            String line = reader.readLine();
            while (line != null) {
                String[] text = line.split(" ");
                for (int i = 0; i < text.length; i++) {
                    if (text[i].equals(username)) {
                        return true;
                    }
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            UserFunctionality.writeMessage("Unable to check registered user.Try again or contact " +
                    "administrator by providing the logs in stacktrace.txt", " ");
            UserFunctionality.saveStackTrace(UserFunctionality.getStacktrace(), e);
        }
        return false;
    }

    public static void registration(String[] commands) {
        try {
            UserFunctionality.getFile().write(commands[1].getBytes());
            UserFunctionality.getFile().write(" ".getBytes());
            UserFunctionality.getFile().write(commands[2].getBytes());
            UserFunctionality.getFile().write("\n".getBytes());
            User user = new User(commands[1]);
            UserFunctionality.getRegisteredUser().put(commands[1], user);
            UserFunctionality.getSocketChannel().write(StandardCharsets.UTF_8.encode("Successful registration!"));
        } catch (IOException e) {
            UserFunctionality.writeMessage("Unable to register user.Try again or contact " +
                            "administrator by providing the logs in stacktrace.txt",
                    UserFunctionality.getCurrentUser() + "tried to register");

            UserFunctionality.saveStackTrace(UserFunctionality.getStacktrace(), e);
        }
    }

    public static void login(String[] commands) {

        try {
            UserFunctionality.getSocketChannel().write(StandardCharsets.UTF_8.encode("You logged in!"));
            User user = UserFunctionality.getRegisteredUser().get(commands[1]);
            UserFunctionality.getLoggedIn().put(user, UserFunctionality.getSocketChannel());
            UserFunctionality.getLoggedInSocket().put(UserFunctionality.getSocketChannel(), user);
            UserFunctionality.getNotified();

        } catch (IOException e) {
            UserFunctionality.writeMessage("Unable to login user.Try again or contact " +
                            "administrator by providing the logs in stacktrace.txt",
                    UserFunctionality.getCurrentUser() + "tried to log in");
            UserFunctionality.saveStackTrace(UserFunctionality.getStacktrace(), e);
        }
    }

}
