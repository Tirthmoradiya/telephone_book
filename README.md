# Contact Management System

A modern Java Swing-based contact manager using an AVL tree for fast operations.

## Features
- Add, update, and delete contacts
- Mark contacts as favorites (starred)
- Batch delete (select multiple rows and delete at once)
- Live search by name, phone number, or email (type and press Enter)
- Import/export contacts to CSV
- Dark mode toggle
- Accessible UI (keyboard navigation, screen reader support)
- Undo/redo support for contact changes
- Batch import/export
- Modern, responsive UI
- No group/tag support (feature removed for simplicity)

## How to Run
1. **Compile:**
   ```sh
   javac *.java
   ```
2. **Run:**
   ```sh
   java Main
   ```

## Usage
- **Add Contact:** Click 'Add', fill in the details, and save.
- **Edit Contact:** Select a row, click 'Update', edit, and save.
- **Delete Contact:** Select a row, click 'Delete'.
- **Batch Delete:** Select multiple rows (Ctrl/Cmd+Click), click 'Batch Delete'.
- **Favorite:** Click the star in the table to mark/unmark as favorite.
- **Live Search:** Type in the search box and press Enter to filter by name, number, or email.
- **Import/Export:** Use the File menu.
- **Dark Mode:** Toggle from the menu bar.

## Troubleshooting
- If the app window appears blank or buttons are missing, maximize the window or check your Java version.
- If you get errors about missing classes, ensure you compiled all `.java` files.
- For best experience, use a modern Java IDE or run from terminal.

## Contributing
Pull requests are welcome! For major changes, please open an issue first to discuss what you would like to change.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
