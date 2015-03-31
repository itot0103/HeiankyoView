package org.heiankyoview2.core.fileio;

import org.heiankyoview2.core.tree.Tree;
import org.heiankyoview2.core.tree.Node;
import org.heiankyoview2.core.table.Table;
import org.heiankyoview2.core.table.TreeTable;
import org.heiankyoview2.core.table.NodeTablePointer;


/**
 * Tableの情報をtreeファイルに書き出す
 * @author itot
 */
public class TableFileWriter {

	/* var */
	Tree tree;
	FileOutput output;
	String dummy = " ";

	/**
	 * Constructor
	 * @param tree Tree
	 * @param output  出力先ファイル
	 */
	public TableFileWriter(Tree tree, FileOutput output) {

		this.tree = tree;
		this.output = output;
	}

	/**
	 * Tableに関連する情報を書き出す
	 * @param tree Tree
	 */
	public void writeTables(Tree tree) {
		String linebuffer;
		TreeTable ttree = tree.table;
		int numtable = ttree.getNumTable();

		//
		// Num of table
		//
		linebuffer = "numtable  " + Integer.toString(numtable);
		output.println(linebuffer);
		output.println("");

		//
		// for each table
		//
		for (int i = 1; i <= numtable; i++) {
			Table table = ttree.getTable(i);
			int type = 0, size = 0;

			//
			// table name
			//
			String name = table.getName();
			if (name != null && name.length() > 0)
				output.println("tablename " + i + " " + name);

			//
			// table type
			//
			type = table.getType();
			if (type == table.TABLE_STRING)
				output.println("tabletype " + i + " " + "string");
			if (type == table.TABLE_DOUBLE)
				output.println("tabletype " + i + " " + "double");
			if (type == table.TABLE_INT)
				output.println("tabletype " + i + " " + "int");

			//
			// num of table element
			//
			size = table.getSize();
			output.println("tablenumline " + i + " " + size);

			//
			// for each table element
			//
			for (int j = 1; j <= size; j++) {

				String value = "";
				if (type == table.TABLE_STRING)
					value = table.getString(j);
				if (type == table.TABLE_DOUBLE)
					value = Double.toString(table.getDouble(j));
				if (type == table.TABLE_INT)
					value = Integer.toString(table.getInt(j));
				output.println("tl " + " " + j + " " + value);
			}

			output.println("");
		}

	}

	/**
	 * NodeからTableへのポインタに関する情報を書き出す
	 * @param node Node
	 * @param isBranch Branchであればtrue
	 */
	public void writeNodeTablePointer(Node node, boolean isBranch) {

		int id;
		
		if (isBranch == false)
			id = node.getId();
		else
			id = node.getChildBranch().getId();

		Integer Iid = new Integer(id);
		NodeTablePointer tn = node.table;

		//
		// for each table
		//
		if (isBranch == false) {
			for (int i = 1; i <= tn.getNumId(); i++) {

				String linebuf =
					"ntl "
						+ Iid.toString()
						+ " "
						+ i
						+ " "
						+ tn.getId(i);
				output.println(linebuf);
			}
		} else {
			for (int i = 1; i <= tn.getNumId(); i++) {

				String linebuf =
					"btl "
						+ Iid.toString()
						+ " "
						+ i
						+ " "
						+ tn.getId(i);
				output.println(linebuf);
			}
		}

		output.println(dummy);
	}

}
