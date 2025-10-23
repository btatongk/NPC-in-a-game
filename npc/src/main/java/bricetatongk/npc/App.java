package bricetatongk.npc;

import java.util.Scanner;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.chain.ConversationalChain;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;


public class App 
{
    public static void main( String[] args )
    {
        // retrieve the setting from application.properties
        // environment variables can also be used
        String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY");

        if (OPENAI_API_KEY == null || OPENAI_API_KEY.isEmpty()) {
            System.err.println("Error: OPENAI_API_KEY is not valid: " + OPENAI_API_KEY);
            return;
        }

        System.out.println( ">>>>>> Npc started <<<<<" );

        ChatModel model = OpenAiChatModel
                .builder()
                .temperature(0.8)
                .apiKey(OPENAI_API_KEY)
				.modelName(OpenAiChatModelName.GPT_4_O_MINI)
				.build();

        ConversationalChain cc = ConversationalChain
                .builder()
                .chatModel(model)
                .build();

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print(">>");
                if (!scanner.hasNextLine()) {
                    break;
                }

                String prompt = scanner.nextLine();
                System.out.println("\n" + cc.execute(prompt));
            }
        }
    }
}
