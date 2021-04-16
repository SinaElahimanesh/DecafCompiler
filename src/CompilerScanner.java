import java.security.Key;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CompilerScanner {

    public String startScanning(String input) {
        input = input.replaceAll(RegexHandler.comment_regex,"");
        Pattern r = Pattern.compile(RegexHandler.regex);
        Matcher matcher = r.matcher(input);

        KeyWords.setKeywordList();
        String output = "";
        while (matcher.find()) {
            Literals literal = findLiterals(matcher.group());
            if(literal == Literals.COMMENT_LITERAL) {
                // nothing to do
            } else if(literal == Literals.NOT_LITERAL) {
                output += (matcher.group() + "\n");
            } else {
                System.out.println(literal.toString() + " " + matcher.group());
                output += (literal.toString() + " " + matcher.group() + "\n");
            }
        }
        return output;
    }

    private static Literals findLiterals(String token) {
        if(isLiteral(RegexHandler.string_regex, token)) {
            return Literals.T_STRINGLITERAL;
        } else if(token.equalsIgnoreCase("true") || token.equalsIgnoreCase("false")) {
            return Literals.T_BOOLEANLITERAL;
        } else if(isLiteral(RegexHandler.hex_integer_regex, token)) {
            return Literals.T_INTLITERAL;
        } else if(isLiteral(RegexHandler.identifier_regex, token)) {
            if(KeyWords.isKeyWord(token)) {
                return Literals.NOT_LITERAL;
            }
            return Literals.T_ID;
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

//    private int numericId(String numeric) {
//        if(numeric.toLowerCase().contains("0x")) {
//
//        } else if(numeric.toLowerCase().contains("0x")) {
//
//        } else {
//
//        }
//        return 0;
//    }
}
