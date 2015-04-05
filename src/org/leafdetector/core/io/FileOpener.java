/*
 * ???: 2006/07/26
 *
 * TODO ???????????????????????????????:
 * ????? - ?? - Java - ???????? - ??????????
 */
package org.leafdetector.core.io;

import java.awt.Component;
import java.io.File;

import javax.swing.JDialog;
import javax.swing.JFileChooser;

/**
 * @author fyamashi
 *
 * TODO ????????????????????????????????:
 * ????? - ?? - Java - ???????? - ??????????
 */
public abstract class FileOpener extends JDialog{
	File file, currentDirectory;
	Component windowContainer;

	public File getFile(){
		JFileChooser chooser=new JFileChooser(currentDirectory);
		int selected = chooser.showOpenDialog(windowContainer);
		if (selected == JFileChooser.APPROVE_OPTION) { // open selected
			currentDirectory = chooser.getCurrentDirectory();
			return chooser.getSelectedFile();
		} else if (selected == JFileChooser.CANCEL_OPTION) { // cancel selected
			return null;
		} 
		return null;
	}
	
	public abstract void readFile();
	
	public abstract void saveFile();

}
