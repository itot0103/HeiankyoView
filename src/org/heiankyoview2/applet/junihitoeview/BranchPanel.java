package org.heiankyoview2.applet.junihitoeview;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.Panel;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import java.util.Vector;

import org.heiankyoview2.core.tree.*;
import org.heiankyoview2.core.table.TreeTable;

/**
 * IdsView ��ŕ\�������m�[�h���N���b�N�����ۂɁA
 * ���̃m�[�h�Ɋւ��ڍ׏���\������p�l�����N������
 * 
 * @author itot
 */
public class BranchPanel extends JDialog {

	/* var */
	public JTextField logText[], numlogText;
	Font font;
	public Container container;
	public JButton okButton;
	
	Tree tree;
	TreeTable tg;

	/**
	 * Constructor
	 * @param 
	 */
	public BranchPanel(Tree tree, Branch branch) {

		// super class init
		super();

		this.tree = tree;
		this.tg = tree.table;
		Vector nodelist = new Vector();
		getLeafNodeList(tree, branch, nodelist);
		
		// this setup
		setResizable(true);
		setTitle("SAR branch panel");
		setSize(300, 100 + nodelist.size() * 20);
		makeWindowCloseCheckBox();
		font = new Font("Serif", Font.ITALIC, 16);

		container = getContentPane();
		container.setLayout(new BorderLayout());

		//
		// Panel for number of logs
		//
		Panel northPanel = generateNorthPanel(nodelist.size());

		//
		// Panel for node parameters
		//
		Panel centerPanel = generateCenterPanel(nodelist);
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.add(centerPanel);

		//
		// Panel for buttons
		//
		Panel southPanel = generateSouthPanel();
		container.add(northPanel, "North");
		container.add(scrollPane, "Center");
		container.add(southPanel, "South");

		ButtonListener bl = null;
		if (bl == null)
			bl = new ButtonListener();
		addButtonListener(bl);

	}

	/**
	 * �p�l���̃^�C�g���Ȃǂ�ݒu����
	 */
	Panel generateNorthPanel(int numlog) {

		Panel northPanel = new Panel();
		northPanel.setLayout(new GridLayout(1, 2));

		numlogText = new JTextField();
		numlogText.setText(Integer.toString(numlog));
		northPanel.add(numlogText);

		return northPanel;
	}

	/**
	 * �p�l���������Ɋe�������̖��O�����X�g�A�b�v����
	 */
	Panel generateCenterPanel(Vector nodelist) {

		Panel c = new Panel();
		c.setLayout(new GridLayout(nodelist.size(), 1));

		logText = new JTextField[nodelist.size()];
		for (int i = 0; i < nodelist.size(); i++) {
			Node node = (Node)nodelist.elementAt(i);
			String name = tg.getNodeAttributeName(node, 1);
			logText[i] = new JTextField();
			logText[i].setText(name);
			c.add(logText[i]);
		}

		/*
		JScrollPane scroll = new JScrollPane(c);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		Panel centerPanel = new Panel();
		centerPanel.add(scroll);
		*/
		return c;
	}

	/**
	 * �p�l��������OK�{�^����z�u����
	 */
	Panel generateSouthPanel() {
		Panel southPanel = new Panel();
		okButton = new JButton("OK");
		southPanel.add(okButton);

		return southPanel;
	}
	
	/**
	 * ���YBranch�̉��ɂ���t�m�[�h�i���������m�[�h�j�����X�g�ɒǉ�����
	 */
	void getLeafNodeList(Tree tree, Branch branch, Vector nodelist) {

		/*
		 * �m�[�h�̒ǉ�
		 */
		for(int i = 0; i < branch.getNodeList().size(); i++) {
			Node node = (Node)branch.getNodeList().elementAt(i);
			if(node.getChildBranch() == null) nodelist.add(node);
		}
		
		/*
		 * �q�O���[�v�̒ǐ�
		 */
		for(int i = 0; i < branch.getNodeList().size(); i++) {
			Node node = (Node)branch.getNodeList().elementAt(i);
			if(node.getChildBranch() != null) 
				getLeafNodeList(tree, node.getChildBranch(), nodelist);
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

	public void addButtonListener(ActionListener actionListener) {
		okButton.addActionListener(actionListener);
	}

	/**
	 * �{�^���̃A�N�V���������m����ActionListener
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
