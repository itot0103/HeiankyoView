package org.leafdetector.core.window;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;


public class ListMenuBar extends JMenuBar {

	ListDialog diag=null;
	TreeListDialog treeDiag=null;
	
	public ListMenuBar() {
		super();
		// TODO 自動生成さ?たコンストラクター・スタブ
		buildMenu();
		addMenuItemListener(ml);
	}

	private MenuItemListener ml=new MenuItemListener();
	
	public JMenu menuFile=new JMenu("File");
	public JMenuItem openMenuItem=new JMenuItem("Open");
	public JMenuItem saveMenuItem=new JMenuItem("Save");
	public JMenuItem exitMenuItem=new JMenuItem("Exit");

	public void buildMenu(){
		add(menuFile);
		menuFile.add(openMenuItem);
		menuFile.add(saveMenuItem);
		menuFile.add(exitMenuItem);
	}
	
	public void setFrame(ListDialog diag){
		this.diag=diag;
	}
	
	public void setFrame(TreeListDialog diag){
		this.treeDiag=diag;
	}

	
	private void addMenuItemListener(ActionListener a){
		openMenuItem.addActionListener(a);
		saveMenuItem.addActionListener(a);
		exitMenuItem.addActionListener(a);
	}
	
	class MenuItemListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			JMenuItem item=(JMenuItem)e.getSource();
			if(item==openMenuItem) {
				treeDiag.createTreeNodes();
				treeDiag.readData();
			}
			if(item==saveMenuItem) {
				treeDiag.saveData();
			}
			if(item==exitMenuItem) {
				System.exit(0);
			}
		}
	}

}
