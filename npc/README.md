# NPC Game - Interactive Chef AI

## Project Description

This project implements an interactive text-based game where players engage with an AI-powered Chef NPC (Non-Player Character). The game demonstrates the use of Large Language Models (LLM) in gaming contexts, specifically for creating dynamic NPC interactions.

### Game Concept

Players must discover a secret recipe by interacting with a Chef NPC in a two-phase challenge:

1. **Persuasion Phase**: Convince the Chef that you're not an inspector
2. **Negotiation Phase**: Make a valuable offer to learn the secret recipe

### Technical Implementation

- Built in Java using Maven
- Uses OpenAI's GPT model via langchain4j
- Implements a state-based conversation system
- Features real-time console interactions with loading indicators

## Requirements

- Java 21 or higher
- Maven
- OpenAI API key (set as environment variable `OPENAI_API_KEY`)

## Setup Instructions

1. Clone the repository
2. Set your OpenAI API key:
   ```powershell
   $env:OPENAI_API_KEY="your-api-key-here"  # Windows PowerShell
   ```
   ```bash
   export OPENAI_API_KEY="your-api-key-here" # Linux/Mac
   ```
3. Build the project:
   ```bash
   mvn clean install
   ```
4. Run the game:
   ```bash
   mvn exec:java -Dexec.mainClass="bricetatongk.npc.App"
   ```

## Game Instructions

1. Start a conversation with the Chef
2. Try to convince them you're not an inspector using natural dialogue
3. Once convinced, make an offer to learn the secret recipe
4. The game ends when you either:
   - Win by learning the secret recipe
   - Lose by failing to convince the Chef or making an insufficient offer

## Code Structure

- `App.java`: Main game logic and LLM integration
- Uses prompt engineering to ensure consistent NPC responses
- Implements a spinner for visual feedback during API calls

## Assignment Notes

This project demonstrates:

- Object-Oriented Programming principles
- External API integration (OpenAI)
- State management in interactive applications
- User input handling and validation
- Clean code practices and documentation

## Future Improvements

- Add more NPCs with different personalities
- Implement persistent game state
- Add unit tests for game logic
- Support for multiple conversation paths
