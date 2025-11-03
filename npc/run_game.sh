#!/bin/bash
# Run the NPC Game

echo "Starting NPC Game..."

# Check for Java
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed"
    echo "Please install Java 21 or higher"
    exit 1
fi

# Check for OPENAI_API_KEY
if [ -z "$OPENAI_API_KEY" ]; then
    echo "Warning: OPENAI_API_KEY environment variable is not set"
    echo "You will be prompted to enter it when the game starts"
fi

# Run the game
java -jar target/npc-0.0.1-SNAPSHOT-jar-with-dependencies.jar