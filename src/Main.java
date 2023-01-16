
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static final File DICTIONARY = new File("dictionary.txt");
    public static final File PARSED_DICTIONARY = new File("parsed_dictionary.txt");
    public static final File SUBSTRING_SET = new File("substring_set.txt");

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GRID = "\u001B[34m"; // Blue
    private static final String ANSI_CHAR_COLOR = "\u001B[36m"; // Cyan
    private static final String ANSI_WORD_COLOR = "\u001B[36m"; // Cyan
    private static final String ANSI_TOTAL_POINTS_COLOR = "\u001B[33m"; // Yellow

    public static final int DIMENSION = 15;
    public static final int[] GRID_DIMENSIONS = new int[]{DIMENSION, DIMENSION};
    public static final char[][] grid = new char[GRID_DIMENSIONS[0]][GRID_DIMENSIONS[1]];
    public static final int[] pointValues = new int[14];
    public static final int[] numWordsFound = new int[14];

    public static void main(String[] args) {
        long startTime;
        long endTime;

        System.out.println("==============================================================\n" +
                "                          Word Hunt");

        if (!DICTIONARY.exists()) {
            System.out.println("""
                    ==============================================================
                    
                    "dictionary.txt" does not exist within the directory.
                    Create one, then try again.""");
            System.exit(0);
        }

        if (!PARSED_DICTIONARY.exists()) {
            System.out.print("""
                    ==============================================================

                    "parsed_dictionary.txt" does not exist within the directory.
                    Parsing "dictionary.txt" ...\s""");

            startTime = System.nanoTime();
            DictionaryParser.parse(DICTIONARY, PARSED_DICTIONARY);
            endTime = System.nanoTime();

            System.out.println("completed in " +
                    ((double) (endTime - startTime) / 1_000_000_000) + "s.\n");
        }

        if (!SUBSTRING_SET.exists()) {
            System.out.print("""
                    ==============================================================
                    
                    "substring_set.txt" does not exist within the directory.
                    Creating set from "dictionary.txt" ...\s""");

            startTime = System.nanoTime();
            SubstringSetter.createMappings(PARSED_DICTIONARY, SUBSTRING_SET);
            endTime = System.nanoTime();

            System.out.println("completed in " +
                    ((double) (endTime - startTime) / 1_000_000_000) + "s.\n");
        }

        String input = requestUserInput();
        setGrid(input);

        System.out.print("\nSearching for valid words ... ");

        startTime = System.nanoTime();
        Map<Integer, Set<String>> words = WordHunter.findWords(PARSED_DICTIONARY, SUBSTRING_SET);
        endTime = System.nanoTime();

        System.out.println("completed in " +
                ((double) (endTime - startTime) / 1_000_000_000) + "s.");

        System.out.print("Sorting words ... ");

        startTime = System.nanoTime();
        String[] sortedLines = sort(words);
        endTime = System.nanoTime();

        System.out.println("completed in " +
                ((double) (endTime - startTime) / 1_000_000_000) + "s.\n");

        System.out.println(getBoardAsString());

        System.out.println("Found the following words:");
        for (String sortedLine : sortedLines) {
            System.out.println(ANSI_WORD_COLOR + sortedLine + ANSI_RESET);
        }

        int totalNumWords = 0;
        int totalPoints = 0;
        for (int i = 0; i < numWordsFound.length; ++i) {
            totalNumWords += numWordsFound[i];
            totalPoints += pointValues[i];
        }

        System.out.println("Total of words found: " + ANSI_TOTAL_POINTS_COLOR + addCommas(totalNumWords) + ANSI_RESET +
                "\n= " + Arrays.stream(numWordsFound)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(" + ")));
        System.out.println("Total points: " + ANSI_TOTAL_POINTS_COLOR + addCommas(totalPoints) + ANSI_RESET +
                "\n= " + Arrays.stream(pointValues)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(" + ")));
    }

    private static String requestUserInput() {
        Scanner in = new Scanner(System.in);
        boolean valid = false;
        String input = "";

        System.out.printf("""
                ==============================================================

                Enter the %d letters from left-right, top-bottom (no spaces):
                 >>\s""", GRID_DIMENSIONS[0] * GRID_DIMENSIONS[1]);

        while (!valid) {
            input = in.nextLine().toUpperCase();

            if (input.length() != (GRID_DIMENSIONS[0] * GRID_DIMENSIONS[1])) {
                System.out.print("String length must be of length " +
                        (GRID_DIMENSIONS[0] * GRID_DIMENSIONS[1]) + "; please try again.\n >> ");
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
                int padLength = (i <= largestListSize - 1) ? 4 : 0;
                if (i < value.size()) {
                    sortedLines[i] = justifiedLeftString(value.get(i), key + padLength) + sortedLines[i];
                } else {
                    sortedLines[i] = justifiedLeftString("", key + padLength) + sortedLines[i];
                }
            }
        }

        return sortedLines;
    }

    private static void setGrid(String input) {
        int inputLength = input.length();
        int currentRow = 0;

        for (int i = 0; i < inputLength; ++i) {
            int currentColumn = i % GRID_DIMENSIONS[1];

            if (i != 0 && i % GRID_DIMENSIONS[0] == 0) {
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
                result.append(ANSI_GRID).append("+").append("---+".repeat(gridXSize)).append(ANSI_RESET).append("\n");
            }

            for (int j = 0; j < gridXSize; ++j) {
                result.append(ANSI_GRID).append("| ").append(ANSI_RESET)
                        .append(ANSI_CHAR_COLOR).append(grid[i][j]).append(ANSI_RESET)
                        .append(ANSI_GRID).append(" ");
            }
            result.append("|").append(ANSI_RESET).append("\n")
                    .append(ANSI_GRID).append("+").append("---+".repeat(gridXSize)).append(ANSI_RESET).append("\n");
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

    private static String addCommas(Number num) {
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
