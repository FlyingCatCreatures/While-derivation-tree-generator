package while_language.visiting.visitors;

import while_language.Syntax.stm.*;
import while_language.visiting.AexpVisitor;
import while_language.visiting.BexpVisitor;
import while_language.visiting.StmVisitor;
import while_language.Syntax.aexp.*;
import while_language.Syntax.bexp.*;

public class PrintVisitor implements StmVisitor<Void>, AexpVisitor<Void>, BexpVisitor<Void> {
    private final StringBuilder sb = new StringBuilder();

    public String getResult() {
        return sb.toString();
    }

    public String toString() {
        return this.getResult();
    }
    // Statements
    @Override
    public Void visit(skip s) {
        sb.append("$skip$");
        return null;
    }

    @Override
    public Void visit(assign a) {
        sb.append(a.x().x()).append(" := ");
        a.a().accept(this);
        return null;
    }

    @Override
    public Void visit(compound c) {
        c.s1().accept(this);
        sb.append("; ");
        c.s2().accept(this);
        return null;
    }

    @Override
    public Void visit(if_then_else ite) {
        boolean doParenthesisForS1 = ite.s1() instanceof compound;
        boolean doParenthesisForS2 = ite.s2() instanceof compound;

        sb.append("$if $");
        ite.b().accept(this);
        
        sb.append("$ then $");
        if(doParenthesisForS1) sb.append("(");
        ite.s1().accept(this);
        if(doParenthesisForS1) sb.append(")");

        sb.append("$ else $");
        if(doParenthesisForS2) sb.append("(");
        ite.s2().accept(this);
        if(doParenthesisForS2) sb.append(")");
        return null;
    }

    @Override
    public Void visit(while_do w) {
        boolean doParenthesis = w.s() instanceof compound;

        sb.append("$while $");
        w.b().accept(this);
        sb.append("$ do $");
        if (doParenthesis) sb.append("(");
        w.s().accept(this);
        if (doParenthesis) sb.append(")");
        return null;
    }
    @Override
    public Void visit(repeat_until ru){
        boolean doParenthesis = ru.s() instanceof compound;

        sb.append("$repeat $");
        if (doParenthesis) sb.append("(");
        ru.s().accept(this);
        if (doParenthesis) sb.append(")");
        sb.append("$ until $");
        ru.b().accept(this);
        return null;
    }

    @Override
    public Void visit(Num n) {
        sb.append(n.n());
        return null;
    }

    @Override
    public Void visit(Var v) {
        sb.append(v.x());
        return null;
    }

    @Override
    public Void visit(multiply m) {
        appendExp(m.a1(), getPrecedence(m));
        sb.append(" * ");
        appendExp(m.a2(), getPrecedence(m));
        return null;
    }

    @Override
    public Void visit(addition a) {
        appendExp(a.a1(), getPrecedence(a));
        sb.append(" + ");
        appendExp(a.a2(), getPrecedence(a));
        return null;
    }

    @Override
    public Void visit(subtract s) {
        appendExp(s.a1(), getPrecedence(s));
        sb.append(" - ");
        appendExp(s.a2(), getPrecedence(s));
        return null;
    }

    // Boolean expressions
    @Override
    public Void visit(True t) {
        sb.append("$true$");
        return null;
    }

    @Override
    public Void visit(False f) {
        sb.append("$false$");
        return null;
    }

    @Override
    public Void visit(conjunction c) {
        appendExp(c.b1(), getPrecedence(c));
        sb.append(" \\land ");
        appendExp(c.b2(), getPrecedence(c));
        return null;
    }

    @Override
    public Void visit(negation n) {
        sb.append("\\neg ");
        appendExp(n.b(), getPrecedence(n));
        return null;
    }
    
    @Override
    public Void visit(equals e) {
        e.a1().accept(this);
        sb.append(" = ");
        e.a2().accept(this);
        return null;
    }

    @Override
    public Void visit(leq l) {
        l.a1().accept(this);
        sb.append(" <= ");
        l.a2().accept(this);
        return null;
    }

    @Override
    public Void visit(geq q) {
        q.a1().accept(this);
        sb.append(" >= ");
        q.a2().accept(this);
        return null;
    }

    @Override
    public Void visit(lt l) {
        l.a1().accept(this);
        sb.append(" < ");
        l.a2().accept(this);
        return null;
    }

    @Override
    public Void visit(gt g) {
        g.a1().accept(this);
        sb.append(" > ");
        g.a2().accept(this);
        return null;
    }

    // ------- HELPERS --------

    private int getPrecedence(Aexp aexp) {
        if (aexp instanceof Num || aexp instanceof Var) return 99;          // Terminal expressions, never need to be parenthesised
        if (aexp instanceof multiply) return 1;                             // Higher precedence than + and -
        if (aexp instanceof addition || aexp instanceof subtract) return 0; // Lowest precedence
        return -1; 
    }

    private int getPrecedence(Bexp bexp) {
        if (bexp instanceof True || 
            bexp instanceof False || 
            bexp instanceof equals ||
            bexp instanceof leq ||
            bexp instanceof geq ||
            bexp instanceof lt ||
            bexp instanceof gt) return 99;          // Terminal expressions, never need to be parenthesised
        if (bexp instanceof negation) return 3;     // Higher than conjunction
        if (bexp instanceof conjunction) return 0;  // Lowest precedence
        throw new RuntimeException("Unknown precedence for boolean expression of type: " + bexp.getClass());
    }

    private void appendExp(Aexp aexp, int parentPrecedence) {
        int childPrecedence = getPrecedence(aexp);
        if (childPrecedence < parentPrecedence) {
            sb.append("(");
            aexp.accept(this);
            sb.append(")");
        } else {
            aexp.accept(this);
        }
    }

    private void appendExp(Bexp bexp, int parentPrecedence) {
        int childPrecedence = getPrecedence(bexp);
        if (childPrecedence < parentPrecedence) {
            sb.append("(");
            bexp.accept(this);
            sb.append(")");
        } else {
            bexp.accept(this);
        }
    }
}