package tv.starcards.starcardstv.application.data.state;

public class IsLogged {
    public static IsLogged instance = new IsLogged();
    public boolean isLogged;

    private IsLogged() {
    }

    public static IsLogged getInstance() {
        if (instance == null) {
            instance = new IsLogged();
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
