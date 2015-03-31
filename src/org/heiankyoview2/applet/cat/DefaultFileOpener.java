
package org.heiankyoview2.applet.cat;

import java.io.File;

import java.awt.*;
import javax.swing.*;

import org.heiankyoview2.core.tree.Tree;
import org.heiankyoview2.core.tree.Template;
import org.heiankyoview2.core.table.TreeTable;
import org.heiankyoview2.core.fileio.TreeFileReader;
import org.heiankyoview2.core.fileio.TemplateFileReader;
import org.heiankyoview2.core.fileio.TemplateFileWriter;
import org.heiankyoview2.core.xmlio.XmlTreeFileReader;
import org.heiankyoview2.core.gldraw.Canvas;
import org.heiankyoview2.core.glwindow.*;



public class DefaultFileOpener implements org.heiankyoview2.core.glwindow.FileOpener {

	File currentDirectory, inputFile, outputFile;
	Component windowContainer;
	Canvas canvas;
	Tree tree, templateTree = null;
	CatPacking packing = new CatPacking();
	

	
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
			if (input == null)
				return null;
			tree = input.getTree();
			if (tree == null) return null;
			TreeTable tg = tree.table;
			tree.setTemplateTree(templateTree);
			packing.placeNodesAllBranch(tree);
			tg.setNameType(1);
		}
		else if (  fileName.endsWith(".xml") == true
				|| fileName.endsWith(".XML") == true) {

			XmlTreeFileReader input = new XmlTreeFileReader(inputFile);
			if (input == null)
				return null;
			tree = input.getTree();
			if (tree == null) return null;
			TreeTable tg = tree.table;
			tg.setNameType(1);
			tree.setTemplateTree(templateTree);
			packing.placeNodesAllBranch(tree);
		}
		else if (fileName.endsWith(".tpl") == true
				|| fileName.endsWith(".TPL") == true) {

			TemplateFileReader templateInput = new TemplateFileReader(inputFile);
			templateTree = templateInput.getData();
			tree = templateTree = templateInput.getData();
			if (tree == null) return null;
			TreeTable tg = tree.table;
			tree.setTemplateTree(tree);
			packing.placeNodesAllBranch(tree);
			tg.setNameType(1);
		} 
		
		return tree;
	}
	
	
	/*
	 * frame�t�@�C����ǂݍ���
	 */
	public void readFrameFile() {
	}
	
	/*
	 * text�t�@�C����ǂݍ���
	 */
	public void readTextFile(){	
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

		System.out.println("WithPosition");
		output.writeData();
	}
	
	/*
	 * �e���v���[�g�t�@�C����ǂݍ���
	 */
	public void readTemplateFile() {
		inputFile = getFile(); // via file open menu
		TemplateFileReader templateInput = new TemplateFileReader(inputFile);
		templateTree = templateInput.getData();
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
		return null;
	}
}
