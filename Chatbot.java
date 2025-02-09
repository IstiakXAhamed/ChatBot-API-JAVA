import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

public class ChatBot {

    // Define the intents and responses that the chatbot will support
    private static Map<String, String[]> intents = new HashMap<>();

    static {
        intents.put("greeting", new String[]{"hello", "hi", "hey", "good morning", "good afternoon", "good evening"});
        intents.put("goodbye", new String[]{"bye", "goodbye", "see you later", "see ya"});
        intents.put("weather", new String[]{"what's the weather like today?", "will it rain today?"});
        intents.put("time", new String[]{"what time is it?", "what's the time?"});
    }

    // Load the models for tokenization, named entity recognition, and sentence detection
    private static TokenizerModel tokenizerModel;
    private static TokenNameFinderModel nameFinderModel;
    private static SentenceModel sentenceModel;

    static {
        try (InputStream tokenizerStream = new FileInputStream("en-token.bin");
             InputStream nameFinderStream = new FileInputStream("en-ner-person.bin");
             InputStream sentenceModelStream = new FileInputStream("en-sent.bin")) {

            tokenizerModel = new TokenizerModel(tokenizerStream);
            nameFinderModel = new TokenNameFinderModel(nameFinderStream);
            sentenceModel = new SentenceModel(sentenceModelStream);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Create the components for tokenization, named entity recognition, and sentence detection
        TokenizerME tokenizer = new TokenizerME(tokenizerModel);
        NameFinderME nameFinder = new NameFinderME(nameFinderModel);
        SentenceDetectorME sentenceDetector = new SentenceDetectorME(sentenceModel);

        // Define a loop to handle user input
        Scanner scanner = new Scanner(System.in);
        while (true) {
            // Get user input
            System.out.print("User: ");
            String input = scanner.nextLine();

            // Check for exit command
            if (input.equals("exit")) {
                break;
            }

            // Tokenize the input
            String[] tokens = tokenizer.tokenize(input);

            // Find named entities in the input
            Span[] nameSpans = nameFinder.find(tokens);
            String[] names = Span.spansToStrings(nameSpans, tokens);

            // Determine the appropriate response based on the intent
            String response = "I'm sorry, I don't understand. Can you please rephrase?";
            for (String intent : intents.keySet()) {
                for (String example : intents.get(intent)) {
                    if (input.toLowerCase().contains(example)) {
                        response = generateResponse(intent, names);
                        break;
                    }
                }
            }

            // Print the response
            System.out.println("ChatBot: " + response);
}
scanner.close();
}
// Generate a response based on the intent and any named entities found
private static String generateResponse(String intent, String[] names) {
    switch (intent) {
        case "greeting":
            return "Hello! How can I assist you today?";
        case "goodbye":
            return "Goodbye! Have a nice day!";
        case "weather":
            return "I'm sorry, I cannot provide weather information at this time.";
        case "time":
            return "The current time is " + java.time.LocalTime.now().toString() + ".";
        default:
            return "I'm sorry, I don't understand. Can you please rephrase?";
    }
}

}