package tv.starcards.starcardstv.application.ui.models;

public class TvChannelListModel {

    private String id;
    private String title;
    private String genre;
    private String number;
    private String url;
    private String archiveRange;
    private String logo;
    private boolean archive;
    private boolean pvr;
    private boolean censored;
    private boolean favorite;
    private boolean available;
    private String packetId;

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setArchiveRange(String archiveRange) {
        this.archiveRange = archiveRange;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public void setArchive(boolean archive) {
        this.archive = archive;
    }

    public void setPvr(boolean pvr) {
        this.pvr = pvr;
    }

    public void setCensored(boolean censored) {
        this.censored = censored;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void setPacketId(String packetId) {
        this.packetId = packetId;
    }


    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public String getNumber() {
        return number;
    }

    public String getUrl() {
        return url;
    }

    public String getArchiveRange() {
        return archiveRange;
    }

    public String getLogo() {
        return logo;
    }

    public boolean isArchivable() {
        return archive;
    }

    public boolean isPvr() {
        return pvr;
    }

    public boolean isCensored() {
        return censored;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public boolean isAvailable() {
        return available;
    }

    public String getPacketId() {
        return packetId;
    }
}
