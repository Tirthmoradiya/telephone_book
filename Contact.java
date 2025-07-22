import java.io.Serializable;

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
} 