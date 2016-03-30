package tv.starcards.starcardstv.application.ui.models;

public class PacketListModel {

    private  String id = "";
    private  String name="";
    private  String date="";
    private String password="";
    private String status="";

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getPassword() {
        return password;
    }

    public String getStatus() {
        return status;
    }
}
