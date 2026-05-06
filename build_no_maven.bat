@echo off
echo ========================================
echo MiniERP No-Maven Build & Run
echo ========================================
echo Creating lib directory...
if not exist lib mkdir lib

echo Downloading required dependencies...
curl.exe -L --fail -S -o lib\sqlite-jdbc-3.45.1.0.jar "https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.45.1.0/sqlite-jdbc-3.45.1.0.jar"
curl.exe -L --fail -S -o lib\flatlaf-3.4.jar "https://repo1.maven.org/maven2/com/formdev/flatlaf/3.4/flatlaf-3.4.jar"
curl.exe -L --fail -S -o lib\flatlaf-extras-3.4.jar "https://repo1.maven.org/maven2/com/formdev/flatlaf-extras/3.4/flatlaf-extras-3.4.jar"
curl.exe -L --fail -S -o lib\itextpdf-5.5.13.3.jar "https://repo1.maven.org/maven2/com/itextpdf/itextpdf/5.5.13.3/itextpdf-5.5.13.3.jar"
curl.exe -L --fail -S -o lib\commons-lang3-3.14.0.jar "https://repo1.maven.org/maven2/org/apache/commons/commons-lang3/3.14.0/commons-lang3-3.14.0.jar"

if %ERRORLEVEL% NEQ 0 (
    echo Failed to download dependencies. Check internet connection.
    pause
    exit /b 1
)

echo Dependencies downloaded successfully.

echo Compiling Java sources...
if not exist target\classes mkdir target\classes

REM Compile all Java files with classpath
for /r src\main\java %%f in (*.java) do (
    echo Compiling %%f
    javac -cp "lib\*" -d target\classes "%%f"
    if !ERRORLEVEL! NEQ 0 (
        echo Compile failed for %%f
        pause
        exit /b 1
    )
)

echo Copying resources...
xcopy /s /e /y /i src\main\resources\* target\classes\ >nul

echo Build complete!

echo Starting MiniERP...
java -cp "target\classes;lib\*" com.minierp.main.MainApp

echo.
echo ========================================
echo App closed. Press any key to exit.
pause
