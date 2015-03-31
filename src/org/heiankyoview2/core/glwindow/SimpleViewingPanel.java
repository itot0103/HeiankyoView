/*
 * Created on 2006/03/26
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.heiankyoview2.core.glwindow;

import java.awt.*;
import java.awt.event.*;
import java.io.File;

import javax.swing.*;

import org.heiankyoview2.core.gldraw.Canvas;
import org.heiankyoview2.core.tree.Tree;

public class SimpleViewingPanel extends JPanel implements ViewingPanel {

	public JButton  treeFileOpenButton, viewResetButton;
	public JRadioButton viewRotateButton, viewScaleButton, viewShiftButton, viewFixButton,
		 cursorSensorButton;
	public Container container;
	
	/* Selective canvas */
	Canvas canvas;
	FileOpener fileOpener;
	public final int DOUBLE_CLICK_PANEL = 1;
	
	/* Cursor Sensor */
	boolean cursorSensorFlag = false;
	
	/* Action listener */
	ButtonListener bl = null;
	RadioButtonListener rbl = null;

	public SimpleViewingPanel() {
		// super class init
		super();

		setSize(200, 800);

		
		//
		// ファイル入力および視点変更操作のパネル
		// （起動したとき右に出るパネルの設定）
		//
		JPanel p1 = new JPanel();
		p1.setLayout(new GridLayout(2,1));
		treeFileOpenButton = new JButton("Tree File Open");
		viewResetButton = new JButton("View Reset");
		p1.add(treeFileOpenButton);
		p1.add(viewResetButton);
	
		JPanel p2 = new JPanel();
		p2.setLayout(new GridLayout(5,1));
		ButtonGroup group1 = new ButtonGroup();
		viewRotateButton = new JRadioButton("View Rotate");
		group1.add(viewRotateButton);
		p2.add(viewRotateButton);
		viewScaleButton = new JRadioButton("View Scale");
		group1.add(viewScaleButton);
		p2.add(viewScaleButton);
		viewShiftButton = new JRadioButton("View Shift");
		group1.add(viewShiftButton);
		p2.add(viewShiftButton);
		viewFixButton = new JRadioButton("View Fix");
		group1.add(viewFixButton);
		p2.add(viewFixButton);
		cursorSensorButton = new JRadioButton("Cursor Sensor");
		p2.add(cursorSensorButton);
		
		//
		// パネル群のレイアウト
		//
		this.setLayout(new GridLayout(2,1));
		this.add(p1);
		this.add(p2);
	
		//
		// リスナーの追加
		//
		if (bl == null)
			bl = new ButtonListener();
		addButtonListener(bl);

		if (rbl == null)
			rbl = new RadioButtonListener();
		addRadioButtonListener(rbl);
	}
	
	/**
	 * Canvasをセットする
	 * @param c Canvas
	 */
	public void setCanvas(Object c) {
		canvas = (Canvas) c;
	}
	
	/**
	 * FileOpener をセットする
	 */
	public void setFileOpener(FileOpener fo) {
		fileOpener = fo;
	}

	
	/**
	 * ダブルクリック時の動作を指定するフラグを返す
	 * @return doubleClickFlag
	 */
	public int getDoubleClickFlag() {
		return DOUBLE_CLICK_PANEL ;
	}
	
	/**
	 * Cursor Sensor の ON/OFF を指定するフラグを返す
	 * @return cursorSensorFlag
	 */
	public boolean getCursorSensorFlag() {
		return cursorSensorFlag;
	}
	
	
	/**
	 * ラジオボタンのアクションの検出を設定する
	 * @param actionListener ActionListener
	 */
	public void addRadioButtonListener(ActionListener actionListener) {
		viewRotateButton.addActionListener(actionListener);
		viewScaleButton.addActionListener(actionListener);
		viewShiftButton.addActionListener(actionListener);
		viewFixButton.addActionListener(actionListener);
		cursorSensorButton.addActionListener(actionListener);
	}

	/**
	 * ボタンのアクションの検出を設定する
	 * @param actionListener ActionListener
	 */
	public void addButtonListener(ActionListener actionListener) {
		treeFileOpenButton.addActionListener(actionListener);
		viewResetButton.addActionListener(actionListener);
	}
	
	/**
	 * ボタンのアクションを検知するActionListener
	 * @author itot
	 */
	class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JButton buttonPushed = (JButton) e.getSource();
			if (buttonPushed == treeFileOpenButton) {
				Tree tree = fileOpener.readTreeFile();
				canvas.setTree(tree);
				canvas.display();
				
			}
			if (buttonPushed == viewResetButton) {				
				canvas.viewReset();
				canvas.display();
			}
		}
	}

	/**
	 * ラジオボタンのアクションを検知するActionListener
	 * @author itot
	 */
	class RadioButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JRadioButton buttonPushed = (JRadioButton) e.getSource();
			if (buttonPushed == viewRotateButton) {
				canvas.setDragMode(3);
			}
			if (buttonPushed == viewScaleButton) {
				canvas.setDragMode(1);
			}
			if (buttonPushed == viewShiftButton) {
				canvas.setDragMode(2);
			}
			if (buttonPushed == viewFixButton) {
				canvas.setDragMode(0);
			}
			if (buttonPushed == cursorSensorButton) {
				cursorSensorFlag = (cursorSensorFlag == true) ? false : true;
			}
		}
	}

}
