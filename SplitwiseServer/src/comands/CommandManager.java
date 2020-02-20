package comands;
import user.Registration;
import user.UserFunctionality;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CommandManager {

    public static final int REGISTRATION_LENGTH = 3;
    public static final int LOGIN_LENGTH = 3;
    public static final int ADD_FRIEND_LENGTH = 2;
    public static final int GROUP_LENGTH = 3;
    public static final int SPLIT_LENGTH = 4;
    public static final int SPLIT_GROUP_LENGTH = 4;
    public static final int PAY_LENGTH = 4;
    public static final int HISTORY_LENGTH = 1;
    public static final int STATUS_LENGTH = 1;
    public static final int ONE = 1;
    public static final int TWO = 2;
    public static final int THREE = 3;
    UserFunctionality userFunctionality;

    public CommandManager(UserFunctionality uf) {
        this.userFunctionality = uf;
    }

    public static void checkCommand(String[] commands) throws IOException {
        if (commands[0].equalsIgnoreCase("register") && commands.length == REGISTRATION_LENGTH) {
            if (!Registration.checkRegistered(commands[ONE])) {
                Registration.registration(commands);
            } else {
                UserFunctionality.getSocketChannel().write(StandardCharsets.UTF_8.encode("User already registered!"));
            }
        } else if (commands[0].equalsIgnoreCase("login") && commands.length == LOGIN_LENGTH) {
            if (Registration.checkRegistered(commands[ONE]) && Registration.checkRegistered(commands[TWO])) {
                Registration.login(commands);
            } else {
                UserFunctionality.getSocketChannel().write(StandardCharsets.UTF_8.encode("You are not registered. " +
                        "To continue, please register."));
            }
        } else if (commands[0].equalsIgnoreCase("add-friend") && commands.length == ADD_FRIEND_LENGTH) {
            if (!UserFunctionality.getFriendList().contains(UserFunctionality.getRegisteredUser().get(commands[1]))) {
                UserFunctionality.addFriend(commands);
            } else {
                UserFunctionality.getSocketChannel().write((StandardCharsets.UTF_8.encode
                        ("User already in this group")));
            }
        } else if (commands[0].equalsIgnoreCase("create-group") && commands.length >= GROUP_LENGTH) {
            if (UserFunctionality.getLoggedInSocket().containsKey(UserFunctionality.getSocketChannel())) {
                UserFunctionality.createGroup(commands);
            } else {
                UserFunctionality.getSocketChannel().write(StandardCharsets.UTF_8.encode("You must log in"));
            }
        } else if (commands[0].equalsIgnoreCase("split") && commands.length == SPLIT_LENGTH) {
            if (UserFunctionality.getLoggedInSocket().containsKey(UserFunctionality.getSocketChannel())) {
                UserFunctionality.split(commands);
            } else {
                UserFunctionality.getSocketChannel().write(StandardCharsets.UTF_8.encode("You must log in"));
            }
        } else if (commands[0].equalsIgnoreCase("split-group") && commands.length >= SPLIT_GROUP_LENGTH) {
            if (UserFunctionality.getLoggedInSocket().containsKey(UserFunctionality.getSocketChannel())) {
                UserFunctionality.splitGroup(commands);
            } else {
                UserFunctionality.getSocketChannel().write(StandardCharsets.UTF_8.encode("You must log in"));
            }
        } else if (commands[0].equalsIgnoreCase("payed") && commands.length == PAY_LENGTH) {
            if (UserFunctionality.getLoggedInSocket().containsKey(UserFunctionality.getSocketChannel())) {
                if (Registration.checkRegistered(commands[THREE])) {
                    UserFunctionality.pay(commands);
                } else {
                    UserFunctionality.getSocketChannel().write(StandardCharsets.UTF_8.encode("User is not registered"));
                }
            } else {
                UserFunctionality.getSocketChannel().write(StandardCharsets.UTF_8.encode("You must log in"));
            }
        } else if (commands[0].equalsIgnoreCase("payment-history") && commands.length == HISTORY_LENGTH) {
            UserFunctionality.printPaymentHistory();
        } else if (commands[0].equalsIgnoreCase("get-status") && commands.length == STATUS_LENGTH) {
            UserFunctionality.printStatus();
        } else {
            UserFunctionality.getSocketChannel().write(StandardCharsets.UTF_8.encode
                    ("Something is wrong with your input.Try again"));
        }
    }
}
