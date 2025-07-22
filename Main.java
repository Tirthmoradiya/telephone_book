import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.table.TableCellRenderer;

// our main frame work
public class Main {
    // setting tree loading contacts from csv file to tree,filepath,validation
    // and other things to make gui frame work
    private static final AVLTree cTree = new AVLTree();
    private static final String n_REGEX = "^[A-Za-z]+$";
    private static final String PHONE_REGEX = "^\\d{10}$";
    private static final String e_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    private static boolean validateInput(String input, String regex) {
        return !input.matches(regex);
    }

    public static void setStatus(JLabel statusBar, Timer[] statusTimer, String msg, Color color) {
        statusBar.setText(msg);
        statusBar.setForeground(color);
        if (statusTimer[0] != null && statusTimer[0].isRunning()) statusTimer[0].stop();
        statusTimer[0] = new Timer(3000, evt -> {
            statusBar.setText("Ready");
            statusBar.setForeground(Color.BLACK);
        });
        statusTimer[0].setRepeats(false);
        statusTimer[0].start();
    }

    /**
     * Main entry point for the Contact Management System application.
     */
    public static void main(String[] args) {
        // Set Nimbus LookAndFeel for modern appearance
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, fall back to default
        }

        // List<c> cs1 = CSVReader.rcsv(filePath);
        // for (c c : cs1)
        //     cTree.ac(c.n, c.pn, c.e);

        JOptionPane.showMessageDialog(null, "Welcome to contact Management System!\nNote: Contacts are not saved automatically. Use Export/Import to save or load contacts.");

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("contact Management System");
            // Set app icon if available
            try {
                frame.setIconImage(Toolkit.getDefaultToolkit().getImage("icon.png"));
            } catch (Exception ex) {
                // Ignore if icon not found
            }
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setLayout(new BorderLayout());

            // Use a modern, professional font throughout
            Font uiFont = new Font("Segoe UI", Font.PLAIN, 18);
            Font uiFontBold = new Font("Segoe UI", Font.BOLD, 20);
            Font uiFontHeader = new Font("Segoe UI", Font.BOLD, 22);

            // Add a prominent notification label at the top
            JLabel notificationLabel = new JLabel("Note: Contacts are NOT saved automatically. Use File > Export/Import to save or load contacts.");
            notificationLabel.setFont(uiFontHeader);
            notificationLabel.setForeground(Color.RED);
            notificationLabel.setHorizontalAlignment(SwingConstants.CENTER);

            // Menu bar for import/export and dark mode
            JMenuBar menuBar = new JMenuBar();
            JMenu fileMenu = new JMenu("File");
            JMenuItem importItem = new JMenuItem("Import Contacts");
            JMenuItem exportItem = new JMenuItem("Export Contacts");
            fileMenu.add(importItem);
            fileMenu.add(exportItem);
            menuBar.add(fileMenu);
            JCheckBoxMenuItem darkModeToggle = new JCheckBoxMenuItem("Dark Mode");
            menuBar.add(darkModeToggle);
            // Help/About menu
            JMenu helpMenu = new JMenu("Help");
            JMenuItem aboutItem = new JMenuItem("About");
            helpMenu.add(aboutItem);
            menuBar.add(helpMenu);
            aboutItem.addActionListener(e -> {
                JOptionPane.showMessageDialog(frame,
                    "Contact Management System\nVersion 2.0\n\nBuilt with Java Swing and AVL Tree\n(c) 2024 Your Name\nFor help, contact: youremail@example.com",
                    "About",
                    JOptionPane.INFORMATION_MESSAGE);
            });
            frame.setJMenuBar(menuBar);

            // Control panel for operations (buttons)
            JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
            controlPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            JButton addButton = new JButton("Add");
            JButton updateButton = new JButton("Update");
            JButton deleteButton = new JButton("Delete");
            JButton clearSearchButton = new JButton("Clear Search");
            JButton batchDeleteButton = new JButton("Batch Delete");
            addButton.setFont(uiFontBold);
            updateButton.setFont(uiFontBold);
            deleteButton.setFont(uiFontBold);
            clearSearchButton.setFont(uiFontBold);
            batchDeleteButton.setFont(uiFontBold);
            controlPanel.add(addButton);
            controlPanel.add(updateButton);
            controlPanel.add(deleteButton);
            controlPanel.add(clearSearchButton);
            controlPanel.add(batchDeleteButton);

            // Remove the Search button from the control panel
            // (Do not add searchButton to controlPanel)

            // Update filterPanel to only include the search label and field
            JLabel searchLabel = new JLabel("Search:");
            JTextField liveSearchField = new JTextField(20);
            liveSearchField.setFont(uiFont);
            JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            filterPanel.add(searchLabel);
            filterPanel.add(liveSearchField);

            // Update topPanel to only include notification, controlPanel, and filterPanel
            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.add(notificationLabel, BorderLayout.PAGE_START);
            topPanel.add(controlPanel, BorderLayout.CENTER);
            topPanel.add(filterPanel, BorderLayout.SOUTH);
            frame.add(topPanel, BorderLayout.NORTH);

            // Center panel for contact display (now JTable)
            String[] columnNames = {"No.", "★", "Name", "Phone Number", "Email"};
            javax.swing.table.DefaultTableModel tableModel = new javax.swing.table.DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    // Only the star column is editable for toggling favorite
                    return column == 1;
                }
            };
            JTable contactTable = new JTable(tableModel) {
                @Override
                public Class<?> getColumnClass(int column) {
                    if (column == 1) return Boolean.class;
                    return super.getColumnClass(column);
                }
            };
            JScrollPane tableScrollPane = new JScrollPane(contactTable);
            tableScrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            JPanel centerPanel = new JPanel(new BorderLayout());
            centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            centerPanel.add(tableScrollPane, BorderLayout.CENTER);
            frame.add(centerPanel, BorderLayout.CENTER);

            // Helper to refresh table from AVLTree and auto-resize columns
            Runnable refreshTable = () -> {
                tableModel.setRowCount(0);
                int rowNum = 1;
                String searchText = liveSearchField.getText().trim().toLowerCase();
                for (Contact contact : cTree.getContactList()) {
                    if (!searchText.isEmpty()) {
                        boolean matches = contact.name.toLowerCase().contains(searchText)
                            || contact.phoneNumber.toLowerCase().contains(searchText)
                            || contact.email.toLowerCase().contains(searchText);
                        if (!matches) continue;
                    }
                    tableModel.addRow(new Object[]{rowNum++, contact.favorite, contact.name, contact.phoneNumber, contact.email});
                }
                // Auto-resize columns to fit content
                for (int col = 0; col < contactTable.getColumnCount(); col++) {
                    int maxWidth = 100;
                    for (int row = 0; row < contactTable.getRowCount(); row++) {
                        TableCellRenderer renderer = contactTable.getCellRenderer(row, col);
                        Component comp = contactTable.prepareRenderer(renderer, row, col);
                        maxWidth = Math.max(comp.getPreferredSize().width + 20, maxWidth);
                    }
                    contactTable.getColumnModel().getColumn(col).setPreferredWidth(maxWidth);
                }
            };
            refreshTable.run();

            // Add key listener to liveSearchField to filter on Enter
            liveSearchField.addActionListener(e -> refreshTable.run());

            // Status bar at the bottom
            JLabel statusBar = new JLabel("Ready");
            statusBar.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            statusBar.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
            frame.add(statusBar, BorderLayout.SOUTH);

            // Dark mode logic (moved here after all components are declared)
            Color darkBg = new Color(40, 40, 40);
            Color darkFg = new Color(220, 220, 220);
            Color darkPanel = new Color(30, 30, 30);
            Color darkTableHeader = new Color(60, 60, 60);
            Runnable applyDarkMode = () -> {
                boolean dark = darkModeToggle.isSelected();
                Color bg = dark ? darkBg : Color.WHITE;
                Color fg = dark ? darkFg : Color.BLACK;
                Color panelBg = dark ? darkPanel : frame.getBackground();
                Color headerBg = dark ? darkTableHeader : contactTable.getTableHeader().getBackground();
                // Frame and panels
                frame.getContentPane().setBackground(panelBg);
                topPanel.setBackground(panelBg);
                controlPanel.setBackground(panelBg);
                centerPanel.setBackground(panelBg);
                tableScrollPane.getViewport().setBackground(bg);
                // Table
                contactTable.setBackground(bg);
                contactTable.setForeground(fg);
                contactTable.setSelectionBackground(dark ? new Color(70, 130, 180) : new Color(184, 207, 229));
                contactTable.setSelectionForeground(dark ? Color.WHITE : Color.BLACK);
                contactTable.getTableHeader().setBackground(headerBg);
                contactTable.getTableHeader().setForeground(fg);
                // Status bar
                statusBar.setBackground(panelBg);
                statusBar.setForeground(fg);
                // Buttons
                addButton.setBackground(panelBg);
                addButton.setForeground(fg);
                updateButton.setBackground(panelBg);
                updateButton.setForeground(fg);
                deleteButton.setBackground(panelBg);
                deleteButton.setForeground(fg);
                clearSearchButton.setBackground(panelBg);
                clearSearchButton.setForeground(fg);
                // Notification label
                notificationLabel.setBackground(panelBg);
                notificationLabel.setForeground(dark ? new Color(255, 100, 100) : Color.RED);
            };
            darkModeToggle.addActionListener(e -> applyDarkMode.run());

            // Toggle favorite (star) in table
            contactTable.getModel().addTableModelListener(e -> {
                if (e.getColumn() == 1 && e.getFirstRow() >= 0) {
                    int row = e.getFirstRow();
                    String name = (String) tableModel.getValueAt(row, 2);
                    String phone = (String) tableModel.getValueAt(row, 3);
                    String email = (String) tableModel.getValueAt(row, 4);
                    for (Contact contact : cTree.getContactList()) {
                        if (contact.name.equals(name) && contact.phoneNumber.equals(phone) && contact.email.equals(email)) {
                            contact.favorite = (Boolean) tableModel.getValueAt(row, 1);
                            break;
                        }
                    }
                }
            });

            // Group filter action
            // groupFilterCombo.addActionListener(e -> refreshTable.run()); // This line was moved

            // Add live search field
            // liveSearchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() { // This line was moved
            //     public void insertUpdate(javax.swing.event.DocumentEvent e) { refreshTable.run(); }
            //     public void removeUpdate(javax.swing.event.DocumentEvent e) { refreshTable.run(); }
            //     public void changedUpdate(javax.swing.event.DocumentEvent e) { refreshTable.run(); }
            // }); // This line was moved

            // Import contacts action
            importItem.addActionListener(e -> {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    java.util.List<Contact> imported = CSVReader.readCSV(file.getAbsolutePath());
                    int count = 0;
                    for (Contact contact : imported) {
                        if (cTree.addContact(contact.name, contact.phoneNumber, contact.email)) count++;
                    }
                    refreshTable.run();
                    setStatus(statusBar, new Timer[]{null}, "Imported " + count + " contacts from " + file.getName(), new Color(0, 128, 0)); // green
                }
            });
            // Export contacts action
            exportItem.addActionListener(e -> {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showSaveDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    AVLTree.saveContacts(cTree.getContactList(), file.getAbsolutePath());
                    setStatus(statusBar, new Timer[]{null}, "Exported contacts to " + file.getName(), new Color(255, 140, 0)); // orange
                }
            });

            // Add Contact Dialog
            addButton.addActionListener(e -> {
                JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
                JTextField nameField = new JTextField();
                JTextField phoneField = new JTextField();
                JTextField emailField = new JTextField();
                panel.add(new JLabel("Name:")); panel.add(nameField);
                panel.add(new JLabel("Phone Number:")); panel.add(phoneField);
                panel.add(new JLabel("Email:")); panel.add(emailField);
                // Focus first field and allow Enter to submit
                Runnable resetBorders = () -> {
                    nameField.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border"));
                    phoneField.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border"));
                    emailField.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border"));
                };
                nameField.requestFocusInWindow();
                JDialog dialog = new JDialog(frame, "Add Contact", true);
                dialog.setResizable(true);
                dialog.setContentPane(panel);
                dialog.pack();
                dialog.setLocationRelativeTo(frame);
                dialog.getRootPane().setDefaultButton(new JButton()); // dummy to allow Enter
                panel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("ENTER"), "submit");
                panel.getActionMap().put("submit", new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        resetBorders.run();
                        String n = nameField.getText().trim();
                        String pn = phoneField.getText().trim();
                        String em = emailField.getText().trim();
                        boolean valid = true;
                        if (validateInput(n, n_REGEX)) {
                            nameField.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                            setStatus(statusBar, new Timer[]{null}, "Invalid name. Only letters allowed.", Color.RED);
                            valid = false;
                        }
                        if (validateInput(pn, PHONE_REGEX)) {
                            phoneField.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                            setStatus(statusBar, new Timer[]{null}, "Invalid phone number. Must be 10 digits.", Color.RED);
                            valid = false;
                        }
                        if (validateInput(em, e_REGEX)) {
                            emailField.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                            setStatus(statusBar, new Timer[]{null}, "Invalid email format.", Color.RED);
                            valid = false;
                        }
                        if (!valid) return;
                        boolean x = cTree.addContact(n, pn, em);
                        refreshTable.run();
                        if (x) setStatus(statusBar, new Timer[]{null}, "Contact added successfully.", new Color(0, 128, 0)); // green
                        else setStatus(statusBar, new Timer[]{null}, "Contact already exists, not added.", Color.RED);
                        dialog.dispose();
                    }
                });
                dialog.getRootPane().setDefaultButton(new JButton() {{
                    addActionListener(panel.getActionMap().get("submit"));
                }});
                dialog.setVisible(true);
            });

            // Update Contact Dialog
            updateButton.addActionListener(e -> {
                int selectedRow = contactTable.getSelectedRow();
                if (selectedRow == -1) {
                    setStatus(statusBar, new Timer[]{null}, "Select a contact to update.", Color.YELLOW);
                    return;
                }
                String oldName = (String) tableModel.getValueAt(selectedRow, 2); // Corrected column index
                String oldPhone = (String) tableModel.getValueAt(selectedRow, 3); // Corrected column index
                String oldEmail = (String) tableModel.getValueAt(selectedRow, 4); // Corrected column index
                JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
                JTextField nameField = new JTextField(oldName);
                JTextField phoneField = new JTextField(oldPhone);
                JTextField emailField = new JTextField(oldEmail);
                panel.add(new JLabel("New Name:")); panel.add(nameField);
                panel.add(new JLabel("New Phone Number:")); panel.add(phoneField);
                panel.add(new JLabel("New Email:")); panel.add(emailField);
                Runnable resetBorders = () -> {
                    nameField.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border"));
                    phoneField.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border"));
                    emailField.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border"));
                };
                nameField.requestFocusInWindow();
                JDialog dialog = new JDialog(frame, "Update Contact", true);
                dialog.setResizable(true);
                dialog.setContentPane(panel);
                dialog.pack();
                dialog.setLocationRelativeTo(frame);
                dialog.getRootPane().setDefaultButton(new JButton());
                panel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("ENTER"), "submit");
                panel.getActionMap().put("submit", new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        resetBorders.run();
                        String nn = nameField.getText().trim();
                        String np = phoneField.getText().trim();
                        String ne = emailField.getText().trim();
                        boolean valid = true;
                        if (validateInput(nn, n_REGEX)) {
                            nameField.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                            setStatus(statusBar, new Timer[]{null}, "Invalid name. Only letters allowed.", Color.RED);
                            valid = false;
                        }
                        if (validateInput(np, PHONE_REGEX)) {
                            phoneField.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                            setStatus(statusBar, new Timer[]{null}, "Invalid phone number. Must be 10 digits.", Color.RED);
                            valid = false;
                        }
                        if (validateInput(ne, e_REGEX)) {
                            emailField.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                            setStatus(statusBar, new Timer[]{null}, "Invalid email format.", Color.RED);
                            valid = false;
                        }
                        if (!valid) return;
                        boolean cUpdated = cTree.updateContact(oldName, oldPhone, oldEmail, nn, np, ne);
                        refreshTable.run();
                        if (cUpdated) setStatus(statusBar, new Timer[]{null}, "Contact updated successfully.", new Color(0, 128, 0)); // green
                        else setStatus(statusBar, new Timer[]{null}, "Contact not found. No updates were made.", Color.RED);
                        dialog.dispose();
                    }
                });
                dialog.getRootPane().setDefaultButton(new JButton() {{
                    addActionListener(panel.getActionMap().get("submit"));
                }});
                dialog.setVisible(true);
            });

            // Delete Contact
            deleteButton.addActionListener(e -> {
                int selectedRow = contactTable.getSelectedRow();
                if (selectedRow == -1) {
                    setStatus(statusBar, new Timer[]{null}, "Select a contact to delete.", Color.YELLOW);
                    return;
                }
                String name = (String) tableModel.getValueAt(selectedRow, 2); // Corrected column index
                String phone = (String) tableModel.getValueAt(selectedRow, 3); // Corrected column index
                String email = (String) tableModel.getValueAt(selectedRow, 4); // Corrected column index
                int confirm = JOptionPane.showConfirmDialog(frame, "Delete selected contact?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    // Remove from AVL tree
                    cTree.root = cTree.delete(cTree.root, name, phone, email);
                    refreshTable.run();
                    setStatus(statusBar, new Timer[]{null}, "Contact deleted.", Color.RED);
                }
            });

            // Clear Search Button
            clearSearchButton.addActionListener(e -> refreshTable.run());

            // Batch delete logic
            batchDeleteButton.addActionListener(e -> {
                int[] selectedRows = contactTable.getSelectedRows();
                if (selectedRows.length == 0) {
                    setStatus(statusBar, new Timer[]{null}, "No contacts selected for batch delete.", Color.YELLOW);
                    return;
                }
                int confirm = JOptionPane.showConfirmDialog(frame, "Delete all selected contacts?", "Confirm Batch Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    java.util.List<Contact> toDelete = new java.util.ArrayList<>();
                    for (int row : selectedRows) {
                        String name = (String) tableModel.getValueAt(row, 2);
                        String phone = (String) tableModel.getValueAt(row, 3);
                        String email = (String) tableModel.getValueAt(row, 4);
                        for (Contact contact : cTree.getContactList()) {
                            if (contact.name.equals(name) && contact.phoneNumber.equals(phone) && contact.email.equals(email)) {
                                toDelete.add(contact);
                                break;
                            }
                        }
                    }
                    for (Contact contact : toDelete) {
                        cTree.root = cTree.delete(cTree.root, contact.name, contact.phoneNumber, contact.email);
                    }
                    refreshTable.run();
                    setStatus(statusBar, new Timer[]{null}, "Batch delete completed.", Color.RED);
                }
            });

            frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    int choice = JOptionPane.showConfirmDialog(frame,
                            "Do you want to close the contact Management System?", "Confirmation",
                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                    if (choice == JOptionPane.YES_OPTION) {
                        // No auto-save to CSV
                        JOptionPane.showMessageDialog(frame, "Thank you for using the system!");
                        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    } else {
                        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                    }
                }
            });
            frame.setVisible(true);

            // Keyboard shortcuts
            frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK), "addContact");
            frame.getRootPane().getActionMap().put("addContact", new AbstractAction() {
                public void actionPerformed(ActionEvent e) { addButton.doClick(); }
            });
            // Remove all references to searchButton
            // (Do not declare, add, or use searchButton anywhere)
            frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "deleteContact");
            frame.getRootPane().getActionMap().put("deleteContact", new AbstractAction() {
                public void actionPerformed(ActionEvent e) { deleteButton.doClick(); }
            });

            // Support keyboard navigation for table rows
            contactTable.setRowSelectionAllowed(true);
            contactTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            contactTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "selectPreviousRow");
            contactTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "selectNextRow");
            contactTable.getActionMap().put("selectPreviousRow", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    int row = contactTable.getSelectedRow();
                    if (row > 0) contactTable.setRowSelectionInterval(row - 1, row - 1);
                }
            });
            contactTable.getActionMap().put("selectNextRow", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    int row = contactTable.getSelectedRow();
                    if (row < contactTable.getRowCount() - 1) contactTable.setRowSelectionInterval(row + 1, row + 1);
                }
            });

            // At the end of SwingUtilities.invokeLater, apply the current mode
            applyDarkMode.run();
        });
    }
}
