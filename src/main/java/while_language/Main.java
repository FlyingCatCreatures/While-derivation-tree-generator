package while_language;

import while_language.AST_constructor.Parser;
import while_language.AST_constructor.Tokenizer;
import while_language.AST_constructor.Tokenizer.Token;
import while_language.Syntax.stm.Stm;
import while_language.visiting.StmVisitor;
import while_language.visiting.visitors.DerivationTreeGenerator;
import while_language.visiting.visitors.Evaluator;
import while_language.visiting.visitors.PrintVisitor;
import while_language.visiting.visitors.SyntaxTreePrintVisitor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java path/to/Main <filename> [--pdf-maxwidth x] [--init_state n var_1 val_1 var_2 val_2 ... var_n val_n]");
            System.err.println("Where filename is relative to the input_files directory");
            System.err.println("And x is an integer representing a maximum pdf width in centimeters");
            System.err.println("And var_i and val_i are a variable name and an integer respectively.");
            System.exit(1);
        }

        String filename = args[0];
        String pdf_maxwidth = "100cm";
        Map<String, Integer> init_state = new HashMap<>();
        for(int i=1; i<args.length; i++){
            if (args[i].equals("--pdf-maxwidth")){
                try{
                    i++; // Consume flag

                    // We want to make sure it is an integer, but we want to pass the string
                    // So we don't store the parsed integer, just check if its valid
                    Integer.parseInt(args[i]); 
                   
                    // x is automatically consumed in the loop update statement
                }catch (NumberFormatException e){
                    System.err.println("--pdf-maxwidth must be an integer, but found " + args[2]);
                    System.exit(1);
                }
                pdf_maxwidth = args[i] + "cm";
            } else if(args[i].equals("--init-state")){
                i++; // Consume flag
                int n = 0;
                try{
                    n = Integer.parseInt(args[i]);
                    i++; // Consume n
                }catch (NumberFormatException e){
                    System.err.println("argument 'n' for --init-state must be an integer");
                    System.exit(1);
                }

                for(int j=i; j < 2*n + i && j+1 < args.length; j+=2){
                    try{
                        init_state.put(args[j], Integer.parseInt(args[j+1]));
                    } catch(NumberFormatException e){
                        System.err.println("all 'val' arguments for --init-state must be integers, but found " + args[j+1]);
                        System.exit(1);
                    }
                }
                i = i + 2*n - 1; // Skip processed arguments
            } else {
                System.err.println("Unknown argument given at position " + i + " . Found: " + args[i]);
                System.exit(1);
            }
        }
        

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

            // Print program
            // System.out.println("\n Program:");
            // PrintVisitor pv = new PrintVisitor();
            // ast.accept(pv);
            // System.out.println(pv);

            // Evaluate program
            System.out.println("Evaluating...");
            Evaluator e = new Evaluator(new HashMap<>(init_state)); // new Hashmap(...) used to make a copy of init_state instead of modifying it
            ast.accept(e);
            System.out.println("Final state: " + e.state);

            // Create derivation tree
            System.out.println("Generating syntax Tree...");
            StmVisitor<Void> visitor = new DerivationTreeGenerator(vars, pdf_maxwidth, init_state);
            ast.accept(visitor);
            
            // Write all output to <filename>.out
            Path outFile = Path.of("output_files/" + filename + "-tree.tex");
            Files.writeString(outFile, visitor.toString());
            System.out.println("Output written to " + outFile.toAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
