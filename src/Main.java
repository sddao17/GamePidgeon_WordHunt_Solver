
import java.io.File;
import java.util.*;

public class Main {

    public static final int[] gridDimensions = new int[]{4, 4};
    public static final char[][] grid = new char[gridDimensions[0]][gridDimensions[1]];
    public static final File dictionary = new File("dictionary.txt");
    public static final File parsedDictionary = new File("parsedDictionary.txt");
    public static final File substringSet = new File("substringSet.txt");

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GRID = "\u001B[34m"; // Blue
    private static final String ANSI_CHAR_COLOR = "\u001B[36m"; // Cyan
    private static final String ANSI_WORD_COLOR = "\u001B[36m"; // Cyan

    public static void main(String[] args) {
        long startTime;
        long endTime;

        System.out.println("==============================================================\n" +
                "                          Word Hunt");

        if (!dictionary.exists()) {
            System.out.println("""
                    ==============================================================
                    
                    "dictionary.txt" does not exist within the directory.
                    Create one, then try again.""");
            System.exit(0);
        }

        if (!parsedDictionary.exists()) {
            System.out.print("""
                    ==============================================================

                    "parsedDictionary.txt" does not exist within the directory.
                    Parsing "dictionary.txt" ...\s""");

            startTime = System.nanoTime();
            DictionaryParser.parse(dictionary, parsedDictionary);
            endTime = System.nanoTime();

            System.out.println("completed in " +
                    ((double) (endTime - startTime) / 1_000_000_000) + "s.\n");
        }

        if (!substringSet.exists()) {
            System.out.print("""
                    ==============================================================
                    
                    "substringSet.txt" does not exist within the directory.
                    Creating set from "dictionary.txt" ...\s""");

            startTime = System.nanoTime();
            SubstringSetter.createMappings(parsedDictionary, substringSet);
            endTime = System.nanoTime();

            System.out.println("completed in " +
                    ((double) (endTime - startTime) / 1_000_000_000) + "s.\n");
        }

        String input = requestUserInput();
        setGrid(input);
        System.out.println("\n" + getBoardAsString());

        System.out.print("Searching for valid words ... ");

        startTime = System.nanoTime();
        Map<Integer, Set<String>> words = WordHunter.findWords(grid, parsedDictionary, substringSet);
        endTime = System.nanoTime();

        System.out.println("completed in " +
                ((double) (endTime - startTime) / 1_000_000_000) + "s.");

        System.out.print("Sorting words ... ");

        startTime = System.nanoTime();
        String[] sortedLines = sort(words);
        endTime = System.nanoTime();

        System.out.println("completed in " +
                ((double) (endTime - startTime) / 1_000_000_000) + "s.\n");

        System.out.println("Found the following words:");
        for (String sortedLine : sortedLines) {
            System.out.println(ANSI_WORD_COLOR + sortedLine + ANSI_RESET);
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

    private static void setGrid(String input) {
        int inputLength = input.length();
        int currentRow = 0;

        for (int i = 0; i < inputLength; ++i) {
            int currentColumn = i % gridDimensions[1];

            if (i != 0 && i % gridDimensions[1] == 0) {
                ++currentRow;
                currentColumn = 0;
            }

            grid[currentRow][currentColumn] = input.charAt(i);
            ++currentColumn;
        }
    }

    private static String getBoardAsString() {
        StringBuilder result = new StringBuilder();
        int gridYSize = grid.length;
        int gridXSize = grid[0].length;

        for (int i = 0; i < gridYSize; ++i) {
            if (i == 0) {
                result.append(ANSI_GRID).append("+").append("---+".repeat(gridXSize)).append("\n");
            }

            for (int j = 0; j < gridXSize; ++j) {
                result.append(ANSI_GRID).append("| ").append(ANSI_RESET)
                        .append(ANSI_CHAR_COLOR).append(grid[i][j])
                        .append(ANSI_RESET).append(ANSI_GRID).append(" ");
            }
            result.append("|\n+").append("---+".repeat(gridXSize)).append("\n").append(ANSI_RESET);
        }

        return result.toString();
    }

    private static String justifiedLeftString(String string, int minLength) {
        int stringLength = string.length();

        if (stringLength < minLength) {
            return string + " ".repeat(minLength - stringLength);
        }

        return string;
    }
}
