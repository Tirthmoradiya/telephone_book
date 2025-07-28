@echo off
REM Telephone Book Application - Windows Build Script
REM This script creates Windows distributable packages

echo 🚀 Creating Windows packages for Telephone Book Application...

REM Create distributable directory
if not exist distributable mkdir distributable

REM Compile Java source files
echo 📝 Compiling Java source files...
javac *.java
if errorlevel 1 (
    echo ❌ Compilation failed!
    pause
    exit /b 1
)

REM Create JAR file
echo 📦 Creating JAR file...
jar cfe TelephoneBook.jar Main *.class
if errorlevel 1 (
    echo ❌ JAR creation failed!
    pause
    exit /b 1
)

REM Test JAR file
echo 🧪 Testing JAR file...
java -jar TelephoneBook.jar
if errorlevel 1 (
    echo ❌ JAR test failed!
    pause
    exit /b 1
)

REM Create Windows EXE package
echo 📦 Creating Windows EXE package...
jpackage --type exe ^
    --name "Telephone Book" ^
    --input . ^
    --main-jar TelephoneBook.jar ^
    --main-class Main ^
    --app-version 1.0.0 ^
    --vendor "Telephone Book Team" ^
    --description "A modern contact management application built with Java Swing" ^
    --win-menu ^
    --win-shortcut ^
    --win-dir-chooser ^
    --win-per-user-install

if exist "Telephone Book-1.0.0.exe" (
    move "Telephone Book-1.0.0.exe" distributable\
    echo ✅ Windows EXE package created: distributable\Telephone Book-1.0.0.exe
)

REM Create Windows MSI package
echo 📦 Creating Windows MSI package...
jpackage --type msi ^
    --name "Telephone Book" ^
    --input . ^
    --main-jar TelephoneBook.jar ^
    --main-class Main ^
    --app-version 1.0.0 ^
    --vendor "Telephone Book Team" ^
    --description "A modern contact management application built with Java Swing" ^
    --win-menu ^
    --win-shortcut ^
    --win-dir-chooser ^
    --win-per-user-install

if exist "Telephone Book-1.0.0.msi" (
    move "Telephone Book-1.0.0.msi" distributable\
    echo ✅ Windows MSI package created: distributable\Telephone Book-1.0.0.msi
)

REM Copy JAR file to distributable folder
copy TelephoneBook.jar distributable\
echo ✅ JAR file copied: distributable\TelephoneBook.jar

echo.
echo 🎉 Windows package creation completed!
echo 📁 All packages are available in the 'distributable' folder
echo.
echo 📋 Summary of created packages:
dir distributable
echo.
echo 📖 See distributable\README.txt for installation instructions
pause 