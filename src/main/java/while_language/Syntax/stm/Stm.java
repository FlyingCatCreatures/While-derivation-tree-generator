package while_language.Syntax.stm;

import while_language.visiting.StmVisitor;

public interface Stm {
    <R> R accept(StmVisitor<R> visitor);
}
