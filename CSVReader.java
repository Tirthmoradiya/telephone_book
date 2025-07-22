import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Utility class to read contacts from a CSV file.
 */
public class CSVReader {
    /**
     * Reads contacts from a CSV file and returns them as a list.
     * @param filePath The path to the CSV file
     * @return List of Contact objects
     */
    public static List<Contact> readCSV(String filePath) {
        List<Contact> contacts = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.mark(1);
            if (br.read() != 0xFEFF) {
                br.reset();
            }

            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                if (data.length >= 3) {
                    String name = data[0];
                    String phoneNumber = data[1];
                    String email = data[2];

                    Contact contact = new Contact(name, phoneNumber, email);
                    contacts.add(contact);
                } else {
                    System.out.println("Invalid CSV format: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return contacts;
    }
} 