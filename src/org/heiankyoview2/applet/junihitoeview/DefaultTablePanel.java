
package org.heiankyoview2.applet.junihitoeview;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.heiankyoview2.core.table.Table;
import org.heiankyoview2.core.table.TreeTable;
import org.heiankyoview2.core.tree.Tree;
import org.heiankyoview2.core.window.TablePanel;


public class DefaultTablePanel extends JDialog implements TablePanel {

	Tree tree = null;
	DefaultCanvas canvas = null;
	TreeTable tg = null;
	int numTable;
	
	JCheckBox checkBoxes[], isLod;
	String tableNames[];
	TableColorPanel  colorPanels[];
	JButton okButton;
	JRadioButton painterButtons[] = new JRadioButton[2];
	int paintType = 1;
	
	Font font;
	public Container container;
	
	/* Action listener */
	ButtonListener bl = null;
	RadioButtonListener rbl = null;
	CheckBoxListener cbl = null;
	
	
	/**
	 * Constructor
	 * @param tree Tree 
	 */
	public DefaultTablePanel(Tree tree) {
		// super class init
		super();
		if (tree == null) return;
		tg = tree.table;

		
		// allocate buttons for tables
		numTable = tg.getNumTable();

		// this setup
		setTitle("Table attribute panel");
		setSize(250, (numTable * 30 + 200));
		makeWindowCloseCheckBox();
		font = new Font("Serif", Font.ITALIC, 16);

		container = getContentPane();
		container.setLayout(new BorderLayout());

		//
		// Panel for colors
		//
		Panel p = new Panel();
		p.setLayout(new GridLayout((numTable + 3), 1));

		checkBoxes = new JCheckBox[numTable];
		colorPanels = new TableColorPanel[numTable];
		tableNames = new String[numTable];
		tableNames[0] = "None";
		for (int i = 0; i < numTable; i++) {
			Table table = tg.getTable(i + 1);
			tableNames[i] = table.getName();
			checkBoxes[i] = new JCheckBox(tableNames[i]);
			checkBoxes[i].setSelected(true);
			colorPanels[i] = new TableColorPanel();
			colorPanels[i].setSize(30,80);
			colorPanels[i].setBackground(Color.black);
			
			Panel pp = new Panel();
			pp.setLayout(new GridLayout(1, 2));
			pp.add(checkBoxes[i]);
			pp.add(colorPanels[i]);
			p.add(pp);
		}
		
		ButtonGroup group = new ButtonGroup();
		for(int i = 0; i < 2; i++) {
			painterButtons[i] = new JRadioButton("Painter " + Integer.toString(i + 1));
			p.add(painterButtons[i]);
			group.add(painterButtons[i]);
		}
			
		isLod = new JCheckBox("LOD");
		isLod.setSelected(false);
		p.add(isLod);
		
		JScrollPane scroll = new JScrollPane(p);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		Panel southPanel = new Panel();
		okButton = new JButton("Ok");

		southPanel.add(okButton);

		container.add(scroll, "Center");
		container.add(southPanel, "South");
		setVisible(true);

		if (bl == null)
			bl = new ButtonListener();
		addButtonListener(bl);

		if (rbl == null)
			rbl = new RadioButtonListener();
		addRadioButtonListener(rbl);
		
		if (cbl == null)
			cbl = new CheckBoxListener();
		addCheckBoxListener(cbl);

		this.tree = tree;
	}
	
	/**
	 * Treeをセットする
	 * @param tree Tree
	 */
	public void setTree(Tree tree) {
		this.tree = tree;
		canvas.display();
	}

	/**
	 * 選択されたテーブルの配列をCanvasにセットする
	 */
	void updateTargetTable() {
		int count = 0;
		for(int i = 0; i < numTable; i++) {
			if(checkBoxes[i].isSelected() == true) count++;
		}
		
		int targetTables[] = new int[count];		
		if(paintType == 1) updateColors1(targetTables, count);
		if(paintType == 2) updateColors2(targetTables, count);
		canvas.setTargetTables(targetTables);

	}

	
	/**
	 * パネルのカラーテーブルを更新する(1)
	 */
	void updateColors1(int targetTables[], int count) {
		Color color[] = new Color[2];
		int n = 0;
		
		for(int i = 0; i < numTable; i++) {
			Graphics2D g2 = (Graphics2D)colorPanels[i].getGraphics();
			colorPanels[i].paintComponents(g2);
			if(checkBoxes[i].isSelected() == false) {
				color[0] = color[1] = Color.black;
			}
			else {
				color[0] = Color.getHSBColor(((float)n / (float)count), 1.0f, 1.0f);
				color[1] = Color.getHSBColor(((float)n / (float)count), 0.5f, 0.5f);
				targetTables[n++] = i;
			}
			colorPanels[i].draw(color);
		
		}
	}
	
	
	/**
	 * パネルのカラーテーブルを更新する(1)
	 */
	void updateColors2(int targetTables[], int count) {
		Color color[] = new Color[4];
		int n = 0;
		
		for(int i = 0; i < numTable; i++) {
			Graphics2D g2 = (Graphics2D)colorPanels[i].getGraphics();
			colorPanels[i].paintComponents(g2);
			if(checkBoxes[i].isSelected() == false) {
				color[0] = color[1] = color[2] = color[3] = Color.black;
			}
			else {
				color[0] = Color.getHSBColor(((float)n / (float)count), 0.2f, 0.2f);
				color[1] = Color.getHSBColor(((float)n / (float)count), 0.5f, 0.5f);
				color[2] = Color.getHSBColor(((float)n / (float)count), 0.8f, 0.8f);
				color[3] = Color.getHSBColor(((float)n / (float)count), 1.0f, 1.0f);
				targetTables[n++] = i;
			}
			colorPanels[i].draw(color);
		
		}
	}
	
	/**
	 * Canvasをセットする
	 * @param c Canvas
	 */
	public void setCanvas(Object c) {
		canvas = (DefaultCanvas) c;
		updateTargetTable();
		canvas.display();
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
	 * チェックボックスのアクションの検出を設定する
	 * @param actionListener ActionListener
	 */
	public void addCheckBoxListener(CheckBoxListener checkBoxListener) {
		for (int i = 0; i < numTable; i++) {
			checkBoxes[i].addItemListener(checkBoxListener);
		}
		isLod.addItemListener(checkBoxListener);
	}

	/**
	 * ボタンのアクションの検出を設定する
	 * @param actionListener ActionListener
	 */
	public void addButtonListener(ActionListener actionListener) {
		okButton.addActionListener(actionListener);
	}
	
	
	/**
	 * ラジオボタンのアクションの検出を設定する
	 * @param actionListener ActionListener
	 */
	public void addRadioButtonListener(ActionListener actionListener) {
		for (int i = 0; i < 2; i++) {
			painterButtons[i].addActionListener(actionListener);
		}
	}
	
	/**
	 * ボタンのアクションを検知するActionListener
	 * @author itot
	 */
	class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JButton buttonPushed = (JButton) e.getSource();
			if (buttonPushed == okButton) {
				setVisible(false);
				canvas.display();
			}
		}
	}

	/**
	 * チェックボックスのアクションを検知するItemListener
	 * @author itot
	 */
	class CheckBoxListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			JCheckBox stateChanged = (JCheckBox) e.getSource();
			
			if(stateChanged == isLod) {
				if(isLod.isSelected() == true) canvas.setLod(true);
				if(isLod.isSelected() == false) canvas.setLod(false);
				return;
			}
				
			updateTargetTable();
			canvas.display();
		}
	}
	
	
	/**
	 * ラジオボタンのアクションを検知するActionListener
	 * @author itot
	 */
	class RadioButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JRadioButton buttonPushed = (JRadioButton) e.getSource();

			for (int i = 0; i <= 2; i++) {

				if (buttonPushed == painterButtons[i]) {
					paintType = i + 1;
					canvas.setPaintType(paintType);
					for (int j = 0; j < numTable; j++) {
						colorPanels[j].setPaintType(paintType);
						updateTargetTable();
					}
					break;
				}
			}

			canvas.display();
		}
	}

}
