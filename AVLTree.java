import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * AVLTree is a self-balancing binary search tree for managing Contact objects.
 * Provides efficient add, update, delete, and search operations.
 */
public class AVLTree implements Serializable {
    public AVLNode root;

    // right rotation
    private AVLNode rightRotate(AVLNode y) {
        AVLNode x = y.left;
        AVLNode T2 = x.right;

        x.right = y;
        y.left = T2;

        y.height = Math.max(getHeight(y.left), getHeight(y.right)) + 1;
        x.height = Math.max(getHeight(x.left), getHeight(x.right)) + 1;

        return x;
    }

    // gets height
    private int getHeight(AVLNode node) {
        if (node == null)
            return 0;
        return node.height;
    }

    // left rotation
    private AVLNode leftRotate(AVLNode x) {
        AVLNode y = x.right;
        AVLNode T2 = y.left;

        y.left = x;
        x.right = T2;

        x.height = Math.max(getHeight(x.left), getHeight(x.right)) + 1;
        y.height = Math.max(getHeight(y.left), getHeight(y.right)) + 1;

        return y;
    }

    // gets height difference
    private int getBalance(AVLNode node) {
        if (node == null)
            return 0;
        return getHeight(node.left) - getHeight(node.right);
    }

    // insert operation
    private AVLNode insert(AVLNode node, Contact contact) {
        if (node == null) {
            return new AVLNode(contact);
        }

        int nc = contact.name.compareTo(node.contact.name);
        int pc = contact.phoneNumber.compareTo(node.contact.phoneNumber);
        int ec = contact.email.compareTo(node.contact.email);

        if (nc < 0) {
            node.left = insert(node.left, contact);
        } else if (nc > 0) {
            node.right = insert(node.right, contact);
        } else {
            if (pc < 0) {
                node.left = insert(node.left, contact);
            } else if (pc > 0) {
                node.right = insert(node.right, contact);
            } else {
                if (ec < 0) {
                    node.left = insert(node.left, contact);
                } else {
                    node.right = insert(node.right, contact);
                }
            }
        }

        node.height = 1 + Math.max(getHeight(node.left), getHeight(node.right));

        int balance = getBalance(node);

        if (balance > 1) {
            if (nc > 0) {
                node.left = leftRotate(node.left);
            }
            return rightRotate(node);
        }
        if (balance < -1) {
            if (nc < 0) {
                node.right = rightRotate(node.right);
            }
            return leftRotate(node);
        }

        return node;
    }

    /**
     * Deletes a contact from the AVL tree.
     * @param node The root node to start deletion
     * @param name The contact's name
     * @param phoneNumber The contact's phone number
     * @param email The contact's email
     * @return The new root after deletion
     */
    public AVLNode delete(AVLNode node, String name, String phoneNumber, String email) {
        if (node == null) {
            return null;
        }

        AVLNode node1 = findNodeById(root, name, phoneNumber, email);
        if (node1 != null) {
            if (node.left == null) {
                return node.right;
            } else if (node.right == null) {
                return node.left;
            }
            node.contact = findMin(node.right);
            node.right = delete(node.right, node.contact.name, node.contact.phoneNumber, node.contact.email);
        }
        node.height = 1 + Math.max(getHeight(node.left), getHeight(node.right));
        int balance = getBalance(node);

        if (balance > 1 && getBalance(node.left) >= 0) {
            return rightRotate(node);
        }
        if (balance > 1 && getBalance(node.left) < 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }
        if (balance < -1 && getBalance(node.right) <= 0) {
            return leftRotate(node);
        }
        if (balance < -1 && getBalance(node.right) > 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    // finding minimum node
    private Contact findMin(AVLNode node) {
        while (node.left != null) {
            node = node.left;
        }
        return node.contact;
    }

    // in order traversal of tree
    private void inOrderTraversal(AVLNode node, StringBuilder ctsb) {
        if (node == null) {
            return;
        }
        inOrderTraversal(node.left, ctsb);
        ctsb.append("name: ").append(node.contact.name)
                .append(", Phone: ").append(node.contact.phoneNumber)
                .append(", email: ").append(node.contact.email).append("\n");
        inOrderTraversal(node.right, ctsb);
    }

    /**
     * Adds a new contact to the AVL tree.
     * @param name The contact's name
     * @param phoneNumber The contact's phone number
     * @param email The contact's email
     * @return true if the contact was added, false if a duplicate exists
     */
    public boolean addContact(String name, String phoneNumber, String email) {
        Contact newContact = new Contact(name, phoneNumber, email);
        AVLNode node = findNodeById(root, name, phoneNumber, email);
        if (node == null)
            root = insert(root, newContact);
        else
            return false;

        return true;
    }

    /**
     * Updates an existing contact in the AVL tree.
     * @param name The current name
     * @param phoneNumber The current phone number
     * @param email The current email
     * @param newName The new name
     * @param newPhoneNumber The new phone number
     * @param newEmail The new email
     * @return true if the contact was updated, false if not found
     */
    public boolean updateContact(String name, String phoneNumber, String email, String newName, String newPhoneNumber, String newEmail) {
        Contact updatedContact = new Contact(newName, newPhoneNumber, newEmail);
        AVLNode node = findNodeById(root, name, phoneNumber, email);
        if (node != null) {
            root = delete(root, name, phoneNumber, email);
            root = insert(root, updatedContact);
            return true;
        } else {
            return false;
        }
    }

    // search by node
    private AVLNode findNodeById(AVLNode node, String name, String phoneNumber, String email) {
        if (node == null) {
            return null;
        }

        int nc = name.compareTo(node.contact.name);
        if (nc < 0) {
            return findNodeById(node.left, name, phoneNumber, email);
        } else if (nc > 0) {
            return findNodeById(node.right, name, phoneNumber, email);
        } else {
            int pc = phoneNumber.compareTo(node.contact.phoneNumber);
            if (pc < 0) {
                return findNodeById(node.left, name, phoneNumber, email);
            } else if (pc > 0) {
                return findNodeById(node.right, name, phoneNumber, email);
            } else {
                int ec = email.compareTo(node.contact.email);
                if (ec < 0) {
                    return findNodeById(node.left, name, phoneNumber, email);
                } else if (ec > 0) {
                    return findNodeById(node.right, name, phoneNumber, email);
                } else {
                    return node;
                }
            }
        }
    }

    /**
     * Returns a string representation of all contacts in the tree.
     * @return String of all contacts
     */
    public String getContactsString() {
        StringBuilder ctsb = new StringBuilder();
        inOrderTraversal(root, ctsb);
        return ctsb.toString();
    }

    /**
     * Returns a list of all contacts in the tree.
     * @return List of Contact objects
     */
    public List<Contact> getContactList() {
        List<Contact> csList = new ArrayList<>();
        inOrderTraversal(root, csList);
        return csList;
    }

    // inorder traversal for storing contacts in list
    private void inOrderTraversal(AVLNode node, List<Contact> cs) {
        if (node == null) {
            return;
        }
        inOrderTraversal(node.left, cs);
        cs.add(node.contact);
        inOrderTraversal(node.right, cs);
    }

    /**
     * Saves a list of contacts to a CSV file.
     * @param cs The list of contacts
     * @param filePath The file path to save to
     */
    public static void saveContacts(List<Contact> cs, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Contact c : cs) {
                String line = c.name + "," + c.phoneNumber + "," + c.email;
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 