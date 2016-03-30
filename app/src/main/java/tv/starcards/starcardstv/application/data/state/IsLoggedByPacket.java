package tv.starcards.starcardstv.application.data.state;

public class IsLoggedByPacket {
    public static IsLoggedByPacket instance = new IsLoggedByPacket();
    public boolean isLogged = false;

    private IsLoggedByPacket() {
    }

    public static IsLoggedByPacket getInstance() {
        if (instance == null) {
            instance = new IsLoggedByPacket();
        }
        return instance;
    }

    public void saveLoggedState(String state) {
        isLogged = Boolean.parseBoolean(state);
    }

    public boolean isLogged() {
        return isLogged;
    }
}
