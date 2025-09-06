package while_language.visiting;

import while_language.Syntax.stm.assign;
import while_language.Syntax.stm.compound;
import while_language.Syntax.stm.if_then_else;
import while_language.Syntax.stm.skip;
import while_language.Syntax.stm.while_do;

public interface StmVisitor<R> {
    R visit(assign a);
    R visit(skip s);
    R visit(if_then_else ite);
    R visit(while_do w);
    R visit(compound c);
}