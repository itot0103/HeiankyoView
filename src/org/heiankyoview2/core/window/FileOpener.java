
package org.heiankyoview2.core.window;

import java.awt.*;

import org.heiankyoview2.core.tree.Tree;
import org.heiankyoview2.core.draw.Canvas;

public interface FileOpener {


	/**
	 * Container をセットする
	 * @param c Component
	 */
	public void setContainer(Component c);
	
	/**
	 * Canvas をセットする
	 * @param c Canvas
	 */
	public void setCanvas(Canvas c);
	
	/**
	 * Tree を返す
	 * @return tree
	 */
	public Tree getTree(); 
	

	/*
	 * treeファイルを読み込む
	 */
	public Tree readTreeFile(); 
	
	/*
	 * frameファイルを読み込む
	 */
	public void readFrameFile(); 
	
	/*
	 * テンプレートファイルを書き込む
	 */
	public void writeTemplateFile();
	
	/*
	 * テンプレートファイルを読み込む
	 */
	public void readTemplateFile(); 
	
	/*
	 * treeファイルを書き込む
	 */
	public void writeTreeFile(); 
	
	/*
	 * 画像ファイルを書き込む
	 */
	public void saveImageFile();
	
	/*
	 * TableAttributePanel をゲットする
	 */
	public TablePanel getTablePanel(); 
	
	/*
	 * NodeValuePanel をゲットする
	 */
	public NodeValuePanel getNodeValuePanel();
	
}
