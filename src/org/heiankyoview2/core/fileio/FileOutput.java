package org.heiankyoview2.core.fileio;
import java.io.*;


/**
 * ファイル書き出しのためのユーティリティクラス
 * @author itot
 */
public class FileOutput {

	/* var */
	File outputFile = null;
	BufferedWriter outputData;

	/**
	 * Constructor
	 * @param inputFile 読み込みファイル
	 */
	public FileOutput(File outputFile) {
		this.outputFile = outputFile; 
		try {
			outputData = new BufferedWriter(new FileWriter(outputFile));
		} catch (IOException e) {
			System.err.println("Error: exception in " + outputFile.toString() + ".");
		}
	}

	/**
	 * 文字列をファイルに書き出す
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
	 * 文字列をファイルに書き出し、改行する
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
	 * ファイル書き出しを終了し、ファイルを閉じる
	 */
	public void close() {
		try {
			outputData.close();
		} catch (IOException e) {
			System.err.println(e);
		}
	}

}
