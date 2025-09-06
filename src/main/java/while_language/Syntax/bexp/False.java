package while_language.Syntax.bexp;

import while_language.visiting.BexpVisitor;

public record False() implements Bexp{
    @Override
    public <R> R accept(BexpVisitor<R> visitor) {
        return visitor.visit(this);
    }
}