package tv.starcards.starcardstv.application.data.tokens;

public class LoginAccessToken {
    private static LoginAccessToken ourInstance = new LoginAccessToken();
    private static String accessToken;

    private LoginAccessToken() {

    }

    public static LoginAccessToken getInstance() {
        return ourInstance;
    }

    public String getAccessToken(){
        return accessToken;
    }

    public void setAccessToken(String token){
        accessToken = token;
    }
}
