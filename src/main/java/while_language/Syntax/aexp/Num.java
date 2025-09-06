package while_language.Syntax.aexp;

import while_language.visiting.AexpVisitor;

// Numerals are represented as a string representation of the number itself in the syntax. Parsing to an integer is part of semantics
public record Num(String n) implements Aexp{
    @Override
    public <R> R accept(AexpVisitor<R> visitor) {
        return visitor.visit(this);
    }
}