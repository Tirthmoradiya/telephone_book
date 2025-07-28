import java.io.*;
import java.util.*;

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

    // Compare two contacts for ordering
    private int compareContacts(Contact c1, Contact c2) {
        int nameCompare = c1.name.compareTo(c2.name);
        if (nameCompare != 0) return nameCompare;
        
        int phoneCompare = c1.phoneNumber.compareTo(c2.phoneNumber);
        if (phoneCompare != 0) return phoneCompare;
        
        return c1.email.compareTo(c2.email);
    }

    // insert operation
    private AVLNode insert(AVLNode node, Contact contact) {
        if (node == null) {
            return new AVLNode(contact);
        }

        int comparison = compareContacts(contact, node.contact);
        
        if (comparison < 0) {
            node.left = insert(node.left, contact);
        } else if (comparison > 0) {
            node.right = insert(node.right, contact);
        } else {
            // Duplicate contact - don't insert
            return node;
        }

        node.height = 1 + Math.max(getHeight(node.left), getHeight(node.right));

        int balance = getBalance(node);

        if (balance > 1) {
            if (compareContacts(contact, node.left.contact) > 0) {
                node.left = leftRotate(node.left);
            }
            return rightRotate(node);
        }
        if (balance < -1) {
            if (compareContacts(contact, node.right.contact) < 0) {
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

    /**
     * Finds duplicate contacts in the tree.
     * @return List of duplicate contact groups
     */
    public List<List<Contact>> findDuplicates() {
        List<List<Contact>> duplicateGroups = new ArrayList<>();
        List<Contact> allContacts = getContactList();
        
        // Use a map to group contacts by their normalized data
        Map<String, List<Contact>> phoneGroups = new HashMap<>();
        Map<String, List<Contact>> emailGroups = new HashMap<>();
        Map<String, List<Contact>> nameGroups = new HashMap<>();
        
        for (Contact contact : allContacts) {
            // Group by normalized phone
            String normPhone = Contact.normalizePhone(contact.phoneNumber);
            if (!normPhone.isEmpty()) {
                phoneGroups.computeIfAbsent(normPhone, k -> new ArrayList<>()).add(contact);
            }
            
            // Group by normalized email
            String normEmail = Contact.normalizeEmail(contact.email);
            if (!normEmail.isEmpty()) {
                emailGroups.computeIfAbsent(normEmail, k -> new ArrayList<>()).add(contact);
            }
            
            // Group by normalized name
            String normName = Contact.normalizeName(contact.name);
            if (!normName.isEmpty()) {
                nameGroups.computeIfAbsent(normName, k -> new ArrayList<>()).add(contact);
            }
        }
        
        // Find groups with more than one contact
        for (List<Contact> group : phoneGroups.values()) {
            if (group.size() > 1) {
                duplicateGroups.add(new ArrayList<>(group));
            }
        }
        
        for (List<Contact> group : emailGroups.values()) {
            if (group.size() > 1) {
                duplicateGroups.add(new ArrayList<>(group));
            }
        }
        
        for (List<Contact> group : nameGroups.values()) {
            if (group.size() > 1) {
                duplicateGroups.add(new ArrayList<>(group));
            }
        }
        
        // Remove duplicate groups (same contacts in different groups)
        return removeDuplicateGroups(duplicateGroups);
    }

    /**
     * Removes duplicate groups that contain the same contacts.
     * @param groups List of duplicate groups
     * @return List with duplicate groups removed
     */
    private List<List<Contact>> removeDuplicateGroups(List<List<Contact>> groups) {
        List<List<Contact>> uniqueGroups = new ArrayList<>();
        
        for (List<Contact> group : groups) {
            boolean isDuplicate = false;
            for (List<Contact> existingGroup : uniqueGroups) {
                if (areGroupsEqual(group, existingGroup)) {
                    isDuplicate = true;
                    break;
                }
            }
            if (!isDuplicate) {
                uniqueGroups.add(group);
            }
        }
        
        return uniqueGroups;
    }

    /**
     * Checks if two groups contain the same contacts.
     * @param group1 First group
     * @param group2 Second group
     * @return true if groups are equal
     */
    private boolean areGroupsEqual(List<Contact> group1, List<Contact> group2) {
        if (group1.size() != group2.size()) return false;
        
        for (Contact contact : group1) {
            boolean found = false;
            for (Contact other : group2) {
                if (contact.equals(other)) {
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }
        
        return true;
    }

    /**
     * Merges duplicate contacts and removes the originals.
     * @param duplicateGroups List of duplicate groups to merge
     * @return Number of contacts merged
     */
    public int mergeDuplicates(List<List<Contact>> duplicateGroups) {
        int mergedCount = 0;
        
        for (List<Contact> group : duplicateGroups) {
            if (group.size() < 2) continue;
            
            // Merge all contacts in the group
            Contact merged = group.get(0);
            for (int i = 1; i < group.size(); i++) {
                merged = merged.mergeWith(group.get(i));
            }
            
            // Remove all original contacts
            for (Contact contact : group) {
                root = delete(root, contact.name, contact.phoneNumber, contact.email);
            }
            
            // Add the merged contact
            addContact(merged.name, merged.phoneNumber, merged.email);
            mergedCount += group.size() - 1; // Number of contacts merged
        }
        
        return mergedCount;
    }

    /**
     * Validates all contacts in the tree.
     * @return List of invalid contacts
     */
    public List<Contact> findInvalidContacts() {
        List<Contact> invalidContacts = new ArrayList<>();
        List<Contact> allContacts = getContactList();
        
        for (Contact contact : allContacts) {
            if (!contact.isValid()) {
                invalidContacts.add(contact);
            }
        }
        
        return invalidContacts;
    }
} 