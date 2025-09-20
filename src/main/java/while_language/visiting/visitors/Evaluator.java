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
import while_language.Syntax.bexp.lt;
import while_language.Syntax.bexp.negation;
import while_language.Syntax.bexp.equals;
import while_language.Syntax.bexp.geq;
import while_language.Syntax.bexp.gt;
import while_language.Syntax.stm.assign;
import while_language.Syntax.stm.compound;
import while_language.Syntax.stm.if_then_else;
import while_language.Syntax.stm.skip;
import while_language.Syntax.stm.while_do;
import while_language.Syntax.stm.repeat_until;
import while_language.visiting.AexpVisitor;
import while_language.visiting.BexpVisitor;
import while_language.visiting.StmVisitor;

public class Evaluator implements StmVisitor<Void>, AexpVisitor<Integer>, BexpVisitor<Boolean> {
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
    public Boolean visit(geq q) {
        return q.a1().accept(this)>=q.a2().accept(this);
    }

    @Override
    public Boolean visit(lt l) {
        return l.a1().accept(this)<l.a2().accept(this);
    }

    @Override
    public Boolean visit(gt g) {
        return g.a1().accept(this)>g.a2().accept(this);
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
    public Void visit(assign a) {
        state.put(a.x().x(), a.a().accept(this));
        return null;
    }

    @Override
    public Void visit(skip s) {
        return null;
    }

    @Override
    public Void visit(if_then_else ite) {
        if (ite.b().accept(this)){
            return ite.s1().accept(this);
        }else{
            return ite.s2().accept(this);
        }
    }

    @Override
    public Void visit(compound c) {
        c.s1().accept(this);
        c.s2().accept(this);
        return null;
    }
    
    @Override
    public Void visit(while_do w) {
        // By definition this would have to be recursive but of course a while loop is equivalent
        while(w.b().accept(this)){
            w.s().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(repeat_until ru) {
        // By definition this would have to be recursive but of course a do-while loop with the condition swapped is equivalent
        // And a stackoverflow is not our goal, so we do that
        do{
            ru.s().accept(this);
        }
        while(!ru.b().accept(this));
        return null;
    }
}
