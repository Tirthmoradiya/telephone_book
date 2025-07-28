import java.io.*;
import java.util.*;

/**
 * Represents a contact with name, phone number, email, favorite status, groups, and optional photo.
 */
public class Contact implements Serializable {
    public String name;
    public String phoneNumber;
    public String email;
    /** True if this contact is a favorite/starred. */
    public boolean favorite;
    /** Comma-separated groups/tags for this contact (e.g., Family, Work). */
    public String groups;
    /** Optional path to a photo/avatar for this contact. */
    public String photoPath;

    /**
     * Constructs a Contact with the given name, phone number, and email.
     * @param name The contact's name
     * @param phoneNumber The contact's phone number
     * @param email The contact's email
     */
    public Contact(String name, String phoneNumber, String email) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.favorite = false;
        this.groups = "";
        this.photoPath = null;
    }

    /**
     * Validates the contact's data.
     * @return true if all fields are valid, false otherwise
     */
    public boolean isValid() {
        return isValidName(name) && isValidPhone(phoneNumber) && isValidEmail(email);
    }

    /**
     * Validates a name (letters and spaces only).
     * @param name The name to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidName(String name) {
        return name != null && name.trim().matches("^[A-Za-z\\s]+$") && name.trim().length() >= 2;
    }

    /**
     * Validates a phone number (10 digits).
     * @param phone The phone number to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && phone.trim().matches("^\\d{10}$");
    }

    /**
     * Validates an email address.
     * @param email The email to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        return email != null && email.trim().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    }

    /**
     * Normalizes phone number by removing non-digits.
     * @param phone The phone number to normalize
     * @return Normalized phone number
     */
    public static String normalizePhone(String phone) {
        if (phone == null) return "";
        return phone.replaceAll("[^\\d]", "");
    }

    /**
     * Normalizes email by converting to lowercase and trimming.
     * @param email The email to validate
     * @return Normalized email
     */
    public static String normalizeEmail(String email) {
        if (email == null) return "";
        return email.trim().toLowerCase();
    }

    /**
     * Normalizes name by trimming and capitalizing first letter of each word.
     * @param name The name to normalize
     * @return Normalized name
     */
    public static String normalizeName(String name) {
        if (name == null || name.trim().isEmpty()) return "";
        
        String[] words = name.trim().split("\\s+");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            if (i > 0) result.append(" ");
            if (words[i].length() > 0) {
                result.append(Character.toUpperCase(words[i].charAt(0)))
                      .append(words[i].substring(1).toLowerCase());
            }
        }
        return result.toString();
    }

    /**
     * Checks if this contact is a duplicate of another contact.
     * @param other The other contact to compare with
     * @return true if duplicates, false otherwise
     */
    public boolean isDuplicate(Contact other) {
        if (other == null) return false;
        
        // Check exact matches first
        if (normalizePhone(phoneNumber).equals(normalizePhone(other.phoneNumber)) && 
            !normalizePhone(phoneNumber).isEmpty()) {
            return true;
        }
        
        if (normalizeEmail(email).equals(normalizeEmail(other.email)) && 
            !normalizeEmail(email).isEmpty()) {
            return true;
        }
        
        // Check name similarity (fuzzy matching)
        String thisName = normalizeName(name);
        String otherName = normalizeName(other.name);
        
        if (thisName.equals(otherName) && !thisName.isEmpty()) {
            return true;
        }
        
        // Check if names are very similar (one is contained in the other)
        if (thisName.contains(otherName) || otherName.contains(thisName)) {
            if (Math.abs(thisName.length() - otherName.length()) <= 2) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Merges this contact with another contact, keeping the best data from both.
     * @param other The other contact to merge with
     * @return A new merged contact
     */
    public Contact mergeWith(Contact other) {
        if (other == null) return this;
        
        Contact merged = new Contact(
            name.isEmpty() ? other.name : name,
            phoneNumber.isEmpty() ? other.phoneNumber : phoneNumber,
            email.isEmpty() ? other.email : email
        );
        
        // Keep favorite status if either is favorite
        merged.favorite = favorite || other.favorite;
        
        // Merge groups
        if (groups.isEmpty()) {
            merged.groups = other.groups;
        } else if (!other.groups.isEmpty()) {
            merged.groups = groups + ", " + other.groups;
        } else {
            merged.groups = groups;
        }
        
        // Keep photo path if available
        merged.photoPath = photoPath != null ? photoPath : other.photoPath;
        
        return merged;
    }

    @Override
    public String toString() {
        return "Contact{name='" + name + "', phone='" + phoneNumber + "', email='" + email + "'}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Contact contact = (Contact) obj;
        return Objects.equals(normalizeName(name), normalizeName(contact.name)) &&
               Objects.equals(normalizePhone(phoneNumber), normalizePhone(contact.phoneNumber)) &&
               Objects.equals(normalizeEmail(email), normalizeEmail(contact.email));
    }

    @Override
    public int hashCode() {
        return Objects.hash(normalizeName(name), normalizePhone(phoneNumber), normalizeEmail(email));
    }
} 