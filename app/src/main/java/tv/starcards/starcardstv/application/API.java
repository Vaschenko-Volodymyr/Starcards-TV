package tv.starcards.starcardstv.application;

public class API {

    public static final String REGISTER_REDIRECT = "http://starcards.tv/register"; // http://dev28.starcards.tv/register
    public static final String REMIND_REDIRECT = "http://starcards.tv/"; // http://dev28.starcards.tv/

    public static final String API = "https://api.starcards.tv/user"; // http://api28.starcards.tv/user
    public static final String AUTH            = API + "/login";
    public static final String USER_INFO       = API + "/info";
    public static final String PACKETS_SUMMARY = API + "/packets";
    public static final String LOG_IN_PACKET   = API + "/token";
    public static final String MESSAGE_HISTORY = API + "/messages/history";
    public static final String MESSAGE         = API + "/messages/";

    public static final String LINK = "/link";
    public static final String TV_CHANNELS = "/tv-channels";

}
