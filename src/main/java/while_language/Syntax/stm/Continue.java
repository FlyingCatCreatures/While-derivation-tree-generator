package while_language.Syntax.stm;

import while_language.visiting.StmVisitor;

public record Continue() implements Stm{
    @Override
    public <R> R accept(StmVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
