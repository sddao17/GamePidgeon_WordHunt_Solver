
package server.com.portfolio.wordhunt.model;

import server.com.portfolio.wordhunt.helper.DictionaryParser;
import server.com.portfolio.wordhunt.helper.SubstringSetter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static server.com.portfolio.wordhunt.helper.ArrayHelper.deepCopyArray;
import static server.com.portfolio.wordhunt.helper.StringHelper.justifiedLeftString;

public class Board {

    // Creating files using designated paths
    private final File DICTIONARY = new File("dictionary_collins_2019.txt");
    private final File PARSED_DICTIONARY = new File("parsed_dictionary.txt");
    private final File SUBSTRING_SET = new File("substring_set.txt");

    // Only for logging to console
    private final String ANSI_RESET = "\u001B[0m";
    private final String ANSI_BLUE = "\u001B[34m";
    private final String ANSI_CYAN = "\u001B[36m";
    private final String ANSI_YELLOW = "\u001B[33m";

    protected final Map<Integer, Integer> POINT_DISTRIBUTION = new HashMap<>() {{
        put(3, 100);
        put(4, 400);
        put(5, 800);
        put(6, 1_400);
        put(7, 1_800);
        put(8, 2_200);
        put(9, 2_600);
        put(10, 3_000);
        put(11, 3_400);
        put(12, 3_800);
        put(13, 4_200);
        put(14, 4_600);
        put(15, 5_000);
        put(16, 5_400);
    }};

    protected char[][] grid;
    protected final int[] pointValues = new int[POINT_DISTRIBUTION.size()];
    protected final int[] numWordsFound = new int[POINT_DISTRIBUTION.size()];
    protected final Map<Integer, Set<String>> words = new TreeMap<>();
    protected final Map<String, ArrayList<Pair<Integer, Integer>>> wordPaths = new TreeMap<>();

    public Board(String boardLetters) {
        long startTime;
        long endTime;

        if (!DICTIONARY.exists()) {
            System.out.println(ANSI_YELLOW + """
                    
                    "dictionary.txt" does not exist within the directory.
                    Create one, then try again.""" + ANSI_RESET);
            System.exit(1);
        }

        if (!PARSED_DICTIONARY.exists()) {
            System.out.print(ANSI_YELLOW + """

                    Dictionary has not been parsed yet.
                    Parsing "dictionary.txt" ...""");

            startTime = System.nanoTime();
            DictionaryParser.parse(DICTIONARY, PARSED_DICTIONARY);
            endTime = System.nanoTime();

            System.out.println("completed in " +
                    ((double) (endTime - startTime) / 1_000_000_000) + "s.\n" + ANSI_RESET);
        }

        if (!SUBSTRING_SET.exists()) {
            System.out.print(ANSI_YELLOW + """
                    
                    Substring set has not been created yet.
                    Creating set from "dictionary.txt" ...""");

            startTime = System.nanoTime();
            SubstringSetter.createMappings(PARSED_DICTIONARY, SUBSTRING_SET);
            endTime = System.nanoTime();

            System.out.println("completed in " +
                    ((double) (endTime - startTime) / 1_000_000_000) + "s.\n" + ANSI_RESET);
        }

        try {
            int dimensions = (int) Math.sqrt(boardLetters.length());
            grid = new char[dimensions][dimensions];
            setGrid(boardLetters);
            solveBoard();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setGrid(String boardLetters) {
        int inputLength = boardLetters.length();
        int currentRow = 0;

        for (int i = 0; i < inputLength; ++i) {
            int currentColumn = i % grid.length;

            if (i != 0 && i % grid.length == 0) {
                ++currentRow;
                currentColumn = 0;
            }

            grid[currentRow][currentColumn] = boardLetters.charAt(i);
            ++currentColumn;
        }
    }

    public void solveBoard() {
        Set<String> words = readFile(PARSED_DICTIONARY);
        Set<String> substrings = readFile(SUBSTRING_SET);

        for (int i = 0; i < grid.length; ++i) {
            for (int j = 0; j < grid.length; ++j) {
                checkAdjacentTiles(new ArrayList<>(), words, substrings,
                        new boolean[grid.length][grid.length],
                        new Pair<>(i, j), String.valueOf(grid[i][j]));
            }
        }
    }

    private void checkAdjacentTiles(ArrayList<Pair<Integer, Integer>> currentPath,
                                    Set<String> dictionaryWords, Set<String> substrings,
                                    boolean[][] visitedMemo, Pair<Integer, Integer> currentPositions,
                                    String currentString) {
        if (visitedMemo[currentPositions.first][currentPositions.second] || !substrings.contains(currentString)) {
            return;
        }

        currentPath.add(currentPositions);
        boolean[][] newVisitedMemo = deepCopyArray(visitedMemo);
        assert newVisitedMemo != null;
        newVisitedMemo[currentPositions.first][currentPositions.second] = true;

        if (dictionaryWords.contains(currentString)) {
            int currentStringLength = currentString.length();
            words.putIfAbsent(currentStringLength, new TreeSet<>());

            if (!words.get(currentStringLength).contains(currentString)) {
                words.get(currentStringLength).add(currentString);
                wordPaths.put(currentString, currentPath);

                pointValues[currentStringLength - 3] += POINT_DISTRIBUTION.get(currentStringLength);
                ++numWordsFound[currentStringLength - 3];
            }
        }

        // Check northern tile
        if (currentPositions.first - 1 >= 0) {
            checkAdjacentTiles(new ArrayList<>(currentPath), dictionaryWords, substrings, newVisitedMemo,
                    new Pair<>(currentPositions.first - 1, currentPositions.second),
                    currentString + grid[currentPositions.first - 1][currentPositions.second]);
        }

        // Check north-eastern tile
        if (currentPositions.first - 1 >= 0 && currentPositions.second + 1 < grid.length) {
            checkAdjacentTiles(new ArrayList<>(currentPath), dictionaryWords, substrings, newVisitedMemo,
                    new Pair<>(currentPositions.first - 1, currentPositions.second + 1),
                    currentString + grid[currentPositions.first - 1][currentPositions.second + 1]);
        }

        // Check eastern tile
        if (currentPositions.second + 1 < grid.length) {
            checkAdjacentTiles(new ArrayList<>(currentPath), dictionaryWords, substrings, newVisitedMemo,
                    new Pair<>(currentPositions.first, currentPositions.second + 1),
                    currentString + grid[currentPositions.first][currentPositions.second + 1]);
        }

        // Check south-eastern tile
        if (currentPositions.first + 1 < grid.length && currentPositions.second + 1 < grid.length) {
            checkAdjacentTiles(new ArrayList<>(currentPath), dictionaryWords, substrings, newVisitedMemo,
                    new Pair<>(currentPositions.first + 1, currentPositions.second + 1),
                    currentString + grid[currentPositions.first + 1][currentPositions.second + 1]);
        }

        // Check southern tile
        if (currentPositions.first + 1 < grid.length) {
            checkAdjacentTiles(new ArrayList<>(currentPath), dictionaryWords, substrings, newVisitedMemo,
                    new Pair<>(currentPositions.first + 1, currentPositions.second),
                    currentString + grid[currentPositions.first + 1][currentPositions.second]);
        }

        // Check south-western tile
        if (currentPositions.first + 1 < grid.length && currentPositions.second - 1 >= 0) {
            checkAdjacentTiles(new ArrayList<>(currentPath), dictionaryWords, substrings, newVisitedMemo,
                    new Pair<>(currentPositions.first + 1, currentPositions.second - 1),
                    currentString + grid[currentPositions.first + 1][currentPositions.second - 1]);
        }

        // Check western tile
        if (currentPositions.second - 1 >= 0) {
            checkAdjacentTiles(new ArrayList<>(currentPath), dictionaryWords, substrings, newVisitedMemo,
                    new Pair<>(currentPositions.first, currentPositions.second - 1),
                    currentString + grid[currentPositions.first][currentPositions.second - 1]);
        }

        // Check north-western tile
        if (currentPositions.first - 1 >= 0 && currentPositions.second - 1 >= 0) {
            checkAdjacentTiles(new ArrayList<>(currentPath), dictionaryWords, substrings, newVisitedMemo,
                    new Pair<>(currentPositions.first - 1, currentPositions.second - 1),
                    currentString + grid[currentPositions.first - 1][currentPositions.second - 1]);
        }
    }

    public String[] sortWordPaths() {
        int longestWordLength = 0;
        int largestListSize = 0;
        Set<Map.Entry<Integer, Set<String>>> entrySet = words.entrySet();

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

    public int[] getPointValues() {
        return pointValues;
    }

    public int[] getNumWordsFound() {
        return numWordsFound;
    }

    public Map<Integer, Set<String>> getWords() {
        return words;
    }

    public Map<String, ArrayList<Pair<Integer, Integer>>> getWordPaths() {
        return wordPaths;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        int gridYSize = grid.length;
        int gridXSize = grid[0].length;

        for (int i = 0; i < gridYSize; ++i) {
            if (i == 0) {
                result.append(ANSI_BLUE).append("+").append("---+".repeat(gridXSize)).append(ANSI_RESET).append("\n");
            }

            for (int j = 0; j < gridXSize; ++j) {
                result.append(ANSI_BLUE).append("| ").append(ANSI_RESET)
                        .append(ANSI_CYAN).append(grid[i][j]).append(ANSI_RESET)
                        .append(ANSI_BLUE).append(" ");
            }
            result.append("|").append(ANSI_RESET).append("\n")
                    .append(ANSI_BLUE).append("+").append("---+".repeat(gridXSize)).append(ANSI_RESET).append("\n");
        }

        return result.toString();
    }
}
