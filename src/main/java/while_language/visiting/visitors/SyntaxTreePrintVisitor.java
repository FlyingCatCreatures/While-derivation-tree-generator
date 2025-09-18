package while_language.visiting.visitors;

import while_language.Syntax.stm.*;
import while_language.visiting.AexpVisitor;
import while_language.visiting.BexpVisitor;
import while_language.visiting.StmVisitor;
import while_language.Syntax.aexp.*;
import while_language.Syntax.bexp.*;

public class SyntaxTreePrintVisitor implements StmVisitor<Void>, AexpVisitor<Void>, BexpVisitor<Void> {
    private final StringBuilder sb = new StringBuilder();
    private int indent = 0;

    private void printIndent() {
        for (int i = 0; i < indent; i++) sb.append("    ");
    }

    public String getResult() {
        return sb.toString();
    }

    // Statements
    @Override
    public Void visit(skip s) {
        printIndent();
        sb.append("skip\n");
        return null;
    }

    @Override
    public Void visit(Break b) {
        printIndent();
        sb.append("break\n");
        return null;
    }

    @Override
    public Void visit(Continue c) {
        printIndent();
        sb.append("continue\n");
        return null;
    }

    @Override
    public Void visit(assign a) {
        printIndent();
        sb.append("assign\n");
        indent++;
        printIndent(); sb.append("var: "); a.x().accept(this); sb.append("\n");
        printIndent(); sb.append("aexp: "); a.a().accept(this); sb.append("\n");
        indent--;
        return null;
    }

    @Override
    public Void visit(compound c) {
        printIndent();
        sb.append("compound\n");
        indent++;
        c.s1().accept(this);
        c.s2().accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(if_then_else ite) {
        printIndent();
        sb.append("if_then_else\n");
        indent++;
        printIndent(); sb.append("cond: "); ite.b().accept(this); sb.append("\n");
        printIndent(); sb.append("then:\n"); indent++; ite.s1().accept(this); indent--;
        printIndent(); sb.append("else:\n"); indent++; ite.s2().accept(this); indent--;
        indent--;
        return null;
    }

    @Override
    public Void visit(while_do w) {
        printIndent();
        sb.append("while_do\n");
        indent++;
        printIndent(); sb.append("cond: "); w.b().accept(this); sb.append("\n");
        printIndent(); sb.append("body:\n"); indent++; w.s().accept(this); indent--;
        indent--;
        return null;
    }

    @Override
    public Void visit(repeat_until ru) {
        printIndent();
        sb.append("repeat_until\n");
        indent++;
        printIndent(); sb.append("body:\n"); indent++; ru.s().accept(this); indent--;
        printIndent(); sb.append("cond: "); ru.b().accept(this); sb.append("\n");
        indent--;
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
        sb.append("addition\n");
        indent++;
        printIndent(); sb.append("left: "); a.a1().accept(this); sb.append("\n");
        printIndent(); sb.append("right: "); a.a2().accept(this); sb.append("\n");
        indent--;
        return null;
    }

    @Override
    public Void visit(subtract s) {
        sb.append("subtract\n");
        indent++;
        printIndent(); sb.append("left: "); s.a1().accept(this); sb.append("\n");
        printIndent(); sb.append("right: "); s.a2().accept(this); sb.append("\n");
        indent--;
        return null;
    }

    @Override
    public Void visit(multiply m) {
        sb.append("multiply\n");
        indent++;
        printIndent(); sb.append("left: "); m.a1().accept(this); sb.append("\n");
        printIndent(); sb.append("right: "); m.a2().accept(this); sb.append("\n");
        indent--;
        return null;
    }

    // Boolean expressions
    @Override
    public Void visit(True t) {
        sb.append("true");
        return null;
    }

    @Override
    public Void visit(False f) {
        sb.append("false");
        return null;
    }

    @Override
    public Void visit(conjunction c) {
        sb.append("conjunction\n");
        indent++;
        printIndent(); sb.append("left: "); c.b1().accept(this); sb.append("\n");
        printIndent(); sb.append("right: "); c.b2().accept(this); sb.append("\n");
        indent--;
        return null;
    }

    @Override
    public Void visit(negation n) {
        sb.append("negation\n");
        indent++;
        printIndent(); sb.append("bexp: "); n.b().accept(this); sb.append("\n");
        indent--;
        return null;
    }

    @Override
    public Void visit(equals e) {
        sb.append("equals\n");
        indent++;
        printIndent(); sb.append("left: "); e.a1().accept(this); sb.append("\n");
        printIndent(); sb.append("right: "); e.a2().accept(this); sb.append("\n");
        indent--;
        return null;
    }

    @Override
    public Void visit(leq l) {
        sb.append("leq\n");
        indent++;
        printIndent(); sb.append("left: "); l.a1().accept(this); sb.append("\n");
        printIndent(); sb.append("right: "); l.a2().accept(this); sb.append("\n");
        indent--;
        return null;
    }

    @Override
    public Void visit(geq q) {
        sb.append("geq\n");
        indent++;
        printIndent(); sb.append("left: "); q.a1().accept(this); sb.append("\n");
        printIndent(); sb.append("right: "); q.a2().accept(this); sb.append("\n");
        indent--;
        return null;
    }

    @Override
    public Void visit(lt l) {
        sb.append("lt\n");
        indent++;
        printIndent(); sb.append("left: "); l.a1().accept(this); sb.append("\n");
        printIndent(); sb.append("right: "); l.a2().accept(this); sb.append("\n");
        indent--;
        return null;
    }

    @Override
    public Void visit(gt g) {
        sb.append("gt\n");
        indent++;
        printIndent(); sb.append("left: "); g.a1().accept(this); sb.append("\n");
        printIndent(); sb.append("right: "); g.a2().accept(this); sb.append("\n");
        indent--;
        return null;
    }
}