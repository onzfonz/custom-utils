import java.util.ArrayList;

public class ParsingUtils {
	public static String reversePairNames(String pairName) {
		String andStr = " & ";
		return flipOperands(pairName, andStr);
	}

	/* In this situation, the operand should be everything that is not
	 * to be reversed.
	 */
	private static String flipOperands(String pairName, String operator) {
		int andPos = pairName.indexOf(operator);
		if(andPos == -1) {
			return pairName;
		}
		String name1 = pairName.substring(0, andPos);
		String name2 = pairName.substring(andPos+operator.length());
		return name2+operator+name1;
	}
	
	public static void convertLinesToCSVFormat(ArrayList<String> lines) {
		for(int i = 0; i < lines.size(); i++) {
			String line = lines.remove(i);
			line = convertLineToCSVFormat(line);
			lines.add(i, line);
		}
	}
	
	private static String convertLineToCSVFormat(String line) {
		ArrayList<String> pieces = breakUpLine(line, " ");
		return fuseLine(pieces, ", ");
	}
	
	private static String fuseLine(ArrayList<String> pieces, String delim) {
		StringBuilder s = new StringBuilder();
		if(pieces.size() == 0) {
			return "";
		}
		for(int i = 0 ; i < pieces.size()-1; i++) {
			String piece = pieces.get(i);
			s.append(piece);
			s.append(delim);
		}
		s.append(pieces.get(pieces.size()-1));
		return s.toString();
	}

	private static ArrayList<String> breakUpLine(String line, String delim) {
		return StringUtils.splitString(line, delim, true);
	}

	public static void main(String[] args) {
		System.out.println(reversePairNames("Selina & Josh"));
		System.out.println(reversePairNames("Belmin"));
		System.out.println(convertLineToCSVFormat("Oh yeah look at this cool line with so many spaces and numbers 4.83 5 6 7.8"));
	}
}
