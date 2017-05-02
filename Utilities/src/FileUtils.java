import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;

public class FileUtils {
	public static void writeLinesToFile(ArrayList<String> lines) {
		String filename = getFilenamePathFromDialog();
		BufferedWriter wr = openFileWriter(filename);
		writeOutLines(wr, lines);
		try {
			wr.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void writeStringOutToFile(String s) {
		String filename = getFilenamePathFromDialog();
		BufferedWriter wr = openFileWriter(filename);
		writeOutLine(wr, s);
		try {
			wr.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}

	private static String getFilenamePathFromDialog() {
		JFileChooser jf = new JFileChooser();
		jf.setCurrentDirectory(new File(".."));
		int returnVal = jf.showSaveDialog(null);
		while(returnVal != JFileChooser.APPROVE_OPTION) {
			jf.showSaveDialog(null);
		}
		return jf.getSelectedFile().getAbsolutePath();
	}

	public static ArrayList<String> getFileLines() {
		JFileChooser jf = new JFileChooser();
		jf.setCurrentDirectory(new File(".."));
		int returnVal = jf.showOpenDialog(null);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			return getFileLines(jf.getSelectedFile().getAbsolutePath());
		}
		return null;
	}

	public static ArrayList<String> getFileLines(String filename) {
		BufferedReader bf = openFileReader(filename);
		return readAllLines(bf);
	}

	/*
	 * Requests the name of an input file from the user and opens
	 * that file to obtain a BufferedReader.  If the file doesnt
	 * exist, the user gets a chance to reenter the file name.
	 */
	private static BufferedReader openFileReader(String filename) {
		BufferedReader rd = null;
		while (rd == null) {
			try {
				rd = new BufferedReader(new FileReader(filename));
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return rd;
	}

	private static BufferedWriter openFileWriter (String filename) {
		BufferedWriter wr = null;
		while (wr == null) {
			try {
				wr = new BufferedWriter(new FileWriter(filename));
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return wr;
	}

	/*
	 * Reads all available lines from the specified reader and returns
	 * an ArrayList containing those lines.  This method closes the
	 * reader at the end of the file.
	 */
	private static ArrayList<String> readAllLines(BufferedReader rd) {
		ArrayList<String> lineList = new ArrayList<String>();
		try {
			while (true) {
				String line = rd.readLine();
				if (line == null) break;
				lineList.add(line);
			}
			rd.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return lineList;
	}


	private static void writeOutLines(BufferedWriter bf, ArrayList<String> lines) {
		for(String line:lines) {
			writeOutLine(bf, line+"\n");
		}
	}

	private static void writeOutLine(BufferedWriter bf, String text) {
		try {
			bf.write(text);
			bf.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		ArrayList<String> lines = getFileLines();
		ParsingUtils.convertLinesToCSVFormat(lines);
		writeLinesToFile(lines);
	}
}
