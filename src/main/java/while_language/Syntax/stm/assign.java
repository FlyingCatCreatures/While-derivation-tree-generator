package while_language.Syntax.stm;

import while_language.Syntax.aexp.Aexp;
import while_language.Syntax.aexp.Var;
import while_language.visiting.StmVisitor;

public record assign(Var x, Aexp a) implements Stm{
    @Override
    public <R> R accept(StmVisitor<R> visitor) {
        return visitor.visit(this);
    }
} 
