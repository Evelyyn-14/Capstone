#!/bin/sh

# Ensure folder exists
mkdir -p /data/accounts

# Change ownership to the current user (for Java to write)
chown -R 1000:1000 /data/accounts 2>/dev/null || true

# Set write permissions
chmod -R 777 /data/accounts

# Start Spring Boot
exec java -jar app.jar
