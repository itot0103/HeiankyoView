package org.heiankyoview2.core.xmlio;

import java.io.*;



/**
 * ファイル読み込みのためのユーティリティクラス
 * @author itot
 */
public class XmlFileInput {

	/* var */
	File inputFile = null;
	BufferedReader inputData; 
	
	/**
	 * Constructor
	 * @param inputFile 読み込みファイル
	 */
	public XmlFileInput(File inputFile) {
		this.inputFile = inputFile;
		try { 
			inputData = new BufferedReader(new FileReader(inputFile));
		} catch (FileNotFoundException e) {
  			System.err.println("Error: " + inputFile.toString() + " does not exist.");
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
