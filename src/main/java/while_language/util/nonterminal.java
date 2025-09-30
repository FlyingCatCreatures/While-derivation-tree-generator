package while_language.util;

import java.util.HashMap;
import java.util.Map;
import while_language.Syntax.stm.Stm;
import while_language.visiting.ConfigVisitor;
import while_language.visiting.visitors.Evaluator;

public record nonterminal(Stm S, Map<String, Integer> s) implements configuration {
    public nonterminal(Stm S, Map<String, Integer> s){
        this.S = S;
        this.s = new HashMap<>(s);
    }

    @Override
    public <R> R accept(ConfigVisitor<R> visitor) {
        return visitor.visit(this);
    }

}
