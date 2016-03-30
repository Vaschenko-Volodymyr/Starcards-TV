package tv.starcards.starcardstv.application.data.userdata;

public class UserEmail {
    private static UserEmail ourInstance = new UserEmail();
    private String userEmail;

    private UserEmail() {
    }

    public static UserEmail getInstance() {
        return ourInstance;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
