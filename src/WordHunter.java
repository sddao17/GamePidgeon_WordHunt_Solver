
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class WordHunter {

    private static char[][] grid;
    private static int gridYSize;
    private static int gridXSize;
    private static final Map<Integer, Integer> POINT_DISTRIBUTION = new HashMap<>() {{
        put(3, 400);
        put(4, 800);
        put(5, 1_400);
        put(6, 1_800);
        put(7, 2_200);
        put(8, 2_600);
        put(9, 3_000);
        put(10, 3_400);
        put(11, 3_800);
        put(12, 4_200);
        put(13, 4_600);
        put(14, 5_000);
        put(15, 5_400);
        put(16, 5_800);
    }};

    public static Map<Integer, Set<String>> findWords(char[][] grid, File parsedDictionary, File substringSet,
                                                      int[] pointValues) {
        Map<Integer, Set<String>> foundWords = new TreeMap<>();
        Set<String> words = readFile(parsedDictionary);
        Set<String> substrings = readFile(substringSet);
        WordHunter.grid = grid;
        gridYSize = grid.length;
        gridXSize = grid[0].length;

        for (int i = 0; i < gridYSize; ++i) {
            for (int j = 0; j < gridXSize; ++j) {
                checkAdjacentTiles(foundWords, words, substrings, new boolean[gridYSize][gridXSize], new int[]{i, j},
                        String.valueOf(grid[i][j]), pointValues);
            }
        }

        return foundWords;
    }

    private static void checkAdjacentTiles(Map<Integer, Set<String>> foundWords, Set<String> words,
                                           Set<String> substrings, boolean[][] visitedMemo, int[] currentPositions,
                                           String currentString, int[] pointValues) {
        if (visitedMemo[currentPositions[0]][currentPositions[1]] || !substrings.contains(currentString)) {
            return;
        }

        boolean[][] newVisitedMemo = deepCopyArray(visitedMemo);
        assert newVisitedMemo != null;
        newVisitedMemo[currentPositions[0]][currentPositions[1]] = true;

        if (words.contains(currentString)) {
            int currentStringLength = currentString.length();
            if (foundWords.get(currentStringLength) == null) {
                foundWords.put(currentStringLength, new TreeSet<>(){{add(currentString);}});
                pointValues[currentStringLength - 3] += POINT_DISTRIBUTION.get(currentStringLength);
            } else {
                boolean wordExists = foundWords.get(currentString.length()).add(currentString);
                if (!wordExists) {
                    pointValues[currentStringLength - 3] += POINT_DISTRIBUTION.get(currentStringLength);
                }
            }
        }

        // Check northern tile
        if (currentPositions[0] - 1 >= 0) {
            checkAdjacentTiles(foundWords, words, substrings, newVisitedMemo,
                    new int[]{currentPositions[0] - 1, currentPositions[1]},
                    currentString + grid[currentPositions[0] - 1][currentPositions[1]], pointValues);
        }

        // Check north-eastern tile
        if (currentPositions[0] - 1 >= 0 && currentPositions[1] + 1 < gridXSize) {
            checkAdjacentTiles(foundWords, words, substrings, newVisitedMemo,
                    new int[]{currentPositions[0] - 1, currentPositions[1] + 1},
                    currentString + grid[currentPositions[0] - 1][currentPositions[1] + 1], pointValues);
        }

        // Check eastern tile
        if (currentPositions[1] + 1 < gridXSize) {
            checkAdjacentTiles(foundWords, words, substrings, newVisitedMemo,
                    new int[]{currentPositions[0], currentPositions[1] + 1},
                    currentString + grid[currentPositions[0]][currentPositions[1] + 1], pointValues);
        }

        // Check south-eastern tile
        if (currentPositions[0] + 1 < gridYSize && currentPositions[1] + 1 < gridXSize) {
            checkAdjacentTiles(foundWords, words, substrings, newVisitedMemo,
                    new int[]{currentPositions[0] + 1, currentPositions[1] + 1},
                    currentString + grid[currentPositions[0] + 1][currentPositions[1] + 1], pointValues);
        }

        // Check southern tile
        if (currentPositions[0] + 1 < gridYSize) {
            checkAdjacentTiles(foundWords, words, substrings, newVisitedMemo,
                    new int[]{currentPositions[0] + 1, currentPositions[1]},
                    currentString + grid[currentPositions[0] + 1][currentPositions[1]], pointValues);
        }

        // Check south-western tile
        if (currentPositions[0] + 1 < gridYSize && currentPositions[1] - 1 >= 0) {
            checkAdjacentTiles(foundWords, words, substrings, newVisitedMemo,
                    new int[]{currentPositions[0] + 1, currentPositions[1] - 1},
                    currentString + grid[currentPositions[0] + 1][currentPositions[1] - 1], pointValues);
        }

        // Check western tile
        if (currentPositions[1] - 1 >= 0) {
            checkAdjacentTiles(foundWords, words, substrings, newVisitedMemo,
                    new int[]{currentPositions[0], currentPositions[1] - 1},
                    currentString + grid[currentPositions[0]][currentPositions[1] - 1], pointValues);
        }

        // Check north-western tile
        if (currentPositions[0] - 1 >= 0 && currentPositions[1] - 1 >= 0) {
            checkAdjacentTiles(foundWords, words, substrings, newVisitedMemo,
                    new int[]{currentPositions[0] - 1, currentPositions[1] - 1},
                    currentString + grid[currentPositions[0] - 1][currentPositions[1] - 1], pointValues);
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
