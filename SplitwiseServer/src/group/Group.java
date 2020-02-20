package group;


import user.User;

import java.util.HashMap;
import java.util.Map;

public abstract class Group {
    private Map<String, HashMap<PaymentType, Double>> debt = new HashMap<>();
    String groupName;

    public String getGroupName() {
        return groupName;
    }

    public enum PaymentType {
        YOU_OWE,
        OWES_ME;
    }

    public void addToGroup(String username) {
        debt.put(username, new HashMap<PaymentType, Double>());
        debt.get(username).put(PaymentType.OWES_ME, 0.0);
        debt.get(username).put(PaymentType.YOU_OWE, 0.0);
    }

    public Map<String, HashMap<PaymentType, Double>> getDebt() {
        return debt;
    }

    abstract void pay(User currentUser, double amount, User user);


}
