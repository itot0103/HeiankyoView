package org.heiankyoview2.core.draw;

import org.heiankyoview2.core.fileio.FileInput;
import org.heiankyoview2.core.fileio.FileOutput;

import java.io.*;
import java.util.*;



/**
 * 視点情報に関するファイル入出力を行うクラス
 * @author itot
 */
public class ViewFile {

	Transformer view1, view2;
	FileInput   input;
	FileOutput  output;

	boolean     isShading = true, isAnnotation = true,
	            isLod = false, isWebSnap = false, isBrowser = true;
	double      bgR = 0.0, bgG = 0.0, bgB = 0.0, linewidth = 1.0;
	


	/**
	 * Constructor
	 * @param view1 View(その1)
	 * @param view2 View(その2)
	 */
	public ViewFile(Transformer view1, Transformer view2) {
		this.view1 = view1;
		this.view2 = view2;
	}	


	/**
	 * 視点設定のファイルを読みはじめる
	 * @return 正しく読めればtrue
	 */
	public boolean openStart() {

		String lineBuffer;
		StringTokenizer tokenBuffer;

		//
		// Open the setting file
		//
		File inputFile = new File("view3d.txt");
		if( inputFile == null ) return false;
		if( inputFile.canRead() == false ) return false;
		input = new FileInput(inputFile);
		if( input == null ) return false;

		//
		// Read lines one-by-one
		//
		while ( input.ready() && (lineBuffer = input.read()) != null) {

			if (lineBuffer.startsWith("#")) {
				continue; //skip comment line
			}

			//
			// isShading
			//
			else if (lineBuffer.startsWith("isShading")) {
				String  flag = lineBuffer.substring(9).trim();
				if( flag.startsWith("true") ) 
					isShading = true;
				else if( flag.startsWith("false") ) 
					isShading = false;
			}

			//
			// isAnnotation
			//
			else if (lineBuffer.startsWith("isAnnotation")) {
				String  flag = lineBuffer.substring(12).trim();
				if( flag.startsWith("true") ) 
					isAnnotation = true;
				else if( flag.startsWith("false") ) 
					isAnnotation = false;
			}

			//
			// isLod
			//
			else if (lineBuffer.startsWith("isLod")) {
				String  flag = lineBuffer.substring(5).trim();
				if( flag.startsWith("true") ) 
					isLod = true;
				else if( flag.startsWith("false") ) 
					isLod = false;
			}

			//
			// isWebSnap
			//
			else if (lineBuffer.startsWith("isWebSnap")) {
				String  flag = lineBuffer.substring(9).trim();
				if( flag.startsWith("true") ) 
					isWebSnap = true;
				else if( flag.startsWith("false") ) 
					isWebSnap = false;
			}

			//
			// isBrowser
			//
			else if (lineBuffer.startsWith("isBrowser")) {
				String  flag = lineBuffer.substring(9).trim();
				if( flag.startsWith("true") ) 
					isBrowser = true;
				else if( flag.startsWith("false") ) 
					isBrowser = false;
			}

			//
			// linewidth
			//
			else if (lineBuffer.startsWith("linewidth")) {
				tokenBuffer = new StringTokenizer(
					lineBuffer.substring(9).trim());
				linewidth = Double.parseDouble(tokenBuffer.nextToken()); 
			}

			//
			// linewidth
			//
			else if (lineBuffer.startsWith("background")) {
				tokenBuffer = new StringTokenizer(
					lineBuffer.substring(10).trim());
				if (tokenBuffer.countTokens() < 3) continue;
				bgR = Double.parseDouble(tokenBuffer.nextToken()); 
				bgG = Double.parseDouble(tokenBuffer.nextToken()); 
				bgB = Double.parseDouble(tokenBuffer.nextToken()); 
			}


			//
			// rotation1
			//
			else if (lineBuffer.startsWith("rotation1")) {
				if(view1 == null) continue;
				tokenBuffer = new StringTokenizer(
					lineBuffer.substring(9).trim());
				if (tokenBuffer.countTokens() < 16) continue;
				for(int i = 0; i < 16; i++) {
					view1.viewRotate[i] = (float)Double.parseDouble(
						tokenBuffer.nextToken()); 
				}
			}

			//
			// rotation2
			//
			else if (lineBuffer.startsWith("rotation2")) {
				if(view2 == null) continue;
				tokenBuffer = new StringTokenizer(
					lineBuffer.substring(9).trim());
				if (tokenBuffer.countTokens() < 16) continue;
				for(int i = 0; i < 16; i++) 
					view2.viewRotate[i] = (float)Double.parseDouble(
						tokenBuffer.nextToken()); 
			}

			//
			// shift1
			//
			else if (lineBuffer.startsWith("shift1")) {
				if(view1 == null) continue;
				tokenBuffer = new StringTokenizer(
					lineBuffer.substring(6).trim());
				if (tokenBuffer.countTokens() < 3) continue;
				for(int i = 0; i < 3; i++) 
					view1.viewShift[i] = (float)Double.parseDouble(
						tokenBuffer.nextToken()); 
			}

			//
			// shift2
			//
			else if (lineBuffer.startsWith("shift2")) {
				if(view2 == null) continue;
				tokenBuffer = new StringTokenizer(
					lineBuffer.substring(6).trim());
				if (tokenBuffer.countTokens() < 3) continue;
				for(int i = 0; i < 3; i++) 
					view2.viewShift[i] = (float)Double.parseDouble(
						tokenBuffer.nextToken()); 
			}

			//
			// scale1
			//
			else if (lineBuffer.startsWith("scale1")) {
				if(view1 == null) continue;
				tokenBuffer = new StringTokenizer(
					lineBuffer.substring(6).trim());
				if (tokenBuffer.countTokens() < 1) continue;
				view1.viewScale = (float)Double.parseDouble(
						tokenBuffer.nextToken()); 
			}

			//
			// scale2
			//
			else if (lineBuffer.startsWith("scale2")) {
				if(view2 == null) continue;
				tokenBuffer = new StringTokenizer(
					lineBuffer.substring(6).trim());
				if (tokenBuffer.countTokens() < 1) continue;
				view2.viewScale = (float)Double.parseDouble(
						tokenBuffer.nextToken()); 
			}

		}

		return true;
	}


	/**
	 * 視点設定のファイルを読み終える
	 */
	public void openEnd() {
		if( input == null ) return;

		input.close();   input = null;
	}

	/**
	 * 視点設定のファイルを書き始める
	 * @return 正しく書ければtrue
	 */
	public boolean saveStart() {
		File outputFile = new File("view3d.txt");
		if( outputFile == null ) return false;
		output = new FileOutput(outputFile);
		if( outputFile == null ) return false;

		return true;
	}

	/**
	 * 視点設定のファイルを書き終える
	 */
	public void saveEnd() {
		if( output == null ) return;

		Boolean  bvalue;
		Double   dvalue;
		String   linebuf;

		//
		// isShading
		//		
		bvalue = new Boolean(isShading);
		linebuf = "isShading  " + bvalue.toString();
		output.println(linebuf);

		//
		// isAnnotation
		//		
		bvalue = new Boolean(isAnnotation);
		linebuf = "isAnnotation  " + bvalue.toString();
		output.println(linebuf);

		//
		// isLod
		//		
		bvalue = new Boolean(isLod);
		linebuf = "isLod  " + bvalue.toString();
		output.println(linebuf);

		//
		// isWebSnap
		//		
		bvalue = new Boolean(isWebSnap);
		linebuf = "isWebSnap  " + bvalue.toString();
		output.println(linebuf);

		//
		// isBrowser
		//		
		bvalue = new Boolean(isBrowser);
		linebuf = "isBrowser  " + bvalue.toString();
		output.println(linebuf);

		//
		// linewidth
		//
		dvalue = new Double(linewidth);
		linebuf = "linewidth  " + dvalue.toString();
		output.println(linebuf);

		//
		// background
		//
		dvalue = new Double(bgR);
		linebuf = "background  " + dvalue.toString();
		dvalue = new Double(bgG);
		linebuf = linebuf + "  " + dvalue.toString();
		dvalue = new Double(bgB);
		linebuf = linebuf + "  " + dvalue.toString();
		output.println(linebuf);

		//
		// Viewing
		//
		saveViewing(view1, 1);
		saveViewing(view2, 2);

		//
		// File close
		//
		output.close();   output = null;
	}


	/**
	 * 視点設定の各パラメータをファイルを書き込む
	 * @param view View 
	 * @param viewId ViewのID
	 */
	void saveViewing(Transformer view, int viewId) {
		Double   dvalue;
		String   linebuf;
		int      i;


		if(view == null) return;

		//
		// Rotation
		//
		if(viewId == 1) linebuf = "rotation1";
		else            linebuf = "rotation2";
		for(i = 0; i < 16; i++) {
			dvalue = new Double(view.viewRotate[i]);
			linebuf = linebuf + "  " + dvalue.toString();
		}
		output.println(linebuf);

		//
		// Shift
		//
		if(viewId == 1) linebuf = "shift1";
		else            linebuf = "shift2";
		for(i = 0; i < 3; i++) {
			dvalue = new Double(view.viewShift[i]);
			linebuf = linebuf + "  " + dvalue.toString();
		}
		output.println(linebuf);

		//
		// Scale
		//
		if(viewId == 1) linebuf = "scale1";
		else            linebuf = "scale2";
		dvalue = new Double(view.viewScale);
		linebuf = linebuf + "  " + dvalue.toString();
		output.println(linebuf);

	}


	/**
	 * アノテーションの有無を設定する
	 * @param flag アノテーションを表示するならtrue
	 */
	public void setAnnotationSwitch(boolean flag) {
		isAnnotation = flag;
	}

	/**
	 * 線の太さをセットする
	 * @param lw 線の太さ（画素数）
	 */
	public void setLinewidth( double linewidth ) {
		this.linewidth = linewidth;
	}
	
	
	/**
	 * 背景色をr,g,bの3値で設定する
	 * @param r 赤（0～1）
	 * @param g 緑（0～1）
	 * @param b 青（0～1）
	 */
	public void setBackground(
		double r, double g, double b ) {
		bgR = r;   bgG = g; bgB = b;
	}

	/**
	 * アノテーションの有無を返す
	 * @return アノテーションを表示するならtrue
	 */
	public boolean getAnnotationSwitch() {
		return isAnnotation;
	}


	/**
	 * 線の太さを返す
	 * @return 線の太さ（画素数）
	 */
	public double getLinewidth() {
		return linewidth;
	}
	
	/**
	 * 背景色のr成分を返す
	 * @return 背景色のr成分（0～1）
	 */
	public double getBackgroundR() {
		return bgR;
	}

	/**
	 * 背景色のg成分を返す
	 * @return 背景色のg成分（0～1）
	 */
	public double getBackgroundG() {
		return bgG;
	}

	/**
	 * 背景色のb成分を返す
	 * @return 背景色のb成分（0～1）
	 */
	public double getBackgroundB() {
		return bgB;
	}

}
