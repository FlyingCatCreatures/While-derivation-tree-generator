package while_language.util;

import java.util.Map;

import while_language.visiting.ConfigVisitor;

public record terminal(Map<String, Integer> s) implements configuration{

    @Override
    public <R> R accept(ConfigVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
