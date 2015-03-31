/*
 * Created on 2006/03/26
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.heiankyoview2.core.glwindow;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.heiankyoview2.core.draw.Canvas;
import org.heiankyoview2.core.tree.Tree;


public interface ViewingPanel {
	
	public final int DOUBLE_CLICK_PANEL = 1;
	public final int DOUBLE_CLICK_BROWSER = 2;
	
	/**
	 * Canvasをセットする
	 * @param c Canvas
	 */
	public void setCanvas(Object c); 
	
	/**
	 * FileOpener をセットする
	 */
	public void setFileOpener(FileOpener fo); 

	
	/**
	 * ダブルクリック時の動作を指定するフラグを返す
	 * @return doubleClickFlag
	 */
	public int getDoubleClickFlag();
	
	/**
	 * Cursor Sensor の ON/OFF を指定するフラグを返す
	 * @return cursorSensorFlag
	 */
	public boolean getCursorSensorFlag();
	
	
	/**
	 * ラジオボタンのアクションの検出を設定する
	 * @param actionListener ActionListener
	 */
	public void addRadioButtonListener(ActionListener actionListener);

	/**
	 * ボタンのアクションの検出を設定する
	 * @param actionListener ActionListener
	 */
	public void addButtonListener(ActionListener actionListener);

}
