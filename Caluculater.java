import java.util.Scanner;
import java.util.regex.*;
import java.lang.String;

public class App {
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
				return -1;
		}
    }

    /**
     * 正規表現にマッチしたパラメータを返す（削除予定）
     * @param  String Regex   正規表現
     * @param  String formula 文字列の式　例："3+2"
     * @return String         正規表現のパラメータを返す
     */
    private static String getStrByRegex (String Regex, String formula) {
        Pattern p = Pattern.compile(Regex);
        Matcher matcher = p.matcher(formula);
        if(matcher.find()){
            return matcher.group();
        }
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
        String symbol = "*";
        int a = Integer.parseInt(getStrByRegex("^(\\-)?[0-9]+", formula));
        try{
            symbol = getStrByRegex("(\\+|\\*|\\/)", formula);
        }catch(Exception Err){}
		int b = Integer.parseInt(getStrByRegex("(\\-)?[0-9]+$", formula));
		return berekening(a, b, symbol);
    }

    /**
     * 計算処理
     * @param input String 入力文字列
     */
    public static void run(String input){
        final String BRACKET_REGEX = "\\([^\\(\\)]*(-)?[0-9]+((\\+|\\-|\\*|\\/)?[0-9]+)*\\)";
        final String BRACKET_ONE_NUM_REGEX = "\\((\\-)?[0-9]+\\)";
        final String FIRST_BEREKENING_REGEX =  "(\\-)?[0-9]+((\\*|\\/)|(\\-))+[0-9]+"; // 乗算割算抽出
        final String SECOND_BEREKENING_REGEX = "(\\-)?[0-9]+(\\+|\\-)+[0-9]+"; // 乗算割算抽出

        final String ERROR_INFI = "0/[0-9]?";

        String inputString = input.replaceAll(" ", "");
        while(getBooByRegex(ERROR_INFI, inputString)){
            System.out.println("無限大の結果が出る恐れのあるものは入力しれください");
            inputString = new Scanner(System.in).nextLine().replaceAll(" ", "");
        }

        inputString = inputString.replaceAll("((\\)|[0-9])+)+\\(", "$1*(");
        while(getBooByRegex(BRACKET_REGEX, inputString) || getBooByRegex(BRACKET_ONE_NUM_REGEX, inputString) ) {
            String tempBracket = getStrByRegex(BRACKET_REGEX, inputString);
            String temp = tempBracket.replace("(", "").replace(")", "");
        	while(getBooByRegex(FIRST_BEREKENING_REGEX, temp)) {
                String result = String.valueOf(berekeningAndReplace(temp));
                inputString = inputString.replace(tempBracket, result);
                try {
                    tempBracket = getStrByRegex(BRACKET_REGEX, inputString);
                    temp = tempBracket.replace("(", "").replace(")", "");
                } catch (Exception Err) {break;}
            }
        	while(getBooByRegex(SECOND_BEREKENING_REGEX, temp)) {
                String result = String.valueOf(berekeningAndReplace(temp));
                inputString = inputString.replace(tempBracket, result);
                try {
                    tempBracket = getStrByRegex(BRACKET_REGEX, inputString);
                    temp = tempBracket.replace("(", "").replace(")", "");
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

    public static void main(String[] args) {
    	System.out.println("input caluculater");
        String inputString = new Scanner(System.in).nextLine();
        run(inputString);
    }
}