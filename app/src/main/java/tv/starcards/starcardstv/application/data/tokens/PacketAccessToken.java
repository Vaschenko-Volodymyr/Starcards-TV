package tv.starcards.starcardstv.application.data.tokens;

public class PacketAccessToken {
    private static PacketAccessToken ourInstance = new PacketAccessToken();
    private static String accessToken;

    private PacketAccessToken() {

    }

    public static PacketAccessToken getInstance() {
        return ourInstance;
    }

    public String getPacketAccessToken(){
        return accessToken;
    }

    public void setPacketAccessToken(String token){
        accessToken = token;
    }
}

