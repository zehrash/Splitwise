
import group.FriendGroup;
import group.Group;
import org.junit.Assert;
import org.junit.Test;
import user.User;

import java.util.HashSet;

public class TestLogic {

    @Test
    public void addFriendTest() {
        FriendGroup friendGroup = new FriendGroup();
        String username = "Zehi";
        User user = new User("Jon");
        user.addToFriendGroup(username);
        //friendGroup.addToGroup(username);
        long size = user.getFriendGroup().getDebt().size();

        Assert.assertEquals(1, size);
    }

    @Test
    public void splitWithFriendTest() {
        User current = new User("Zehi");
        User user = new User("Jon");
        current.addToFriendGroup(user.username);
        user.addToFriendGroup(current.username);
        current.getFriendGroup().splitWithFriend(user, current, 60);


        Double actual = current.getFriendGroup().getDebt().get(user.username).get(Group.PaymentType.OWES_ME);
        Double expected = 30.0;
        Assert.assertEquals(expected, actual);

    }

    @Test
    public void payToGroup() {
        String groupName = "Fam";
        User user1 = new User("Arya");
        User user2 = new User("Jon");
        User user3 = new User("Sansa");
        HashSet<User> users = new HashSet<>();
        users.add(user1);
        users.add(user2);
        users.add(user3);
        for (User u : users) {
            u.createUserGroup(groupName, users);
        }

        user1.getUserGroups(groupName).splitWithGroup(user1, 90, users);
        user1.getUserGroups(groupName).pay(user1, 30, user2);
        Double actual = user1.getUserGroups(groupName).getDebt().get(user2.username).get(Group.PaymentType.OWES_ME);
        Double expected = 0.0;
        Assert.assertEquals(expected, actual);

    }

    @Test
    public void createGroupTest() {
        String groupName = "Family";
        User user1 = new User("Arya");
        User user2 = new User("Jon");
        User user3 = new User("Sansa");
        HashSet<User> users = new HashSet<>();
        users.add(user1);
        users.add(user2);
        users.add(user3);

        user1.createUserGroup(groupName, users);
        int expected = 1;
        int actual = user1.getUserGroups().size();

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void payToFriend() {
        User current = new User("Zehi");
        User user = new User("Jon");
        current.addToFriendGroup(user.username);
        user.addToFriendGroup(current.username);
        current.getFriendGroup().splitWithFriend(user, current, 60);

        current.getFriendGroup().pay(current, 30, user);
        Double actual = current.getFriendGroup().getDebt().get(user.username).get(Group.PaymentType.OWES_ME);

        Double expected = 0.0;
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void searchDebtTest() {
        String groupName = "North";
        User user1 = new User("Arya");
        User user2 = new User("Jon");
        User user3 = new User("Sansa");
        HashSet<User> users = new HashSet<>();
        users.add(user1);
        users.add(user2);
        users.add(user3);
        for (User u : users) {
            u.createUserGroup(groupName, users);
        }
        user2.getUserGroups(groupName).splitWithGroup(user2, 30, users);
        boolean actual = user1.searchDebt();
        Assert.assertTrue(actual);
    }


}
