package org.leafdetector.core.io;
import java.io.*;


/**
 * ??????o???????????????
 * @author itot
 */
public class FileOutput {

	/* var */
	File outputFile = null; // input data file
	BufferedWriter outputData;

	/**
	 * Constructor
	 * @param inputFile ????????
	 */
	public FileOutput(File outputFile) {
		this.outputFile = outputFile; 
		try {// open output file
			outputData = new BufferedWriter(new FileWriter(outputFile));
		} catch (IOException e) {
			System.err.println("Error: I/O exception in " + outputFile.toString() + ".");
		}
	}

	/**
	 * ???????????o?
	 * @param line
	 */
	public void print(String line) {
		try {
			outputData.write(line, 0, line.length());
			outputData.flush();
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	/**
	 * ???????????o??????
	 * @param line
	 */
	public void println(String line) {
		try {
			outputData.write(line, 0, line.length());
			outputData.flush();
			outputData.newLine();
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	/**
	 * ??????o?????A????????
	 */
	public void close() {
		try {
			outputData.close();
		} catch (IOException e) {
			System.err.println(e);
		}
	}

}
