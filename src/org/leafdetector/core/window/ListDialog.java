/*
 * ???: 2006/07/26
 *
 * TODO ???????????????????????????????:
 * ????? - ?? - Java - ???????? - ??????????
 */
package org.leafdetector.core.window;

import java.awt.HeadlessException;

import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.heiankyoview2.applet.junihitoeview.GlDefaultCanvas;
import org.heiankyoview2.core.tree.Branch;
import org.leafdetector.core.data.PredictionData;
import org.leafdetector.core.tree.NodeIdentifier;

/**
 * @author fyamashi
 *
 * TODO ????????????????????????????????:
 * ????? - ?? - Java - ???????? - ??????????
 */
public class ListDialog extends JDialog {

	JList list=new JList();
	JScrollPane scroll=new JScrollPane(list);

	GlDefaultCanvas canvas=null;
	NodeIdentifier nodeIdentifier=new NodeIdentifier(); 
	
	/**
	 * @throws java.awt.HeadlessException
	 */
	public ListDialog(String title) throws HeadlessException {
		super();
		// TODO
		
		setBounds(770,0,150,300);
		
		list.addListSelectionListener(new ListSelectionListener(){

			public void valueChanged(ListSelectionEvent e) {
				// TODO 自動生成さ?たメソッド・スタブ
				int index=list.getSelectedIndex();
				Branch branch=nodeIdentifier.identifyBranch(index);
				canvas.highlightSpecifiedBranch(branch);
				canvas.display();
			}
			
		});
		add(scroll);
		setVisible(true);
	}

	public void setListData(){
		PredictionData database=PredictionData.getInstance();
		int number=database.getSize();
		
		String[] string=new String[number];
		
		for(int i=0;i<number;i++){
			string[i]=database.getDataName(i);
		}
		list.setListData(string);
		
		nodeIdentifier.create(canvas);
		
	}
	
	public void setCanvas(GlDefaultCanvas canvas){
		this.canvas=canvas;
	}
	
}
