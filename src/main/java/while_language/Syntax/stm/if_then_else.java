package while_language.Syntax.stm;

import while_language.Syntax.bexp.Bexp;
import while_language.visiting.StmVisitor;

public record if_then_else(Bexp b, Stm s1, Stm s2) implements Stm{
    @Override
    public <R> R accept(StmVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
