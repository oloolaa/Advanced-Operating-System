package controller;

public class TextUtil {
    public static boolean checkStringNotEmpty(String item) {
        if (item == null || item.isEmpty() || item.equals("")) {
            return false;
        }
        return true;
    }

    public static String regularingLine(String str) {
        str = str.replaceAll("\n", " ");
        str = str.replaceAll("\t", " ");
        if (str.contains("#")) {
            str = str.substring(0, str.indexOf("#"));
        }

        //Remove extra blank
        str = removeExtralBlank(str);
        return str;
    }


    public static String removeExtralBlank(String str) {
        str = str.trim();
        while (str.contains("  ")) {
            str = str.replaceAll("  ", " ");
        }
        return str;
    }

    public static boolean isValidLine(String str) {
        str = str.trim();
        if (!checkStringNotEmpty(str) || str.startsWith("#")) {
            return false;
        }
        return true;
    }
}
