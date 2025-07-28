import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Contact Management System - Main Application
 * A modern Java Swing-based contact manager using AVL tree for efficient operations.
 */
public class Main {
    private static final AVLTree cTree = new AVLTree();
    private static final String NAME_REGEX = "^[A-Za-z\\s]+$";
    private static final String PHONE_REGEX = "^\\d{10}$";
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    
    // Cache for frequently accessed data
    private static final Map<String, java.util.regex.Pattern> regexCache = new HashMap<>();

    private static boolean validateInput(String input, String regex) {
        if (input == null || input.trim().isEmpty()) return true;
        
        // Use cached pattern for better performance
        java.util.regex.Pattern pattern = regexCache.computeIfAbsent(regex, java.util.regex.Pattern::compile);
        return !pattern.matcher(input.trim()).matches();
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

            // Control panel for operations (buttons) - Responsive layout
            JPanel controlPanel = new JPanel(new GridBagLayout());
            controlPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            JButton addButton = new JButton("Add");
            JButton updateButton = new JButton("Update");
            JButton deleteButton = new JButton("Delete");
            JButton clearSearchButton = new JButton("Clear Search");
            JButton batchDeleteButton = new JButton("Batch Delete");
            JButton selectAllButton = new JButton("Select All");
            JButton findDuplicatesButton = new JButton("Find Duplicates");
            JButton validateContactsButton = new JButton("Validate");
            addButton.setFont(uiFontBold);
            updateButton.setFont(uiFontBold);
            deleteButton.setFont(uiFontBold);
            clearSearchButton.setFont(uiFontBold);
            batchDeleteButton.setFont(uiFontBold);
            selectAllButton.setFont(uiFontBold);
            findDuplicatesButton.setFont(uiFontBold);
            validateContactsButton.setFont(uiFontBold);
            // Add buttons with responsive layout
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(2, 5, 2, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0; // Make buttons expand to fill available space
            
            controlPanel.add(addButton, gbc);
            gbc.gridx++;
            controlPanel.add(updateButton, gbc);
            gbc.gridx++;
            controlPanel.add(deleteButton, gbc);
            gbc.gridx++;
            controlPanel.add(clearSearchButton, gbc);
            gbc.gridx++;
            controlPanel.add(batchDeleteButton, gbc);
            gbc.gridx++;
            controlPanel.add(selectAllButton, gbc);
            gbc.gridx++;
            controlPanel.add(findDuplicatesButton, gbc);
            gbc.gridx++;
            controlPanel.add(validateContactsButton, gbc);

            // Update filterPanel to only include the search label and field
            JLabel searchLabel = new JLabel("Search:");
            JTextField liveSearchField = new JTextField(20);
            liveSearchField.setFont(uiFont);
            JPanel filterPanel = new JPanel(new BorderLayout());
            filterPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            
            JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            searchPanel.add(searchLabel);
            searchPanel.add(liveSearchField);
            filterPanel.add(searchPanel, BorderLayout.WEST);

            // Update topPanel to only include notification, controlPanel, and filterPanel
            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.add(notificationLabel, BorderLayout.PAGE_START);
            topPanel.add(controlPanel, BorderLayout.CENTER);
            topPanel.add(filterPanel, BorderLayout.SOUTH);
            frame.add(topPanel, BorderLayout.NORTH);

            // Center panel for contact display (now JTable)
            String[] columnNames = {"No.", "★", "Name", "Phone Number", "Email"};
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
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
            
            // Set custom renderer for the favorite column to show stars
            contactTable.getColumnModel().getColumn(1).setCellRenderer(new TableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    JLabel label = new JLabel();
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                    label.setFont(new Font("Segoe UI", Font.BOLD, 16));
                    
                    if (value instanceof Boolean) {
                        label.setText((Boolean) value ? "★" : "☆");
                        label.setForeground((Boolean) value ? Color.ORANGE : Color.GRAY);
                    } else {
                        label.setText("☆");
                        label.setForeground(Color.GRAY);
                    }
                    
                    if (isSelected) {
                        label.setBackground(table.getSelectionBackground());
                        label.setOpaque(true);
                    } else {
                        label.setBackground(table.getBackground());
                        label.setOpaque(false);
                    }
                    
                    return label;
                }
            });
            

            JScrollPane tableScrollPane = new JScrollPane(contactTable);
            tableScrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            JPanel centerPanel = new JPanel(new BorderLayout());
            centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            centerPanel.add(tableScrollPane, BorderLayout.CENTER);
            frame.add(centerPanel, BorderLayout.CENTER);

            // Helper to refresh table from AVLTree and auto-resize columns
            Runnable refreshTable = () -> {
                SwingUtilities.invokeLater(() -> {
                    tableModel.setRowCount(0);
                    int rowNum = 1;
                    String searchText = liveSearchField.getText().trim().toLowerCase();
                    List<Contact> contacts = cTree.getContactList();
                    
                    for (Contact contact : contacts) {
                        if (!searchText.isEmpty()) {
                            boolean matches = contact.name.toLowerCase().contains(searchText)
                                || contact.phoneNumber.toLowerCase().contains(searchText)
                                || contact.email.toLowerCase().contains(searchText);
                            if (!matches) continue;
                        }
                        tableModel.addRow(new Object[]{rowNum++, contact.favorite, contact.name, contact.phoneNumber, contact.email});
                    }
                    
                    // Auto-resize columns only if table has data
                    if (tableModel.getRowCount() > 0) {
                        for (int col = 0; col < contactTable.getColumnCount(); col++) {
                            int maxWidth = 100;
                            for (int row = 0; row < contactTable.getRowCount(); row++) {
                                TableCellRenderer renderer = contactTable.getCellRenderer(row, col);
                                Component comp = contactTable.prepareRenderer(renderer, row, col);
                                maxWidth = Math.max(comp.getPreferredSize().width + 20, maxWidth);
                            }
                            contactTable.getColumnModel().getColumn(col).setPreferredWidth(maxWidth);
                        }
                    }
                });
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
            Runnable applyDarkMode = () -> {
                boolean dark = darkModeToggle.isSelected();
                Color bg = dark ? darkBg : Color.WHITE;
                Color fg = dark ? darkFg : Color.BLACK;
                Color panelBg = dark ? darkPanel : frame.getBackground();
                Color headerBg = new Color(240, 240, 240); // Same light grey for both modes
                Color headerFg = Color.BLACK; // Same black text for both modes
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
                contactTable.getTableHeader().setForeground(headerFg);
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
                batchDeleteButton.setBackground(panelBg);
                batchDeleteButton.setForeground(fg);
                selectAllButton.setBackground(panelBg);
                selectAllButton.setForeground(fg);
                findDuplicatesButton.setBackground(panelBg);
                findDuplicatesButton.setForeground(fg);
                validateContactsButton.setBackground(panelBg);
                validateContactsButton.setForeground(fg);
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
                        if (validateInput(n, NAME_REGEX)) {
                            nameField.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                            setStatus(statusBar, new Timer[]{null}, "Invalid name. Only letters and spaces allowed.", Color.RED);
                            valid = false;
                        }
                        if (validateInput(pn, PHONE_REGEX)) {
                            phoneField.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                            setStatus(statusBar, new Timer[]{null}, "Invalid phone number. Must be 10 digits.", Color.RED);
                            valid = false;
                        }
                        if (validateInput(em, EMAIL_REGEX)) {
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
                        if (validateInput(nn, NAME_REGEX)) {
                            nameField.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                            setStatus(statusBar, new Timer[]{null}, "Invalid name. Only letters and spaces allowed.", Color.RED);
                            valid = false;
                        }
                        if (validateInput(np, PHONE_REGEX)) {
                            phoneField.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                            setStatus(statusBar, new Timer[]{null}, "Invalid phone number. Must be 10 digits.", Color.RED);
                            valid = false;
                        }
                        if (validateInput(ne, EMAIL_REGEX)) {
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
            clearSearchButton.addActionListener(e -> {
                liveSearchField.setText("");
                refreshTable.run();
                setStatus(statusBar, new Timer[]{null}, "Search cleared.", Color.GREEN);
                // Clear regex cache periodically to prevent memory buildup
                if (regexCache.size() > 10) {
                    regexCache.clear();
                }
            });
            
            // Select All Button
            selectAllButton.addActionListener(e -> {
                if (contactTable.getRowCount() > 0) {
                    contactTable.selectAll();
                    setStatus(statusBar, new Timer[]{null}, 
                        "Selected all " + contactTable.getRowCount() + " contacts.", Color.BLUE);
                } else {
                    setStatus(statusBar, new Timer[]{null}, "No contacts to select.", Color.YELLOW);
                }
            });

            // Find Duplicates Button
            findDuplicatesButton.addActionListener(e -> {
                List<List<Contact>> duplicates = cTree.findDuplicates();
                if (duplicates.isEmpty()) {
                    setStatus(statusBar, new Timer[]{null}, "No duplicate contacts found.", new Color(0, 128, 0));
                } else {
                    showDuplicatesDialog(duplicates, frame, statusBar, refreshTable);
                }
            });

            // Validate Contacts Button
            validateContactsButton.addActionListener(e -> {
                List<Contact> invalidContacts = cTree.findInvalidContacts();
                if (invalidContacts.isEmpty()) {
                    setStatus(statusBar, new Timer[]{null}, "All contacts are valid.", new Color(0, 128, 0));
                } else {
                    showInvalidContactsDialog(invalidContacts, frame, statusBar, refreshTable);
                }
            });

            // Batch delete logic
            batchDeleteButton.addActionListener(e -> {
                int[] selectedRows = contactTable.getSelectedRows();
                
                if (selectedRows.length == 0) {
                    setStatus(statusBar, new Timer[]{null}, "No contacts selected for batch delete.", Color.YELLOW);
                    return;
                }
                
                int confirm = JOptionPane.showConfirmDialog(frame, 
                    "Delete " + selectedRows.length + " selected contact(s)?", 
                    "Confirm Batch Delete", JOptionPane.YES_NO_OPTION);
                    
                if (confirm == JOptionPane.YES_OPTION) {
                    java.util.List<Contact> toDelete = new java.util.ArrayList<>();
                    
                    // Get all contacts from the tree first
                    List<Contact> allContacts = cTree.getContactList();
                    
                    for (int row : selectedRows) {
                        if (row >= 0 && row < tableModel.getRowCount()) {
                            String name = (String) tableModel.getValueAt(row, 2);
                            String phone = (String) tableModel.getValueAt(row, 3);
                            String email = (String) tableModel.getValueAt(row, 4);
                            
                            for (Contact contact : allContacts) {
                                if (contact.name.equals(name) && contact.phoneNumber.equals(phone) && contact.email.equals(email)) {
                                    toDelete.add(contact);
                                    break;
                                }
                            }
                        }
                    }
                    
                    int deletedCount = 0;
                    for (Contact contact : toDelete) {
                        cTree.root = cTree.delete(cTree.root, contact.name, contact.phoneNumber, contact.email);
                        deletedCount++;
                    }
                    
                    refreshTable.run();
                    setStatus(statusBar, new Timer[]{null}, 
                        "Batch delete completed. Deleted " + deletedCount + " contact(s).", Color.RED);
                }
            });

            frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    int choice = JOptionPane.showConfirmDialog(frame,
                            "Do you want to close the contact Management System?", "Confirmation",
                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                    if (choice == JOptionPane.YES_OPTION) {
                        // Clean up resources
                        regexCache.clear();
                        System.gc(); // Suggest garbage collection
                        JOptionPane.showMessageDialog(frame, "Thank you for using the system!");
                        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    } else {
                        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                    }
                }
            });

            // Add component listener for responsive layout
            frame.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    // Force layout update when window is resized
                    controlPanel.revalidate();
                    controlPanel.repaint();
                }
            });
            frame.setVisible(true);

            // Keyboard shortcuts - cross-platform compatible
            String os = System.getProperty("os.name").toLowerCase();
            int cmdKey = os.contains("mac") ? InputEvent.META_DOWN_MASK : InputEvent.CTRL_DOWN_MASK;
            
            // Add Contact: Ctrl/Cmd + N
            frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_N, cmdKey), "addContact");
            frame.getRootPane().getActionMap().put("addContact", new AbstractAction() {
                public void actionPerformed(ActionEvent e) { addButton.doClick(); }
            });
            
            // Delete Contact: Delete key
            frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "deleteContact");
            frame.getRootPane().getActionMap().put("deleteContact", new AbstractAction() {
                public void actionPerformed(ActionEvent e) { deleteButton.doClick(); }
            });
            
            // Update Contact: Ctrl/Cmd + E
            frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_E, cmdKey), "updateContact");
            frame.getRootPane().getActionMap().put("updateContact", new AbstractAction() {
                public void actionPerformed(ActionEvent e) { updateButton.doClick(); }
            });
            
            // Search: Ctrl/Cmd + F
            frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F, cmdKey), "focusSearch");
            frame.getRootPane().getActionMap().put("focusSearch", new AbstractAction() {
                public void actionPerformed(ActionEvent e) { 
                    liveSearchField.requestFocusInWindow();
                    liveSearchField.selectAll();
                }
            });
            
            // Clear Search: Ctrl/Cmd + L
            frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_L, cmdKey), "clearSearch");
            frame.getRootPane().getActionMap().put("clearSearch", new AbstractAction() {
                public void actionPerformed(ActionEvent e) { 
                    liveSearchField.setText("");
                    refreshTable.run();
                }
            });
            
            // Select All: Ctrl/Cmd + A
            frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_A, cmdKey), "selectAll");
            frame.getRootPane().getActionMap().put("selectAll", new AbstractAction() {
                public void actionPerformed(ActionEvent e) { selectAllButton.doClick(); }
            });
            
            // Batch Delete: Ctrl/Cmd + Shift + D
            frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_D, cmdKey | InputEvent.SHIFT_DOWN_MASK), "batchDelete");
            frame.getRootPane().getActionMap().put("batchDelete", new AbstractAction() {
                public void actionPerformed(ActionEvent e) { batchDeleteButton.doClick(); }
            });
            
            // Import: Ctrl/Cmd + I
            frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_I, cmdKey), "importContacts");
            frame.getRootPane().getActionMap().put("importContacts", new AbstractAction() {
                public void actionPerformed(ActionEvent e) { importItem.doClick(); }
            });
            
            // Export: Ctrl/Cmd + S
            frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_S, cmdKey), "exportContacts");
            frame.getRootPane().getActionMap().put("exportContacts", new AbstractAction() {
                public void actionPerformed(ActionEvent e) { exportItem.doClick(); }
            });
            
            // Toggle Dark Mode: Ctrl/Cmd + T
            frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_T, cmdKey), "toggleDarkMode");
            frame.getRootPane().getActionMap().put("toggleDarkMode", new AbstractAction() {
                public void actionPerformed(ActionEvent e) { 
                    darkModeToggle.setSelected(!darkModeToggle.isSelected());
                    applyDarkMode.run();
                }
            });
            
            // Help/About: F1
            frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "showHelp");
            frame.getRootPane().getActionMap().put("showHelp", new AbstractAction() {
                public void actionPerformed(ActionEvent e) { aboutItem.doClick(); }
            });

            // Support keyboard navigation for table rows
            contactTable.setRowSelectionAllowed(true);
            contactTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
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

    /**
     * Shows a dialog for managing duplicate contacts.
     * @param duplicates List of duplicate contact groups
     * @param parent Parent frame
     * @param statusBar Status bar for messages
     * @param refreshTable Runnable to refresh the table
     */
    private static void showDuplicatesDialog(List<List<Contact>> duplicates, JFrame parent, JLabel statusBar, Runnable refreshTable) {
        JDialog dialog = new JDialog(parent, "Duplicate Contacts Found", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(parent);

        // Create table model for duplicates
        String[] columnNames = {"Group", "Name", "Phone", "Email", "Favorite"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable duplicatesTable = new JTable(tableModel);

        // Populate table with duplicates
        int groupNum = 1;
        for (List<Contact> group : duplicates) {
            for (Contact contact : group) {
                tableModel.addRow(new Object[]{
                    "Group " + groupNum,
                    contact.name,
                    contact.phoneNumber,
                    contact.email,
                    contact.favorite ? "★" : ""
                });
            }
            groupNum++;
        }

        JScrollPane scrollPane = new JScrollPane(duplicatesTable);
        dialog.add(scrollPane, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton mergeButton = new JButton("Merge All Duplicates");
        JButton closeButton = new JButton("Close");

        mergeButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(dialog,
                "This will merge all duplicate contacts. Continue?",
                "Confirm Merge",
                JOptionPane.YES_NO_OPTION);
            
            if (result == JOptionPane.YES_OPTION) {
                int mergedCount = cTree.mergeDuplicates(duplicates);
                refreshTable.run();
                setStatus(statusBar, new Timer[]{null}, 
                    "Merged " + mergedCount + " duplicate contacts.", new Color(0, 128, 0));
                dialog.dispose();
            }
        });

        closeButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(mergeButton);
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    /**
     * Shows a dialog for managing invalid contacts.
     * @param invalidContacts List of invalid contacts
     * @param parent Parent frame
     * @param statusBar Status bar for messages
     * @param refreshTable Runnable to refresh the table
     */
    private static void showInvalidContactsDialog(List<Contact> invalidContacts, JFrame parent, JLabel statusBar, Runnable refreshTable) {
        JDialog dialog = new JDialog(parent, "Invalid Contacts Found", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(parent);

        // Create table model for invalid contacts
        String[] columnNames = {"Name", "Phone", "Email", "Issues"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable invalidTable = new JTable(tableModel);

        // Populate table with invalid contacts
        for (Contact contact : invalidContacts) {
            StringBuilder issues = new StringBuilder();
            if (!Contact.isValidName(contact.name)) {
                issues.append("Invalid name; ");
            }
            if (!Contact.isValidPhone(contact.phoneNumber)) {
                issues.append("Invalid phone; ");
            }
            if (!Contact.isValidEmail(contact.email)) {
                issues.append("Invalid email; ");
            }

            tableModel.addRow(new Object[]{
                contact.name,
                contact.phoneNumber,
                contact.email,
                issues.toString()
            });
        }

        JScrollPane scrollPane = new JScrollPane(invalidTable);
        dialog.add(scrollPane, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton deleteInvalidButton = new JButton("Delete All Invalid");
        JButton closeButton = new JButton("Close");

        deleteInvalidButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(dialog,
                "This will delete all invalid contacts. Continue?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
            
            if (result == JOptionPane.YES_OPTION) {
                for (Contact contact : invalidContacts) {
                    cTree.root = cTree.delete(cTree.root, contact.name, contact.phoneNumber, contact.email);
                }
                refreshTable.run();
                setStatus(statusBar, new Timer[]{null}, 
                    "Deleted " + invalidContacts.size() + " invalid contacts.", Color.RED);
                dialog.dispose();
            }
        });

        closeButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(deleteInvalidButton);
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}
