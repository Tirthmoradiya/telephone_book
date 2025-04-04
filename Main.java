
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

// simple contact block
class c implements Serializable {
    String n;
    String pn;
    String e;

    c(String n, String pn, String e) {
        this.n = n;
        this.pn = pn;
        this.e = e;
    }
}

// avl node from contact block
class AVLNode {
    c c;
    AVLNode left;
    AVLNode right;
    int height;

    AVLNode(c c) {
        this.c = c;
        this.left = null;
        this.right = null;
        this.height = 1;
    }
}

// main avl tree implementations
class AVLTree implements Serializable {
    public AVLNode r;

    // right rotation
    private AVLNode rr(AVLNode y) {
        AVLNode x = y.left;
        AVLNode T2 = x.right;

        x.right = y;
        y.left = T2;

        y.height = Math.max(gh(y.left), gh(y.right)) + 1;
        x.height = Math.max(gh(x.left), gh(x.right)) + 1;

        return x;
    }

    // gets height
    private int gh(AVLNode node) {
        if (node == null)
            return 0;
        return node.height;
    }

    // left rotation
    private AVLNode lr(AVLNode x) {
        AVLNode y = x.right;
        AVLNode T2 = y.left;

        y.left = x;
        x.right = T2;

        x.height = Math.max(gh(x.left), gh(x.right)) + 1;
        y.height = Math.max(gh(y.left), gh(y.right)) + 1;

        return y;
    }

    // gets hight difference
    private int gb(AVLNode node) {
        if (node == null)
            return 0;
        return gh(node.left) - gh(node.right);
    }

    // insert operation
    private AVLNode insert(AVLNode node, c c) {
        if (node == null) {
            return new AVLNode(c);
        }

        int nc = c.n.compareTo(node.c.n);
        int pc = c.pn.compareTo(node.c.pn);
        int ec = c.e.compareTo(node.c.e);

        if (nc < 0) {
            node.left = insert(node.left, c);
        } else if (nc > 0) {
            node.right = insert(node.right, c);
        } else {
            if (pc < 0) {
                node.left = insert(node.left, c);
            } else if (pc > 0) {
                node.right = insert(node.right, c);
            } else {
                if (ec < 0) {
                    node.left = insert(node.left, c);
                } else {
                    node.right = insert(node.right, c);
                }
            }
        }

        node.height = 1 + Math.max(gh(node.left), gh(node.right));

        int balance = gb(node);

        if (balance > 1) {
            if (nc > 0) {
                node.left = lr(node.left);
            }
            return rr(node);
        }
        if (balance < -1) {
            if (nc < 0) {
                node.right = rr(node.right);
            }
            return lr(node);
        }

        return node;
    }

    // delete operation
    private AVLNode dn(AVLNode node, String n, String pn, String e) {
        if (node == null) {
            return null;
        }

        AVLNode node1 = sndbid(r, n, pn, e);
        if (node1 != null) {
            if (node.left == null) {
                return node.right;
            } else if (node.right == null) {
                return node.left;
            }
            node.c = fm(node.right);
            node.right = dn(node.right, node.c.n, node.c.pn, node.c.e);
        }
        node.height = 1 + Math.max(gh(node.left), gh(node.right));
        int balance = gb(node);

        if (balance > 1 && gb(node.left) >= 0) {
            return rr(node);
        }
        if (balance > 1 && gb(node.left) < 0) {
            node.left = lr(node.left);
            return rr(node);
        }
        if (balance < -1 && gb(node.right) <= 0) {
            return lr(node);
        }
        if (balance < -1 && gb(node.right) > 0) {
            node.right = rr(node.right);
            return lr(node);
        }

        return node;
    }

    // finding minimum node
    private c fm(AVLNode node) {
        while (node.left != null) {
            node = node.left;
        }
        return node.c;
    }

    // in order traversal of tree
    private void iot(AVLNode node, StringBuilder ctsb) {
        if (node == null) {
            return;
        }
        iot(node.left, ctsb);
        ctsb.append("name: ").append(node.c.n)
                .append(", Phone: ").append(node.c.pn)
                .append(", email: ").append(node.c.e).append("\n");
        iot(node.right, ctsb);
    }

    // add contact
    public boolean ac(String n, String pn, String e) {

        c newc = new c(n, pn, e);
        AVLNode node = sndbid(r, n, pn, e);
        if (node == null)
            r = insert(r, newc);
        else
            return false;

        return true;
    }

    // update contact
    public boolean uc(String n, String pn, String e, String newn, String newpn,
            String newe) {
        c updatedc = new c(newn, newpn, newe);

        AVLNode node = sndbid(r, n, pn, e);
        if (node != null) {
            r = dn(r, n, pn, e);
            r = insert(r, updatedc);
            return true;
        } else {
            return false;
        }

    }

    // search by node
    private AVLNode sndbid(AVLNode node, String n, String pn, String e) {
        if (node == null) {
            return null;
        }

        int nc = n.compareTo(node.c.n);
        if (nc < 0) {
            return sndbid(node.left, n, pn, e);
        } else if (nc > 0) {
            return sndbid(node.right, n, pn, e);
        } else {
            int pc = pn.compareTo(node.c.pn);
            if (pc < 0) {
                return sndbid(node.left, n, pn, e);
            } else if (pc > 0) {
                return sndbid(node.right, n, pn, e);
            } else {
                int ec = e.compareTo(node.c.e);
                if (ec < 0) {
                    return sndbid(node.left, n, pn, e);
                } else if (ec > 0) {
                    return sndbid(node.right, n, pn, e);
                } else {
                    return node;
                }
            }
        }
    }

    // returns contacts in string form
    public String gc() {
        StringBuilder ctsb = new StringBuilder();
        iot(r, ctsb);
        return ctsb.toString();
    }

    // search by name
    public String sctsbn(String searchTerm) {
        StringBuilder searchResult = new StringBuilder();
        sbn(r, searchTerm, searchResult);
        return searchResult.toString();
    }

    private void sbn(AVLNode node, String n, StringBuilder searchResult) {
        if (node == null) {
            return;
        }
        sbn(node.left, n, searchResult);
        if (node.c.n.toLowerCase().contains(n.toLowerCase())) {
            searchResult.append("name: ").append(node.c.n)
                    .append(", Phone: ").append(node.c.pn)
                    .append(", email: ").append(node.c.e).append("\n");
        }
        sbn(node.right, n, searchResult);
    }

    // search by phone number
    public String sctsbpn(String searchTerm) {
        StringBuilder searchResult = new StringBuilder();
        sbpn(r, searchTerm, searchResult);
        return searchResult.toString();
    }

    private void sbpn(AVLNode node, String pn, StringBuilder searchResult) {
        if (node == null) {
            return;
        }
        sbpn(node.left, pn, searchResult);
        if (node.c.pn.contains(pn)) {
            searchResult.append("name: ").append(node.c.n)
                    .append(", Phone: ").append(node.c.pn)
                    .append(", email: ").append(node.c.e).append("\n");
        }
        sbpn(node.right, pn, searchResult);
    }

    // gets contact from csv file contact list
    public List<c> gcList() {
        List<c> csList = new ArrayList<>();
        iot(r, csList);
        return csList;
    }

    // inorder traversal for storing contacts in csv file
    private void iot(AVLNode node, List<c> cs) {
        if (node == null) {
            return;
        }
        iot(node.left, cs);
        cs.add(node.c);
        iot(node.right, cs);
    }

    // stores contact in csv file
    public static void sc(List<c> cs, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (c c : cs) {
                String line = c.n + "," + c.pn + "," + c.e;
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// class to read csv file contacts and return as list
class CSVReader {
    public static List<c> rcsv(String filePath) {
        List<c> cs = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.mark(1);
            if (br.read() != 0xFEFF) {
                br.reset();
            }

            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                if (data.length >= 3) {
                    String n = data[0];
                    String pn = data[1];
                    String e = data[2];

                    c c = new c(n, pn, e);
                    cs.add(c);
                } else {
                    System.out.println("Invalid CSV format: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return cs;
    }
}

// our main frame work
public class Main {
    // setting tree loading contacts from csv file to tree,filepath,validation
    // and other things to make gui frame work
    private static final AVLTree cTree = new AVLTree();
    private static final String filePath = "C:\\Users\\PREMIUM\\IdeaProjects\\untitled3\\src\\New Microsoft Excel Worksheet.csv";
    private static final String n_REGEX = "^[A-Za-z]+$";
    private static final String PHONE_REGEX = "^\\d{10}$";
    private static final String e_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    private static boolean validateInput(String input, String regex) {
        return !input.matches(regex);
    }

    public static void main(String[] args) {

        List<c> cs1 = CSVReader.rcsv(filePath);
        for (c c : cs1)
            cTree.ac(c.n, c.pn, c.e);

        JOptionPane.showMessageDialog(null, "Welcome to contact Management System!");

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("contact Management System");
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setLayout(new BorderLayout());

            JTextArea outputArea = new JTextArea();
            outputArea.setEditable(false);
            outputArea.setFont(new Font("Comic Sans MS", Font.PLAIN, 22));
            frame.add(new JScrollPane(outputArea), BorderLayout.CENTER);

            JComboBox<String> operationsComboBox = new JComboBox<>(
                    new String[] { "Add contact", "Update contact", "Display contacts", "Search contacts" });
            frame.add(operationsComboBox, BorderLayout.NORTH);
            operationsComboBox.setFont(new Font("Comic Sans MS", Font.PLAIN, 22));

            JButton executeButton = new JButton("Execute");
            executeButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 22));

            executeButton.addActionListener(e -> {
                String selectedOperation = (String) operationsComboBox.getSelectedItem();
                // all our oprations
                if ("Add contact".equals(selectedOperation)) {
                    String n = JOptionPane.showInputDialog("Enter name:");
                    while (validateInput(n, n_REGEX)) {
                        n = JOptionPane.showInputDialog("Enter correct name:");
                    }
                    String pn = JOptionPane.showInputDialog("Enter phone number:");
                    while (validateInput(pn, PHONE_REGEX)) {
                        pn = JOptionPane.showInputDialog("Enter correct phone number:");
                    }
                    String em = JOptionPane.showInputDialog("Enter email:");
                    while (validateInput(em, e_REGEX)) {
                        em = JOptionPane.showInputDialog("Enter correct email:");
                    }

                    boolean x = cTree.ac(n, pn, em);
                    if (x)
                        outputArea.setText("contact added successfully.");
                    else
                        outputArea.setText("contact founded same so no contact added.");
                } else if ("Update contact".equals(selectedOperation)) {
                    String n = JOptionPane.showInputDialog("Enter name to search for update:");

                    while (validateInput(n, n_REGEX)) {
                        n = JOptionPane.showInputDialog("Enter correct name to search for update:");
                    }
                    String p = JOptionPane.showInputDialog("Enter phone number to search for update:");
                    while (validateInput(p, PHONE_REGEX)) {
                        p = JOptionPane.showInputDialog("Enter correct phone number to search for update:");
                    }
                    String em = JOptionPane.showInputDialog("Enter email to search for update:");
                    while (validateInput(em, e_REGEX)) {
                        em = JOptionPane.showInputDialog("Enter correct email to search for update:");
                    }

                    String nn = JOptionPane.showInputDialog("Enter new name:");
                    while (validateInput(nn, n_REGEX)) {
                        nn = JOptionPane.showInputDialog("Enter correct new name:");
                    }
                    String np = JOptionPane.showInputDialog("Enter new phone number:");
                    while (validateInput(np, PHONE_REGEX)) {
                        np = JOptionPane.showInputDialog("Enter correct new phone number:");
                    }
                    String ne = JOptionPane.showInputDialog("Enter new email:");
                    while (validateInput(ne, e_REGEX)) {
                        ne = JOptionPane.showInputDialog("Enter correct new email:");
                    }
                    boolean cUpdated = cTree.uc(n, p, em, nn,
                            np, ne);
                    if (cUpdated) {
                        outputArea.setText("contact updated successfully.");
                    } else {
                        outputArea.setText("contact not found. No updates were made.");
                    }
                } else if ("Display contacts".equals(selectedOperation)) {
                    String cs = cTree.gc();
                    outputArea.setText(cs);
                } else if ("Search contacts".equals(selectedOperation)) {
                    String searchOption = JOptionPane
                            .showInputDialog("Enter search option (1 for name / 2 for Number):");
                    String searchTerm = JOptionPane.showInputDialog("Enter search term:");
                    StringBuilder sr = new StringBuilder();
                    if ("1".equalsIgnoreCase(searchOption)) {
                        String result = cTree.sctsbn(searchTerm);
                        if (result.isEmpty()) {
                            sr.append("No contacts found with the given n.");
                        } else {
                            sr.append(result);
                        }
                    } else if ("2".equalsIgnoreCase(searchOption)) {
                        String result = cTree.sctsbpn(searchTerm);
                        if (result.isEmpty()) {
                            sr.append("No contacts found with the given phone number.");
                        } else {
                            sr.append(result);
                        }
                    } else {
                        sr.append("Invalid search option.");
                    }
                    outputArea.setText(sr.toString());
                }
            });
            frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    int choice = JOptionPane.showConfirmDialog(frame,
                            "Do you want to close the contact Management System?", "Confirmation",
                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                    if (choice == JOptionPane.YES_OPTION) {
                        // saves new contact back to csv file
                        AVLTree.sc(cTree.gcList(), filePath);
                        JOptionPane.showMessageDialog(frame, "Thank you for using the system!");
                        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    } else {
                        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                    }
                }
            });
            frame.add(executeButton, BorderLayout.SOUTH);
            frame.setVisible(true);
        });
    }
}
