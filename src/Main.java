import code_generator.DecafCodeGenerator;
import code_generator.SemanticException;
import code_generator.SyntaxException;
import code_review.DecafCodeReviewer;
import parser.CodeGenerator;
import parser.Parser;
import parser.Action;
import scanner.CompilerScanner;

import java.io.*;

public class Main {
    public static void main(String[] args) {
        try {
            String inputFileName = null;
            String outputFileName = null;
            if (args != null) {
                for (int i = 0; i < args.length; i++) {
                    if (args[i].equals("-i")) {
                        inputFileName = args[i + 1];
                    }
                    if (args[i].equals("-o")) {
                        outputFileName = args[i + 1];
                    }
                }
            }
            Reader reader = null;
            Writer writer = null;
            if (inputFileName != null)
                reader = new FileReader(inputFileName);
            if (outputFileName != null)
                writer = new FileWriter(outputFileName);
            // Read with reader and write the output with writer.

            int data = reader.read();
            StringBuilder input = new StringBuilder();
            while (data != -1) {
                input.append((char) data);
                data = reader.read();
            }
            reader.close();
            String output = code_generator(input.toString());
            System.out.println(output);
            writer.write(output);
            writer.flush();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static String code_generator(String input) {
        CompilerScanner scanner = new CompilerScanner(input);
        DecafCodeReviewer cr = new DecafCodeReviewer(scanner);
        Parser parser = new Parser(scanner, cr, "src/parser/table.npt", false);

        scanner = new CompilerScanner(input);
        DecafCodeGenerator cg = new DecafCodeGenerator(scanner, cr);
        parser = new Parser(scanner, cg, "src/parser/table.npt", false);

        try {
            parser.parse();
            return cg.getResult();
        } catch (SyntaxException ignored) {
            return "Syntax Error";
        } catch (SemanticException e) {
            System.out.println(e.getMessage());
            return "Semantic Error";
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            System.out.println(throwable.getMessage());
            return "Code Generation Error";
        }
    }

    public static String parser(String input) {
        CompilerScanner scanner = new CompilerScanner(input);
        CodeGenerator cg = new CodeGenerator() {
            int call_number = 0;
            int current_call = 0;
            @Override
            public void doSemantic(String sem, Action action) {
                System.out.println(sem);
                if (action != Action.SHIFT)
                    return;
                current_call ++;
                if (sem.equals("is_lvalue_friendly"))
                    call_number = current_call;
                else if (sem.equals("assignment_operator")) {
                    if (current_call - call_number > 2) {
                        System.out.println("##########################");
                        System.out.println(current_call);
                        System.out.println(call_number);
                        throw new RuntimeException("wrong assignment");
                    } else {
                        System.out.println("##########################");
                        System.out.println(current_call);
                        System.out.println(call_number);
                    }
                }
            }
        };
        Parser parser = new Parser(scanner, cg, "src/parser/table.npt", true);
        try {
            parser.parse();
            return "OK";
        } catch (RuntimeException ignored) {
            return "Syntax Error";
        } catch (SemanticException ignored) {
            return "Semantic Error";
        } catch (Throwable throwable) {
            return "Code Generation Error";
        }
    }

    public static String scanner(String input) {
        CompilerScanner compilerScanner = new CompilerScanner("");
        String output = compilerScanner.startScanning(input);
        System.out.println(output);
        return output;
    }
}