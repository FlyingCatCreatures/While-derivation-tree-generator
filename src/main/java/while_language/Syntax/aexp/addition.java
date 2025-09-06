package while_language.Syntax.aexp;

import while_language.visiting.AexpVisitor;

public record addition(Aexp a1, Aexp a2) implements Aexp{
    @Override
    public <R> R accept(AexpVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
