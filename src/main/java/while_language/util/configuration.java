package while_language.util;

import while_language.visiting.ConfigVisitor;

public interface configuration {
    <R> R accept(ConfigVisitor<R> visitor);
}
