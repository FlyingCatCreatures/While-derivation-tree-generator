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
        ite.s1().accept(this);
        sb.append("$ else $");
        ite.s2().accept(this);
        return null;
    }

    @Override
    public Void visit(while_do w) {
        sb.append("$while $");
        w.b().accept(this);
        sb.append("$ do $");
        w.s().accept(this);
        return null;
    }

    // Arithmetic expressions
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
    public Void visit(addition a) {
        sb.append("(");
        a.a1().accept(this);
        sb.append(" + ");
        a.a2().accept(this);
        sb.append(")");
        return null;
    }

    @Override
    public Void visit(subtract s) {
        sb.append("(");
        s.a1().accept(this);
        sb.append(" - ");
        s.a2().accept(this);
        sb.append(")");
        return null;
    }

    @Override
    public Void visit(multiply m) {
        sb.append("(");
        m.a1().accept(this);
        sb.append(" * ");
        m.a2().accept(this);
        sb.append(")");
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
        sb.append(" ^ ");
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
}