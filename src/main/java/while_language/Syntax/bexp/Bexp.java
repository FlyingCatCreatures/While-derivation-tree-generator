package while_language.Syntax.bexp;

import while_language.visiting.BexpVisitor;

public interface Bexp { 
    <R> R accept(BexpVisitor<R> visitor);
}
