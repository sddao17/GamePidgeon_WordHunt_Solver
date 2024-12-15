
package server.com.portfolio.wordhunt;

import server.com.portfolio.wordhunt.model.Board;

import java.util.*;
import java.util.stream.Collectors;

import server.com.portfolio.wordhunt.helper.StringHelper;

public class Main {

    // Only for logging to console
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_YELLOW = "\u001B[33m";

    public static void main(String[] args) {
        //INSASEETITPRNEREASDHTITES got 1,506,200 points.
        //INSASEETITPRNEREASDHTITESASDERPREDASACKERSREDSASU got 1,754,500 points.
        long startTime;
        long endTime;

        System.out.print("""
                ==============================================================
                                          Word Hunt
                ==============================================================
                """);

        String input = requestUserInput();

        System.out.print("\nSolving board ... ");

        startTime = System.nanoTime();
        Board board = new Board(input);
        endTime = System.nanoTime();

        System.out.println("completed in " +
                ((double) (endTime - startTime) / 1_000_000_000) + "s.");

        System.out.print("Sorting words ... ");

        startTime = System.nanoTime();
        String[] sortedLines = board.sortWordPaths();
        endTime = System.nanoTime();

        System.out.println("completed in " +
                ((double) (endTime - startTime) / 1_000_000_000) + "s.\n");

        //System.out.println("Found word paths: " + board.getFoundWords() + "\n");
        System.out.println(board);

        System.out.println("Found the following words:");
        for (String sortedLine : sortedLines) {
            System.out.println(ANSI_CYAN + sortedLine + ANSI_RESET);
        }

        int totalPoints = Arrays.stream(board.getPointValues()).sum();
        int totalNumWords = Arrays.stream(board.getNumWordsFound()).sum();

        System.out.println("Total of words found: " + ANSI_YELLOW + StringHelper.addCommas(totalNumWords) +
                ANSI_RESET + " = " + Arrays.stream(board.getNumWordsFound())
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(" + ")));
        System.out.println("Total points: " + ANSI_YELLOW + StringHelper.addCommas(totalPoints) +
                ANSI_RESET + " = " + Arrays.stream(board.getPointValues())
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(" + ")));
    }

    private static String requestUserInput() {
        Scanner in = new Scanner(System.in);
        boolean valid = false;
        String input = "";

        System.out.print("""

                Enter the letters from left-right, top-bottom (no spaces):
                 >>\s""");

        while (!valid) {
            input = in.nextLine().toUpperCase();
            int squareRoot = (int) Math.sqrt(input.length());

            if (squareRoot * squareRoot != input.length()) {
                System.out.print("Input length must be a perfect square; please try again.\n >> ");
            } else {
                int inputLength = input.length();

                for (int i = 0; i < inputLength; ++i) {
                    if (!Character.isLetter(input.charAt(i))) {
                        System.out.print("Input must consist of only alphabetic letters; please try again.\n >> ");
                        break;
                    } else if (i == inputLength - 1) {
                        valid = true;
                    }
                }
            }
        }

        return input;
    }
}
