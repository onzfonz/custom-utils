package utilities;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DBUtils {
	//Logging Constants
	public static final String LOG_QTRIED = "QTried";
	public static final String LOG_QDONE = "QDone";
	public static final String LOG_QSHOWN = "QShown";
	public static final String LOG_QSTARTED = "QStarted";
	    
	public static final String DB_PROTOCOL = "jdbc:derby://localhost:1527/";
    public static final String DB_DRIVER = "org.apache.derby.jdbc.ClientDriver";
    public static final String DB_SCHEMA = "GAR";
    public static final String DB_NAME = "tugofwar";
    
    public static final int[] SQL_TYPES_CARDS = {Types.SMALLINT, Types.VARCHAR, Types.VARCHAR, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT};
    public static final String SQL_INSERT_CARDS = "insert into cards (cistrick, ctype, content, cnum, cden, cdecimal) values (?, ?, ?, ?, ?, ?)";

    public static final int[] SQL_TYPES_QS = {Types.INTEGER, Types.VARCHAR, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT};
    public static final String SQL_INSERT_QS = "insert into questions (qid, question, qans, qnum, qden, qval, qdecimal) values (?, ?, ?, ?, ?, ?, ?)";
    
    public static final int[] SQL_TYPES_PAIRS = {Types.INTEGER, Types.INTEGER};
    public static final String SQL_INSERT_PAIRS = "insert into pairs (uid1, uid2) values (?, ?)";
    
    public static final int[] SQL_TYPES_GAMES = {Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.TIMESTAMP, Types.TIMESTAMP};
    public static final String SQL_INSERT_GAMES = "insert into games (gid, pid1, pid2, pstart, pend) values (?, ?, ?, ?, ?)";
    
    public static final int[] SQL_TYPES_ULOGS = {Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.TIMESTAMP, Types.VARCHAR, Types.VARCHAR, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT};
    public static final String SQL_INSERT_ULOGS = "insert into userlogs (uid, qaid, uorder, qid, ulogtime, ulogtype, ulogattempt, ulogppl, uloglines, ulogmarks, ulogshown) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    public static final int[] SQL_TYPES_PLOGS = {Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.TIMESTAMP, Types.VARCHAR, Types.VARCHAR};
    public static final String SQL_INSERT_PLOGS = "insert into pairlogs (plogid, gid, pid, porder, qaid, plogtime, plogtype, plogcontent) values (?, ?, ?, ?, ?, ?, ?, ?)";
    
    public static final int[] SQL_TYPES_SLOGS = {Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER};
    public static final String SQL_INSERT_SLOGS = "insert into statelogs (gid, plogid, p1teamid0, p1teamid1, p2teamid0, p2teamid1, p1numleft0, p1numleft1, p2numleft0, p2numleft1, p1trickid0, p1trickid1, p1trickid2, p1trickid3, p2trickid0, p2trickid1, p2trickid2, p2trickid3) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    public static final String SQL_UID_FROM_NAME = "select uid from users where uname = ?";
    public static final String SQL_PID_FROM_UIDS = "select pid from pairs where uid1 = (select uid from users where uname = ?) AND uid2 = (select uid from users where uname = ?)";
    public static final String SQL_PID_FROM_UID = "select pid from pairs where uid1 = (select uid from users where uname = ?) AND uid2 is null";
    public static final String SQL_ALL_USERS = "select uid,camath,pretot,ptot, (pre1+pre2+pre3+pre10+pre11+pre12+pre13+pre14+pre15), (p1+p2+p3+p10+p11+p12+p13+p14+p15) from users where instudy=1";
    public static final String SQL_SHOWN = "select qid from userlogs where uid = ? AND ulogtype = '"+LOG_QSHOWN+"'";
    public static final String SQL_WRONG_Q = "select qid from userlogs where uid = ? AND ulogtype = '"+LOG_QSTARTED+"' AND qid IN (select qid from questions where qans = -1)";
    public static final String SQL_WRONG_Q_FROM_GID = "select * from userlogs where uid = ? AND ulogtype = '"+LOG_QSTARTED+"' AND qid IN (select qid from questions where qans = -1) AND qaid in (select qaid from pairlogs where plogtype = 'QuestionAns' AND gid in (select gid from games where pend >= ? and pend <= ? and (pid1 IN (select pid from pairs where uid1 = ? OR uid2 = ?) OR pid2 IN (select pid from pairs where uid1 = ? OR uid2 = ?))))";
    public static final String SQL_TRIED = "select qid, ulogattempt from userlogs where uid = ? AND ulogtype = '"+LOG_QTRIED+"'";
    public static final String SQL_GAMES = "select gid from games where pid1 IN (select pid from pairs where uid1 = ? OR uid2 = ?) OR pid2 IN (select pid from pairs where uid1 = ? OR uid2 = ?)";
    public static final String SQL_NUM_QS = "select qid from userlogs where uid = ? AND ulogtype = '"+LOG_QDONE+"'";
    public static final String SQL_NUM_QS_ALT = "select qid from userlogs where uid = ? AND ulogtype = '"+LOG_QSTARTED+"'";
    public static final String SQL_QID_ULOGS = "select * from userlogs where qid = ?";
    public static final String SQL_SHADOW_ULOGS = "select * from userlogs where qaid in (select distinct qaid from pairlogs where qaid is not null and plogcontent like '%Shadow%') and uid = ?";
    public static final String SQL_WRONG_MOVES = "select distinct qid from userlogs where uid = ? AND qans = -1";
    
    public static final String SQL_GET_QIDS = "select qid from questions";
    
    //More complex SQL Statements
    //From Hawes
//    public static final String SQL_ULOGS_FROM_UIDGID = "select * from userlogs where uid = ? and qaid in (select qaid from pairlogs where plogtype = 'QuestionAns' and gid in (select gid from games where gid >= ? and gid <= ? and (pid1 IN (select pid from pairs where uid1 = ? OR uid2 = ?) OR pid2 IN (select pid from pairs where uid1 = ? OR uid2 = ?))))";
//    public static final String SQL_SHADOW_ULOGS_FROM_UIDGID = "select * from userlogs where uid = ? and qaid in (select distinct qaid from pairlogs where qaid is not null and plogcontent like '%Shadow%' and gid in (select gid from games where gid >= ? and gid <= ? and (pid1 IN (select pid from pairs where uid1 = ? OR uid2 = ?) OR pid2 IN (select pid from pairs where uid1 = ? OR uid2 = ?))))";
    //From Garfield  (going to reintroduce the timestamp part)
    public static final String SQL_ULOGS_FROM_UIDGID = "select * from userlogs where uid = ? and qaid in (select qaid from pairlogs where plogtype = 'QuestionAns' and gid in (select gid from games where pend >= ? and pend <= ? and (pid1 IN (select pid from pairs where uid1 = ? OR uid2 = ?) OR pid2 IN (select pid from pairs where uid1 = ? OR uid2 = ?))))";
    public static final String SQL_SHADOW_ULOGS_FROM_UIDGID = "select * from userlogs where uid = ? and qaid in (select distinct qaid from pairlogs where qaid is not null and plogcontent like '%Shadow%' and gid in (select gid from games where pend >= ? and pend <= ? and (pid1 IN (select pid from pairs where uid1 = ? OR uid2 = ?) OR pid2 IN (select pid from pairs where uid1 = ? OR uid2 = ?))))";
    
    
    /* These were the user log names for hawes */
//    public static final String[] USER_LOG_NAMES = {"Hawes2011-1.log", "Hawes2011-2.log", "Hawes2011-4.log", "Hawes2011-5.log"};
    public static final String[] USER_LOG_NAMES_PREFIXES = {"Jazz", "Tree", "Otter"};
    public static final String USER_LOG_PATH = "logs/";
    public static final String[] USER_LOG_NAMES_SUFFIXES = {"1-15-2013", "1-22-2013", "1-29-2013", "2-5-2013", "2-12-2013"};
    public static final String[] DB_DATES = {"2013-01-15", "2013-01-22", "2013-01-29", "2013-02-05", "2013-02-12"};
    public static final String[] DB_TIMES = {"07:30:00", "17:30:00"};
    public static final String USER_LOG_EXT = ".log";
    public static final String USER_LOG_SEPARATOR = " - ";
    public static String[] USER_LOG_NAMES;
    //easiest thing to make this work is just to modify the session markers, though it's not the prettiest
    public static final int[] session1gids = {1, 7, 36, 42, 71, 77};
    public static final int[] session2gids = {8, 14, 43, 49, 78, 84};
    public static final int[] session3gids = {15, 21, 50, 56, 85, 91};
    public static final int[] session4gids = {22, 28, 57, 63, 92, 98};
    public static final int[] session5gids = {29, 35, 64, 70, 99, 105};
    public static final String[] VALID_MOVES = {"myHand", "myTeam", "move", "radios", "chip"};
    public static final int[][] hawessessionmarkers = {session1gids, session2gids, session3gids, session4gids, session5gids};
	
    public static final String FILE_DELIMITER = "----------------------------";
    public static final String INFO_REGEX = "(.*)\\sINFO\\s(.+)\\ssen\\w+\\s(((\\w+):(.*))|(\\.(\\w+)))";
    public static final String WARNING_REGEX = "(.*)\\sWARNING\\s(.+)\\s(quit)!";
    public static final String NAMES_REGEX = "(\\w+)\\s+&\\s+(\\w+)";
    public static final String NAMES2_REGEX = "(\\w+)\\sand\\s(\\w+)";
    //Dianna - Done tried 6| ppl 13, , | shownHow? n
    //Edgar - Started What is 1/2 (.5)  of 8?
    public static final String USER_LOG_REGEX = "(\\w+)\\s-\\s(\\w+)\\s(.+)";
    public static final String UL_DONE_REGEX = "tried\\s(.*)\\|\\s(.*),\\s(.*),\\s(.*)\\|\\sshownHow\\?\\s(.*)";
    public static final String UL_TRIED_REGEX = "tried\\s(.*)\\|\\s(.*),\\s(.*),\\s(.*)";
    public static final String UL_NUM_REGEX = "(.+)\\s(\\d+)";
    
    public static final String QATYPE = "QuestionAns";
    
	private static Pattern namesPattern;
    private static Pattern namesPattern2;
    private static Pattern infoPattern;
    private static Pattern warningPattern;
    private static Pattern userLogPattern;
    private static Pattern uLogDonePattern;
    private static Pattern uLogTriedPattern;
    private static Pattern uLogNumPattern;
    
    static {
    	namesPattern = Pattern.compile(NAMES_REGEX);
    	namesPattern2 = Pattern.compile(NAMES2_REGEX);
    	infoPattern = Pattern.compile(INFO_REGEX);
    	warningPattern = Pattern.compile(WARNING_REGEX);
    	userLogPattern = Pattern.compile(USER_LOG_REGEX);
    	uLogDonePattern = Pattern.compile(UL_DONE_REGEX);
    	uLogTriedPattern = Pattern.compile(UL_TRIED_REGEX);
    	uLogNumPattern = Pattern.compile(UL_NUM_REGEX);
    	USER_LOG_NAMES = generateAllLogFiles();
    }
    
    public static void main(String[] args) {
//    	String[] list = generateAllLogFiles();
//    	for(int i = 0; i < list.length; i++) {
//    		System.out.println(list[i]);
//    	}
    	String s = "Tue Jan 15 11:05:37 PST 2013 INFO treysi & maria sent log:treysi - Oops tried 2| ppl 4, lines 1, marks 14";
    	String s1 = "Tue Jan 15 10:40:09 PST 2013 INFO Junior & Carla sent log:Carla - Help tried 6, 236, 2| ppl 6, lines 3, ";
    	System.out.println(getFullyParsedUserLogTest(s));
        System.out.println(getFullyParsedUserLogTest(s1));
    }
    
    //Testing method not really used for anything other than to solve some bugs
    private static ArrayList<String> getFullyParsedUserLogTest(String line) {
    	ArrayList<String> parsed = DBUtils.getParsedRegex(line);
    	ArrayList<String> parsedUser = DBUtils.getParsedUserLog(parsed);
    	removeExtraTriesFromLog(parsedUser);
    	System.out.println(parsedUser);
    	ArrayList<String> parts = DBUtils.getParsedUserTriedLog(parsedUser);
    	removeMarksFromHelpLog(parts);
    	return parts;
    }
    
    public static void removeExtraTriesFromLog(ArrayList<String> parsedUser) {
    	parsedUser.set(2, cleanUpTriedInHelp(parsedUser.get(2)));
    }
    
    public static void removeMarksFromHelpLog(ArrayList<String> parts) {
    	parts.set(parts.size()-1, extractNumber(parts.get(parts.size()-1)));
    }
    
    /* Hack to take out all the extra tried calls in the number */
    private static String cleanUpTriedInHelp(String s) {
    	String triedStr = "tried ";
    	int triedPos = s.indexOf(triedStr);
    	int pipePos = s.indexOf("|");
    	int spacePos = s.lastIndexOf(" ", pipePos);
    	String newPart = s.substring(spacePos+1, pipePos);
    	return s.substring(0, triedPos + triedStr.length()) + newPart + s.substring(pipePos);
    }
    
    public static String[] generateAllLogFiles() {
    	int numPerType = USER_LOG_NAMES_SUFFIXES.length;
    	int types = USER_LOG_NAMES_PREFIXES.length;
    	int max = types * numPerType;
    	
    	String[] logFileNames = new String[max];
    	for(int i = 0; i < types; i++) {
    		String[] generatedLogs = generateLogFilesForAGroup(i);
    		for(int j = 0; j < numPerType; j++) {
    			logFileNames[i*numPerType+j] = generatedLogs[j];
    		}
    	}
    	return logFileNames;
    }
    
    public static String[] generateLogFilesForAGroup(int index) {
    	if(index < 0 || index >= USER_LOG_NAMES_PREFIXES.length) return null;
    	String[] logFileNames = new String[USER_LOG_NAMES_SUFFIXES.length];
    	for(int i = 0; i < logFileNames.length; i++) {
    		logFileNames[i] = USER_LOG_PATH + USER_LOG_NAMES_PREFIXES[index] + USER_LOG_SEPARATOR + USER_LOG_NAMES_SUFFIXES[i] + USER_LOG_EXT;
    	}
    	return logFileNames;
    }
    
    public static boolean isNull(ResultSet rs, int columnIndex) throws SQLException {
    	return rs.getString(columnIndex) == null;
    }
    
    public static Pattern setUpParser(String regex) {
		return Pattern.compile(regex);
	}
	
	public static ArrayList<String> getParsedRegex(String line) {
		Matcher a = infoPattern.matcher(line);
		if(!a.find()) {
			a = warningPattern.matcher(line);
			if(!a.find()) {
				return null;
			}
		}
		return transferParsedResultsToList(a);
	}
	
	private static ArrayList<String> transferParsedResultsToList(Matcher m) {
		ArrayList<String> elems = new ArrayList<String>();
		for(int i = 1; i < m.groupCount()+1; i++) {
			elems.add(m.group(i));
		}
		return elems;
	}
	
	public static ArrayList<String> getParsedUserLog(ArrayList<String> parsed) {
		String userInfo = parsed.get(5);
		Matcher m = userLogPattern.matcher(userInfo);
		if(!m.find()) {
			return null;
		}
		return transferParsedResultsToList(m);
	}
	
	public static String extractNumber(String str) {
		Matcher m = uLogNumPattern.matcher(str);
		if(!m.find()) {
			return null;
		}
		return m.group(2);
	}
	
	public static ArrayList<String> getParsedUserTriedLog(ArrayList<String> parsedUser) {
		return getParsedLog(parsedUser, uLogTriedPattern);
	}
	
	public static ArrayList<String> getParsedUserDoneLog(ArrayList<String> parsedUser) {
		return getParsedLog(parsedUser, uLogDonePattern);
	}
	
	private static ArrayList<String> getParsedLog(ArrayList<String> parsedUser, Pattern pattern) {
		String userInfo = parsedUser.get(2);
		Matcher m = pattern.matcher(userInfo);
		if(!m.find()) {
			return null;
		}
		ArrayList<String> results = transferParsedResultsToList(m);
		return cleanedUpUserDoneLog(results);
	}
	
	public static ArrayList<String> cleanedUpUserDoneLog(ArrayList<String> results) {
		for(int i = 0; i < results.size()-1; i++) {
			String s = results.get(i);
			if(s == null || s.equals("") || s.equals(" ")) {
				results.set(i, null);
			}else if(i >= 1){
				results.set(i, extractNumber(s));
			}
		}
		return results;
	}
	
	public static boolean decideIfUserLog(ArrayList<String> parsed) {
		return parsed != null && parsed.size() >= 5 && parsed.get(4) != null && parsed.get(4).equals("log");
	}
	
	public static boolean decideIfUserLog(String line) {
		return decideIfUserLog(getParsedRegex(line));
	}
	
	public static String getNameLineFromRegex(String line) {
		ArrayList<String> parsed = getParsedRegex(line);
		if(parsed == null) {
			return null;
		}
		return getParsedNameString(parsed.get(1));
	}
	
	public static String getTimestampFromRegex(String line) {
		ArrayList<String> parsed = getParsedRegex(line);
		return getTimestampFromRegex(parsed);
	}
	
	public static String getTimestampFromRegex(ArrayList<String> parsed) {
		if(parsed == null) {
			return null;
		}
		String tstamp = parsed.get(0);
		//Fri Apr 29 13:34:01 PDT 2011 
		return convertToSQLTimestamp(tstamp);		
	}
	
	public static String convertToSQLTimestamp(String tstamp) {
		String sqlTStamp = null;
		try {
			Date d = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US).parse(tstamp);
			sqlTStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(d);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return sqlTStamp;
	}
	
	private static boolean isTwoNames(String nameLine) {
		return nameMatcher(nameLine).find();
	}
	
	private static Matcher nameMatcher(String nameLine) {
		Matcher temp = namesPattern.matcher(nameLine);
		boolean foundAMatch = temp.find();
		if(!foundAMatch) {
			temp = namesPattern2.matcher(nameLine);
		}else{
			temp = namesPattern.matcher(nameLine);
		}
		return temp;
	}
	
	public static String[] getParsedNames(String nameLine) {
		String[] names = new String[2];
		if(!isTwoNames(nameLine)) {
			names[0] = StringUtils.removeNumbers(StringUtils.convertToSentenceCase(nameLine));
		}else{
//			System.out.println("two names" + nameLine);
			Matcher temp = nameMatcher(nameLine);
			temp.find();
			names[0] = StringUtils.removeNumbers(StringUtils.convertToSentenceCase(temp.group(1)));
			names[1] = StringUtils.removeNumbers(StringUtils.convertToSentenceCase(temp.group(2)));
		}
		return names;
	}
	
	public static String getParsedNameString(String nameLine) {
		String[] realVals = getParsedNames(nameLine);
//		System.out.println(realVals[0] + "|" + realVals[1]);
		if(isTwoNames(nameLine)) {
			return realVals[0] + " & " + realVals[1];
		}
		return realVals[0];
	}
    
    public static ArrayList<String> readFilesIntoList(String[] aFileNames) {
    	ArrayList<String> lines = new ArrayList<String>();
    	for(int i = 0; i < aFileNames.length; i++) {
//    		System.out.println(aFileNames[i]);
    		lines.addAll(readFileIntoList(aFileNames[i]));
    		lines.add(FILE_DELIMITER);
    	}
    	return lines;
    }
  
    public static ArrayList<ArrayList<String>> readFilesIntoListOfLists(String[] aFileNames) {
    	ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
    	for(int i = 0; i < aFileNames.length; i++) {
    		ArrayList<String> lines = new ArrayList<String>();
    		lines.addAll(readFileIntoList(aFileNames[i]));
    		list.add(lines);
    	}
    	return list;
    }

    public static ArrayList<String> readFileIntoList(String aFileName) {
    	BufferedReader bf = openDBFile(aFileName);
    	return retrieveLines(bf);
    }
    
    public static BufferedReader openDBFile(String aFileName) {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		InputStream is = cl.getResourceAsStream(aFileName);
		return new BufferedReader(new InputStreamReader(is));
	}
	
	private static ArrayList<String> retrieveLines(BufferedReader bf) {
		ArrayList<String> temp = new ArrayList<String>();
		try {
			String l = bf.readLine(); //throw away first line.
			l = bf.readLine();
			while (l != null && !l.equals("")){
				temp.add(l);
				l = bf.readLine();
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
		return temp;
	}
    
    public static void printArrayList(ArrayList<String> lines) {
    	for(int i = 0; i < lines.size(); i++) { 
        	System.out.println(lines.get(i));
        }
    }
    
    public static void printArrayOfArrayList(ArrayList<String[]> lines) {
    	for(int i = 0; i < lines.size(); i++) {
    		System.out.println(Arrays.asList(lines.get(i)));
        }
    }
    
    public static ArrayList<Integer> columnIndexesWithData(ResultSet rs) throws SQLException {
    	ArrayList<Integer> intlist = new ArrayList<Integer>();
    	int cols = rs.getMetaData().getColumnCount();
    	for(int i = 1; i < cols + 1; i++) {
    		if(isNull(rs, i)) {   //another call could be rs.getMetaData().getColumnName(i);
    			intlist.add(i);   
    		}
    	}
    	return intlist;
    }
	
    public static void prepareSingleInsertIntoTable(String[] vals, PreparedStatement ps, int[] sql_types, boolean isDebug) throws SQLException{
    	int i;
//    	System.out.println("array is " + vals.length);
    	for(i = 0; i < sql_types.length; i++) {
    		String val = "";
    		if(i < vals.length) {
    			val = vals[i];
    		}
//    		System.out.println("trying value " + val + " with index " + i);
    		if(isDebug) {
    			try {
    				setObject(val, ps, i, sql_types);
    			}catch (SQLException e) {
    				DBUtils.printSQLException(e);
    			}
    		}else{
    			setObject(val, ps, i, sql_types);
    		}
    	}
//    	System.out.println("Ready to go");
    	ps.executeUpdate();
//    	System.out.println("exiting the function");
    }
    
    public static String getUIDFromName(String name, PreparedStatement ps) throws SQLException {
    	ps.setString(1, name);
    	ResultSet rs = ps.executeQuery();
    	if(!rs.next()) {
    		return null;
    	}
    	return rs.getString(1);
    }
    	
	public static ArrayList<String> getSQLResultList(PreparedStatement ps) throws SQLException {
		ArrayList<String> list = new ArrayList<String>();
		ResultSet rs = ps.executeQuery();
		while(rs.next()) {
			list.add(rs.getString(1));
		}
		return list;
	}
	
	public static ArrayList<Integer> getSQLResultListInt(PreparedStatement ps) throws SQLException {
		ArrayList<Integer> list = new ArrayList<Integer>();
		ResultSet rs = ps.executeQuery();
		while(rs.next()) {
			list.add(rs.getInt(1));
		}
		return list;
	}

	public static int getSQLResultCount(PreparedStatement ps) throws SQLException {
		return getSQLResultList(ps).size();
	}
    public static HashMap<String, String> linkUIDsToNames(String[] names, PreparedStatement ps) throws SQLException {
    	HashMap<String, String> uidNameMap = new HashMap<String, String>();
    	for(int i = 0; i < names.length; i++) {
    		if(names[i] != null) {
    			String uid = getUIDFromName(names[i], ps);
    			uidNameMap.put(names[i], uid);
    			uidNameMap.put(uid, names[i]);
    		}
    	}
    	return uidNameMap;
    }
    
    public static String getPIDFromNames(String name1, String name2, PreparedStatement ps) throws SQLException {
    	setObject(name1, ps, 0, Types.VARCHAR);
    	setObject(name2, ps, 1, Types.VARCHAR);
    	ResultSet rs = ps.executeQuery();
    	if(!rs.next()) {
    		setObject(name2, ps, 0, Types.VARCHAR);
    		setObject(name1, ps, 1, Types.VARCHAR);
    		rs = ps.executeQuery();
    		if(!rs.next()) {
    			return null;
    		}
    	}
    	return rs.getString(1);
    }
    
    public static String getPIDFromName(String name1, PreparedStatement ps) throws SQLException {
    	setObject(name1, ps, 0, Types.VARCHAR);
    	ResultSet rs = ps.executeQuery();
    	if(!rs.next()) {
    		return null;
    	}
    	return rs.getString(1);
    }

    private static void setObject(String val, PreparedStatement ps, int index, int[] sql_types) throws SQLException{
    	setObject(val, ps, index, sql_types[index]);
    }
    
    private static void setObject(String val, PreparedStatement ps, int index, int sql_type) throws SQLException{
    	if(val == null || val.length() == 0 || val.equals("null")) {
			ps.setNull(index+1, sql_type);
		}else{
			ps.setObject(index+1, val, sql_type);
		}
    }
    
    /* -------------------Utilities for Cards ------------------------------*/
    public static String[] copyArray(String[] arr) {
    	return copyAndRemove(arr, -1, 1);
    }
    
    public static String[] copyAndRemove(String[] arr, int index, int pairIndex) {
    	String[] newArr = new String[arr.length];
    	int otherI = 0;
    	for(int i = 0; i < arr.length; i++) {
    		if(pairIndex == 0 && i == (arr.length/2)) {
    			otherI = i;
    		}
    		if(i != index) {
    			newArr[otherI] = arr[i];
    			otherI++;
    		}
    	}
    	return newArr;
    }
    
    public static String[] convertCommaStrToArray(String line) {
//    	String[] elems = line.split(",");
//		for(int i = 0; i < elems.length; i++) {
//			elems[i] = elems[i].trim();
//		}
//		return elems;
		return (String[]) StringUtils.splitString(line, ",", true).toArray();
    }
    
    /* --------------------Utilities for Questions ---------------------------*/
  //These converters are good to go.
  	public static int convertFractToID(int num, int den, int ppl, boolean isDecimal) {
  		String idStr = ""+num+"0"+den+"0"+ppl;
  		int id = Integer.parseInt(idStr);
  		if(isDecimal) {
  			id *= -1;
  		}
  		return id;
  	}
  	
  	public static String convertFractToKey(String num, String den, String ppl, String isDecimal) {
  		return "" + convertFractToID(Integer.parseInt(num), Integer.parseInt(den), Integer.parseInt(ppl), isDecimal.equals("1"));
  	}
  	
  	public static int getNext0Index(String str, int startPos) {
  		for(int i = startPos; i < str.length()-1; i++) {
  			char ch = str.charAt(i);
  			char nextCh = str.charAt(i+1);
  			if(ch == '0' && nextCh != '0') {
  				return i;
  			}
  		}
  		return -1;
  	}
  	
  	public static boolean isIDDecimal(int id) {
  		return id < 0;
  	}
	/* --------------------Utilities for JDBC Driver -------------------------*/
  	public static PreparedStatement prepareAStatement(Connection conn, String sqlString, ArrayList<Statement> statements) throws SQLException{
  		PreparedStatement temp = conn.prepareStatement(sqlString);
  		statements.add(temp);
  		return temp;
  	}
  	
  	
    public static Connection getDBConnection(String schema, String dbName, String protocol) throws SQLException {
		Properties props = new Properties(); // connection properties
        props.put("user", schema);
        props.put("password", "user1");
        return DriverManager.getConnection(protocol + dbName + ";create=true", props);
	}
	
	/**
     * Loads the appropriate JDBC driver for this environment/framework. For
     * example, if we are in an embedded environment, we load Derby's
     * embedded Driver, <code>org.apache.derby.jdbc.EmbeddedDriver</code>.
     */
    public static void loadDriver(String driver) {
        try {
            Class.forName(driver).newInstance();
            System.out.println("Loaded the appropriate driver");
        } catch (ClassNotFoundException cnfe) {
            System.err.println("\nUnable to load the JDBC driver " + driver);
            System.err.println("Please check your CLASSPATH.");
            cnfe.printStackTrace(System.err);
        } catch (InstantiationException ie) {
            System.err.println(
                        "\nUnable to instantiate the JDBC driver " + driver);
            ie.printStackTrace(System.err);
        } catch (IllegalAccessException iae) {
            System.err.println(
                        "\nNot allowed to access the JDBC driver " + driver);
            iae.printStackTrace(System.err);
        }
    }

    /**
     * Reports a data verification failure to System.err with the given message.
     *
     * @param message A message describing what failed.
     */
    public static void reportFailure(String message) {
        System.err.println("\nData verification failed:");
        System.err.println('\t' + message);
    }

    /**
     * Prints details of an SQLException chain to <code>System.err</code>.
     * Details included are SQL State, Error code, Exception message.
     *
     * @param e the SQLException from which to print details.
     */
    public static void printSQLException(SQLException e)
    {
        // Unwraps the entire exception chain to unveil the real cause of the
        // Exception.
        while (e != null)
        {
            System.err.println("\n----- SQLException -----");
            System.err.println("  SQL State:  " + e.getSQLState());
            System.err.println("  Error Code: " + e.getErrorCode());
            System.err.println("  Message:    " + e.getMessage());
            // for stack traces, refer to derby.log or uncomment this:
            //e.printStackTrace(System.err);
            e = e.getNextException();
        }
    }
    
  //Cleanup and close the connection, didn't really write this code
    public static void cleanUp(ResultSet rs, ArrayList<Statement> statements, Connection conn) {
    	 try {
             if (rs != null) {
                 rs.close();
                 rs = null;
             }
         } catch (SQLException sqle) {
             printSQLException(sqle);
         }

         // Statements and PreparedStatements
         int i = 0;
         while (!statements.isEmpty()) {
             // PreparedStatement extend Statement
             Statement st = (Statement)statements.remove(i);
             try {
                 if (st != null) {
                     st.close();
                     st = null;
                 }
             } catch (SQLException sqle) {
                 printSQLException(sqle);
             }
         }

         //Connection
         try {
             if (conn != null) {
            	 conn.commit();
                 conn.close();
                 conn = null;
             }
         } catch (SQLException sqle) {
             printSQLException(sqle);
         }
    }
    
    /* Other possible utilities */
	public static boolean samePair(String[] twoNames, String pair) {
		String twoNameStr = twoNames[0]+twoNames[1];
		String twoNameStrAlt= twoNames[1]+twoNames[0];
		return pair.equals(twoNameStr) || pair.equals(twoNameStrAlt);
	}

	public static boolean pairInTheGame(String key1, String key2, String[] pair1) {
		String pairKey = pair1[0]+pair1[1];
		String pairKeyAlt = pair1[1]+pair1[0];
		return key1.equals(pairKey) || key1.equals(pairKeyAlt) || key2.equals(pairKey) || key2.equals(pairKeyAlt);
	}
	
	public static boolean pairsAreTheSame(String key1, String key2, String[] pair1, String[] pair2) {
		String pairKey = pair1[0]+pair1[1];
		String pairKeyAlt = pair1[1]+pair1[0];
		String oppoPairKey = pair2[0]+pair2[1];
		String oppoPairKeyAlt = pair2[1]+pair2[0];
		return ((key1.equals(pairKey) || key1.equals(pairKeyAlt)) && ((key2.equals(oppoPairKey)) || key2.equals(oppoPairKeyAlt))) || ((key1.equals(oppoPairKey) || key1.equals(oppoPairKeyAlt)) && (key2.equals(pairKey) || key2.equals(pairKeyAlt)));
	}
	
	public static boolean pairNamesAreTheSame(String pairName1, String pairName2) {
		if(pairName1 == null && pairName2 == null) return true;
		return pairName1 != null && pairName2 != null && (pairName1.equals(pairName2) || pairName1.equals(ParsingUtils.reversePairNames(pairName2)));
	}
}
