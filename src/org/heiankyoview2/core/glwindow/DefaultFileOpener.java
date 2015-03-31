
package org.heiankyoview2.core.glwindow;

import java.io.*;

import java.awt.*;
import javax.swing.*;

import org.heiankyoview2.core.tree.*;
import org.heiankyoview2.core.fileio.*;
import org.heiankyoview2.core.xmlio.*;
import org.heiankyoview2.core.placement.Packing;
import org.heiankyoview2.core.gldraw.Canvas;
import org.heiankyoview2.datagen.csv2tree.*;


public class DefaultFileOpener extends Frame implements FileOpener {

	File currentDirectory, inputFile, outputFile;
	Component windowContainer;
	Canvas canvas;
	Tree tree, templateTree = null;
	Packing packing = new Packing();
	private JTextArea textArea = new JTextArea();

	
	// component
	AppearancePanel appearancePanel = null;
	TablePanel tablePanel = null;
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
		tree.setTemplateTree(templateTree);
		System.out.println(" ... completed to read " + fileName);
		
		//packing.placeNodesAllBranch(tree);
		/*
		{
			org.heiankyoview2.core.othermethods.SquarifiedTreemap st = 
					new org.heiankyoview2.core.othermethods.SquarifiedTreemap();
			st.placeNodesAllBranch(tree, null, 1);
		}
		*/
		
		{
			org.heiankyoview2.core.othermethods.UnbrellaNodeLink unl = 
					new org.heiankyoview2.core.othermethods.UnbrellaNodeLink();
			unl.placeNodesAllBranch(tree);
		}
		
		
		tablePanel = new DefaultTablePanel(tree);
		tablePanel.setCanvas(canvas);
		nodeValuePanel = new NodeValuePanel(tree);
		
		return tree;
	}
	
	
	/*
	 * frame�t�@�C����ǂݍ���
	 */
	public void readFrameFile() {
		if(tree == null) return;
		inputFile = getFile();
		if (inputFile == null) return;

		String fileName = inputFile.getName();

		if (fileName.endsWith(".frame") == true
				|| fileName.endsWith(".FRAME") == true) {

			FrameFileReader input = new FrameFileReader(inputFile);
			if (input == null) return;
			input.getFrame(tree);
		} else if (
			fileName.endsWith(".xml") == true
				|| fileName.endsWith(".XML") == true) {

			XmlFrameFileReader input = new XmlFrameFileReader(inputFile);
			if (input == null) return;
			input.getFrame(tree);
		}
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
		
		inputFile = getFile(); // via file open menu
		
		TemplateFileReader templateInput = new TemplateFileReader(inputFile);
		templateTree = templateInput.getData();
		System.out.println("  open template: " + templateTree);
		
	}

	/*
	 * Tree�t�@�C������������
	 */
	public void writeTreeFile() {
		File outputFile = getFile();
		if (inputFile == null) return;
		String fileName = outputFile.getName();

		if (fileName.endsWith(".tree") == true
				|| fileName.endsWith(".TREE") == true) {

			TreeFileWriter output = new TreeFileWriter(outputFile, tree);
			if (output == null) return;
			output.writeTree();
			
		} else if (
			fileName.endsWith(".xml") == true
				|| fileName.endsWith(".XML") == true) {

			XmlTreeFileWriter output = new XmlTreeFileWriter(outputFile, tree);
			if (output == null) return;
			output.writeTree();
			
		}
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
		return tablePanel;
	}
	
	/*
	 * NodeValuePanel ���Q�b�g����
	 */
	public NodeValuePanel getNodeValuePanel() {
		return nodeValuePanel;
	}
}