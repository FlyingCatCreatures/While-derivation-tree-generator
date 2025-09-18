package while_language.visiting;

import while_language.Syntax.bexp.False;
import while_language.Syntax.bexp.True;
import while_language.Syntax.bexp.conjunction;
import while_language.Syntax.bexp.equals;
import while_language.Syntax.bexp.geq;
import while_language.Syntax.bexp.gt;
import while_language.Syntax.bexp.leq;
import while_language.Syntax.bexp.lt;
import while_language.Syntax.bexp.negation;

public interface BexpVisitor<R> {
    R visit(True t);
    R visit(False f);
    R visit(negation n);
    R visit(conjunction c);
    R visit(equals e);
    R visit(leq l);
    R visit(geq q);
    R visit(lt l);
    R visit(gt g);
}