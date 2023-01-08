
import java.io.File;
import java.util.*;

public class Main {

    public static final File dictionary = new File("dictionary.txt");
    public static final File parsedDictionary = new File("parsedDictionary.txt");
    public static final File substringSet = new File("substringSet.txt");
    public static final int[] gridDimensions = new int[]{4, 4};

    public static void main(String[] args) {
        long startTime;
        long endTime;

        if (!dictionary.exists()) {
            System.out.println("""
                    ==============================================================
                    
                    "dictionary.txt" does not exist within the directory.
                    Create one, then try again.""");
            System.exit(0);
        }

        if (!parsedDictionary.exists()) {
            System.out.println("""
                    ==============================================================

                    "parsedDictionary.txt" does not exist within the directory.
                    Parsing "dictionary.txt" ...""");

            startTime = System.nanoTime();
            DictionaryParser.parse(dictionary, parsedDictionary);
            endTime = System.nanoTime();

            System.out.println("Parse completed in " +
                    ((double) (endTime - startTime) / 1_000_000_000) + "s.\n");
        }

        if (!substringSet.exists()) {
            System.out.println("""
                    ==============================================================
                    
                    "substringSet.txt" does not exist within the directory.
                    Creating set from "dictionary.txt" ...""");

            startTime = System.nanoTime();
            SubstringSetter.createMappings(parsedDictionary, substringSet);
            endTime = System.nanoTime();

            System.out.println("Set completed in " +
                    ((double) (endTime - startTime) / 1_000_000_000) + "s.\n");
        }

        String input = requestUserInput();

        System.out.println("\nSearching for valid words ...");

        startTime = System.nanoTime();
        Map<Integer, Set<String>> words = WordHunter.findWords(input, parsedDictionary, substringSet);
        endTime = System.nanoTime();

        System.out.println("Search completed in " +
                ((double) (endTime - startTime) / 1_000_000_000) + "s.");

        System.out.println("\nSorting words ...");

        startTime = System.nanoTime();
        String[] sortedLines = sort(words);
        endTime = System.nanoTime();

        System.out.println("Sort completed in " +
                ((double) (endTime - startTime) / 1_000_000_000) + "s.");

        final String ANSI_RESET = "\u001B[0m";
        final String ANSI_CYAN = "\u001B[36m";

        System.out.println("\nFound the following words:");
        for (String sortedLine : sortedLines) {
            System.out.println(ANSI_CYAN + sortedLine + ANSI_RESET);
        }
    }

    private static String requestUserInput() {
        Scanner in = new Scanner(System.in);
        boolean valid = false;
        String input = "";

        System.out.printf("""
                ==============================================================

                Enter the %d letters from left-right, top-bottom (no spaces):
                 >>\s""", gridDimensions[0] * gridDimensions[1]);

        while (!valid) {
            input = in.nextLine().toUpperCase();

            if (!(input.length() % gridDimensions[0] == 0 && input.length() % gridDimensions[1] == 0)) {
                System.out.print("String length must be of length " +
                        (gridDimensions[0] * gridDimensions[1]) + "; please try again.\n >> ");
            } else {
                int inputLength = input.length();

                for (int i = 0; i < inputLength; ++i) {
                    if (!Character.isLetter(input.charAt(i))) {
                        System.out.print("String must consist of only alphabetic letters; please try again.\n >> ");
                        break;
                    } else if (i == inputLength - 1) {
                        valid = true;
                    }
                }
            }
        }

        return input;
    }

    private static String[] sort(Map<Integer, Set<String>> words) {

        Map<Integer, Set<String>> sortedMap = new TreeMap<>(words);

        return convertMapToList(sortedMap);
    }

    private static String[] convertMapToList(Map<Integer, Set<String>> sortedMap) {
        int longestWordLength = 0;
        int largestListSize = 0;

        Set<Map.Entry<Integer, Set<String>>> entrySet = sortedMap.entrySet();

        for (Map.Entry<Integer, Set<String>> entry : entrySet) {
            int key = entry.getKey();
            Set<String> value = entry.getValue();

            if (key > longestWordLength) {
                longestWordLength = key;
            }

            if (value.size() > largestListSize) {
                largestListSize = value.size();
            }
        }

        String[] sortedLines = new String[largestListSize];
        Arrays.fill(sortedLines, "");

        for (Map.Entry<Integer, Set<String>> entry : entrySet) {
            int key = entry.getKey();
            List<String> value = new ArrayList<>(entry.getValue());

            for (int i = 0; i < largestListSize; ++i) {
                if (i < value.size()) {
                    sortedLines[i] = justifiedLeftString(value.get(i), key + 4) + sortedLines[i];
                } else {
                    sortedLines[i] = justifiedLeftString("", key + 4) + sortedLines[i];
                }
            }
        }

        return sortedLines;
    }

    private static String justifiedLeftString(String string, int minLength) {
        int stringLength = string.length();

        if (stringLength < minLength) {
            return string + " ".repeat(minLength - stringLength);
        }

        return string;
    }
}
