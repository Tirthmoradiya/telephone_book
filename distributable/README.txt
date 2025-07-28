Telephone Book Application - Distributable Packages
==================================================

This folder contains distributable packages for the Telephone Book application.

Available Packages:
------------------

🍎 macOS Package:
1. Telephone Book-1.0.0.dmg
   - macOS disk image installer
   - Double-click to install on macOS 10.14 or later
   - Includes bundled Java runtime

📦 Portable Package:
2. TelephoneBook.jar
   - Portable Java Archive
   - Requires Java 8 or later to be installed
   - Run with: java -jar TelephoneBook.jar

Creating Packages for Other Platforms:
-------------------------------------

🪟 Windows Packages (Need to be created on Windows):
- Run: build-windows.bat (on Windows with JDK 14+)
- Creates: TelephoneBook-1.0.0.exe and TelephoneBook-1.0.0.msi

🐧 Linux Packages (Need to be created on Linux):
- Run: ./build-linux.sh (on Linux with JDK 14+)
- Creates: telephone-book_1.0.0-1_amd64.deb and telephone-book-1.0.0-1.x86_64.rpm

Installation Instructions:
-------------------------

For macOS:
- DMG: Double-click and drag to Applications folder
- PKG: Double-click and follow installation wizard
- JAR: java -jar TelephoneBook.jar

For Windows:
- EXE: Double-click and follow installation wizard
- MSI: Use with Windows Installer or Group Policy
- JAR: java -jar TelephoneBook.jar

For Linux:
- DEB: sudo dpkg -i telephone-book_1.0.0-1_amd64.deb
- RPM: sudo rpm -i telephone-book-1.0.0-1.x86_64.rpm
- JAR: java -jar TelephoneBook.jar

Creating Packages for Other Platforms:
-------------------------------------

To create packages for other platforms:

1. Windows Packages (on Windows):
   build-windows.bat

2. Linux Packages (on Linux):
   ./build-linux.sh

Note: These build scripts are located in this distributable folder.

System Requirements:
-------------------
- Windows: Windows 10 or later
- macOS: macOS 10.14 or later
- Linux: Ubuntu 18.04+, Fedora 28+, or equivalent
- JAR: Java 8 or later

Troubleshooting:
---------------
- If installation fails, ensure you have administrator privileges
- For JAR file, ensure Java 8+ is installed: java -version
- Check system requirements before installation
- Contact support if issues persist

For more information, visit: https://github.com/Tirthmoradiya/telephone_book
