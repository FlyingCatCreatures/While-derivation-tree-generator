package while_language.Syntax.stm;

import while_language.visiting.StmVisitor;

// Used to annotate that something is a statement
public interface Stm {
    <R> R accept(StmVisitor<R> visitor);
}
