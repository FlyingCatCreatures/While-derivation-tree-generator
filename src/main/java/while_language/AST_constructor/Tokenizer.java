package while_language.AST_constructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Tokenizer {
   public enum TokenType {
    IDENT{
        @Override public String toString() { return "identifier"; }
    },
    NUMBER{
        @Override public String toString() { return "number"; }
    },
    OP{
        @Override public String toString() { return "operator"; }
    },
    KEYWORD{
        @Override public String toString() { return "keyword"; }
    },
    LPARENTHESIS {
        @Override public String toString() { return "opening_parentheis"; }
    },
    RPARENTHESIS {
        @Override public String toString() { return "closing_parentheis"; }
    },
    SEMICOLON {
        @Override public String toString() { return "semicolon"; }
    },
    EOF {
        @Override public String toString() { return "end of file"; }
    }
}
    public record Token(TokenType type, String value) {}

    private final String input;
    private int pos;
    private final List<Token> tokens;

    private static final Set<String> KEYWORDS = Set.of(
        "skip", "if", "then", "else", "while", "do", "true", "false", "repeat", "until", "break"
    );
    private static final Set<String> OPS = Set.of(
        ":=", "+", "-", "*", "=", "<=", "!", "&"
    );

    public Tokenizer(String input) {
        this.input = input;
        this.pos = 0;
        this.tokens = new ArrayList<>();
    }

    // Tokenizes input into a list of well... tokens
    // Does not validate syntactical validity. That will be done by the parser this is fed into
    public List<Token> tokenize() {
        while (pos < input.length()) {
            char c = input.charAt(pos);
            if (Character.isWhitespace(c)) { pos++;}                    // Whitespace can be skipped, as newlines and spaces are allowed
            else if (Character.isDigit(c)) { tokens.add(readNumber());} // A digit signifies a Num will follow
            else if (Character.isLetter(c)) {tokens.add(readIdentOrKeyword());} // A letter signifies either a Var or 
            else if (c == '(') {
                tokens.add(new Token(TokenType.LPARENTHESIS, "("));
                pos++;
            } else if (c == ')') {
                tokens.add(new Token(TokenType.RPARENTHESIS, ")"));
                pos++;
            } else if (c == ';') {
                tokens.add(new Token(TokenType.SEMICOLON, ";"));
                pos++;
            } else if (c== '%') {
                do pos++;
                while(input.charAt(pos) != '\n');
            }else {
                tokens.add(readOp());
            } 
        }
        tokens.add(new Token(TokenType.EOF, ""));
        return tokens;
    }

    private Token readNumber() {
        int start = pos;
        while (pos < input.length() && Character.isDigit(input.charAt(pos))) pos++;
        return new Token(TokenType.NUMBER, input.substring(start, pos));
    }

    private Token readIdentOrKeyword() {
        int start = pos;
        while (pos < input.length() && Character.isLetter(input.charAt(pos))) pos++;
        String word = input.substring(start, pos);
        if (KEYWORDS.contains(word)) {
            return new Token(TokenType.KEYWORD, word);
        } else {
            return new Token(TokenType.IDENT, word);
        }
    }

    private Token readOp() {
        for (String op : OPS) {
            if (input.startsWith(op, pos)) { // Every operator happens to start with a different character so we can do this
                int start = pos;
                pos += op.length();

                String read_op = input.substring(start, pos);

                if(!read_op.equals(op)) 
                    throw new RuntimeException("Unknown operator at position " + pos + ". Expected " + op);
                
                return new Token(TokenType.OP, read_op);
            }
        }

        // If we get here we couldn't recognize any operator at pos, otherwise we'd have returned by now
        throw new RuntimeException("Unknown operator at position " + pos);
    }
}

/*
 * The language specification we use has been changed slightly to allow for easier use of ascii, see:
 *  - The negation operator's symbol
 *  - The less than or equal to operator
 *  - The conjunction operator
 * This is the full specification:
S ::=   x := a | 
        skip | 
        S1 ;S2 | 
        if b then S1 else S2 |
        while b do S
a ::=   n | 
        x | 
        a1 + a2 | 
        a1 * a2 | 
        a1 - a2
b ::=   true |
        false | 
        a1 = a2 | 
        a1 <= a2| 
        !b | 
        b1 & b2
 */