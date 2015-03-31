package org.heiankyoview2.core.glwindow;

import org.heiankyoview2.core.tree.*;
import org.heiankyoview2.core.table.Table;
import org.heiankyoview2.core.table.TreeTable;
import org.heiankyoview2.core.table.NodeTablePointer;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

/**
 * Nodeに紐つけられた属性値を表示するパネル
 * @author itot
 */
public class NodeValuePanel extends JDialog {

	/* var */
	public Container container;
	public JButton okButton;
	public JRadioButton nameButtons[], colorButtons[], heightButtons[];

	int numTable = 0, nameType;
	String tableNames[];
	Label tablenameLabel[] = null;
	JTextField valueField[] = null, pathField;

	Font font = null;
	Tree tree;
	TreeTable tg;

	/* Action listener */
	ButtonListener bl = null;

	/**
	 * Constructor
	 * @param tree Tree
	 */
	public NodeValuePanel(Tree tree) {

		// super class init
		super();

		if (tree == null) return;
		tg = (TreeTable) tree.table;

		int i;

		// allocate buttons for tables
		nameType = tg.getNameType();
		numTable = tg.getNumTable();
		tablenameLabel = new Label[numTable];
		valueField = new JTextField[numTable];
		pathField = new JTextField("", 30);

		//	属性の名前の一覧をつくる
		for (i = 1; i <= numTable; i++) {
			Table table = tg.getTable(i);
			tablenameLabel[i - 1] = new Label(table.getName());
			valueField[i - 1] = new JTextField("", 10);
		}

		// this setup
		setTitle("Node Value Dialog Window");
		setSize(400, (numTable * 30 + 100));
		makeWindowCloseCheckBox();
		font = new Font("Serif", Font.ITALIC, 16);

		container = getContentPane();
		container.setLayout(new BorderLayout());

		//
		// Panel for names
		//
		Panel p1 = new Panel();
		p1.setLayout(new GridLayout(numTable, 1));
		for (i = 0; i < numTable; i++) {
			Panel p2 = new Panel();
			p2.setLayout(new GridLayout(1, 2));
			p2.add(tablenameLabel[i]);
			p2.add(valueField[i]);
			p1.add(p2);
		}

		Panel p3 = new Panel();
		p3.setLayout(new GridLayout(2, 1));
		p3.add(new Label("Path"));
		p3.add(pathField);
		
		Panel panel = new Panel();
		panel.setLayout(new FlowLayout());
		panel.add(p1);
		panel.add(p3);

		Panel southPanel = new Panel();
		okButton = new JButton("Ok");
		southPanel.add(okButton);

		container.add(panel, "Center");
		container.add(southPanel, "South");
		setVisible(true);

		if (bl == null)
			bl = new ButtonListener();
		addButtonListener(bl);

		this.tree = tree;
	}

	/**
	 * Nodeをセットする
	 * @param node Node
	 */
	public void setNode(Node node) {
		if(node == null) return;

		Vector pathlist = new Vector();
		Node nnode = node;
		String pathname = "";
		for(;;) {
			if(nnode == tree.getRootNode()) break;
			Branch nbranch = nnode.getCurrentBranch();
			if(nbranch == tree.getRootBranch()) break;
			String name = nbranch.getName();
			if(name == null || name.length() <= 0) {
				name = tg.getNodeAttributeName(nnode, nameType);
			}
			pathlist.add(name);
			nnode = nbranch.getParentNode();
		}
		for(int i = pathlist.size() - 1; i >= 0; i--) {
			String name = (String)pathlist.elementAt(i);
			pathname += (name + " ");
		}
		pathField.setText(pathname);
		
		for (int i = 1; i <= numTable; i++) {
			if (node == null || node.getChildBranch() != null) {
				valueField[i - 1].setText("");
				continue;
			}

			Table table = tg.getTable(i);
			int tabletype = table.getType();
			NodeTablePointer tn = node.table;
			int id = tn.getId(i);
			String value = "";
			if (tabletype == table.TABLE_STRING) { /* STRING */
				value = table.getString(id);
			}
			if (tabletype == table.TABLE_DOUBLE) { /* DOUBLE */
				value = Double.toString(table.getDouble(id));
			}
			if (tabletype == table.TABLE_INT) { /* INT */
				value = Integer.toString(table.getInt(id));
			}
			valueField[i - 1].setText(value);
		}
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
		okButton.addActionListener(actionListener);

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
			}
		}
	}

}
