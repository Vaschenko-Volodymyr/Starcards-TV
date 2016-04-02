package tv.starcards.starcardstv.application.util;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

public class Translit {

    final static int UPPER = 1;

    final static int LOWER = 2;

    final static Map<String, String> map = makeTranslitMap();

    private static Map<String, String> makeTranslitMap() {
        Map<String, String> map = new HashMap<>();
        map.put("а", "a");
        map.put("б", "b");
        map.put("в", "v");
        map.put("г", "g");
        map.put("д", "d");
        map.put("е", "e");
        map.put("ё", "yo");
        map.put("ж", "zh");
        map.put("з", "z");
        map.put("и", "i");
        map.put("й", "j");
        map.put("к", "k");
        map.put("л", "l");
        map.put("м", "m");
        map.put("н", "n");
        map.put("о", "o");
        map.put("п", "p");
        map.put("р", "r");
        map.put("с", "s");
        map.put("т", "t");
        map.put("у", "u");
        map.put("ф", "f");
        map.put("х", "h");
        map.put("ц", "ts");
        map.put("ч", "ch");
        map.put("ш", "sh");
        map.put("щ", "shch");
        map.put("ъ", "");
        map.put("ь", "");
        map.put("ы", "y");
        map.put("э", "e");
        map.put("ю", "iu");
        map.put("я", "ia");
        return map;
    }

    private static int charClass(char c) {
        return Character.isUpperCase(c) ? UPPER : LOWER;
    }

    private static String get(String s) {
        int charClass = charClass(s.charAt(0));
        String result = map.get(s.toLowerCase());
        return result == null ? "" : (charClass == UPPER ? (result.charAt(0) + "").toUpperCase() +
                (result.length() > 1 ? result.substring(1) : "") : result);
    }

    public static String translit(String text) {
        int len = text.length();
        if (len == 0) {
            return text;
        }
        if (len == 1) {
            return get(text);
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; ) {
            // get next 2 symbols
            String toTranslate = text.substring(i, i <= len - 2 ? i + 2 : i + 1);
            // trying to translate
            String translated = get(toTranslate);
            // if these 2 symbols are not connected try to translate one by one
            if (TextUtils.isEmpty(translated)) {
                translated = get(toTranslate.charAt(0) + "");
                sb.append(TextUtils.isEmpty(translated) ? toTranslate.charAt(0) : translated);
                i++;
            } else {
                sb.append(TextUtils.isEmpty(translated) ? toTranslate : translated);
                i += 2;
            }
        }
        return sb.toString().toLowerCase();
    }
}