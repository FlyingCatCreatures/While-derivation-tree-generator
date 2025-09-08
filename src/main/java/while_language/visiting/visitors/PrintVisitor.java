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
        sb.append("(");
        c.s1().accept(this);
        sb.append("; ");
        c.s2().accept(this);
        sb.append(")");
        return null;
    }

    @Override
    public Void visit(if_then_else ite) {
        sb.append("$if $");
        ite.b().accept(this);
        sb.append("$ then $");
        sb.append("(");
        ite.s1().accept(this);
        sb.append(")");
        sb.append("$ else $");
        sb.append("(");
        ite.s2().accept(this);
        sb.append(")");
        return null;
    }

    @Override
    public Void visit(while_do w) {
        sb.append("$while $");
        w.b().accept(this);
        sb.append("$ do $");
        sb.append("(");
        w.s().accept(this);
        sb.append(")");
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
        appendAexp(m.a1(), getPrecedence(m));
        sb.append(" * ");
        appendAexp(m.a2(), getPrecedence(m));
        return null;
    }

    @Override
    public Void visit(addition a) {
        appendAexp(a.a1(), getPrecedence(a));
        sb.append(" + ");
        appendAexp(a.a2(), getPrecedence(a));
        return null;
    }

    @Override
    public Void visit(subtract s) {
        appendAexp(s.a1(), getPrecedence(s));
        sb.append(" - ");
        appendAexp(s.a2(), getPrecedence(s));
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
    public Void visit(equals e) {
        sb.append("(");
        e.a1().accept(this);
        sb.append(" = ");
        e.a2().accept(this);
        sb.append(")");
        return null;
    }

    @Override
    public Void visit(leq l) {
        sb.append("(");
        l.a1().accept(this);
        sb.append(" <= ");
        l.a2().accept(this);
        sb.append(")");
        return null;
    }

    @Override
    public Void visit(conjunction c) {
        sb.append("(");
        c.b1().accept(this);
        sb.append(" \\land ");
        c.b2().accept(this);
        sb.append(")");
        return null;
    }

    @Override
    public Void visit(negation n) {
        sb.append("!");
        n.b().accept(this);
        return null;
    }

    // ------- HELPERS --------

    private int getPrecedence(Aexp aexp) {
        if (aexp instanceof Num || aexp instanceof Var) return 99;          // Terminal expressions, never need to be parenthesised
        if (aexp instanceof multiply) return 1;                             // Higher precedence than + and -
        if (aexp instanceof addition || aexp instanceof subtract) return 0; // Lowest precedence
        return -1; 
    }

    private void appendAexp(Aexp aexp, int parentPrecedence) {
        int childPrecedence = getPrecedence(aexp);
        if (childPrecedence < parentPrecedence) {
            sb.append("(");
            aexp.accept(this);
            sb.append(")");
        } else {
            aexp.accept(this);
        }
    }
}