package while_language.Syntax.aexp;

import while_language.visiting.AexpVisitor;

// Variables are represented as a string representation of the variable name
public record Var(String x) implements Aexp{
    @Override
    public <R> R accept(AexpVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
