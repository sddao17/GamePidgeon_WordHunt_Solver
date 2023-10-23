
package server.com.portfolio.wordhunt.helper;

import java.util.Arrays;

public class ArrayHelper {

    public static boolean[][] deepCopyArray(boolean[][] original) {
        if (original == null) {
            return null;
        }

        final boolean[][] result = new boolean[original.length][];
        for (int i = 0; i < original.length; i++) {
            result[i] = Arrays.copyOf(original[i], original[i].length);
        }

        return result;
    }
}
