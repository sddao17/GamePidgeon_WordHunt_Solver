
package server.com.portfolio.wordhunt.helper;

public class StringHelper {

    public static String justifiedLeftString(String string, int minLength) {
        int stringLength = string.length();

        if (stringLength < minLength) {
            return string + " ".repeat(minLength - stringLength);
        }

        return string;
    }

    public static String addCommas(Number num) {
        String numAsString = String.valueOf(num);

        if (numAsString.length() < 4) {
            return numAsString;
        }

        StringBuilder newString = new StringBuilder(numAsString);
        for (int i = numAsString.length() - 4; i >= 0; i -= 3) {
            newString.insert(i + 1, ',');
        }
        return newString.toString();
    }
}
