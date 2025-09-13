import while_language.AST_constructor.Parser;
import while_language.AST_constructor.Tokenizer;
import while_language.AST_constructor.Tokenizer.Token;
import while_language.Syntax.stm.Stm;
import while_language.visiting.visitors.Evaluator;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestHelper {
    public static void assertFinalState(String filename, Map<String, Integer> initState, Map<String, Integer> expectedState) throws Exception {
        // Load file from test/resources
        Path filePath = Path.of("src/test/resources/" + filename);
        String input = Files.readString(filePath);

        // Tokenize and parse
        Tokenizer tokenizer = new Tokenizer(input);
        List<Token> tokens = tokenizer.tokenize();
        Parser parser = new Parser(tokens);
        Stm ast = parser.generateAST();

        // Evaluate
        Evaluator evaluator = new Evaluator(new HashMap<>(initState));
        ast.accept(evaluator);

        // Assert final state
        assertEquals(expectedState, evaluator.state, "Final state does not match expected state for " + filename);
    }

    public static void assertFinalState(String filename, Map<String, Integer> expectedState) throws Exception {
        assertFinalState(filename, Map.of(), expectedState);
    }
}