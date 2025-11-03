@echo off
REM Run the NPC Game
echo Starting NPC Game...

REM Check for Java
java -version >nul 2>&1
if errorlevel 1 (
    echo Error: Java is not installed or not in PATH
    echo Please install Java 21 or higher
    pause
    exit /b 1
)

REM Check for OPENAI_API_KEY
if "%OPENAI_API_KEY%"=="" (
    echo Warning: OPENAI_API_KEY environment variable is not set
    echo You will be prompted to enter it when the game starts
)

REM Run the game
java -jar target/npc-0.0.1-SNAPSHOT-jar-with-dependencies.jar

pause