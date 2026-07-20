public class AfterClass {
    public static void main(String[] args) {
        Account account = new Account("test user");
        StringBuffer user = account.getUser();
        user.append(" append");
        System.out.println(account.getUser());
    }
}

final class Account {
    
    private final StringBuffer user;

    public Account(String user) {
        this.user = new StringBuffer(user);
    }

    public StringBuffer getUser() {
        return this.user;
    }

    public String toString() {
        return "user:" + user;
    }
}
