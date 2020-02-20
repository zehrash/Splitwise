package group;

import user.User;

import java.util.HashMap;
import java.util.HashSet;

public class UserGroup extends Group {
    private String groupName;
    private HashSet<User> users;


    public UserGroup(String name, HashSet<User> users) {
        this.groupName = name;
        this.users = users;
    }

    public HashSet<User> getGroups() {
        return users;
    }

    public String getGroupName() {
        return groupName;
    }

    @Override
    public void pay(User currentUser, double amount, User user) {
        double currentAmount = getDebt().get(user.username).get(PaymentType.OWES_ME);

        getDebt().get(user.username).put(PaymentType.OWES_ME, currentAmount - amount);
        user.getUserGroups(groupName).getDebt().get(currentUser.username)
                .put(PaymentType.YOU_OWE, currentAmount - amount);
        user.addToHistory("You payed " + currentUser.username + amount + "lv");
        currentUser.addNotification(groupName, user.username + " payed you " + amount);
        user.addNotification(groupName, currentUser.username + " approved your payment");
    }

    public void splitWithGroup(User currUser, double amount, HashSet<User> users) {
        double userAmount = amount / users.size();

        for (User u : users) {
            if (u.equals(currUser)) {
                continue;
            }
            if (getDebt().containsKey(u.username)) {
                double currentAmount = getDebt().get(u.username).get(Group.PaymentType.YOU_OWE);
                double currUserAmount = getDebt().get(u.username).get(Group.PaymentType.OWES_ME);
                double finalAmount = userAmount - currentAmount;
                if (finalAmount >= 0) {
                    if (currentAmount == 0) {
                        getDebt().get(u.username).put(Group.PaymentType.OWES_ME, currUserAmount + userAmount);
                        u.getUserGroups(groupName).getDebt().get(currUser.username)
                                .put(Group.PaymentType.YOU_OWE, currUserAmount + userAmount);
                    } else {
                        getDebt().get(u.username).put(Group.PaymentType.OWES_ME, finalAmount);
                        u.getUserGroups(groupName).getDebt().get(currUser.username).put(Group.PaymentType.YOU_OWE, finalAmount);
                    }
                } else {
                    u.getUserGroups(groupName).getDebt().get(currUser.username).put(Group.PaymentType.OWES_ME, Math.abs(finalAmount));
                    getDebt().get(u.username).put(Group.PaymentType.YOU_OWE, Math.abs(finalAmount));
                }
            } else {
                getDebt().put(u.username, new HashMap<>());
                getDebt().get(u.username).put(Group.PaymentType.OWES_ME, userAmount);
                getDebt().get(u.username).put(Group.PaymentType.YOU_OWE, 0.0);
            }
        }
    }
}
