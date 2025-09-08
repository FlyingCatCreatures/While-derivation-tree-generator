package while_language.visiting.visitors;

import java.util.Map;
import java.util.Set;

import while_language.Syntax.stm.*;
import while_language.visiting.StmVisitor;

public class DerivationTree implements StmVisitor<Void> {
    private StringBuilder sb;
    private int indent = 0;
    private final Set<String> allVars;
    private final Evaluator eval = new Evaluator();

    public DerivationTree(Set<String> vars, String varwidth){
        allVars = vars;
        String preamble = """
            %% !TEX TS-program = XeLaTeX
            \\documentclass[varwidth=%s]{standalone}
            \\usepackage{prooftree}

            \\begin{document}
            """.formatted(varwidth);
        sb = new StringBuilder(preamble + makeLegend() + "\\\\  \n\n");
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
        int idx = 0;
        for (String var : allVars) {
            if (!first) {
                vars.append(",");
                mapping.append("][");
            }
            // symbolic subscript: a, b, c, ...
            char sym = (char) ('a' + idx);
            vars.append(sym);
            mapping.append(var).append("->").append(sym);
            

            idx++;
            first = false;
        }

        return "For this tree we denote $s_{" + vars + "}$ to denote $s[" + mapping + "]$, where " + vars + "\\in \\mathBB{N}";
    }

    @Override
    public String toString() {
        return sb.toString() + "\\end{document}";
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
        a.accept(printer);
        String originalState = str(eval.state); // Store string representation of s
        eval.state.put(var, value); // Transition to s's
        indent();
        appendLine("\\langle " + printer.toString() + ", "+ originalState + " \\rangle \\rightarrow " + str(eval.state) + " \\ ^{[ass_{ns}]}");
        dedent();
        return null;
    }

    // < skip, s> -> s [skip_ns]
    public Void visit(skip s) {
        String stateStr = str(eval.state);
        indent();
        appendLine("\\langle skip, "+ stateStr + " \\rangle \\rightarrow" + stateStr + " \\ ^{[skip_{ns}]}");
        dedent();
        return null;
    }

    public Void visit(if_then_else ite){
        String state_before = str(eval.state);
        PrintVisitor printer = new PrintVisitor();
        ite.accept(printer);

        boolean cond = ite.b().accept(eval);
        
        appendLine("\\begin{prooftree}");
        indent();
        Stm s = cond? ite.s1() : ite.s2();
        s.accept(this);
        dedent();
        appendLine("\\justifies");
        indent();
        appendLine("\\langle " + printer.toString() + ", "+ state_before + "\\rangle \\rightarrow " + str(eval.state));
        dedent();
         appendLine("\\using");
        indent();
        appendLine("[if_{ns}^]" + (cond ? "{tt}":"{ff}"));
        dedent();
        appendLine("\\end{prooftree}");
        return null;
    }
    
    public Void visit(while_do wd){
        PrintVisitor printer = new PrintVisitor();
        wd.accept(printer);

        String originalState = str(eval.state); 

        boolean cond = wd.b().accept(eval);
        if(!cond){
            indent();
            appendLine("\\langle " + printer.toString() + ", "+ originalState + " \\rangle \\rightarrow " + originalState + " \\ ^{[while_{ns}^{ff}]}");
            dedent();
            return null;
        }

        appendLine("\\begin{prooftree}");
        indent();
        wd.s().accept(this);
        wd.accept(this);
        dedent();
        appendLine("\\justifies");
        indent();
        appendLine("\\langle " + printer.toString() + ", "+ originalState + "\\rangle \\rightarrow " + str(eval.state));
        dedent();
         appendLine("\\using");
        indent();
        appendLine("[while_{ns}^{tt}]");
        dedent();
        appendLine("\\end{prooftree}");
        return null;
    }    
    
    public Void visit(compound c){
        String state_before = str(eval.state);
        PrintVisitor printer = new PrintVisitor();
        c.accept(printer);


        String originalState = str(eval.state); 

        appendLine("\\begin{prooftree}");
        indent();
        c.s1().accept(this);
        c.s2().accept(this);
        dedent();
        appendLine("\\justifies");
        indent();
        appendLine("\\langle " + printer.toString() + ", "+ state_before + "\\rangle \\rightarrow " + str(eval.state));
        dedent();
         appendLine("\\using");
        indent();
        appendLine("[comp_{ns}]");
        dedent();
        appendLine("\\end{prooftree}");
        return null;
    }
}

/*
 * A multilayer tree is made by an if statement, composition statement, or while statement with true condition
 * A multilayer tree is made of the folowing format:
 *  \begin{prooftree}
 *      hyp*
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