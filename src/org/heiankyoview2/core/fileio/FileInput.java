package org.heiankyoview2.core.fileio;

import java.io.*;



/**
 * ファイル読み込みのためのユーティリティクラス
 * @author itot
 */
public class FileInput {

	/* var */
	File inputFile = null;
	String filename = null;
	boolean streamFlag = false;
	BufferedReader inputData; 
	
	/**
	 * Constructor
	 * @param inputFile 読み込みファイル
	 */
	public FileInput(File inputFile) {
		this.inputFile = inputFile;
		try { 
			inputData = new BufferedReader(new FileReader(inputFile));
		} catch (FileNotFoundException e) {
  			System.err.println("Error: " + inputFile.toString() + " does not exist.");
		}
	}
	
	/**
	 * Constructor
	 * @param inputFile 読み込みファイル
	 */
	public FileInput(String filename, boolean streamFlag) {
		this.filename = filename;
		this.streamFlag = streamFlag;
		if(streamFlag == false) {
			try { 
				inputFile = new File(filename);
				inputData = new BufferedReader(new FileReader(inputFile));
			} catch (Exception e) {
				System.err.println("Error: " + inputFile.toString() + " does not exist.");
			}
		}
		else {
			ClassLoader cl = this.getClass().getClassLoader();
			InputStream inputStream = null;
			try {
				inputStream = cl.getResource(filename).openStream();
				inputData = new BufferedReader(new InputStreamReader(inputStream));
			} catch (Exception e) {
				System.err.println("Error: " + filename + " does not exist.");
				e.printStackTrace();
			}
		}
	}


	/**
	 * ファイルが読み込みできる状態にあるか確認する
	 * @return 読み込みできる状態であればtrue
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
	 * ファイルの中身を1行読み込む
	 * @return 読み込んだ1行の文字列
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
	 * ファイル読み込みを終了し、ファイルを閉じる
	 */
	public void close() { 
		try {
			inputData.close();
		} catch (IOException e) {
			System.err.println(e); 
		}
	}
}
