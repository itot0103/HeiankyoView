package org.heiankyoview2.core.fileio;

import org.heiankyoview2.core.tree.Tree;
import org.heiankyoview2.core.tree.Branch;
import org.heiankyoview2.core.tree.Node;
import org.heiankyoview2.core.table.Table;
import org.heiankyoview2.core.table.TreeTable;
import org.heiankyoview2.core.table.NodeTablePointer;

import java.util.*;


/**
 * Tableを構築するための内容をファイルから読み込む
 * @author itot
 */
public class TableFileReader {

	/* var */
	Tree tree;
	TreeTable tg;
	Table table = null;
	int numTable = 0;

	/**
	 * Constructor
	 * @param tree Tree
	 */
	public TableFileReader(Tree tree) {
		this.tree = tree;
		tg = tree.table;
	}

	/**
	 * Tableを構築するための内容を記述した1行を解読する
	 * @param tree Tree
	 * @param branchId 現在処理しているBranchのID
	 * @param lineBuffer ファイルから読み込んだ行
	 */
	public void parseTableData(
		Tree tree,
		int branchId,
		String lineBuffer) {

		
		// get string separeted space;
		StringTokenizer tokenBuffer = new StringTokenizer(lineBuffer);
		if (tokenBuffer.hasMoreTokens() == false)
			return;
		String header = tokenBuffer.nextToken();

		/* Set WebAccess data */
		if (header == null || header.charAt(0) == '#') {
			return;
		} else if (header.startsWith("ntl")
				|| header.startsWith("nodetableline")) {
			int ct = tokenBuffer.countTokens();
			if (ct < 2) return;

			int nodeId = Integer.parseInt(tokenBuffer.nextToken());
			Branch branch = tree.getBranchAt(branchId);
			Node node = branch.getNodeAt(nodeId);

			int tableId, elementId = -1;
			tableId = Integer.parseInt(tokenBuffer.nextToken());
			if(ct >= 3)
				elementId = Integer.parseInt(tokenBuffer.nextToken());

			NodeTablePointer tn = node.table;
			if(tn.getNumId() <= 0)
				tn.setNumId(numTable);
			tn.setId(tableId, elementId);
			
		} else if (header.startsWith("btl")
				|| header.startsWith("branchtableline")) {
			int ct = tokenBuffer.countTokens();
			if (ct < 2) return;
			
			tokenBuffer.nextToken();
			Branch branch = tree.getBranchAt(branchId);
			
			int tableId, elementId = -1;
			tableId = Integer.parseInt(tokenBuffer.nextToken());
			if(ct >= 3)
				elementId = Integer.parseInt(tokenBuffer.nextToken());

			Node pnode = branch.getParentNode();
			if(pnode == null)
				pnode = tree.getRootNode();	
			NodeTablePointer tn = pnode.table;
			if(tn.getNumId() <= 0)
				tn.setNumId(numTable);
			tn.setId(tableId, elementId);
			
			/*
			Node node = branch.getParentNode();
			if (node != null) {
				tn = node.table;
				tn.setNumId(numTable);
				tn.setId(tableId, elementId);
			}
			*/
			
		} else if (header.startsWith("numtable")) {
			if (tokenBuffer.countTokens() < 1)
				return;

			numTable = Integer.parseInt(tokenBuffer.nextToken());
			tg.setNumTable(numTable);
			for (int i = 1; i <= numTable; i++) {
				tg.setTable(i, (new Table()));
			}

		} else if (header.startsWith("tablename")) {
			if (tokenBuffer.countTokens() < 2)
				return;

			int tableId = Integer.parseInt(tokenBuffer.nextToken());
			String tableName = "";
			while (tokenBuffer.countTokens() > 0)
				tableName += (tokenBuffer.nextToken() + " ");

			table = tg.getTable(tableId);
			table.setName(tableName);

		} else if (header.startsWith("tabletype")) {
			if (tokenBuffer.countTokens() < 2)
				return;

			int tableId = Integer.parseInt(tokenBuffer.nextToken());
			String tableType = tokenBuffer.nextToken();
			table = tg.getTable(tableId);
			table.setType(1);
			if (tableType.compareTo("double") == 0)
				table.setType(2);
			if (tableType.compareTo("int") == 0)
				table.setType(3);

		} else if (header.startsWith("tablenumline")) {
			if (tokenBuffer.countTokens() < 2)
				return;

			int tableId = Integer.parseInt(tokenBuffer.nextToken());
			int numElement = Integer.parseInt(tokenBuffer.nextToken());
			table = tg.getTable(tableId);
			table.setSize(numElement);

		} else if (header.startsWith("tl")
				|| header.startsWith("tableline")) {
			if (tokenBuffer.countTokens() < 2)
				return;

			int elementId = Integer.parseInt(tokenBuffer.nextToken());
			int tableType = table.getType();

			if (tableType == Table.TABLE_STRING) {
				String value = "";
				while (tokenBuffer.countTokens() > 0) {
					value += tokenBuffer.nextToken();
					value += " ";
				}
				table.set(elementId, value);
			}
			if (tableType == Table.TABLE_DOUBLE) {
				String value = tokenBuffer.nextToken();
				table.set(elementId, Double.parseDouble(value));
			}
			if (tableType == Table.TABLE_INT) {
				String value = tokenBuffer.nextToken();
				table.set(elementId, Integer.parseInt(value));
			}

		}
	}
}
