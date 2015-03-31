package org.heiankyoview2.core.window;

import org.heiankyoview2.core.tree.Tree;
import org.heiankyoview2.core.table.Table;
import org.heiankyoview2.core.table.TreeTable;
import org.heiankyoview2.core.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


/**
 * カラーパレットを表示するパネル
 * @author itot
 */
public class ColorPalettePanel extends JDialog {

	/* var */
	public Container container;
	public JButton closeButton;

	Font font = null;
	Tree tree;
	Table table;
	int colorType, tableSize;

	/* Action listener */
	ButtonListener bl = null;

	/**
	 * Constructor
	 * @param tree Tree
	 */
	public ColorPalettePanel(Tree tree, ColorCalculator cc) {
		// super class init
		super();

		TreeTable tg = tree.table;

		if (tree == null) {
			setVisible(false);
			return;
		}
		this.tree = tree;
		colorType = tg.getColorType();
		if (colorType < 0) {
			setVisible(false);
			return;
		}
		table = tg.getTable(colorType + 1);
		tableSize = table.getSize();

		// this setup
		setTitle("Color Palette");
		setSize(100, (tableSize * 20 + 50));
		makeWindowCloseCheckBox();
		font = new Font("Serif", Font.ITALIC, 10);

		container = getContentPane();
		container.setLayout(new BorderLayout());

		JPanel panel = new JPanel();
		panel.setSize(100, (tableSize * 20));
		panel.setLayout(new GridLayout(tableSize, 1));
		//for(int i = 1; i <= tableSize; i++) {
		for (int i = 1; i <= tableSize; i++) {
			Panel pp = new Panel();
			pp.setLayout(new GridLayout(1, 2));
			pp.setSize(100, 10);
			panel.add(pp);

			Panel ppp = new Panel();
			double cv = table.getAppearanceValue(i - 1);
			Color color = cc.calculate((float) cv);
			ppp.setSize(50, 15);
			ppp.setBackground(color);
			pp.add(ppp);

			String l = "";
			if (table.getType() == 1)
				l = table.getString(i);
			if (table.getType() == 2)
				l = Double.toString(table.getDouble(i));
			if (table.getType() == 3)
				l = Integer.toString(table.getInt(i));
			Label lll = new Label(l);
			pp.add(lll);
		}

		Panel southPanel = new Panel();
		closeButton = new JButton("Close");
		southPanel.add(closeButton);

		container.add(panel, "Center");
		container.add(southPanel, "South");
		setVisible(true);

		if (bl == null)
			bl = new ButtonListener();
		addButtonListener(bl);

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
	 * ボタンのアクションの検出を設定する
	 * @param actionListener ActionListener
	 */
	public void addButtonListener(ActionListener actionListener) {
		closeButton.addActionListener(actionListener);
	}


	/**
	 * ボタンのアクションを検知するActionListener
	 * @author itot
	 */
	class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JButton buttonPushed = (JButton) e.getSource();
			if (buttonPushed == closeButton) {
				setVisible(false);
			}
		}
	}

}
