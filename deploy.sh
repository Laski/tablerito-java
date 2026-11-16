#!/bin/sh
# Deploy to production
git pull
./mvnw package
java -jar target/tablerito.jar
