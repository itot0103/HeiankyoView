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

public class DefaultViewingPanel extends JPanel implements ViewingPanel {

	public JButton  treeFileOpenButton, frameFileOpenButton,
		    viewResetButton, viewDefaultButton, imageSaveButton,
		    appearancePanelButton, tableAttributeButton, 
		    logButton;	//	他人のメモが見られるボタン
	public JRadioButton viewRotateButton, viewScaleButton, viewShiftButton, viewFixButton,
			dcPanelButton, dcBrowserButton, cursorSensorButton;
	public Container container;
	
	/* Selective canvas */
	Canvas canvas;
	FileOpener fileOpener;
	AppearancePanel appearancePanel = null;
	TablePanel tablePanel = null;
	NodeValuePanel nodeValuePanel = null;

	
	/* Double Click Attribute */
	int doubleClickFlag = this.DOUBLE_CLICK_PANEL;
	public final int DOUBLE_CLICK_PANEL = 1;
	public final int DOUBLE_CLICK_BROWSER = 2;
	
	/* Cursor Sensor */
	boolean cursorSensorFlag = false;
	
	/* Action listener */
	ButtonListener bl = null;
	RadioButtonListener rbl = null;

	public DefaultViewingPanel() {
		// super class init
		super();

		setSize(200, 800);

		
		//
		// ファイル入力および視点変更操作のパネル
		// （起動したとき右に出るパネルの設定）
		//
		JPanel p1 = new JPanel();
		p1.setLayout(new GridLayout(7,1));
		treeFileOpenButton = new JButton("Tree File Open");
		frameFileOpenButton = new JButton("Frame File Open");
		imageSaveButton = new JButton("Image Output");
		viewDefaultButton = new JButton("View Default");
		viewResetButton = new JButton("View Reset");
		appearancePanelButton = new JButton("Appearance");
		tableAttributeButton = new JButton("Table Attribute");
		logButton = new JButton("Log");	//	他人のログとか見れるボタン
		p1.add(treeFileOpenButton);
		p1.add(frameFileOpenButton);
		p1.add(imageSaveButton);
		p1.add(viewDefaultButton);
		p1.add(viewResetButton);
		p1.add(appearancePanelButton);
		p1.add(tableAttributeButton);
		p1.add(logButton);
	
		JPanel p2 = new JPanel();
		p2.setLayout(new GridLayout(4,1));
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
		
		JPanel p3 = new JPanel();
		p3.setLayout(new GridLayout(2,1));
		ButtonGroup group2 = new ButtonGroup();
		dcPanelButton = new JRadioButton("Panel by Double Click");
		group2.add(dcPanelButton);
		p3.add(dcPanelButton);
		dcBrowserButton = new JRadioButton("Browser by Double Click");
		group2.add(dcBrowserButton);
		p3.add(dcBrowserButton);
		
		JPanel p4 = new JPanel();
		p4.setLayout(new GridLayout(1,1));
		cursorSensorButton = new JRadioButton("Cursor Sensor");
		p4.add(cursorSensorButton);
		
		//
		// パネル群のレイアウト
		//
		this.setLayout(new GridLayout(4,1));
		this.add(p1);
		this.add(p2);
		this.add(p3);
		this.add(p4);
	
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
		return doubleClickFlag;
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
		dcPanelButton.addActionListener(actionListener);
		dcBrowserButton.addActionListener(actionListener);
		cursorSensorButton.addActionListener(actionListener);
	}

	/**
	 * ボタンのアクションの検出を設定する
	 * @param actionListener ActionListener
	 */
	public void addButtonListener(ActionListener actionListener) {
		treeFileOpenButton.addActionListener(actionListener);
		frameFileOpenButton.addActionListener(actionListener);
		imageSaveButton.addActionListener(actionListener);
		viewDefaultButton.addActionListener(actionListener);
		viewResetButton.addActionListener(actionListener);
		appearancePanelButton.addActionListener(actionListener);
		tableAttributeButton.addActionListener(actionListener);
		logButton.addActionListener(actionListener);	//logボタンもアクションリスナーつけないと。
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
			if (buttonPushed == frameFileOpenButton) {
				fileOpener.readFrameFile();
				canvas.display();
				
			}
			if (buttonPushed == imageSaveButton) {
				fileOpener.saveImageFile();
			}
			if (buttonPushed == viewDefaultButton) {
			}
			if (buttonPushed == viewResetButton) {				
				canvas.viewReset();
				canvas.display();
			}
			if (buttonPushed == appearancePanelButton) {
				if (appearancePanel == null)
					appearancePanel = new AppearancePanel();
				appearancePanel.setVisible(true);
				appearancePanel.setCanvas(canvas);
				appearancePanel.setCanvas(canvas);
			}
			if (buttonPushed == tableAttributeButton) {
				if (tablePanel == null)
					tablePanel = fileOpener.getTablePanel();
					if(tablePanel != null)
						tablePanel.setVisible(true);
			}
			if (buttonPushed == logButton){	//	もしログボタンが押されたらログファイルを開く
				TextFileOpenrer textfileopenrer = new TextFileOpenrer();
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
			if (buttonPushed == dcPanelButton) {
				//radioボタンのPanelbyDoubleClickが押されてたらこっち
				doubleClickFlag = DOUBLE_CLICK_PANEL;	
			}
			if (buttonPushed == dcBrowserButton) {
				//radioボタンのBrowserbyDoubleClickが押されてたらこっち
				doubleClickFlag = DOUBLE_CLICK_BROWSER;
			}
			if (buttonPushed == cursorSensorButton) {
				cursorSensorFlag = (cursorSensorFlag == true) ? false : true;
			}
		}
	}

}
