package while_language.visiting.visitors;

import java.util.HashMap;
import java.util.Map;

import while_language.Syntax.aexp.Num;
import while_language.Syntax.aexp.Var;
import while_language.Syntax.aexp.addition;
import while_language.Syntax.aexp.multiply;
import while_language.Syntax.aexp.subtract;
import while_language.Syntax.bexp.False;
import while_language.Syntax.bexp.True;
import while_language.Syntax.bexp.conjunction;
import while_language.Syntax.bexp.leq;
import while_language.Syntax.bexp.negation;
import while_language.Syntax.bexp.equals;
import while_language.Syntax.stm.Break;
import while_language.Syntax.stm.assign;
import while_language.Syntax.stm.compound;
import while_language.Syntax.stm.if_then_else;
import while_language.Syntax.stm.skip;
import while_language.Syntax.stm.while_do;
import while_language.util.BreakStatus;
import while_language.Syntax.stm.repeat_until;
import while_language.visiting.AexpVisitor;
import while_language.visiting.BexpVisitor;
import while_language.visiting.StmVisitor;

public class Evaluator implements StmVisitor<BreakStatus>, AexpVisitor<Integer>, BexpVisitor<Boolean> {
    public final Map<String, Integer> state;

    public Evaluator(){
        state = new HashMap<>();
    }

    public Evaluator(Map<String, Integer> init_state){
        state = init_state;
    }
    @Override
    public Boolean visit(True t) {
        return true;
    }

    @Override
    public Boolean visit(False f) {
        return false;
    }

    @Override
    public Boolean visit(negation n) {
        return !n.b().accept(this);
    }

    @Override
    public Boolean visit(conjunction c) {
        return c.b1().accept(this) && c.b2().accept(this);
    }

    @Override
    public Boolean visit(equals e) {
        return e.a1().accept(this).equals(e.a2().accept(this));
    }

    @Override
    public Boolean visit(leq l) {
        return l.a1().accept(this)<=l.a2().accept(this);
    }

    @Override
    public Integer visit(Num n) {
        return Integer.parseInt(n.n());
    }

    @Override
    public Integer visit(Var v) {
        Integer val = state.get(v.x());
        if(val==null) throw new RuntimeException("No value found in state for variable " + v.x());

        return val;
    }

    @Override
    public Integer visit(addition a) {
        return a.a1().accept(this) + a.a2().accept(this);
    }

    @Override
    public Integer visit(subtract s) {
        return s.a1().accept(this) - s.a2().accept(this);
    }

    @Override
    public Integer visit(multiply m) {
        return m.a1().accept(this) * m.a2().accept(this);
    }

    @Override
    public BreakStatus visit(assign a) {
        state.put(a.x().x(), a.a().accept(this));
        return BreakStatus.NO_BREAK;
    }

    @Override
    public BreakStatus visit(skip s) {
        return BreakStatus.NO_BREAK;
    }

    @Override
    public BreakStatus visit(Break b) {
        return BreakStatus.BREAK_ENCOUNTERED;
    }

    @Override
    public BreakStatus visit(if_then_else ite) {
        if (ite.b().accept(this)){
            return ite.s1().accept(this);
        }else{
            return ite.s2().accept(this);
        }
    }

    @Override
    public BreakStatus visit(compound c) {
        if(c.s1().accept(this)==BreakStatus.BREAK_ENCOUNTERED) return BreakStatus.BREAK_ENCOUNTERED;
        return c.s2().accept(this);
    }
    
    @Override
    public BreakStatus visit(while_do w) {
        // By definition this would have to be recursive but of course a while loop is equivalent
        // And a stackoverflow is not our goal, so we do that
        while( w.b().accept(this)){
            if(w.s().accept(this)==BreakStatus.BREAK_ENCOUNTERED) break;
        }
        return BreakStatus.NO_BREAK;
    }

    @Override
    public BreakStatus visit(repeat_until ru) {
        // By definition this would have to be recursive but of course a do-while loop with the condition swapped is equivalent
        // And a stackoverflow is not our goal, so we do that
        do{
            if(ru.s().accept(this)==BreakStatus.BREAK_ENCOUNTERED) break;
        }
        while(!ru.b().accept(this));
        return BreakStatus.NO_BREAK;
    }
}
