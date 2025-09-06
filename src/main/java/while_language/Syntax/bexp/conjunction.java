package while_language.Syntax.bexp;

import while_language.visiting.BexpVisitor;

public record conjunction(Bexp b1, Bexp b2) implements Bexp{
    @Override
    public <R> R accept(BexpVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
