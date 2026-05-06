@echo off
echo Building Mini ERP...
call mvn clean package -q
if %ERRORLEVEL% NEQ 0 (
    echo Build failed! Make sure Maven and Java 11+ are installed.
    pause
    exit /b 1
)
echo Starting Mini ERP...
java -jar target\MiniERP.jar
