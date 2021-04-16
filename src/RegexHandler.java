public class RegexHandler {
    public static final String identifier_regex = "[a-zA-Z][\\w_]*";
    public static final String decimal_integer_regex = "\\d+";
    public static final String hex_integer_regex = "0[Xx][\\da-fA-F]+";
    public static final String double_regex = "\\d+\\.\\d*([Ee][+-]?\\d+)?";
    public static final String string_regex = "\\\"[\\\"\\n]*\\\"";
    public static final String boolean_regex = "true|false";
    public static final String operator_regex = "<=|>=|\\+=|-=|\\*=|\\/=|==|!=|&&|\\|\\||\\+|-|\\*|\\/|%|<|>|=|!|;|,|\\.|\\[|\\]|\\(|\\)";
    public static final String comment_regex= "\\/\\/[\\n]*\\n|\\/\\*.*\\*\\/";


    public static final String regex = "(\\/\\/[\\n]*\\n|\\/\\*.*\\*\\/)|(\\\"[\\\"\\n]*\\\")|(true|false)|(<=|>=|\\+=|-=|\\*=|\\/=|==|!=|&&|\\|\\||\\+|-|\\*|\\/|%|<|>|=|!|;|,|\\.|\\[|\\]|\\(|\\))|(0[Xx][\\da-fA-F]+)|(\\d+\\.\\d*([Ee][+-]?\\d+)?)|(\\d+)|([a-zA-Z][\\w_]*)";

//    public static final String identifier_regex = "^[a-zA-Z][\\w_]*";
//    public static final String decimal_integer_regex = "^\\d+";
//    public static final String hex_integer_regex = "^0[Xx][\\da-fA-F]+";
//    public static final String double_regex = "^\\d+\\.\\d*([Ee][+-]?\\d+)?";
//    public static final String string_regex = "^\\\"[^\\\"\\n]*\\\"";
//    public static final String boolean_regex = "^true|^false";
//    public static final String operator_regex = "^<=|^>=|^\\+=|^-=|^\\*=|^\\/=|^==|^!=|^&&|^\\|\\||^\\+|^-|^\\*|^\\/|^%|^<|^>|^=|^!|^;|^,|^\\.|^\\[|^\\]|^\\(|^\\)";
//    public static final String comment_regex= "^\\/\\/[^\\n]*\\n|^\\/\\*.*\\*\\/";
}
