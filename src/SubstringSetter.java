
import java.io.*;
import java.util.*;

public class SubstringSetter {

    public static void createMappings(File parsedDictionary, File substringSet) {
        try {
            if (!substringSet.exists()) {
                substringSet.createNewFile();
            }

            readAndWriteToFile(parsedDictionary, substringSet);
        } catch (IOException err) {
            err.printStackTrace();
        }
    }

    /**
     * Reads from a given file, parses the data, and then writes the parsed data to a new file.
     *
     * @param fileToRead the file to read from
     * @param fileToWrite the file to write to
     */
    private static void readAndWriteToFile(File fileToRead, File fileToWrite) throws IOException {
        Set<String> words = new HashSet<>();
        String line;

        try (BufferedReader bReader = new BufferedReader(new FileReader(fileToRead))) {
            while ((line = bReader.readLine()) != null) {
                words.add(line);
            }
        }

        Set<String> substringSet  = new TreeSet<>();

        words.forEach((word) -> {
            for (int i = 0; i < word.length(); ++i) {
                String substring = word.substring(0, i + 1).toUpperCase();
                substringSet.add(substring);
            }
        });

        StringBuilder substrings = new StringBuilder();

        substringSet.forEach((substring) -> substrings.append(substring).append("\n"));

        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(fileToWrite, true))) {
            fileWriter.write(substrings.toString());
        }
    }
}
