
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
	 * frameファイルを読み込む
	 */
	public void readFrameFile() {
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

		output.writeData();
	}
	
	/*
	 * テンプレートファイルを読み込む
	 */
	public void readTemplateFile() {
		if(tree == null) return;
		inputFile = getFile(); // via file open menu
		TemplateFileReader templateInput = new TemplateFileReader(inputFile);
		Tree templateTree = templateInput.getData();
		tree.setTemplateTree(templateTree);

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
		return nodeValuePanel;
	}
}
