
package org.heiankyoview2.applet.junihitoeview;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;

import org.heiankyoview2.core.table.TreeTable;
import org.heiankyoview2.core.gldraw.Canvas;
import org.heiankyoview2.core.tree.Tree;
import org.heiankyoview2.core.tree.Template;
import org.heiankyoview2.core.xmlio.XmlTreeFileReader;
import org.heiankyoview2.core.fileio.TreeFileReader;
import org.heiankyoview2.core.fileio.TemplateFileReader;
import org.heiankyoview2.core.fileio.TemplateFileWriter;
import org.heiankyoview2.core.placement.Packing;
import org.heiankyoview2.core.glwindow.AppearancePanel;
import org.heiankyoview2.core.glwindow.NodeValuePanel;
import org.heiankyoview2.core.glwindow.TablePanel;
import org.heiankyoview2.core.glwindow.FileOpener;
import org.heiankyoview2.datagen.csv2tree.CsvFileReader;


public class GlDefaultFileOpener implements FileOpener {
	File currentDirectory, inputFile, outputFile;
	Component windowContainer;
	Canvas canvas;
	Tree tree;
	Packing packing = new Packing();
	
	// component
	AppearancePanel appearancePanel = null;
	GlDefaultTablePanel tablePanel = null;
	NodeValuePanel nodeValuePanel = null;
	
	/**
	 * Container ���Z�b�g����
	 * @param c Component
	 */
	public void setContainer(Component c) {
		windowContainer = c;
	}
	
	
	/**
	 * Canvas ���Z�b�g����
	 * @param c Canvas
	 */
	public void setCanvas(Canvas c) {
		canvas = c;
	}
	
	/**
	 * Tree ��Ԃ�
	 * @return tree
	 */
	public Tree getTree() {
		return tree;
	}
	
	
	/**
	 * �t�@�C���_�C�A���O�ɃC�x���g���������Ƃ��ɁA�Ή�����t�@�C������肷��
	 * @return �t�@�C��
	 */
	public File getFile() {
		JFileChooser fileChooser = new JFileChooser(currentDirectory);
		int selected = fileChooser.showOpenDialog(windowContainer);
		if (selected == JFileChooser.APPROVE_OPTION) { // open selected
			currentDirectory = fileChooser.getCurrentDirectory();
			return fileChooser.getSelectedFile();
		} else if (selected == JFileChooser.CANCEL_OPTION) { // cancel selected
			return null;
		} 
		
		return null;
	}
	
	
	/*
	 * tree�t�@�C����ǂݍ���
	 */
	public Tree readTreeFile() {
		inputFile = getFile();
		if (inputFile == null) {
			tree = null;  return null;
		} 

		String fileName = inputFile.getName();
		
		if (fileName.endsWith(".tree") == true
				|| fileName.endsWith(".TREE") == true) {

			TreeFileReader input = new TreeFileReader(inputFile);
			if (input == null) return null;
			tree = input.getTree();
		} else if (
			fileName.endsWith(".xml") == true
				|| fileName.endsWith(".XML") == true) {

			XmlTreeFileReader input = new XmlTreeFileReader(inputFile);
			if (input == null)
				return null;
			tree = input.getTree();
		} else if (
			fileName.endsWith(".csv") == true
			|| fileName.endsWith(".CSV") == true) {

			CsvFileReader input = new CsvFileReader(inputFile);
			if (input == null) return null;
			tree = input.getTree();
		}
			
		if (tree == null) return null;
		tree.table.setNameType(0);
		System.out.println(" ... completed to read " + fileName);
		packing.placeNodesAllBranch(tree);
		
		tablePanel = new GlDefaultTablePanel(tree);
		tablePanel.setCanvas(canvas);
		nodeValuePanel = new NodeValuePanel(tree);
		canvas.viewReset();

		return tree;
	}
	
	
	/*
	 * frame�t�@�C����ǂݍ���
	 */
	public void readFrameFile() {
	}
	
	
	/*
	 * �e���v���[�g�t�@�C������������
	 */
	public void writeTemplateFile() {
		if (tree == null) return;
		outputFile = getFile();

		Template template = new Template();
		template.makeTemplateTree(tree);

		TemplateFileWriter output =
			new TemplateFileWriter(outputFile, tree);

		output.writeData();
	}
	
	/*
	 * �e���v���[�g�t�@�C����ǂݍ���
	 */
	public void readTemplateFile() {
		if(tree == null) return;
		inputFile = getFile(); // via file open menu
		TemplateFileReader templateInput = new TemplateFileReader(inputFile);
		Tree templateTree = templateInput.getData();
		tree.setTemplateTree(templateTree);

	}

	/*
	 * Tree�t�@�C������������
	 */
	public void writeTreeFile() {
		
	}
	
	/*
	 * �摜�t�@�C������������
	 */
	public void saveImageFile() {
		if(tree == null || canvas == null) return;
		outputFile = getFile(); // via file open menu
	
		canvas.setTree(tree);
		canvas.saveImageFile(outputFile);
	}
	
	/*
	 * TableAttributePanel ���Q�b�g����
	 */
	public TablePanel getTablePanel() {
		return null;
	}
	
	/*
	 * NodeValuePanel ���Q�b�g����
	 */
	public NodeValuePanel getNodeValuePanel() {
		return nodeValuePanel;
	}
}
