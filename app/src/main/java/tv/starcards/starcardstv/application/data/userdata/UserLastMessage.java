package tv.starcards.starcardstv.application.data.userdata;

public class UserLastMessage {
    private static UserLastMessage ourInstance = new UserLastMessage();
    private String userMessage;

    private UserLastMessage() {
    }

    public static UserLastMessage getInstance() {
        return ourInstance;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }
}
