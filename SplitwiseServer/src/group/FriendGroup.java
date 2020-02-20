package group;

import user.User;
public class FriendGroup extends Group {
    String groupName = "Friends";

    public void splitWithFriend(User user, User currUser, double amount) {

        double userAmount = amount / 2;
        double currentAmount = user.getFriendGroup().getDebt().get(currUser.username).get(Group.PaymentType.OWES_ME);
        double finalAmount = userAmount - currentAmount;
        if (getDebt().containsKey(user.username)) {
            if (finalAmount >= 0) {
                getDebt().get(user.username).put(Group.PaymentType.OWES_ME, finalAmount);
                user.getFriendGroup().getDebt().get(currUser.username).put(Group.PaymentType.YOU_OWE, finalAmount);
            } else {
                user.getFriendGroup().getDebt().get(currUser.username).put(Group.PaymentType.OWES_ME, Math.abs(finalAmount));
                getDebt().get(user.username).put(Group.PaymentType.YOU_OWE, Math.abs(finalAmount));
            }
        } else {
            getDebt().get(user.username).put(Group.PaymentType.OWES_ME, userAmount);
        }
    }
    @Override
    public void pay(User currentUser, double amount, User user) {
        double currentAmount = getDebt().get(user.username).get(PaymentType.OWES_ME);
        getDebt().get(user.username).put(PaymentType.OWES_ME, currentAmount - amount);
        user.getFriendGroup().getDebt().get(currentUser.username).put(PaymentType.YOU_OWE, currentAmount - amount);
        user.addToHistory("You payed " + currentUser.username + " " + amount + "lv");
        currentUser.addNotification(groupName, user.username + " payed you " + amount);
        user.addNotification(groupName, currentUser.username + " approved your payment");
    }
}
