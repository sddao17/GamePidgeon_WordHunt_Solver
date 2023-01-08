
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class WordHunter {

    private static final int gridYSize = Main.gridDimensions[0];
    private static final int gridXSize = Main.gridDimensions[1];
    public static final char[][] grid = new char[gridYSize][gridXSize];

    public static Map<Integer, Set<String>> findWords(String input, File parsedDictionary, File substringSet) {
        Map<Integer, Set<String>> foundWords = new TreeMap<>();
        Set<String> words = readFile(parsedDictionary);
        Set<String> substrings = readFile(substringSet);

        setGrid(input);

        for (int i = 0; i < gridYSize; ++i) {
            for (int j = 0; j < gridXSize; ++j) {
                checkAdjacentTiles(foundWords, words, substrings,
                        new boolean[gridYSize][gridXSize], new int[]{i, j}, String.valueOf(grid[i][j]));
            }
        }

        return foundWords;
    }

    private static void setGrid(String input) {
        int inputLength = input.length();
        int currentRow = 0;

        for (int i = 0; i < inputLength; ++i) {
            int currentColumn = i % gridXSize;

            if (i != 0 && i % gridYSize == 0) {
                ++currentRow;
                currentColumn = 0;
            }

            grid[currentRow][currentColumn] = input.charAt(i);
            ++currentColumn;
        }
    }

    private static void checkAdjacentTiles(Map<Integer, Set<String>> foundWords, Set<String> words, Set<String> substrings,
                                           boolean[][] visitedMemo, int[] currentPositions, String currentString) {
        if (visitedMemo[currentPositions[0]][currentPositions[1]] || !substrings.contains(currentString)) {
            return;
        }

        boolean[][] newVisitedMemo = deepCopyArray(visitedMemo);
        assert newVisitedMemo != null;
        newVisitedMemo[currentPositions[0]][currentPositions[1]] = true;

        if (words.contains(currentString)) {
            int currentStringLength = currentString.length();
            if (foundWords.get(currentStringLength) == null) {
                foundWords.put(currentStringLength, new HashSet<>(){{add(currentString);}});
            } else {
                foundWords.get(currentString.length()).add(currentString);
            }
        }

        // Check northern tile
        if (currentPositions[0] - 1 >= 0) {
            checkAdjacentTiles(foundWords, words, substrings, newVisitedMemo,
                    new int[]{currentPositions[0] - 1, currentPositions[1]},
                    currentString + grid[currentPositions[0] - 1][currentPositions[1]]);
        }

        // Check north-eastern tile
        if (currentPositions[0] - 1 >= 0 && currentPositions[1] + 1 < gridXSize) {
            checkAdjacentTiles(foundWords, words, substrings, newVisitedMemo,
                    new int[]{currentPositions[0] - 1, currentPositions[1] + 1},
                    currentString + grid[currentPositions[0] - 1][currentPositions[1] + 1]);
        }

        // Check eastern tile
        if (currentPositions[1] + 1 < gridXSize) {
            checkAdjacentTiles(foundWords, words, substrings, newVisitedMemo,
                    new int[]{currentPositions[0], currentPositions[1] + 1},
                    currentString + grid[currentPositions[0]][currentPositions[1] + 1]);
        }

        // Check south-eastern tile
        if (currentPositions[0] + 1 < gridYSize && currentPositions[1] + 1 < gridXSize) {
            checkAdjacentTiles(foundWords, words, substrings, newVisitedMemo,
                    new int[]{currentPositions[0] + 1, currentPositions[1] + 1},
                    currentString + grid[currentPositions[0] + 1][currentPositions[1] + 1]);
        }

        // Check southern tile
        if (currentPositions[0] + 1 < gridYSize) {
            checkAdjacentTiles(foundWords, words, substrings, newVisitedMemo,
                    new int[]{currentPositions[0] + 1, currentPositions[1]},
                    currentString + grid[currentPositions[0] + 1][currentPositions[1]]);
        }

        // Check south-western tile
        if (currentPositions[0] + 1 < gridYSize && currentPositions[1] - 1 >= 0) {
            checkAdjacentTiles(foundWords, words, substrings, newVisitedMemo,
                    new int[]{currentPositions[0] + 1, currentPositions[1] - 1},
                    currentString + grid[currentPositions[0] + 1][currentPositions[1] - 1]);
        }

        // Check western tile
        if (currentPositions[1] - 1 >= 0) {
            checkAdjacentTiles(foundWords, words, substrings, newVisitedMemo,
                    new int[]{currentPositions[0], currentPositions[1] - 1},
                    currentString + grid[currentPositions[0]][currentPositions[1] - 1]);
        }

        // Check north-western tile
        if (currentPositions[0] - 1 >= 0 && currentPositions[1] - 1 >= 0) {
            checkAdjacentTiles(foundWords, words, substrings, newVisitedMemo,
                    new int[]{currentPositions[0] - 1, currentPositions[1] - 1},
                    currentString + grid[currentPositions[0] - 1][currentPositions[1] - 1]);
        }
    }

    private static Set<String> readFile(File substringSet) {
        Set<String> substrings = new HashSet<>();
        String line;

        try (BufferedReader bReader = new BufferedReader(new FileReader(substringSet))) {
            while ((line = bReader.readLine()) != null) {
                if (!line.equals("\n")) {
                    substrings.add(line);
                }
            }
        } catch (IOException err) {
            err.printStackTrace();
        }

        return substrings;
    }

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
