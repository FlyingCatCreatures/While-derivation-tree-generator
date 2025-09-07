package while_language.visiting.visitors;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import while_language.Syntax.stm.*;

public class DerivationTree extends Evaluator {
    private StringBuilder sb;
    public final Map<String, Integer> state = new HashMap<>();
    private int indent = 0;
    private final Set<String> allVars;

    public DerivationTree(Collection<String> vars){
        allVars = new TreeSet<>(vars);
        sb = new StringBuilder(makeLegend() + "\n\n");
    }

    private void indent() { indent++; }
    private void dedent() { indent--; }
    
    private void appendLine(String line) {
        sb.append("\t".repeat(indent)).append(line).append("\n");
    }
    
    private String makeLegend() {
        if (allVars.isEmpty()) {
            return "For this tree we denote s to denote the empty state.";
        }

        StringBuilder vars = new StringBuilder();
        StringBuilder mapping = new StringBuilder();
        boolean first = true;

        for (String var : allVars) {
            if (!first) {
                vars.append(",");
                mapping.append("][");
            }
            Integer val = state.get(var);
            if (val == null) {
                vars.append("\\bot");
                mapping.append(var).append("->\\bot");
            } else {
                vars.append(val);
                mapping.append(var).append("->").append(val);
            }
            first = false;
        }

        return "For this tree we denote $s_{" + vars + "}$ to denote $s[" + mapping + "]$.";
    }


    @Override
    public String toString() {
        return sb.toString();
    }

    // Returns a representation like s_{a,b,c}
    private String str(Map<String, Integer> s) {
        StringBuilder state_sb = new StringBuilder("s_{");

        boolean first = true;
        for (String var : allVars) {
            if (!first) {
                state_sb.append(",");
            }
            Integer val = s.get(var);
            if (val == null) {
                state_sb.append("\\bot");
            } else {
                state_sb.append(val);
            }
            first = false;
        }

        state_sb.append("}");
        return state_sb.toString();
    }

   public Void visit(assign a) {
        String var = a.x().x();
        Integer value = a.a().accept(this);
        String originalState = str(state);
        state.put(var, value);
        indent();
        appendLine("\\langle " + var + " := " + value + ", "+ originalState + " \\rangle \\rightarrow" + str(state) + " \\ ^{[ass_{ns}]}");
        dedent();
        return null;
    }

    @Override
    public Void visit(skip s) {
        String stateStr = str(state);
        indent();
        appendLine("\\langle skip, "+ stateStr + " \\rangle \\rightarrow" + stateStr);
        dedent();
        return null;
    }

}

/*
 * A multilayer tree is made by an if statement, composition statement, or while statement with true condition
 * A multilayer tree is made of the folowing format:
 *  \begin{prooftree}
 *      hyp1
 *      hyp2
 *      hyp3_opt
 *  \justifies
 *      concl
 *  \ using
 *      rule name
 *  \end{prooftree}
 *  Where hyp1-3 are trees and concl is of the form
 *  \langle S, s \rangle \rightarrow s

 * A leaf tree is made by an assignment, skip, or while statement with false condition
 * A leaf tree is made of the following format:
 * \langle S, s \rangle \rightarrow s \ ^{rule name}
 */