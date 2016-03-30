package tv.starcards.starcardstv.application.data.tokens;

public class PacketRefreshToken {
    private static PacketRefreshToken ourInstance = new PacketRefreshToken();
    private static String refreshToken;

    private PacketRefreshToken() {
    }

    public static PacketRefreshToken getInstance() {
        return ourInstance;
    }

    public String getPacketRefreshToken(){
        return refreshToken;
    }

    public void setPacketRefreshToken(String token){
        refreshToken = token;
    }

}
