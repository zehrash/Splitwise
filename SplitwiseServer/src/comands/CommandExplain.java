package comands;

public class CommandExplain {

    public void help() {
        System.out.println();
        System.out.println();
        System.out.println("**********Welcome to Split(NotSo)wise**********");
        System.out.println("To use the functions you have to register--->choose register and then login in");
        System.out.println("To continue, please choose one of the following commands");
        System.out.println("--->register");
        System.out.println("--->login");
        System.out.println("--->add-friend <username> - if you want to add friend to your friends list");
        System.out.println("--->create-group <group_name> <username> <username> ... <username> - if you want to create a group and add users to it");
        System.out.println("--->split <amount> <username> <reason_for_payment> - if you want to split money with a friend ");
        System.out.println("--->split-group <amount> <group_name> <reason_for_payment> if you want to split money in a group");
        System.out.println("--->payed <amount> <group> <username> -  if you have received money from a user, you can log it in your group");
        System.out.println("--->get-status - if you want to see your current status");
        System.out.println("--->payment-history - if you want to see your payment history ");
    }
}
