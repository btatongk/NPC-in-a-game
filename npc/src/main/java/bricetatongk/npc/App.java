package bricetatongk.npc;

import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

import dev.langchain4j.chain.ConversationalChain;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;


public class App 
{
    public static void main( String[] args )
    {
        System.out.println( ">>>>>> Npc started <<<<<" );

        String OPENAI_API_KEY = "";

        // retrieve the setting from application.properties
        try (InputStream input = App.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                System.out.println("Uable to find application.properties");
            }

            Properties prop = new Properties();
            prop.load(input);

            OPENAI_API_KEY = prop.getProperty("OPENAI_API_KEY");

        } catch (IOException ex){
            ex.printStackTrace();
        }

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
