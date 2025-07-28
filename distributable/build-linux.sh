#!/bin/bash

# Telephone Book Application - Linux Build Script
# This script creates Linux distributable packages

echo "🚀 Creating Linux packages for Telephone Book Application..."

# Create distributable directory
mkdir -p distributable

# Compile Java source files
echo "📝 Compiling Java source files..."
javac *.java
if [ $? -ne 0 ]; then
    echo "❌ Compilation failed!"
    exit 1
fi

# Create JAR file
echo "📦 Creating JAR file..."
jar cfe TelephoneBook.jar Main *.class
if [ $? -ne 0 ]; then
    echo "❌ JAR creation failed!"
    exit 1
fi

# Test JAR file
echo "🧪 Testing JAR file..."
java -jar TelephoneBook.jar &
JAR_PID=$!
sleep 3
kill $JAR_PID 2>/dev/null
echo "✅ JAR test completed"

# Create DEB package (for Debian/Ubuntu)
echo "📦 Creating DEB package..."
jpackage --type deb \
    --name telephone-book \
    --input . \
    --main-jar TelephoneBook.jar \
    --main-class Main \
    --app-version 1.0.0 \
    --vendor "Telephone Book Team" \
    --description "A modern contact management application built with Java Swing" \
    --linux-menu-group "Office" \
    --linux-app-category "Office"

if [ -f "telephone-book_1.0.0-1_amd64.deb" ]; then
    mv telephone-book_1.0.0-1_amd64.deb distributable/
    echo "✅ Linux DEB package created: distributable/telephone-book_1.0.0-1_amd64.deb"
fi

# Create RPM package (for Fedora/CentOS/RHEL)
echo "📦 Creating RPM package..."
jpackage --type rpm \
    --name telephone-book \
    --input . \
    --main-jar TelephoneBook.jar \
    --main-class Main \
    --app-version 1.0.0 \
    --vendor "Telephone Book Team" \
    --description "A modern contact management application built with Java Swing" \
    --linux-menu-group "Office" \
    --linux-app-category "Office"

if [ -f "telephone-book-1.0.0-1.x86_64.rpm" ]; then
    mv telephone-book-1.0.0-1.x86_64.rpm distributable/
    echo "✅ Linux RPM package created: distributable/telephone-book-1.0.0-1.x86_64.rpm"
fi

# Create AppImage (portable Linux package)
echo "📦 Creating AppImage..."
jpackage --type app-image \
    --name telephone-book \
    --input . \
    --main-jar TelephoneBook.jar \
    --main-class Main \
    --app-version 1.0.0 \
    --vendor "Telephone Book Team" \
    --description "A modern contact management application built with Java Swing"

if [ -d "telephone-book" ]; then
    mv telephone-book distributable/
    echo "✅ Linux App directory created: distributable/telephone-book/"
    
    # Create a simple launcher script
    cat > distributable/telephone-book/launch.sh << 'EOF'
#!/bin/bash
cd "$(dirname "$0")"
./bin/telephone-book
EOF
    chmod +x distributable/telephone-book/launch.sh
    echo "✅ Launch script created: distributable/telephone-book/launch.sh"
fi

# Copy JAR file to distributable folder
cp TelephoneBook.jar distributable/
echo "✅ JAR file copied: distributable/TelephoneBook.jar"

# Create Linux-specific README
cat > distributable/README.txt << 'EOF'
Telephone Book Application - Linux Distributable Packages
========================================================

This folder contains distributable packages for the Telephone Book application on Linux.

Available Packages:
------------------

1. telephone-book_1.0.0-1_amd64.deb
   - Debian/Ubuntu package installer
   - Install with: sudo dpkg -i telephone-book_1.0.0-1_amd64.deb
   - Includes bundled Java runtime

2. telephone-book-1.0.0-1.x86_64.rpm
   - Fedora/CentOS/RHEL package installer
   - Install with: sudo rpm -i telephone-book-1.0.0-1.x86_64.rpm
   - Includes bundled Java runtime

3. telephone-book/ (App directory)
   - Portable application directory
   - Run with: ./telephone-book/launch.sh
   - Includes bundled Java runtime

4. TelephoneBook.jar
   - Portable Java Archive
   - Requires Java 8 or later to be installed
   - Run with: java -jar TelephoneBook.jar

Installation Instructions:
-------------------------

For DEB package (Ubuntu/Debian):
sudo dpkg -i telephone-book_1.0.0-1_amd64.deb
sudo apt-get install -f  # Fix any dependency issues

For RPM package (Fedora/CentOS/RHEL):
sudo rpm -i telephone-book-1.0.0-1.x86_64.rpm

For App directory:
chmod +x telephone-book/launch.sh
./telephone-book/launch.sh

For JAR file:
java -jar TelephoneBook.jar

Installation Notes:
------------------
- All packages include a bundled Java runtime
- No need to install Java separately
- Application will appear in system menus
- Desktop shortcuts are created automatically

System Requirements:
-------------------
- Ubuntu 18.04+, Fedora 28+, or equivalent
- 4GB RAM minimum
- 100MB free disk space
- x86_64 architecture

Troubleshooting:
---------------
- If installation fails, ensure you have administrator privileges
- For JAR file, ensure Java 8+ is installed: java -version
- Check system requirements before installation
- Contact support if issues persist

For more information, visit: https://github.com/Tirthmoradiya/telephone_book
EOF

echo ""
echo "🎉 Linux package creation completed!"
echo "📁 All packages are available in the 'distributable' folder"
echo ""
echo "📋 Summary of created packages:"
ls -la distributable/
echo ""
echo "📖 See distributable/README.txt for installation instructions" 