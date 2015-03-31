package org.heiankyoview2.core.table;

import java.awt.Color;
import org.heiankyoview2.core.tree.Tree;
import org.heiankyoview2.core.tree.Branch;
import org.heiankyoview2.core.tree.Node;
import org.heiankyoview2.core.util.*;


/**
 * Treeの属性を表形式データで表現するためのクラス
 * @author itot
 */
public class TreeTable {

	Tree tree;
	Table table[] = null;
	int numTable = 0;

	int colorType = -1, heightType = -1, nameType = -1, urlType = -1;
	int branchType1 = -1, branchType2 = -1, branchType3 = -1;
	boolean flagarray[] = null;
	
	/**
	 * Constructor
	 * @param tree Tree
	 */
	public TreeTable(Tree tree) {
		this.tree = tree;
	}

	/**
	 * Treeが保有するTableの総数をセットする
	 * @param numId Tableの総数
	 */
	public void setNumTable(int nt) {
		if (nt < 1)
			return;
		
		this.numTable = nt;
		table = new Table[numTable];
		for (int i = 0; i < numTable; i++)
			table[i] = null;
		
		flagarray = new boolean[numTable];
		for(int i = 0; i < numTable; i++)
			flagarray[i] = true;
	}

	/**
	 * Treeが保有するTableの総数を返す=属性数
	 * @return Tableの総数
	 */
	public int getNumTable() {
		return numTable;
	}
	
	/**
	 * 新しいテーブルを1個追加して返す
	 */
	public Table addNumTable() {
		numTable++;
		Table newtable[] = new Table[numTable];
		boolean newflagarray[] = new boolean[numTable];
		
		for(int i = 0; i < (numTable - 1); i++) {
			newtable[i] = table[i];
			newflagarray[i] = flagarray[i];
		}
		
		Table t = new Table();
		newtable[numTable - 1] = t;
		newflagarray[numTable - 1] = true;
		
		table = newtable;
		flagarray = newflagarray;
		
		return t;
	}
	
	
	/**
	 * Tableをセットする
	 * @param tableId TableのID
	 * @param newTable 新しく生成したTable
	 */
	public void setTable(int tableId, Table newTable) {

		if (numTable < 1)
			return;
		if (tableId <= 0 || tableId > numTable)
			return;

		table[tableId - 1] = newTable;
	}

	/**
	 * Tableを返す
	 * @param tableId TableのID
	 * @return IDに対応するTable
	 */
	public Table getTable(int tableId) {

		if (numTable < 1)
			return null;
		if (tableId <= 0 || tableId > numTable)
			return null;

		return table[tableId - 1];
	}

	/**
	 * 色計算に用いるTableのIDをセットする
	 * @param type 色計算に用いるTableのID
	 */
	public void setColorType(int type) {
		colorType = type;
	}

	/**
	 * 色計算に用いるTableのIDを返す
	 * @return 色計算に用いるTableのID
	 */
	public int getColorType() {
		return colorType;
	}

	/**
	 * 高さ計算に用いるTableのIDをセットする
	 * @param type 高さ計算に用いるTableのID
	 */
	public void setHeightType(int type) {
		heightType = type;
	}

	/**
	 * 高さ計算に用いるTableのIDを返す
	 * @return 高さ計算に用いるTableのID
	 */
	public int getHeightType() {
		return heightType;
	}

	/**
	 * 名前を指定するTableのIDをセットする
	 * @param type 名前を指定するTableのID
	 */
	public void setNameType(int type) {
		nameType = type;
	}

	/**
	 * 名前を指定するTableのIDを返す
	 * @return 名前を指定するTableのID
	 */
	public int getNameType() {
		return nameType;
	}

	/**
	 * URLを指定するTableのIDをセットする
	 * @param type URLを指定するTableのID
	 */
	public void setUrlType(int type) {
		urlType = type;
	}

	/**
	 * URLを指定するTableのIDを返す
	 * @return URLを指定するTableのID
	 */
	public int getUrlType() {
		return urlType;
	}

	
	/**
	 * Tableを参照しながらNodeをBranchに分ける際に、第一階層のBranchわけの参照するTableのIDをセットする
	 * @param type TableのID
	 */
	public void setBranchType1(int type) {
		branchType1 = type;
	}

	/**
	 * Tableを参照しながらNodeをBranchに分ける際に、第一階層のBranchわけの参照するTableのIDを返す
	 * @return TableのID
	 */
	public int getBranchType1() {
		return branchType1;
	}

	/**
	 * Tableを参照しながらNodeをBranchに分ける際に、第二階層のBranchわけの参照するTableのIDをセットする
	 * @param type TableのID
	 */
	public void setBranchType2(int type) {
		branchType2 = type;
	}

	/**
	 * Tableを参照しながらNodeをBranchに分ける際に、第二階層のBranchわけの参照するTableのIDを返す
	 * @return TableのID
	 */
	public int getBranchType2() {
		return branchType2;
	}

	/**
	 * Tableを参照しながらNodeをBranchに分ける際に、第三階層のBranchわけの参照するTableのIDをセットする
	 * @param type TableのID
	 */
	public void setBranchType3(int type) {
		branchType3 = type;
	}

	/**
	 * Tableを参照しながらNodeをBranchに分ける際に、第三階層のBranchわけの参照するTableのIDを返す
	 * @return TableのID
	 */
	public int getBranchType3() {
		return branchType3;
	}

	/**
	 * 1個のTableに対してフラグを設定する
	 * @param id
	 * @param flag
	 */
	public void setFlag(int id, boolean flag) {
		if(flagarray == null) return;
		flagarray[id] = flag;
	}
	
	/**
	 * 1個のTableに対するフラグを返す
	 * @param id
	 * @return
	 */
	public boolean getFlag(int id) {
		if(flagarray == null) return false;
		return flagarray[id];
	}
	
	/**
	 * フラグの配列を返す
	 * @return
	 */
	public boolean[] getFlagArray() {
		return flagarray;
	}
	
	/**
	 * Nodeの色算出のための値（0.0～1.0）を算出する
	 * @param node Node
	 * @return Nodeの色算出のための値（0.0～1.0）
	 */
	public double calcNodeColorValue(Node node) {
		double value = 0.5;

		Table branchTable;

		if (table == null || colorType < 0)
			return value;
		branchTable = table[colorType];

		NodeTablePointer tn = node.table;
		int id = tn.getId(colorType + 1);
		value = branchTable.getAppearanceValue(id - 1);
		
		return value;
	}

	/**
	 * Nodeの色を算出する
	 * @param node Node
	 * @return Nodeの色
	 */
	public Color calcNodeColor(Node node, ColorCalculator cc) {
		double value = -1.0;
		Color color = Color.gray;

		Table branchTable;

		if (table == null || colorType < 0)
			return color;
		branchTable = table[colorType];

		NodeTablePointer tn = node.table;
		int id = tn.getId(colorType + 1);
		value = branchTable.getAppearanceValue(id - 1);
		color = cc.calculate((float) value);

		// temporary
		/*
		Branch cbranch = node.getCurrentBranch();
		Node pnode = cbranch.getParentNode();
		Table branchTable2 = table[table.length - 1];
		int id2 = pnode.table.getId(table.length);
		double value2 = branchTable2.getAppearanceValue(id2 - 1);
		color = colorMap.calcHSBSmoothColor((float) value, (float)value2);
		*/
		return color;
	}

	/**
	 * Nodeの高さ算出のための値（0.0～1.0）を算出する
	 * @param node Node
	 * @return Nodeの高さ算出のための値（0.0～1.0）
	 */
	public double calcNodeHeightValue(Node node) {
		double value = -1.0;

		Table branchTable;

		if (table == null || heightType < 0)
			return 0.01;
		branchTable = table[heightType];

		NodeTablePointer tn = node.table;
		int id = tn.getId(heightType + 1);
		value = branchTable.getAppearanceValue(id - 1);
		if (value < 0.0)
			value = 0.01;
		
		return value;
	}

	/**
	 * Tableを参照しながら、1つのBranchを構成するNodeを複数の子Branchに分類する
	 * @param branch Branch
	 * @param branchType 参照するTableのID
	 */
	/*
	public void divideOneBranch(Branch branch, int branchType) {

		BranchByAttributeId byId = new BranchByAttributeId(tree);
		byId.divideOneBranch(branch, branchType);

	}
	*/

	/**
	 * NodeからTableへのポインタとなるIDを返す
	 * @param node Node
	 * @param type 参照するTableのID
	 * @return  NodeからTableへのポインタとなるID
	 */
	public int getNodeTablePointerId(Node node, int type) {
		NodeTablePointer tn = node.table;
		int id = tn.getId(type);
		return id;
	}

	/**
	 * Tableを参照しながらNodeの名前を返す
	 * @param node Node
	 * @param type 参照するTableのID
	 * @return Nodeの名前
	 */
	public String getNodeAttributeName(Node node, int type) {

		int id;
		String name = null;
		Table branchTable;

		if (table == null)
			return null;
		if (type < 0 || type >= numTable)
			return null;
		branchTable = table[type];

		NodeTablePointer tn = node.table;
		id = tn.getId(type + 1);
		if (id <= 0)
			return null;

		if (branchTable.getType() == branchTable.TABLE_STRING) {
			name = branchTable.getString(id);
		} else if (branchTable.getType() == branchTable.TABLE_DOUBLE) {
			name = Double.toString(branchTable.getDouble(id));
		} else if (branchTable.getType() == branchTable.TABLE_INT) {
			name = Integer.toString(branchTable.getInt(id));
		}

		return name;
	}


}
