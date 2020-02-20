package user;

import group.FriendGroup;
import group.Group;
import group.UserGroup;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class User {
    public String username;
    private FriendGroup friendGroup;

    public HashSet<Group> getUserGroups() {
        return userGroups;
    }

    HashSet<Group> userGroups;
    private FileOutputStream paymentLog;
    private FileOutputStream friendNotification;
    private FileOutputStream groupNotification;
    private BufferedReader reader;


    BufferedReader getReader() {
        return reader;
    }

    public void loadUser() {
        try {
            paymentLog = new FileOutputStream("resources/" + username + "payment.txt");
            reader = new BufferedReader(new FileReader("resources/" + username + "payment.txt"));
            friendNotification = new FileOutputStream("resources/" + username + "friendNotify.txt");
            groupNotification = new FileOutputStream("resources/" + username + "groupNotify.txt");
        } catch (FileNotFoundException e) {
            UserFunctionality.saveStackTrace(UserFunctionality.getStacktrace(), e);
        }
    }

    public void addNotification(String groupName, String message) {
        try {
            if (groupName.equals("Friends")) {
                friendNotification.write(("-->" + groupName + "<--" + message).getBytes());
                friendNotification.write("\n".getBytes());
            } else {
                groupNotification.write(("-->" + groupName + "<--" + message).getBytes());
                groupNotification.write("\n".getBytes());
            }
        } catch (IOException e) {
            UserFunctionality.saveStackTrace(UserFunctionality.getStacktrace(), e);
        }
    }

    public boolean searchDebt() {
        if (userGroups.size() == 0) {
            return false;
        }
        for (Group group : userGroups) {
            for (Map.Entry<String, HashMap<Group.PaymentType, Double>> entry :
                    getUserGroups(group.getGroupName()).getDebt().entrySet()) {
                if (entry.getValue().get(Group.PaymentType.YOU_OWE) > 0) {
                    addNotification(group.getGroupName(),
                            "You owe " + entry.getKey() + entry.getValue().get(Group.PaymentType.YOU_OWE));
                }
            }
        }
        return true;
    }

    public FriendGroup getFriendGroup() {
        return friendGroup;
    }

    public void addToHistory(String message) {
        try {
            paymentLog.write(message.getBytes());
            paymentLog.write("\n".getBytes());
        } catch (IOException e) {
            UserFunctionality.saveStackTrace(UserFunctionality.getStacktrace(), e);
        }
    }

    public UserGroup getUserGroups(String name) {
        UserGroup current = null;
        for (Group group : userGroups) {
            if (group.getGroupName().equals(name)) {
                current = (UserGroup) group;
            }
        }
        return current;
    }

    public User(String username) {
        this.username = username;
        userGroups = new HashSet<>();
        friendGroup = new FriendGroup();
        loadUser();
    }

    public void createUserGroup(String groupName, HashSet<User> users) {
        Group group = new UserGroup(groupName, users);
        userGroups.add(group);
        for (User u : users) {
            group.addToGroup(u.username);
        }
    }

    public void addToFriendGroup(String name) {
        friendGroup.addToGroup(name);
    }
}

