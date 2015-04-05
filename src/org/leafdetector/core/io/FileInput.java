package org.leafdetector.core.io;

import java.io.*;



/**
 * ??????????????????????
 * @author itot
 */
public class FileInput {

	/* var */
	File inputFile = null; // input data file
	BufferedReader inputData; // input stream
	
	/**
	 * Constructor
	 * @param inputFile ????????
	 */
	public FileInput(File inputFile) {
		this.inputFile = inputFile;
		try { // open input file
			inputData = new BufferedReader(new FileReader(inputFile));
		} catch (FileNotFoundException e) {
  			System.err.println("Error: can't find " + inputFile.toString() + ".");
		}
	}

	/**
	 * ??????????????????m???
	 * @return ?????????????rue
	 */
	public boolean ready() { 
		try {
			return inputData.ready();
		} catch (IOException e) {
			System.err.println(e); 
			return false;
		}
	}

	/**
	 * ????????1?????
	 * @return ?????1?????
	 */
	public String read() { 
		try {
			return inputData.readLine();
		} catch (IOException e) {
			System.err.println(e); 
		}
		return null; // ERROR ?
	}

	/**
	 * ????????????A????????
	 */
	public void close() { 
		try {
			inputData.close();
		} catch (IOException e) {
			System.err.println(e); 
		}
	}
}
