package while_language.Syntax.aexp;

import while_language.visiting.AexpVisitor;

public interface Aexp {    
    <R> R accept(AexpVisitor<R> visitor);
}
