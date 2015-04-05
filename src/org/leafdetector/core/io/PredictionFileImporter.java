package org.leafdetector.core.io;

import java.io.File;

import org.heiankyoview2.applet.junihitoeview.GlDefaultCanvas;
import org.heiankyoview2.core.tree.Branch;
import org.leafdetector.core.data.PredictionData;
import org.leafdetector.core.tree.MyMutableTreeNode;
import org.leafdetector.core.tree.NodeIdentifier;

public class PredictionFileImporter extends FileOpener {

	String delimiter="\t";
	MyMutableTreeNode root;
	
	PredictionData database=PredictionData.getInstance();

	public void readFile() {
		// TODO 
		File inputFile=getFile();
		if(inputFile==null) return;
		database.clearData();
		
		FileInput input=new FileInput(inputFile);

		String lineBuffer;

		if(input.ready() && (lineBuffer=input.read())!=null){
			String[] buf=lineBuffer.split(delimiter);
			database.setFieldName(buf);
		}
		
		while(input.ready() && (lineBuffer=input.read())!=null){
			String[] buf=lineBuffer.split(delimiter);
			database.setData(buf);
		}
	}
	
	public void identifyNodeFromFile(GlDefaultCanvas canvas){

		File inputFile=getFile();
		if(inputFile==null) return;
		database.clearData();
		
		FileInput input=new FileInput(inputFile);

		String lineBuffer;

		if(input.ready() && (lineBuffer=input.read())!=null){
			String[] buf=lineBuffer.split(delimiter);
			database.setFieldName(buf);
		}
		
		NodeIdentifier nodeIdentifier=new NodeIdentifier();
		nodeIdentifier.create(canvas);
		while(input.ready() && (lineBuffer=input.read())!=null){
			String[] buf=lineBuffer.split(delimiter);
			Branch branch=nodeIdentifier.identifyBranch(buf);
			MyMutableTreeNode currentLeaf=(MyMutableTreeNode)root.getFirstLeaf();
			do{
				if(currentLeaf.getBranch().getId()==branch.getId()){
					currentLeaf.addTerminalData(buf[0]);
					break;
				}
			}while((currentLeaf=(MyMutableTreeNode)currentLeaf.getNextLeaf())!=null);
		}
		
	}
	
	public void setMutableTreeRoot(MyMutableTreeNode root){
		this.root=root;
	}

	/* 
	 * @see org.leafdetector.core.io.FileOpener#saveFile()
	 */
	@Override
	public void saveFile() {
		// TODO
		
	}
}
