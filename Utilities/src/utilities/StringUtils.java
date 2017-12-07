package utilities;
import java.util.Scanner;
import java.util.ArrayList;

public class StringUtils {
	public static String getNextStr(Scanner s) {
		return s.next().trim();
	}
	
	public static int getNextInt(Scanner s) {
		String num = getNextStr(s);
		return Integer.parseInt(num);
	}
	
	public static String substring(String s, String leftChar, String rightChar) {
		if((leftChar == null || rightChar == null || s == null) || ((!s.contains(leftChar) || !s.contains(rightChar)))) {
			return null;
		}
		return s.substring(s.indexOf(leftChar)+leftChar.length(), s.indexOf(rightChar));
	}
	
	public static ArrayList<String> splitString(String longS, String pattern) {
		return splitString(longS, pattern, false);
	}

	public static ArrayList<String> splitString(String longS, String pattern, boolean shouldTrim) {
		Scanner s = new Scanner(longS);
		s.useDelimiter(pattern);
		ArrayList<String> strs = new ArrayList<String>();
		while(s.hasNext()) {
			String nextStr = StringUtils.getNextStr(s);
			if(shouldTrim) {
				nextStr = nextStr.trim();
			}
			strs.add(nextStr);
		}
		return strs;
	}
	
	public static String convertToSentenceCase(String line) {
		ArrayList<String> words = splitString(line, " ");
		String newStr = "";
		int i;
		for(i = 0; i < words.size() - 1; i++) {
			String w = words.get(i);
			String corrected = convertWordToSentenceCase(w);
			newStr = corrected + " ";
		}
		return newStr + convertWordToSentenceCase(words.get(i)); 
	}
	
	public static String removeNumbers(String word) {
		int startPos, endPos;
		for(endPos = word.length()-1; endPos >= 0; endPos--) {
			char ch = word.charAt(endPos);
			if(Character.isLetter(ch)) {
				break;
			}
		}
		for(startPos = 0; startPos < word.length(); startPos++) {
			char ch = word.charAt(startPos);
			if(Character.isLetter(ch)) {
				break;
			}
		}
		return word.substring(startPos, endPos+1);
	}
	
	private static String convertWordToSentenceCase(String word) {
		String str = word.toLowerCase();
		char firstChar = str.charAt(0);
		return Character.toUpperCase(firstChar) + str.substring(1);
	}
}
