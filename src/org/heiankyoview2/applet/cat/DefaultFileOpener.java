
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
	 * Container をセットする
	 * @param c Component
	 */
	public void setContainer(Component c) {
		windowContainer = c;
	}
	
	
	/**
	 * Canvas をセットする
	 * @param c Canvas
	 */
	public void setCanvas(Canvas c) {
		canvas = c;
	}
	
	/**
	 * Tree を返す
	 * @return tree
	 */
	public Tree getTree() {
		return tree;
	}
	
	
	/**
	 * ファイルダイアログにイベントがあったときに、対応するファイルを特定する
	 * @return ファイル
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
	 * treeファイルを読み込む
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
	 * frameファイルを読み込む
	 */
	public void readFrameFile() {
	}
	
	/*
	 * textファイルを読み込む
	 */
	public void readTextFile(){	
	}
	
	/*
	 * テンプレートファイルを書き込む
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
	 * テンプレートファイルを読み込む
	 */
	public void readTemplateFile() {
		inputFile = getFile(); // via file open menu
		TemplateFileReader templateInput = new TemplateFileReader(inputFile);
		templateTree = templateInput.getData();
	}

	/*
	 * Treeファイルを書き込む
	 */
	public void writeTreeFile() {
		
	}
	
	
	/*
	 * 画像ファイルを書き込む
	 */
	public void saveImageFile() {
		if(tree == null || canvas == null) return;
		outputFile = getFile(); // via file open menu
	
		canvas.setTree(tree);
		canvas.saveImageFile(outputFile);
	}
	
	/*
	 * TableAttributePanel をゲットする
	 */
	public TablePanel getTablePanel() {
		return null;
	}
	
	/*
	 * NodeValuePanel をゲットする
	 */
	public NodeValuePanel getNodeValuePanel() {
		return null;
	}
}
