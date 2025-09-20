package while_language.AST_constructor;

import while_language.AST_constructor.Tokenizer.Token;
import while_language.AST_constructor.Tokenizer.TokenType;
import while_language.Syntax.aexp.Aexp;
import while_language.Syntax.aexp.Num;
import while_language.Syntax.aexp.Var;
import while_language.Syntax.aexp.addition;
import while_language.Syntax.aexp.multiply;
import while_language.Syntax.aexp.subtract;
import while_language.Syntax.bexp.Bexp;
import while_language.Syntax.bexp.False;
import while_language.Syntax.bexp.True;
import while_language.Syntax.bexp.conjunction;
import while_language.Syntax.bexp.negation;
import while_language.Syntax.bexp.equals;
import while_language.Syntax.bexp.geq;
import while_language.Syntax.bexp.gt;
import while_language.Syntax.bexp.leq;
import while_language.Syntax.bexp.lt;
import while_language.Syntax.stm.Stm;
import while_language.Syntax.stm.assign;
import while_language.Syntax.stm.compound;
import while_language.Syntax.stm.if_then_else;
import while_language.Syntax.stm.skip;
import while_language.Syntax.stm.while_do;
import while_language.Syntax.stm.repeat_until;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Parser {
    private final List<Token> tokens;
    private int pos = 0;
    private final Set<String> vars = new TreeSet<>();


    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public Set<String> getVars() {
       return vars;
    }

    private Token peek() {
        return tokens.get(pos);
    }

    private Token consume() {
        return tokens.get(pos++);
    }

    public Stm generateAST(){
        Stm ret = this.parseStm();
        if(pos!=tokens.size()-1 && pos != tokens.size()){
            throw new RuntimeException("Program done parsing but input buffer not exhausted. Did you forget a semicolon at poition " + pos + "?");
        }
        return ret;
    }

    private Stm parseStm() {
        Stm stmt = parseAtomicStm();

        if (peek().type() == TokenType.SEMICOLON) {
            consume(); // eat ;
            Stm rest = parseStm(); 
            return new compound(stmt, rest);
        }

        return stmt;
    }

    private Stm parseAtomicStm() {
        Token next = consume();
        Stm stmt;

        switch (next.type()) {
            case IDENT:
                String varName = next.value();
                vars.add(varName);
                Token assign = consume();
                if (assign.type() != TokenType.OP || !assign.value().equals(":="))
                    throw new RuntimeException("Expected ':=' after identifier at position " + pos);
                Aexp rhs = parseAexp();
                stmt = new assign(new Var(varName), rhs);
                break;

            case KEYWORD:
                switch (next.value()) {
                    case "skip":
                        stmt = new skip();
                        break;
                    case "if":
                        Bexp cond = parseBexp();
                        Token thenToken = consume();
                        if (!thenToken.value().equals("then"))
                            throw new RuntimeException("Expected 'then' after condition at position " + pos);
                        Stm thenStm = parseStm();
                        Token elseToken = consume();
                        if (!elseToken.value().equals("else"))
                            throw new RuntimeException("Expected 'else' after then-branch at position " + pos);
                        Stm elseStm = parseAtomicStm(); // only parse one atomic statement as body
                        stmt = new if_then_else(cond, thenStm, elseStm);
                        break;
                    case "while":
                        Bexp whileCond = parseBexp();
                        Token doToken = consume();
                        if (!doToken.value().equals("do"))
                            throw new RuntimeException("Expected 'do' after while condition at position " + pos);
                        Stm body = parseAtomicStm(); // only parse one atomic statement as body
                        stmt = new while_do(whileCond, body);
                        break;
                    case "repeat":
                        Stm repeatBody = parseStm();
                        Token untilToken = consume();
                        if (!untilToken.value().equals("until"))
                            throw new RuntimeException("Expected 'until' after repeat body at position " + pos);
                        Bexp untilCond = parseBexp();
                        stmt = new repeat_until(repeatBody, untilCond);
                        break;
                    default:
                        throw new RuntimeException("Unexpected keyword " + next.value() + " at position " + pos);
                }
                break;

            case LPARENTHESIS:
                stmt = parseStm(); // parse everything inside parentheses
                if (consume().type() != TokenType.RPARENTHESIS)
                    throw new RuntimeException("Expected ')' at position " + pos);
                break;

            default:
                throw new RuntimeException("Unexpected token " + next.value() + " at position " + pos);
        }

        return stmt;
    }


    // Arithmetic expressions are a bit harder than boolean ones because of operator precedence between +, -, *
    // Highest 'priority' are factors (Vars, Nums, things in parentheses)
    // Then terms (multiplications)
    // Finally just additions, subtractions
    private Aexp parseAexp() {
        Aexp exp = parseTerm();
        while (peek().type() == TokenType.OP &&
            (peek().value().equals("+") || peek().value().equals("-"))) {
            Token op = consume();
            Aexp right = parseTerm();
            if (op.value().equals("+")) {
                exp = new addition(exp, right);
            } else {
                exp = new subtract(exp, right);
            }
        }
        return exp;
    }

    private Aexp parseTerm() {
        Aexp exp = parseFactor();
        while (peek().type() == TokenType.OP &&
            peek().value().equals("*")) {
            consume(); // consume the '*'
            Aexp right = parseFactor();
            exp = new multiply(exp, right);
        }
        return exp;
    }

    private Aexp parseFactor() {
        Token next = consume();
        switch (next.type()) {
            case NUMBER:
                return new Num(next.value());
            case IDENT:
                String varName = next.value();
                vars.add(varName);
                return new Var(varName);
            case LPARENTHESIS:
                Aexp inside = parseAexp();
                if (consume().type() != TokenType.RPARENTHESIS) throw new RuntimeException("Expected ')' at position " + pos);
                return inside;
            default:
                throw new RuntimeException("Unexpected token " + next.value() + " in factor at position " + pos);
        }
    }

    private Bexp parseBexp() {
        Token next = consume();
        Bexp exp;

        switch (next.type()) {
            case KEYWORD:
                if (next.value().equals("false")) exp = new False();
                else if (next.value().equals("true")) exp = new True();
                else throw new RuntimeException("Unexpected keyword, expected false or true but found " + next.value() + " at position " + pos);
                break;

            case OP:
                if (next.value().equals("!")) exp = new negation(parseBexp());
                else throw new RuntimeException("Unexpected operator, expected '!' but found " + next.value() + " at position " + pos);
                
                break;

            case LPARENTHESIS:
                exp = parseBexp();
                Token closing = consume();
                if (closing.type() != TokenType.RPARENTHESIS)
                    throw new RuntimeException("Expected ')' at position " + pos);
                break;

            case IDENT, NUMBER:
                // start of an arithmetic expression for = or <=
                // we already consumed one token of the aexp, so we put the token back so parseAexp sees it
                pos--;
                Aexp a1 = parseAexp();
                Token op = consume(); // Consume operator
                Set<String> validOps = Set.of("=", "<=", "<", ">", ">=");
                if(op.type() != TokenType.OP || !validOps.contains(op.value()))  throw new RuntimeException("Expected comparison operator after arithmetic expression at position " + pos);

                Aexp a2 = parseAexp();
                switch(op.value()) {
                    case "<" -> exp = new lt(a1, a2);
                    case ">" -> exp = new gt(a1, a2);
                    case ">=" -> exp = new geq(a1, a2);
                    case "<=" -> exp = new leq(a1, a2);
                    case "=" -> exp = new equals(a1, a2);
                    default -> throw new RuntimeException("Expected comparison operator after arithmetic expression at position " + pos);
                }

                break;
            default:
                throw new RuntimeException("Unexpected token " + next.value() + " of type " + next.type() + " at position " + pos);
        }
        
        // Now check for conjunction: b1 & b2
        if (peek().type() == TokenType.OP && peek().value().equals("&")) {
            consume(); // eat &
            Bexp right = parseBexp();
            exp = new conjunction(exp, right);
        }

        return exp;
    }

}

/*
 * The language specification we use has been changed slightly to allow for easier use in an ascii editor, see:
 *  - The negation operator's symbol
 *  - The less than or equal to operator
 *  - The conjunction operator
 *  - Explicitly allow grouping using parentheses
 * This is the full specification:
S ::=   x := a | 
        skip | 
        S1;S2 | 
        if b then S1 else S2 |
        while b do S |
        (S)
a ::=   n | 
        x | 
        a1 + a2 | 
        a1 * a2 | 
        a1 - a2 |
        (a)
b ::=   true |
        false | 
        a1 = a2 | 
        a1 <= a2| 
        !b | 
        b1 & b2 |
        (b)
 */