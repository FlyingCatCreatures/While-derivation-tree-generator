package while_language.visiting;

import while_language.Syntax.aexp.Num;
import while_language.Syntax.aexp.Var;
import while_language.Syntax.aexp.addition;
import while_language.Syntax.aexp.multiply;
import while_language.Syntax.aexp.subtract;

public interface AexpVisitor<R> {
    R visit(Num n);
    R visit(Var v);
    R visit(addition a);
    R visit(subtract s);
    R visit(multiply m);
}