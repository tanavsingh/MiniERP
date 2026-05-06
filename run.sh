#!/bin/bash
echo "Building Mini ERP..."
mvn clean package -q
if [ $? -ne 0 ]; then
    echo "Build failed! Make sure Maven and Java 11+ are installed."
    exit 1
fi
echo "Starting Mini ERP..."
java -jar target/MiniERP.jar
