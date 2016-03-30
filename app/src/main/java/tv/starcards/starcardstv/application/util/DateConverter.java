package tv.starcards.starcardstv.application.util;

public class DateConverter {

    public String timestampToDate(String date) {
        String result = "";
        result = result + date.substring(8, 10) + " ";

        switch (date.substring(4, 7)) {
            case "Jan":
                result = result + "января ";
                break;
            case "Feb":
                result = result + "февраля ";
                break;
            case "Mar":
                result = result + "марта ";
                break;
            case "Apr":
                result = result + "апреля ";
                break;
            case "May":
                result = result + "мая ";
                break;
            case "Jun":
                result = result + "июня ";
                break;
            case "Jul":
                result = result + "июля ";
                break;
            case "Aug":
                result = result + "августа ";
                break;
            case "Sep":
                result = result + "сентября ";
                break;
            case "Oct":
                result = result + "октября ";
                break;
            case "Nov":
                result = result + "ноября ";
                break;
            case "Dec":
                result = result + "декабря ";
                break;
            default:
                break;
        }
        result = result + date.substring(date.length() - 4, date.length());
        result = result + ", ";
        result = result + date.substring(11, 16);
        return result;
    }
}
