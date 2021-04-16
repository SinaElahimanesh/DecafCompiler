import java.security.Key;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Scanner {

    public static void main(String[] args) {
        String input = "{−123−a35 , id3a ,+∗;}[||===!=()&&]<><=>==";
        Matcher matcher1 = rx1.matcher(input);
        Matcher matcher2 = rx2.matcher(input);
        Matcher matcher3 = rx3.matcher(input);
        Matcher matcher4 = rx4.matcher(input);
        Matcher matcher5 = rx5.matcher(input);
        Matcher matcher6 = rx6.matcher(input);
        Matcher matcher7 = rx7.matcher(input);
        Matcher matcher8 = rx8.matcher(input);

        ArrayList<Matcher> allMatchers = new ArrayList<>();
        allMatchers.add(matcher1);
        allMatchers.add(matcher2);
        allMatchers.add(matcher3);
        allMatchers.add(matcher4);
        allMatchers.add(matcher5);
        allMatchers.add(matcher6);
        allMatchers.add(matcher7);
        allMatchers.add(matcher8);

//        matcher2.find();
//        String string2 = input.substring(matcher2.start(), matcher2.end());
//        System.out.println(string2);
        boolean isFound = true;
        int l0 = 0;
        int l1 = 0;
        int l2 = 0;
        int l3 = 0;
        int l4 = 0;
        int l5 = 0;
        int l6 = 0;
        int l7 = 0;


        int i=0;
        int j=0;
        while (i < input.length()) {
            String string = "^";
            if (matcher1.matches()) {
                string = input.substring(matcher1.start(), matcher1.end());
                j = i;
                while (true) {
                    if (matcher1.find()) {
                        string = input.substring(matcher1.start(), matcher1.end());
                        System.out.println(string);
                    } else if (matcher2.find()) {
                        string = input.substring(matcher2.start(), matcher2.end());
                        System.out.println(string);
                    } else if (matcher3.matches()) {
                        string = input.substring(matcher3.start(), matcher3.end());
                        System.out.println(string);
                    } else if (matcher4.matches()) {
                        string = input.substring(matcher4.start(), matcher4.end());
                        System.out.println(string);
                    } else if (matcher5.matches()) {
                        string = input.substring(matcher5.start(), matcher5.end());
                        System.out.println(string);
                    } else if (matcher6.matches()) {
                        string = input.substring(matcher6.start(), matcher6.end());
                        System.out.println(string);
                    } else if (matcher7.matches()) {
                        string = input.substring(matcher7.start(), matcher7.end());
                        System.out.println(string);
                    } else if (matcher8.matches()) {
                        string = input.substring(matcher8.start(), matcher8.end());
                        System.out.println(string);
                    } else {
                        isFound = false;
                        break;
                    }
                }
                System.out.println(string);
            } else if (matcher2.find()) {
                 string = input.substring(matcher2.start(), matcher2.end());
                System.out.println(string);
            } else if (matcher3.matches()) {
                 string = input.substring(matcher3.start(), matcher3.end());
                System.out.println(string);
            } else if (matcher4.matches()) {
                 string = input.substring(matcher4.start(), matcher4.end());
                System.out.println(string);
            } else if (matcher5.matches()) {
                 string = input.substring(matcher5.start(), matcher5.end());
                System.out.println(string);
            } else if (matcher6.matches()) {
                 string = input.substring(matcher6.start(), matcher6.end());
                System.out.println(string);
            } else if (matcher7.matches()) {
                 string = input.substring(matcher7.start(), matcher7.end());
                System.out.println(string);
            } else if (matcher8.matches()) {
                 string = input.substring(matcher8.start(), matcher8.end());
                System.out.println(string);
            } else {
                isFound = false;
                break;
            }
        }






        Pattern r = Pattern.compile(RegexHandler.regex);
        Matcher matcher = r.matcher("{−123−a35 , id3a ,+∗;}[||===!=()&&]<><=>== a[24]=”7”; n!=if;\n" +
                "false,−if;true32; forpar");
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("");
        KeyWords.setKeywordList();
        while (matcher.find()) {
            Literals literal = findLiterals(matcher.group());
            if(literal == Literals.NOT_LITERAL) {
                System.out.println(matcher.group());
            } else {
                System.out.println(literal.toString() + " " + matcher.group());
            }
        }

        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("");






        while (input.length() > 0) {
            System.out.println(input);
            if (matcher2.matches()) {
                String string = input.substring(matcher2.start(), matcher2.end());
                input = input.substring(matcher2.end() + 1);
                l1 = string.length();
                System.out.println(string);
            }
            if (matcher1.find()) {
                String string = input.substring(matcher1.start(), matcher1.end());
                input = input.substring(matcher1.end() + 1);
                l0 = string.length();
                System.out.println(string);
            }

            if (matcher3.matches()) {
                String string = input.substring(matcher3.start(), matcher3.end());
                input = input.substring(matcher3.end() + 1);
                l2 = string.length();
                System.out.println(string);
            }
            if (matcher4.matches()) {
                String string = input.substring(matcher4.start(), matcher4.end());
                input = input.substring(matcher4.end() + 1);
                l3 = string.length();
                System.out.println(string);
            }
            if (matcher5.matches()) {
                String string = input.substring(matcher5.start(), matcher5.end());
                input = input.substring(matcher5.end() + 1);
                l4 = string.length();
                System.out.println(string);
            }
            if (matcher6.matches()) {
                String string = input.substring(matcher6.start(), matcher6.end());
                input = input.substring(matcher6.end() + 1);
                l5 = string.length();
                System.out.println(string);
            }
            if (matcher7.matches()) {
                String string = input.substring(matcher7.start(), matcher7.end());
                input = input.substring(matcher7.end() + 1);
                l6 = string.length();
                System.out.println(string);
            }
            if (matcher8.matches()) {
                String string = input.substring(matcher8.start(), matcher8.end());
                input = input.substring(matcher8.end() + 1);
                l7 = string.length();
                System.out.println(string);
            } else {
                isFound = false;
                break;
            }
            //int min = Math.min
        }
    }

    private static Literals findLiterals(String token) {

        if(isLiteral(RegexHandler.string_regex, token)) {
            return Literals.T_STRINGLITERAL;
        } else if(token.equalsIgnoreCase("true") || token.equalsIgnoreCase("false")) {
            return Literals.T_BOOLEANLITERAL;
        } else if(isLiteral(RegexHandler.identifier_regex, token)) {
            if(KeyWords.isKeyWord(token)) {
                return Literals.NOT_LITERAL;
            }
            return Literals.T_ID;
        } else if(isLiteral(RegexHandler.hex_integer_regex, token)) {
            return Literals.T_INTLITERAL;
        } else if(isLiteral(RegexHandler.double_regex, token)) {
            return Literals.T_DOUBLELITERAL;
        } else if(isLiteral(RegexHandler.decimal_integer_regex, token)) {
            return Literals.T_INTLITERAL;
        } else {
            return Literals.NOT_LITERAL;
        }
    }

    private static boolean isLiteral(String regexHandler, String token) {
        Pattern regex = Pattern.compile(regexHandler);
        Matcher matcher = regex.matcher(token);
        return matcher.find();
    }

    private int numericId(String numeric) {
        if(numeric.toLowerCase().contains("0x")) {

        } else if(numeric.toLowerCase().contains("0x")) {

        } else {

        }
        return 0;
    }

    public static boolean isFound(ArrayList<Matcher> allMatchers) {
        return allMatchers.get(0).matches()
                || allMatchers.get(1).matches()
                || allMatchers.get(2).matches()
                || allMatchers.get(3).matches()
                || allMatchers.get(4).matches()
                || allMatchers.get(5).matches()
                || allMatchers.get(6).matches()
                || allMatchers.get(7).matches();
    }

//    private static final Pattern
//            rx1 = Pattern.compile("^(forward|keeper)"),
//            rx2 = Pattern.compile("^(for|keep)");
    private static final Pattern
            rx1 = Pattern.compile(RegexHandler.identifier_regex),
        rx2 = Pattern.compile(RegexHandler.decimal_integer_regex),
        rx3 = Pattern.compile(RegexHandler.hex_integer_regex),
        rx4 = Pattern.compile(RegexHandler.double_regex),
        rx5 = Pattern.compile(RegexHandler.string_regex),
        rx6 = Pattern.compile(RegexHandler.boolean_regex),
        rx7 = Pattern.compile(RegexHandler.operator_regex),
        rx8 = Pattern.compile(RegexHandler.comment_regex);


    public void scanner(String input) {


//        Matcher matcher = rx1.matcher(input).matches() || rx2.matcher(input).matches();



        Pattern playerPattern = Pattern.compile("(forward|keeper)");
        Matcher playerMatcher = playerPattern.matcher("");

//        while(playerMatcher.find()) {
//            String string = str.substring(playerMatcher.start(), playerMatcher.end());
//            if(string.contains("keeper")) {
//                continue;
//            }
//
//            startInd = playerMatcher.start();
//            endInd = playerMatcher.end();
//            if ((startInd - endInd) >= 200) {
//                break;
//            }
//
//
//
//            x = Integer.parseInt(playerMatcher.group(2));
//            y = Integer.parseInt(playerMatcher.group(3));
//            d = Integer.parseInt(playerMatcher.group(4));
//            if(d<10){
//                shootFlag = 1;
//                ascendantFlag = 1;
//                lastD = d;
//            }
//
//            if(d<lastD){
//                ascendantFlag=0;
//                shootFlag = 0;
//            }
//            lastD = d;
//
//            if(d>=(fisa(x, y)-10)){
//                if((ascendantFlag==1) && (shootFlag==1)){
//
//                    goals++;
//                    ascendantFlag = 0;
//                    shootFlag = 0;
//                }
//            }
//
//        }
    }
}
