import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        String input1 = "14.2a;sldjf”okok”false f noooo 0x1234fe ";
        String input2 = "class Program { void main () {} }";
        String input3 = "{−123−a35 , id3a ,+∗;}[||===!=()&&]<><=>== a[24]=”7”; n!=if;\n" +
                "false,−if;true32; forpar";

//        Scanner scanner = new java.util.Scanner(System.in);
//        StringBuilder input = new StringBuilder("");
//        while (scanner.hasNext()) {
//            String token = scanner.nextLine();
//            token += " \n";
//            input.append(token);
//        }
//        scanner.close();

        String test1 = "abcdefg\n" +
                "Stephen\n" +
                "stephen_hawking\n" +
                "six_dot_035\n" +
                "foo_\n" +
                "covid_19\n";

        String test2 = "void\n" +
                "int\n" +
                "double\n" +
                "bool\n" +
                "string\n" +
                "class\n" +
                "interface\n" +
                "null\n" +
                "this\n" +
                "extends\n" +
                "implements\n" +
                "for\n" +
                "while\n" +
                "if\n" +
                "else\n" +
                "return\n" +
                "break\n" +
                "new\n" +
                "NewArray\n" +
                "Print\n" +
                "ReadInteger\n" +
                "ReadLine\n" +
                "getArrVal\n";

        String test3 = "foo bar\n" +
                "baz\tquux\n" +
                "meep  \t\t\t\t  peem\n" +
                "whaah\fboom\n" +
                "doom\n" +
                "\n" +
                "\n" +
                "\f\t\t\f\n" +
                "gloom    loom\tweave\n";

        String test4 = "1\n" +
                "01\n" +
                "10\n" +
                "65536\n" +
                "2147483647\n";

        String test5 = "0x0\n" +
                "0x1\n" +
                "0xe43620\n" +
                "0x11\n" +
                "0xbeef\n" +
                "0xF\n" +
                "0xF00\n" +
                "0xB1ad\n";

        String test6 = "7.7\n" +
                "0.123456\n" +
                "2020.\n" +
                "0.0\n" +
                "1.99999999\n" +
                "0.99999999\n";

        String test7 = "+ - * < <= != && += -= *= /=\n";

        String test8 = "//this is a single line comment\n" +
                "this is not a comment\n";

        String test9 = "10\n" +
                "9.6\n" +
                "\"Hello\"\n" +
                "true\n" +
                "false\n";

        String test10 = "// this is a single line comment\n" +
                "this is not a comment\n";

        String test11 = "{-123-a35,id3a,+*;}[||===!=()&&]<><=>==\n" +
                "    a[24]=\"7\"; n!=if;\n" +
                "false,-if;true32;\n" +
                "forpar\n";

        String test12 = "class Program { void main () {} }";

        String test13 = "/* akdf;asdf;ljas;lf\n" +
                "adfadfgg\n" +
                "*/ askdfahskdf";

        CompilerScanner compilerScanner = new CompilerScanner();
        compilerScanner.startScanning(test13);
    }
}
