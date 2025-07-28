import java.io.*;
import java.util.*;

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
            // Handle BOM (Byte Order Mark) for UTF-8 files
            br.mark(1);
            if (br.read() != 0xFEFF) {
                br.reset();
            }

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue; // Skip empty lines
                
                String[] data = line.split(",");
                if (data.length >= 3) {
                    String name = data[0].trim();
                    String phoneNumber = data[1].trim();
                    String email = data[2].trim();
                    
                    // Only add valid contacts
                    if (Contact.isValidName(name) && Contact.isValidPhone(phoneNumber) && Contact.isValidEmail(email)) {
                        Contact contact = new Contact(name, phoneNumber, email);
                        contacts.add(contact);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        }

        return contacts;
    }
} 