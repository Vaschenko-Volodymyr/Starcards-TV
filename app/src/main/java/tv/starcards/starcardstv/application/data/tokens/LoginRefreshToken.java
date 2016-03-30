package tv.starcards.starcardstv.application.data.tokens;


public class LoginRefreshToken {
    private static LoginRefreshToken ourInstance = new LoginRefreshToken();
    private static String refreshToken;

    private LoginRefreshToken() {
    }

    public static LoginRefreshToken getInstance() {
        return ourInstance;
    }

    public String getRefreshToken(){
        return refreshToken;
    }

    public void setRefreshToken(String token){
        refreshToken = token;
    }

}
