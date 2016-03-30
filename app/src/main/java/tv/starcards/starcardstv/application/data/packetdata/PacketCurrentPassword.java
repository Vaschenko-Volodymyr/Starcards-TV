package tv.starcards.starcardstv.application.data.packetdata;

public class PacketCurrentPassword {
    private String password;
    private static PacketCurrentPassword ourInstance = new PacketCurrentPassword();

    private PacketCurrentPassword() {
    }

    public static PacketCurrentPassword getInstance() {
        return ourInstance;
    }

    public void setPacketPassword(String password) {
        this.password = password;
    }

    public String getPacketPassword() {
        return password;
    }
}
