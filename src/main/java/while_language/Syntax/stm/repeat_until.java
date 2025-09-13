package while_language.Syntax.stm;

import while_language.Syntax.bexp.Bexp;
import while_language.visiting.StmVisitor;

public record repeat_until(Stm s, Bexp b) implements Stm{
    @Override
    public <R> R accept(StmVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
