import java.io.*;

/**
 * AVLNode represents a node in the AVL tree, holding a Contact.
 */
public class AVLNode implements Serializable {
    public Contact contact;
    public AVLNode left;
    public AVLNode right;
    public int height;

    /**
     * Constructs an AVLNode with the given contact.
     * @param contact The contact to store in this node
     */
    public AVLNode(Contact contact) {
        this.contact = contact;
        this.left = null;
        this.right = null;
        this.height = 1;
    }
} 