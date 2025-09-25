package while_language.visiting.visitors;

import java.util.Map;
import java.util.Set;

import while_language.Syntax.stm.*;
import while_language.visiting.StmVisitor;

public class DerivationTreeGeneratorNS implements StmVisitor<Void> {
    private StringBuilder sb;
    private int indent = 0;
    private final Set<String> allVars;
    private final Evaluator eval;

    public DerivationTreeGeneratorNS(Set<String> vars, String varwidth,  Map<String, Integer> init_state){
        eval = new Evaluator(init_state);

        allVars = vars;
        String preamble = """
            %% !TEX TS-program = XeLaTeX
            \\documentclass[varwidth=%s]{standalone}
            \\usepackage{prooftree}
            \\usepackage{amsfonts} %s for \\mathbb{}

            \\begin{document}
            %s \\\\ \n\n
            """.formatted(varwidth, '%', makeLegend());
        sb = new StringBuilder(preamble);
    }

    private void indent() { indent++; }
    private void dedent() { indent--; }
    
    private void appendLine(String line) {
        sb.append("\t".repeat(indent)).append(line).append("\n");
    }

    private void appendStep(Stm stm, String originalState, String rule) {
        PrintVisitor printer = new PrintVisitor();
        stm.accept(printer);
        indent();
        appendLine("\\langle %s, %s \\rangle \\rightarrow %s \\ ^{%s}".formatted(
            printer.toString(), originalState, str(eval.state), rule
        ));
        dedent();
    }
   
    private void appendStep(Stm stm, String originalState) {
        PrintVisitor printer = new PrintVisitor();
        stm.accept(printer);
        indent();
        appendLine("\\langle %s, %s \\rangle \\rightarrow %s".formatted(
            printer.toString(), originalState, str(eval.state)
        ));
        dedent();
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
            mapping.append(var).append("\\mapsto ").append(sym);
            

            idx++;
            first = false;
        }

        return "For this tree we denote $s_{" + vars + "}$ to denote $s[" + mapping + "]$, where $" + vars + "\\in \\mathbb{Z}$";
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

    public Void visit(assign a) {
        String var = a.x().x();
        Integer value = a.a().accept(eval);

        String originalState = str(eval.state); // Store string representation of s
        eval.state.put(var, value); // Transition to s'

        appendStep(a, originalState, "[ass_{ns}]");
        return null;
    }

    public Void visit(skip s) {
        appendStep(s, str(eval.state), "[skip_{ns}]");
        return null;
    }

    public Void visit(if_then_else ite){
        String state_before = str(eval.state);

        boolean cond = ite.b().accept(eval);
        Stm s = cond? ite.s1() : ite.s2();

        appendLine("\\begin{prooftree}");
        indent();
        s.accept(this);
        dedent();
        appendLine("\\justifies");
        appendStep(ite, state_before);
        appendLine("\\thickness = 0.1 em");
        appendLine("\\using");
        indent();
        appendLine("[if_{ns}^{%s}]".formatted(cond ? "tt":"ff"));
        dedent();
        appendLine("\\end{prooftree}");
        return null;
    }

    public Void visit(compound c){
        String state_before = str(eval.state);

        appendLine("\\begin{prooftree}");
        indent();
        c.s1().accept(this);
        c.s2().accept(this);
        
        dedent();
        appendLine("\\justifies");
        appendStep(c, state_before);
        appendLine("\\thickness = 0.1 em");
        appendLine("\\using");
        indent();
        appendLine("[comp_{ns}]");
        dedent();
        appendLine("\\end{prooftree}");
        return null;
    }

    public Void visit(while_do wd){
        String originalState = str(eval.state); 
        boolean cond = wd.b().accept(eval);

        if(!cond){
            appendStep(wd, originalState, "[while_{ns}^{ff}]");
            return null;
        }

        appendLine("\\begin{prooftree}");
        indent();
        wd.s().accept(this);
        wd.accept(this);
        dedent();
        appendLine("\\justifies");
        appendStep(wd, originalState);
        appendLine("\\thickness = 0.1 em");
        appendLine("\\using");
        indent();
        appendLine("[while_{ns}^{tt\\circ}]");
        dedent();
        appendLine("\\end{prooftree}");
        return null;
    }  
    
    public Void visit(repeat_until ru) {
        String originalState = str(eval.state);

        appendLine("\\begin{prooftree}");
        indent();
        ru.s().accept(this);
        boolean cond = ru.b().accept(eval);
        if(!cond) ru.accept(this);
        dedent();
        appendLine("\\justifies");
        appendStep(ru, originalState);
        appendLine("\\thickness = 0.1 em");
        appendLine("\\using");
        indent();
        appendLine("[repeat-until_{ns}^{%s}]".formatted(cond? "tt" : "ff"));
        dedent();
        appendLine("\\end{prooftree}");
        return null;
    }

}