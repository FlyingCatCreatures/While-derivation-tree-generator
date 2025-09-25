package while_language.visiting.visitors;

import java.util.Map;
import java.util.Set;

import while_language.Syntax.stm.Stm;
import while_language.Syntax.stm.assign;
import while_language.Syntax.stm.compound;
import while_language.Syntax.stm.if_then_else;
import while_language.Syntax.stm.repeat_until;
import while_language.Syntax.stm.skip;
import while_language.Syntax.stm.while_do;

public class SequenceGeneratorSOS {
    private final StringBuilder sb = new StringBuilder();
    private final Evaluator eval;
    private final Set<String> allVars;

    public SequenceGeneratorSOS(Set<String> vars, String varwidth, Map<String,Integer> init_state) {
        eval = new Evaluator(init_state);
        allVars = vars;
        String preamble = """
            %% !TEX TS-program = XeLaTeX
            \\documentclass[varwidth=%s]{standalone}
            \\usepackage{amsfonts}
            \\usepackage{amsmath}
            \\begin{document}
            %s \\\\ \n\n
            """.formatted(varwidth, makeLegend());
        sb.append(preamble);
    }

    // Run the whole program
    public void run(Stm program) {
        Object current = program;

        // Print the very first configuration
        sb.append("$ ").append(conf(program, eval.state)).append(" $\n\n");

        while (true) {
            if (current instanceof Stm stm) {
                Object next = step(stm);

                if (next instanceof Stm snext) {
                    sb.append("$ \\Rightarrow ").append(conf(snext, eval.state)).append(" $\n\n");
                    current = snext;
                } else {
                    sb.append("$ \\Rightarrow ").append(conf(eval.state)).append(" $\n\n");
                    break;
                }
            } else {
                break; // reached a state
            }
        }
    }



    private Object step(Stm stm) {
        if (stm instanceof assign a) {
            Integer val = a.a().accept(eval);
            eval.state.put(a.x().x(), val);
            return eval.state; // terminates in state
        }
        if (stm instanceof skip) {
            return eval.state; // terminates in state
        }
        if (stm instanceof if_then_else ite) {
            boolean cond = ite.b().accept(eval);
            return cond ? ite.s1() : ite.s2();
        }
        if (stm instanceof compound c) {
            // comp1 or comp2
            Object s1next = step(c.s1());
            if (s1next instanceof Stm s1p) {
                return compoundStep(s1p, c.s2()); // comp₁
            } else {
                return c.s2(); // comp2, s1 finished
            }
        }
        if (stm instanceof while_do w) {
            return new if_then_else(
                w.b(),
                new compound(w.s(), w),
                new skip()
            );
        }
        if (stm instanceof repeat_until ru) {
            return new compound(
                ru.s(),
                new if_then_else(ru.b(), new skip(), ru)
            );
        }
        
        throw new UnsupportedOperationException("Unknown Stm type: " + stm.getClass());
    }

    private Stm compoundStep(Stm s1p, Stm s2) {
        return new compound(s1p, s2);
    }

    // --- LaTeX pretty-printing stuff ---

    private void appendStep(String from, String to) {
        sb.append(from).append(" \\Rightarrow ").append(to).append("\n");
    }

    private String conf(Stm stm, Map<String,Integer> s) {
        PrintVisitor pv = new PrintVisitor();
        stm.accept(pv);
        return "\\langle " + pv.toString() + ", " + str(s) + " \\rangle";
    }

    private String conf(Map<String,Integer> s) {
        return str(s);
    }

    private String str(Map<String,Integer> s) {
        StringBuilder b = new StringBuilder("s_{");
        boolean first = true;
        for (String var : allVars) {
            if (!first) b.append(",");
            b.append(s.getOrDefault(var, null) == null ? "\\bot" : s.get(var));
            first = false;
        }
        return b.append("}").toString();
    }

    private String makeLegend() {
        if (allVars.isEmpty()) return "For this sequence we denote s to denote the empty state.";
        StringBuilder vars = new StringBuilder();
        StringBuilder mapping = new StringBuilder();
        boolean first = true; int idx = 0;
        for (String var : allVars) {
            if (!first) { vars.append(","); mapping.append("]["); }
            char sym = (char)('a' + idx);
            vars.append(sym);
            mapping.append(var).append("\\mapsto ").append(sym);
            idx++; first = false;
        }
        return "For this sequence we denote $s_{" + vars + "}$ to denote $s[" + mapping + "]$, where $" + vars + "\\in \\mathbb{Z}$";
    }

    @Override
    public String toString() {
        return sb.toString() + "\\end{document}";
    }
}
