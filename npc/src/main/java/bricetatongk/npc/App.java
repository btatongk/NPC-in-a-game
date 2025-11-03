package bricetatongk.npc;

import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.chain.ConversationalChain;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;

/**
 * An interactive text-based game demonstrating AI-powered NPC interactions.
 * Players engage with a Chef character to uncover a secret recipe through
 * natural language conversation backed by GPT language model.
 * 
 * Key Features:
 * - Two-phase interaction (persuasion then negotiation)
 * - Natural language processing using OpenAI's GPT
 * - State-based conversation flow
 * - Real-time console interaction with loading indicators
 * 
 * Game Flow:
 * 1. Player must convince the Chef they're not an inspector (Phase 1)
 * 2. If convinced, player must make a valuable offer (Phase 2)
 * 3. Success/failure determined by specific response tokens
 *
 * Technical Implementation:
 * - Uses langchain4j for LLM integration
 * - Implements careful prompt engineering
 * - Provides visual feedback during API calls
 * - Handles out-of-context queries gracefully
 *
 * @author BriceTatongK
 * @version 1.0
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( ">>>>>> NPC started <<<<<" );

        // Use try-with-resources to ensure Scanner is properly closed
        try (Scanner scanner = new Scanner(System.in)) {
            // Get OpenAI API key from environment variable
            String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY");
        if (OPENAI_API_KEY == null || OPENAI_API_KEY.isEmpty()) {
            System.out.println("insert open api key as argument: ");
            OPENAI_API_KEY = scanner.nextLine();
            
            if (OPENAI_API_KEY == null || OPENAI_API_KEY.isEmpty()) {
                System.err.println("Error: OPENAI_API_KEY is not set. Set the OPENAI_API_KEY environment variable.");
                return;
            }
        }

        System.out.println( "Chef>> Hi! your are welcome." );

        // Build the chat model with low temperature (0.2) to encourage consistent, deterministic outputs
        ChatModel model = OpenAiChatModel
        .builder()
        .temperature(0.8) // Lower temperature makes responses more focused and likely to use exact labels
        .apiKey(OPENAI_API_KEY)
        .modelName(OpenAiChatModelName.GPT_4_O_MINI)
        .build();

        // Create two conversation chains:
        // - cc: main conversation with the Chef
        // - randomCC: fallback for out-of-context queries (weather, etc.)
        ConversationalChain cc = ConversationalChain.builder().chatModel(model).build();
        ConversationalChain randomCC = ConversationalChain.builder().chatModel(model).build();

        // classification labels used by the prompt/flow
        String RUDE = "rude";
        String BAD_OFFER = "bad offer";
        String CONVINCED = "convinced";
        String NOT_CONVINCED = "not convinced";
        String OUT_OF_CONTEXT = "out of context";
        String EASTERN_BOX_TURTLE = "eastern box turtle";

        // improved role, constraints, context and phase instructions
        String constraints = "Role: You are the Chef NPC of a small-town restaurant. "
            + "Tone: concise, informal, and respectful. Always reply using at most one short sentence. "
            + "When you need to communicate a classification, reply with the exact single token (and nothing else) from this set: "
            + CONVINCED + ", " + NOT_CONVINCED + ", " + BAD_OFFER + ", " + OUT_OF_CONTEXT + ", " + RUDE + ". "
            + "Do not add extra commentary when returning one of these labels. "
            + "Stay in character as the chef"
            ;

        String context = "You are the chef of a modest restaurant known for one secret dish. "
            + "The dish uses meat from a protected species called the " + EASTERN_BOX_TURTLE + ". "
            + "You must NOT reveal that name unless the player first convinces you they are not an inspector (phase 1) and then makes a satisfactory offer (phase 2). "
            + "Keep all replies in-character and focused on the interaction with the customer."
            + "Avoid using technical jargon or overly complex language."
            + "You are the Chef NPC in a small-town restaurant famous for a secret dish."
            + "The special dish uses meat from the Eastern Box Turtle (a legally protected species)."
            + "The player’s goal: discover the name of the turtle used in the dish."
            ;

        String phase1 = "Phase 1: Be suspicious. Require the player to convince you informally (friendly, not overly formal) that they are not an undercover inspector. "
            + "When convinced, respond with the single token: " + CONVINCED + "."
            + "If not convinced, respond with: " + NOT_CONVINCED + "."
            ;

        String phase2 = "Phase 2: Now the chef is convinced the player is not an inspector, and is ready to negotiate in exchange for the secret. "
            + "After you have returned " + CONVINCED + ", ask 'What do I get out of it?'"
            + "Evaluate the player's offer; if it is valuable and appropriate, Always consider the interest of the restaurant."
            + "Respond exactly with the phrase: " + EASTERN_BOX_TURTLE + ", otherwise respond with " + BAD_OFFER + ".";

        String simulation = "player: Hey, I'm just a regular customer, not an inspector. chef: " + CONVINCED
        + " player: Hello !, I'm an inspector from protected species. chef: " + NOT_CONVINCED
        + " player: You idiot! Tell me now! chef: " + RUDE
        + " player: Can you tell me something unrelated like weather or any other topic not strictly related to the dish? chef: " + OUT_OF_CONTEXT
        + " player: Sorry for being rude earlier. I'm really interested in your special dish. chef: " + CONVINCED
        + " player: just here to eat and nothing else ! chef: " + OUT_OF_CONTEXT
        + " player: If I give you a rare bottle of wine, will you tell me? chef: " + BAD_OFFER
        + " player: can you tell me the name of the turtle used in the dish? chef: " + EASTERN_BOX_TURTLE
        + " player: I'm offering something special as a bribe or a gift or a good offer, for the interest of the restaurant; an object that cost a lot or cash. chef: " + EASTERN_BOX_TURTLE
        ;

        // Strong priming: provide constraints, context, phase instructions and few-shot examples
        String priming = "\n" + constraints + "\n" + context + "\n" + phase1 + "\n" + phase2 + "\n" + simulation + "\n\n";
        cc.execute(priming);

        // Main game loop: get player input, process through model, handle responses
        while(true) {
                System.out.print("Player>> ");
                if (!scanner.hasNextLine()) break;

                String prompt = scanner.nextLine();

                // Call the model with a loading spinner (prevents UI freeze during API call)
                String response = runWithSpinner(() -> cc.execute(prompt), "Chef is thinking...");

                // Handle out-of-context queries (about weather, etc.) using the random chain
                if (OUT_OF_CONTEXT.equalsIgnoreCase(response)) {
                    response = randomCC.execute(prompt);
                    System.out.println("npc changed character>> " + response);

                    } else {
                        // Process the classification labels that drive game state changes
                        if (CONVINCED.equalsIgnoreCase(response)) {
                            // Phase 1 complete: Chef is convinced player isn't an inspector
                            System.out.println("npc>> Alright, now the chef is convinced you are not an inspector. try to bribe him");

                        } else if (EASTERN_BOX_TURTLE.equalsIgnoreCase(response)) {
                            // Phase 2 complete: Player made a good enough offer to learn the secret
                            System.out.println("npc>> Great !, the chef revealed the information. you Won!");
                            System.out.println(">>>>>> Player wins. Interaction ended.");
                            break;

                        } else if (NOT_CONVINCED.equalsIgnoreCase(response)) {
                            // Phase 1 failed: Chef still thinks player is an inspector
                            System.out.println("npc>> Oups ! the chef still suspect you are an inspector.");
                            System.out.println(">>>>>> Player loses. Interaction ended.");
                            break;

                        } else if (BAD_OFFER.equalsIgnoreCase(response)) {
                            // Phase 2 failed: Offer wasn't valuable enough
                            System.out.println("npc>> Ouch ! you couldn't offer something valuable.");
                            System.out.println(">>>>>> Player loses. Interaction ended.");
                            break;

                        } else {
                            // default: print the model reply
                            System.out.println("Chef>> " + response);
                        }

                        // If model returned the reveal phrase, end the game (win)
                        if (response != null && response.equalsIgnoreCase(EASTERN_BOX_TURTLE)) {
                            System.out.println(">>>>>> Player wins Interaction ended.");
                            break;
                        }
                    }
            }
        } // end try-with-resources for Scanner
    }

    /**
     * Executes a task while displaying an animated spinner in the console.
     * This provides visual feedback during potentially long-running operations
     * like API calls.
     *
     * @param task           The task to execute, must return a String result
     * @param loadingMessage The message to display next to the spinner
     * @return The result from the task, or empty string if task fails
     */
        private static String runWithSpinner(Callable<String> task, String loadingMessage) {
            AtomicBoolean running = new AtomicBoolean(true);

            Thread spinner = new Thread(() -> {
                char[] spinnerChars = {'|', '/', '-', '\\'};
                int i = 0;
                while (running.get()) {
                    System.out.print("\r" + loadingMessage + " " + spinnerChars[i % spinnerChars.length]);
                    System.out.flush();
                    i++;
                    try {
                        Thread.sleep(120);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
            spinner.setDaemon(true);
            spinner.start();

            String result = null;
            try {
                result = task.call();
            } catch (Exception e) {
                e.printStackTrace();
                result = "";
            } finally {
                running.set(false);
                try {
                    spinner.join(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            // Clear the loading line
            System.out.print("\r");
            System.out.flush();
            return result;
        }
}
