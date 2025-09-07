package while_language;

import while_language.AST_constructor.Parser;
import while_language.AST_constructor.Tokenizer;
import while_language.AST_constructor.Tokenizer.Token;
import while_language.Syntax.stm.Stm;
import while_language.visiting.visitors.DerivationTree;
import while_language.visiting.visitors.Evaluator;
import while_language.visiting.visitors.PrintVisitor;
import while_language.visiting.visitors.SyntaxTreePrintVisitor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            System.err.println("Usage: java path/to/Main <filename> [--pdf-maxwidth x]");
            System.err.println("Where filename is relative to the input_files directory");
            System.err.println("And x is an integer representing a maximum pdf width in centimeters");
            System.exit(1);
        }

        String filename = args[0];
        String pdf_maxwidth = "100cm";

        // Parse optional flag
        if (args.length == 3){ if(args[1].equals("--pdf-maxwidth")) {
            try{
                Integer.parseInt(args[2]);
            }catch (NumberFormatException e){
                System.err.println("--pdf-maxwidth must be an integer, but found " + args[2]);
                System.exit(1);
            }
            pdf_maxwidth = args[2] + "cm";
        } else {
            System.err.println("Unknown 2nd argument passed: " + args[1]);
            System.err.println("Unknown 2nd argument passed: " + args[1]);
        }}
        
        try {
            // Read file into string
            String input = Files.readString(Path.of("input_files/" + filename));

            // Tokenize
            Tokenizer tokenizer = new Tokenizer(input);
            List<Token> tokens = tokenizer.tokenize();

            // Print tokens
            //System.out.println("Tokens:");
            //for (Token t : tokens) {
            //    System.out.println(t.type() + " : " + t.value());
            //}

            // Parse into AST
            Parser parser = new Parser(tokens);
            Stm ast = parser.generateAST();
            Set<String> vars = parser.getVars();

            // Print AST
            //System.out.println("\nSyntax Tree:");
            //SyntaxTreePrintVisitor printer = new SyntaxTreePrintVisitor();
            //ast.accept(printer);
            //System.out.println(printer.getResult());

            // Evaluate program
            System.out.println("Evaluating...");
            Evaluator e = new Evaluator();
            ast.accept(e);
            System.out.println("Final state: " + e.state);

            // Create derivation tree
            System.out.println("Generating syntax Tree...");
            DerivationTree dt = new DerivationTree(vars, pdf_maxwidth);
            ast.accept(dt);
            
             // Write all output to <filename>.out
            Path outFile = Path.of("output_files/" + filename + "-tree.tex");
            Files.writeString(outFile, dt.toString());
            System.out.println("Output written to " + outFile.toAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
