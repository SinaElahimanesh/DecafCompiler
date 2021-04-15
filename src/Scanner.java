import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Scanner {

    public static void main(String[] args) {
        String input = "forward55forwardkeeperkhubikeeper";
        Matcher matcher = rx1.matcher(input);
        Matcher matcher2 = rx2.matcher(input);
        ArrayList<Matcher> allMatchers = new ArrayList<>();
        allMatchers.add(matcher);
        allMatchers.add(matcher2);
        boolean isFound = true;
        while (isFound) {
            if (matcher.find()) {
                String string = input.substring(matcher.start(), matcher.end());
                System.out.println(string);
            } else if (matcher2.matches()) {
                String string = input.substring(matcher2.start(), matcher2.end());
                System.out.println(string);
            } else {
                isFound = false;
            }
        }
    }

    private int numericId(String numeric) {
        if(numeric.toLowerCase().contains("0x")) {

        } else if(numeric.toLowerCase().contains("0x")) {

        } else {

        }
        return 0;
    }

    public static boolean isFound(ArrayList<Matcher> allMatchers) {
        return allMatchers.get(0).matches() || allMatchers.get(1).matches();
    }

    private static final Pattern
            rx1 = Pattern.compile(RegexHandler.decimal_number_regex),
            rx2 = Pattern.compile(RegexHandler.decimal_number_regex2);


    public void scanner(String input) {


//        Matcher matcher = rx1.matcher(input).matches() || rx2.matcher(input).matches();



        Pattern playerPattern = Pattern.compile(RegexHandler.decimal_number_regex);
        Matcher playerMatcher = playerPattern.matcher(RegexHandler.decimal_number_regex);

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
