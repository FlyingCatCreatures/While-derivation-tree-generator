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
import while_language.util.configuration;
import while_language.util.nonterminal;
import while_language.util.terminal;
import while_language.visiting.StmVisitor;

public class StepVisitorSOS implements StmVisitor<while_language.visiting.visitors.StepVisitorSOS.configStringPair>{
    public record configStringPair(configuration c, String s){};
    
    private final nonterminal nt;
    private final Set<String> allVars;
    private final Evaluator eval;
    private final String terminalStepTemlate;
    public StepVisitorSOS(Map<String, Integer> state, nonterminal nt, Set<String> allVars){
        this(state, nt, allVars, false);
    }

    public StepVisitorSOS(Map<String, Integer> state, nonterminal nt, Set<String> allVars, boolean substep){
        this.nt = nt;
        this.allVars = allVars;
        eval = new Evaluator(state);
        terminalStepTemlate = substep ? 
                                "\\langle %s, %s \\rangle \\Rightarrow %s \\ ^{[%s]}":
                                "$\\langle %s, %s \\rangle \\Rightarrow %s \\ ^{[%s]}$";
    }

    public configStringPair step() {
        return nt.S().accept(this);
    }

    private String str(configuration c) {
        if (c instanceof terminal t) {
            return str(t.s());
        } 
        if (c instanceof nonterminal nt) {
            return "\\langle %s, %s \\rangle".formatted(str(nt.S()), str(nt.s()));
        }
        throw new UnsupportedOperationException("Unknow configuration type: " + c.getClass());
    }

    private String str(Stm stm) {
        PrintVisitor pv = new PrintVisitor();
        stm.accept(pv);
        return pv.toString();
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

    @Override
    public configStringPair visit(skip s) {
        String stateString = str(eval.state);
        String stmString = str(s);
        String ruleString = "skip_{sos}";
        String stepString = terminalStepTemlate.formatted(stmString, stateString, stateString, ruleString);
        return new configStringPair(new terminal(eval.state) , stepString);
    }
    
    @Override
    public configStringPair visit(assign a) {
        String var = a.x().x();
        Integer value = a.a().accept(eval);
        String originalState = str(eval.state); // Store string representation of s
        eval.state.put(var, value); // Transition to s'

        String stmString = str(a);
        String stateString = str(eval.state);
        String ruleString = "ass_{sos}";
        String stepString = terminalStepTemlate.formatted(stmString, originalState, stateString, ruleString);
        return new configStringPair(new terminal(eval.state) , stepString);
    }

    @Override
    public configStringPair visit(if_then_else ite) {
        String stmString = str(ite);
        String originalState = str(eval.state);
        boolean b = ite.b().accept(eval);
        String ruleString = "if_{sos}^{%s}".formatted(b?"tt":"ff");

        Stm next = b? ite.s1() : ite.s2();
        nonterminal nextConfig = new nonterminal(next, eval.state);

        String nextString = str(nextConfig);
        String stepString = terminalStepTemlate.formatted(stmString, originalState, nextString, ruleString);

        return new configStringPair(nextConfig , stepString);
    }

    @Override
    public configStringPair visit(while_do w) {
        String stmString = str(w);
        String stateString = str(eval.state);
        String ruleString = "while_{sos}";

        Stm next = new if_then_else(w.b(), new compound(w.s(), w), new skip());
        nonterminal nextConfig = new nonterminal(next, eval.state);

        String nextString = str(nextConfig);
        String stepString = terminalStepTemlate.formatted(stmString, stateString, nextString, ruleString);
        return new configStringPair(nextConfig , stepString);
    }

    @Override
    public configStringPair visit(repeat_until ru) {
        String stmString = str(ru);
        String stateString = str(eval.state);
        String ruleString = "while_{sos}";

        Stm next = new if_then_else(ru.b(), new compound(ru.s(), ru), ru.s());
        nonterminal nextConfig = new nonterminal(next, eval.state);

        String nextString = str(nextConfig);
        String stepString = terminalStepTemlate.formatted(stmString, stateString, nextString, ruleString);
        return new configStringPair(nextConfig , stepString);
    }

    @Override
    public configStringPair visit(compound c) {
        String oldConfigString = str(new nonterminal(c, eval.state));

        // Step S1
        nonterminal S1Config = new nonterminal(c.s1(), eval.state);
        StepVisitorSOS premiseStepper = new StepVisitorSOS(eval.state, S1Config, allVars, true);
        configStringPair premisePair = premiseStepper.step();

        String premise = premisePair.s();
        configuration next = premisePair.c();
        String ruleString;

        if(next instanceof nonterminal nt){
            // comp1
            ruleString = "comp_{sos}^1";
            next = new nonterminal(new compound(nt.S(), c.s2()), eval.state);
        } else if (next instanceof terminal t){
            // comp2
            ruleString = "comp_{sos}^2";
            next = new nonterminal(c.s2(), eval.state);
        } else { 
            throw new IllegalStateException("Unexpected result from stepping S1");
        } 


        String stepString = """
        \\begin{prooftree}
            %s
        \\justifies
            %s \\Rightarrow %s
        \\using
            [%s]
        \\end{prooftree}""".formatted(premise, oldConfigString, str(next), ruleString);

        return new configStringPair(next, stepString);
    }

}
