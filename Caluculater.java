import java.util.Scanner;
import java.util.regex.*;
import java.util.function.Function;
import java.lang.String;

public class Caluculater {
    /**
     * 計算記号の実行
     * @param  int    a       計算数値
     * @param  int    b       計算数値
     * @param  String symbool 計算符号
     * @return int            計算結果
     */
	private static int berekening (int a, int b, String symbool) {
		switch(symbool) {
			case "+":
				return a + b;
			case "-":
				return a - b;
            case "*":
				return a * b;
			case "/":
				return a / b;
            default:
                System.err.println("符号が不正です");
				return -1;
		}
    }

    /**
     * 正規表現にマッチしたパラメータを返す
     * @param  String Regex   正規表現
     * @param  String formula 文字列の式　例："3+2"
     * @return String         正規表現のパラメータを返す
     */
    private static String getStrByRegex (String Regex, String formula) {
        if (!checkError(formula)){
            System.err.println("無限エラー");
        }
        Pattern p = Pattern.compile(Regex);
        Matcher matcher = p.matcher(formula);
        if(matcher.find()) return matcher.group();
        String res = matcher.group();
        return res;
    }

    /**
     * 審議値をmatchers.findで判定する
     * @param  String  Regex   判定基準(正規表現)
     * @param  String  formula 式
     * @return boolean         matcher.findの実行値
     */
    private static boolean getBooByRegex (String Regex, String formula) {
        Pattern p = Pattern.compile(Regex);
        Matcher matcher = p.matcher(formula);
        return matcher.find();
    }

    /**
     * 文字列内の式を計算した結果を返す
     * @param String formula 文字列で表されてる式　例: 3+2, 3-1
     * @return               計算結果
     */
    private static int berekeningAndReplace(String formula){
        String symbol = "+";
        int a = Integer.parseInt(getStrByRegex("^(\\-)?[0-9]+", formula));
        try { symbol = getStrByRegex("(\\+|\\*|\\/)", formula);}
        catch (Exception Err) {}
		int b = Integer.parseInt(getStrByRegex("(\\-)?[0-9]+$", formula));
		return berekening(a, b, symbol);
    }

    /**
     * 計算処理
     * @param input String 入力文字列
     */
    private static void run(String input){
        final String BRACKET_REGEX = "\\([^\\(\\)]*(-)?[0-9]+((\\+|\\-|\\*|\\/)?[0-9]+)*\\)";
        final String BRACKET_ONE_NUM_REGEX = "\\((\\-)?[0-9]+\\)";
        final String FIRST_BEREKENING_REGEX =  "(\\-)?[0-9]+((\\*|\\/)|(\\-))+[0-9]+"; // 乗算割算抽出
        final String SECOND_BEREKENING_REGEX = "(\\-)?[0-9]+(\\+|\\-)+[0-9]+"; // 乗算割算抽出

        Function<String, String> deleteBracket = (str) -> str.replaceAll("\\(((\\-)?[0-9]+((\\+|\\-|\\*|\\/)+[0-9]+)*)\\)", "$1");

        String inputString = input.replaceAll("((\\)|[0-9])+)+\\(", "$1*(");
        while(getBooByRegex(BRACKET_REGEX, inputString) || getBooByRegex(BRACKET_ONE_NUM_REGEX, inputString) ) {
            String tempBracket = getStrByRegex(BRACKET_REGEX, inputString);
            String temp = deleteBracket.apply(tempBracket);
            while(getBooByRegex(FIRST_BEREKENING_REGEX, temp)) {
                String result = String.valueOf(berekeningAndReplace(temp));
                inputString = inputString.replace(tempBracket, result);
                try {
                    tempBracket = getStrByRegex(BRACKET_REGEX, inputString);
                    temp = deleteBracket.apply(tempBracket);
                } catch (Exception Err) {break;}
            }
            while(getBooByRegex(SECOND_BEREKENING_REGEX, temp)) {
                String result = String.valueOf(berekeningAndReplace(temp));
                inputString = inputString.replace(tempBracket, result);
                try {
                    tempBracket = getStrByRegex(BRACKET_REGEX, inputString);
                    temp = deleteBracket.apply(tempBracket);
                } catch (Exception Err) {break;}
            }
        }

        while(getBooByRegex(FIRST_BEREKENING_REGEX, inputString)) {
            String tempFirst = getStrByRegex(FIRST_BEREKENING_REGEX, inputString);
            inputString = inputString.replace(tempFirst, String.valueOf(berekeningAndReplace(tempFirst)));
        }

        while(getBooByRegex(SECOND_BEREKENING_REGEX, inputString)) {
            String tempSecond = getStrByRegex(SECOND_BEREKENING_REGEX, inputString);
            inputString = inputString.replace(tempSecond, String.valueOf(berekeningAndReplace(tempSecond)));
        }
        System.out.println(inputString);
    }

    private static Boolean checkError(String inputString){
        final String ERROR_INFI = "0/[0-9]?";
        final String ERROR_JPN = "[ぁ-んー]|[ァ-ケー]|[ｦ-ﾟ]";
        final String ERROR_STR = "(!|@|#|\\$|%|\\^|&|:|_|~|\\{|\\}|\\|>|<|\\?|=|\\`|\\[|\\]|;|\\'|,|\\/|\\.)+";

        if(getBooByRegex(ERROR_INFI, inputString)){
            System.out.println("無限大の結果が出る恐れのあるものは入力ないでください");
            return false;
        }
        if(getBooByRegex(ERROR_JPN, inputString)){
            System.out.println("日本語文字を使用しないでください");
            return false;
        }
        if(getBooByRegex(ERROR_STR, inputString)){
            System.out.println("不明な文字が存在しています。");
            return false;
        }
        return true;
    }

    private static String input(){
        while(true){
            System.out.println("input caluculater");
            String inputString = new Scanner(System.in).nextLine().replaceAll(" ", "");
            if(checkError(inputString)){
                return inputString;
            }
        }
    }

    public static void main(String[] args) {
        String inputString = input();
        run(inputString);
    }
}