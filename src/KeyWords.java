import java.util.ArrayList;
import java.util.Arrays;

public class KeyWords {
    public static String[] keywords = {"void", "int", "double", "bool", "string", "class", "null", "this", "for", "while", "if", "else", "return", "break", "continue",
            "new", "NewArray", "getArrVal", "Print", "ReadInteger", "ReadLine", "dtoi", "itod", "btoi", "itob", "private","public", "interface", "extends", "implements"};//, "true", "false"};
    public static ArrayList<String> keywordList = new ArrayList<>();
    public static void setKeywordList(){
        keywordList.addAll(Arrays.asList(keywords));
    }

    public static boolean isKeyWord(String key) {
        return keywordList.contains(key);
    }

}
