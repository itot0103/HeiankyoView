package org.heiankyoview2.core.window;

import org.heiankyoview2.core.tree.Tree;
import org.heiankyoview2.core.fileio.AvsFldFileWriter;
import org.heiankyoview2.core.fileio.AvsMgfFileWriter;
import org.heiankyoview2.core.table.Table;
import org.heiankyoview2.core.table.TreeTable;
import org.heiankyoview2.core.draw.Canvas;
import org.heiankyoview2.core.util.PackingWithTableAttribute;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

import javax.swing.*;

/**
 * Tableに記述された属性を用いて表示属性（名前、色、高さ）を設定するためのパネル
 * @author itot
 */
public class TableAttributePanel extends JDialog {

	/* var */
	public Container container;
	public JButton okButton, cancelButton, applyButton;
	public JRadioButton nameButtons[],
		colorButtons[],
		heightButtons[],
		xaxisButtons[],
		yaxisButtons[];

	int numTable = 0;
	String tableNames[];

	Font font = null;
	Tree tree;
	TreeTable tg;

	/* parameter */
	int nameType = -1,
		colorType = -1,
		heightType = -1,
		xaxisType = -1,
		yaxisType = -1;

	/* Selective canvas */
	Canvas canvas;

	/* Action listener */
	ButtonListener bl = null;
	RadioButtonListener rbl = null;
	

	/**
	 * Constructor
	 * @param tree Tree
	 */
	public TableAttributePanel(Tree tree) {

		// super class init
		super();

		if (tree == null) return;
		tg = tree.table;

		int i;

		// allocate buttons for tables
		numTable = tg.getNumTable();

		nameButtons = new JRadioButton[numTable + 1];
		colorButtons = new JRadioButton[numTable + 1];
		heightButtons = new JRadioButton[numTable + 1];
		xaxisButtons = new JRadioButton[numTable + 1];
		yaxisButtons = new JRadioButton[numTable + 1];
		tableNames = new String[numTable + 1];
		tableNames[0] = "None";
		for (i = 1; i <= numTable; i++) {
			Table table = tg.getTable(i);
			tableNames[i] = table.getName();
		}

		// this setup
		setTitle("Table attribute panel");
		setSize(650, (numTable * 30 + 150));
		makeWindowCloseCheckBox();
		font = new Font("Serif", Font.ITALIC, 16);

		container = getContentPane();
		container.setLayout(new BorderLayout());

		//
		// Panel for names
		//
		Panel p1 = new Panel();
		p1.setLayout(new GridLayout((numTable + 2), 1));
		Label l1 = new Label("Name");
		p1.add(l1);
		ButtonGroup group1 = new ButtonGroup();

		for (i = 0; i <= numTable; i++) {
			nameButtons[i] = new JRadioButton(tableNames[i]);
			group1.add(nameButtons[i]);
			p1.add(nameButtons[i]);
		}

		//
		// Panel for colors
		//
		Panel p2 = new Panel();
		p2.setLayout(new GridLayout((numTable + 2), 1));
		Label l2 = new Label("Color");
		p2.add(l2);
		ButtonGroup group2 = new ButtonGroup();

		for (i = 0; i <= numTable; i++) {
			colorButtons[i] = new JRadioButton(tableNames[i]);
			group2.add(colorButtons[i]);
			p2.add(colorButtons[i]);
		}

		//
		// Panel for heights
		//
		Panel p3 = new Panel();
		p3.setLayout(new GridLayout((numTable + 2), 1));
		Label l3 = new Label("Height");
		p3.add(l3);
		ButtonGroup group3 = new ButtonGroup();

		for (i = 0; i <= numTable; i++) {
			heightButtons[i] = new JRadioButton(tableNames[i]);
			group3.add(heightButtons[i]);
			p3.add(heightButtons[i]);
		}

		//
		// Panel for x-axis
		//
		Panel p4 = new Panel();
		p4.setLayout(new GridLayout((numTable + 2), 1));
		Label l4 = new Label("X-axis");
		p4.add(l4);
		ButtonGroup group4 = new ButtonGroup();

		for (i = 0; i <= numTable; i++) {
			xaxisButtons[i] = new JRadioButton(tableNames[i]);
			group4.add(xaxisButtons[i]);
			p4.add(xaxisButtons[i]);
		}

		//
		// Panel for Y-axis
		//
		Panel p5 = new Panel();
		p5.setLayout(new GridLayout((numTable + 2), 1));
		Label l5 = new Label("Y-axis");
		p5.add(l5);
		ButtonGroup group5 = new ButtonGroup();

		for (i = 0; i <= numTable; i++) {
			yaxisButtons[i] = new JRadioButton(tableNames[i]);
			group5.add(yaxisButtons[i]);
			p5.add(yaxisButtons[i]);
		}

		Panel panel = new Panel();
		panel.setLayout(new FlowLayout());
		panel.add(p1);
		panel.add(p2);
		panel.add(p3);
		panel.add(p4);
		panel.add(p5);

		Panel southPanel = new Panel();
		okButton = new JButton("Ok");
		cancelButton = new JButton("Cancel");
		applyButton = new JButton("Apply");

		southPanel.add(okButton);
		southPanel.add(cancelButton);
		southPanel.add(applyButton);

		container.add(panel, "Center");
		container.add(southPanel, "South");
		setVisible(true);

		if (bl == null)
			bl = new ButtonListener();
		addButtonListener(bl);

		if (rbl == null)
			rbl = new RadioButtonListener();
		addRadioButtonListener(rbl);

		this.tree = tree;
	}

	/**
	 * Treeをセットする
	 * @param tree Tree
	 */
	public void setTree(Tree tree) {
		this.tree = tree;

		if (nameType >= 0 && nameType < numTable)
			tg.setNameType(nameType);
		if (colorType >= 0 && colorType < numTable)
			tg.setColorType(colorType);
		if (heightType >= 0 && heightType < numTable)
			tg.setHeightType(heightType);

		canvas.display();
	}

	/**
	 * Canvasをセットする
	 * @param c Canvas
	 */
	public void setCanvas(Object c) {
		canvas = (Canvas) c;
	}

	public void actionPerformed(ActionEvent evt) { // override
		;
	}

	protected void makeWindowCloseCheckBox() {
		addWindowListener(new WindowAdapter() { // inner class
			public void windowClosing(WindowEvent e) {
				setVisible(false);
			}
		});
	}

	/**
	 * ラジオボタンのアクションの検出を設定する
	 * @param actionListener ActionListener
	 */
	public void addRadioButtonListener(ActionListener actionListener) {
		for (int i = 0; i <= numTable; i++) {
			nameButtons[i].addActionListener(actionListener);
			colorButtons[i].addActionListener(actionListener);
			heightButtons[i].addActionListener(actionListener);
			xaxisButtons[i].addActionListener(actionListener);
			yaxisButtons[i].addActionListener(actionListener);
		}
	}

	/**
	 * ボタンのアクションの検出を設定する
	 * @param actionListener ActionListener
	 */
	public void addButtonListener(ActionListener actionListener) {
		okButton.addActionListener(actionListener);
		cancelButton.addActionListener(actionListener);
		applyButton.addActionListener(actionListener);
	}

	/**
	 * ボタンのアクションを検知するActionListener
	 * @author itot
	 */
	class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JButton buttonPushed = (JButton) e.getSource();
			if (buttonPushed == okButton) {
				if (xaxisType >= 0 && yaxisType >= 0) {
					tg.setNameType(0);
					PackingWithTableAttribute.packingWithTableAttribute(tree, xaxisType, yaxisType);
					canvas.setTree(tree);
				}
				setVisible(false);
				canvas.display();
			}
			if (buttonPushed == cancelButton) {
				setVisible(false);
			}
			if (buttonPushed == applyButton) {
				if (xaxisType >= 0 && yaxisType >= 0) {
					tg.setNameType(0);
					PackingWithTableAttribute.packingWithTableAttribute(tree, xaxisType, yaxisType);
					canvas.setTree(tree);
				}
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

			for (int i = 0; i <= numTable; i++) {
				if (buttonPushed == nameButtons[i]) {
					nameType = i - 1;
					tg.setNameType(nameType);
					canvas.setBranchAnnotations();
					break;
				}
				if (buttonPushed == colorButtons[i]) {
					colorType = i - 1;
					tg.setColorType(colorType);
					break;
				}
				if (buttonPushed == heightButtons[i]) {
					heightType = i - 1;
					tg.setHeightType(heightType);
					break;
				}
				if (buttonPushed == xaxisButtons[i]) {
					xaxisType = i - 1;
					break;
				}
				if (buttonPushed == yaxisButtons[i]) {
					yaxisType = i - 1;
					break;
				}
			}

			AvsFldFileWriter output1 = new AvsFldFileWriter(new File("tmp.fld"), tree);
			output1.writeData();
			
			AvsMgfFileWriter output2 = new AvsMgfFileWriter(new File("tmp.mfg"), tree);
			output2.writeData();

			canvas.display();
		}
	}

}
