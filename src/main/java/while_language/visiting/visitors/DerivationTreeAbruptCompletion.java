package while_language.visiting.visitors;

import java.util.Map;
import java.util.Set;

import while_language.Syntax.stm.*;
import while_language.util.BreakStatus;
import while_language.visiting.StmVisitor;

public class DerivationTreeAbruptCompletion implements StmVisitor<BreakStatus> {
    private StringBuilder sb;
    private int indent = 0;
    private final Set<String> allVars;
    private final Evaluator eval;
    public DerivationTreeAbruptCompletion(Set<String> vars, String varwidth,  Map<String, Integer> init_state){
        eval = new Evaluator(init_state);

        allVars = vars;
        String preamble = """
            %% !TEX TS-program = XeLaTeX
            \\documentclass[varwidth=%s]{standalone}
            \\usepackage{prooftree}
            \\usepackage{amsfonts} %s for \\mathbb{}

            \\begin{document}
            """.formatted(varwidth, '%');
        sb = new StringBuilder(preamble + makeLegend() + "\\\\  \n\n");
    }

    private void indent() { indent++; }
    private void dedent() { indent--; }
    
    private void appendLine(String line) {
        sb.append("\t".repeat(indent)).append(line).append("\n");
    }
    
    private static String marker(BreakStatus b) {
        return switch (b) {
            case NONE -> "\\circ";
            case BREAK -> "\\bullet";
            case CONTINUE -> "\\triangle";
        };
    }
    
    private void appendStep(Stm stm, String originalState, BreakStatus breakStatus, String rule) {
        PrintVisitor printer = new PrintVisitor();
        stm.accept(printer);
        indent();
        appendLine("\\langle %s, %s \\rangle \\rightarrow (%s, %s) \\ ^{%s}".formatted(
            printer.toString(), originalState, str(eval.state), marker(breakStatus), rule
        ));
        dedent();
    }
   
    private void appendStep(Stm stm, String originalState, BreakStatus breakStatus) {
        PrintVisitor printer = new PrintVisitor();
        stm.accept(printer);
        indent();
        appendLine("\\langle %s, %s \\rangle \\rightarrow (%s, %s)".formatted(
            printer.toString(), originalState, str(eval.state), marker(breakStatus)
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

        return "For this tree we denote $s_{" + vars + "}$ to denote $s[" + mapping + "]$, where $" + vars + "\\in \\mathbb{N}$";
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

    public BreakStatus visit(assign a) {
        String var = a.x().x();
        Integer value = a.a().accept(eval);

        String originalState = str(eval.state); // Store string representation of s
        eval.state.put(var, value); // Transition to s'

        appendStep(a, originalState, BreakStatus.NONE, "[ass_{ns}]");
        return BreakStatus.NONE;
    }

    public BreakStatus visit(skip s) {
        appendStep(s, str(eval.state), BreakStatus.NONE, "[skip_{ns}]");
        return BreakStatus.NONE;
    }

    public BreakStatus visit(Break b) {
        appendStep(b, str(eval.state), BreakStatus.BREAK, "[break_{ns}]");
        return BreakStatus.BREAK;
    }

    public BreakStatus visit(Continue c) {
        appendStep(c, str(eval.state), BreakStatus.CONTINUE, "[continue_{ns}]");
        return BreakStatus.CONTINUE;
    }

    public BreakStatus visit(if_then_else ite){
        String state_before = str(eval.state);

        boolean cond = ite.b().accept(eval);
        Stm s = cond? ite.s1() : ite.s2();

        appendLine("\\begin{prooftree}");
        indent();
        BreakStatus breakstatus = s.accept(this);
        dedent();
        appendLine("\\justifies");
        appendStep(ite, state_before, breakstatus);
        appendLine("\\thickness = 0.1 em");
        appendLine("\\using");
        indent();
        appendLine("[if_{ns}^{%s}]".formatted(cond ? "tt":"ff"));
        dedent();
        appendLine("\\end{prooftree}");
        return breakstatus;
    }

    public BreakStatus visit(compound c){
        String state_before = str(eval.state);

        appendLine("\\begin{prooftree}");
        indent();
        BreakStatus b1 = c.s1().accept(this);
        BreakStatus retStatus = b1; // The one we are going to continue with
        if(b1 == BreakStatus.NONE){
            // If no break was encountered we go on with the second statement
            retStatus = c.s2().accept(this);
        }
        
        // if a break or continue was encountered in s1 we skip s2
        dedent();
        appendLine("\\justifies");
        appendStep(c, state_before, retStatus);
        appendLine("\\thickness = 0.1 em");
        appendLine("\\using");
        indent();
        appendLine("[comp_{ns}^{%s}]".formatted(marker(retStatus)));
        dedent();
        appendLine("\\end{prooftree}");
        return retStatus;
    }

    public BreakStatus visit(while_do wd){
        String originalState = str(eval.state); 
        boolean cond = wd.b().accept(eval);

        if(!cond){
            appendStep(wd, originalState, BreakStatus.NONE, "[while_{ns}^{ff}]");
            return BreakStatus.NONE;
        }

        appendLine("\\begin{prooftree}");
        indent();
        BreakStatus b = wd.s().accept(this);
        if(b == BreakStatus.BREAK){
            dedent();
            appendLine("\\justifies");
            appendStep(wd, originalState, BreakStatus.NONE);
            appendLine("\\thickness = 0.1 em");
            appendLine("\\using");
            indent();
            appendLine("[while_{ns}^{tt\\bullet}]");
            dedent();
            appendLine("\\end{prooftree}");
            return BreakStatus.NONE;
        }


        wd.accept(this);
        dedent();
        appendLine("\\justifies");
        appendStep(wd, originalState, BreakStatus.NONE);
        appendLine("\\thickness = 0.1 em");
        appendLine("\\using");
        indent();
        appendLine("[while_{ns}^{tt\\circ}]");
        dedent();
        appendLine("\\end{prooftree}");
        return BreakStatus.NONE;
    }  
    
    public BreakStatus visit(repeat_until ru) {
        String originalState = str(eval.state);

        appendLine("\\begin{prooftree}");
        indent();

        BreakStatus b = ru.s().accept(this);
        if (b == BreakStatus.BREAK) {
            dedent();
            appendLine("\\justifies");
            appendStep(ru, originalState, BreakStatus.NONE);
            appendLine("\\thickness = 0.1 em");
            appendLine("\\using");
            indent();
            appendLine("[repeat-until_{ns}^{\\bullet}]");
            dedent();
            appendLine("\\end{prooftree}");
            return BreakStatus.NONE;
        }

        boolean cond = ru.b().accept(eval);
        if (cond) {
            dedent();
            appendLine("\\justifies");
            appendStep(ru, originalState, BreakStatus.NONE);
            appendLine("\\thickness = 0.1 em");
            appendLine("\\using");
            indent();
            appendLine("[repeat-until_{ns}^{tt}]");
            dedent();
            appendLine("\\end{prooftree}");
            return BreakStatus.NONE;
        } else {
            ru.accept(this);
            dedent();
            appendLine("\\justifies");
            appendStep(ru, originalState, BreakStatus.NONE);
            appendLine("\\thickness = 0.1 em");
            appendLine("\\using");
            indent();
            appendLine("[repeat-until_{ns}^{ff}]");
            dedent();
            appendLine("\\end{prooftree}");
            return BreakStatus.NONE;
        }
    }

}