package while_language.Syntax.aexp;

import while_language.visiting.AexpVisitor;

// Used to annotate that something is an arithmetic expression
public interface Aexp {    
    <R> R accept(AexpVisitor<R> visitor);
}
