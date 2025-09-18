package while_language.Syntax.bexp;

import while_language.Syntax.aexp.Aexp;
import while_language.visiting.BexpVisitor;

public record geq(Aexp a1, Aexp a2) implements Bexp{
    @Override
    public <R> R accept(BexpVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
