package while_language.Syntax.bexp;

import while_language.visiting.BexpVisitor;

public record True() implements Bexp{
    @Override
    public <R> R accept(BexpVisitor<R> visitor) {
        return visitor.visit(this);
    }
}