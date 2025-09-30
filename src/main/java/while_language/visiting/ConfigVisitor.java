package while_language.visiting;


import while_language.util.nonterminal;
import while_language.util.terminal;

public interface ConfigVisitor<R> {
    R visit(terminal t);
    R visit(nonterminal nt);
}
