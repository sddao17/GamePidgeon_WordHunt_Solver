
import java.io.*;

public class DictionaryParser {

    public static void parse(File dictionary, File parsedDictionary) {
        try {
            if (!parsedDictionary.exists()) {
                parsedDictionary.createNewFile();
            }

            readAndWriteToFile(dictionary, parsedDictionary);
        } catch (IOException err) {
            err.printStackTrace();
        }
    }

    /**
     * Reads from a given file, parses the data, and then writes the parsed data to a new file.
     * @param fileToRead the file to read from
     * @param fileToWrite the file to write to
     */
    private static void readAndWriteToFile(File fileToRead, File fileToWrite) throws IOException {
        StringBuilder retrievedData = new StringBuilder();
        String line;

        try (BufferedReader bReader = new BufferedReader(new FileReader(fileToRead))) {
            while ((line = bReader.readLine()) != null) {
                String[] splitWords = line.split(" ");

                if (splitWords.length > 0) {
                    String firstWord = line.split(" ")[0].toUpperCase();

                    if (firstWord.length() >= 3 && stringIsOnlyLetters(firstWord)) {
                        retrievedData.append(firstWord).append("\n");
                    }
                }
            }
        }

        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(fileToWrite, true))) {
            fileWriter.write(retrievedData.toString());
        }
    }

    private static boolean stringIsOnlyLetters(String string) {
        int stringLength = string.length();

        for (int i = 0; i < stringLength; ++i) {
            if (!Character.isLetter(string.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
