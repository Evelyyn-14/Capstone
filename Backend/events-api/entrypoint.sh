#!/bin/sh

# Ensure folder exists
mkdir -p /data/customers

# Change ownership to the current user (for Java to write)
chown -R 1000:1000 /data/customers 2>/dev/null || true

# Set write permissions
chmod -R 777 /data/customers

# Start Spring Boot
exec java -jar app.jar

