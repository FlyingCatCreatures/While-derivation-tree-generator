package while_language.visiting.visitors;

import java.util.Map;
import java.util.Set;

import while_language.Syntax.stm.Stm;
import while_language.util.configuration;
import while_language.util.nonterminal;
import while_language.visiting.visitors.StepVisitorSOS.configStringPair;

public class SequenceGeneratorSOS {
    private final StringBuilder sb = new StringBuilder();
    private Map<String, Integer> state;
    private final Set<String> allVars;
    private int stepCounter = 1; 

    public SequenceGeneratorSOS(Set<String> vars, String varwidth, Map<String,Integer> init_state) {
        state = init_state;
        allVars = vars;
        String preamble = """
            %% !TEX TS-program = XeLaTeX
            \\documentclass[varwidth=%s]{standalone}
            \\usepackage{amsfonts}
            \\usepackage{amsmath}
            \\usepackage{prooftree}
            \\begin{document}
            %s \\\\ \n\n
            """.formatted(varwidth, makeLegend());
        sb.append(preamble);
    }

    // Run the whole program
    public void run(Stm program) {
        configuration current = new nonterminal(program, state);

        while (current instanceof nonterminal nt) {
            StepVisitorSOS stepVisitor = new StepVisitorSOS(state, nt, allVars);
            configStringPair csp = stepVisitor.step();
            current = csp.c();

            sb.append("""
                \\begin{center}
                \\begin{tabular*}{0.3\\textwidth}{@{\\extracolsep{\\fill}} c r}
                %s & (Step %d) \\\\
                \\end{tabular*}
                \\end{center}
                \n\n
                """.formatted(csp.s(), stepCounter++));
        }
    }


    // --- LaTeX pretty-printing stuff ---

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
