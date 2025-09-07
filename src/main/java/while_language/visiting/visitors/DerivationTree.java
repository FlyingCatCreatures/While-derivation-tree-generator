package while_language.visiting.visitors;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import while_language.Syntax.stm.*;
import while_language.visiting.StmVisitor;

public class DerivationTree implements StmVisitor<Void> {
    private StringBuilder sb;
    public final Map<String, Integer> state = new HashMap<>();
    private int indent = 0;
    private final Set<String> allVars;
    private final Evaluator eval = new Evaluator();

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

    // < x:=a, s> -> s' [ass_ns]
    public Void visit(assign a) {
        String var = a.x().x();
        PrintVisitor printer = new PrintVisitor();
        Integer value = a.a().accept(eval);
        a.a().accept(printer);
        String originalState = str(state); // Store string representation of s
        state.put(var, value); // Transition to s's
        indent();
        appendLine("\\langle " + var + " := " + printer.toString() + ", "+ originalState + " \\rangle \\rightarrow" + str(state) + " \\ ^{[ass_{ns}]}");
        dedent();
        return null;
    }

    // < skip, s> -> s [skip_ns]
    public Void visit(skip s) {
        String stateStr = str(state);
        indent();
        appendLine("\\langle skip, "+ stateStr + " \\rangle \\rightarrow" + stateStr + " \\ ^{[skip_{ns}]}");
        dedent();
        return null;
    }


    public Void visit(if_then_else ite){
        Stm s = ite.b().accept(eval)? ite.s1() : ite.s2();

        return null;
    }
    
    public Void visit(while_do wd){
        return null;
    }    
    
    public Void visit(compound c){
        return null;
    }
    //     R visit(assign a);
    // R visit(skip s);
    // R visit(if_then_else ite);
    // R visit(while_do w);
    // R visit(compound c);
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