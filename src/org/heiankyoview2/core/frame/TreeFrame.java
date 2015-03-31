package org.heiankyoview2.core.frame;

import java.awt.Color;
import java.util.Vector;

import org.heiankyoview2.core.table.NodeTablePointer;
import org.heiankyoview2.core.table.Table;
import org.heiankyoview2.core.tree.*;
import org.heiankyoview2.core.util.*;

/**
 * Frame の集合を格納するクラス
 */
public class TreeFrame {
	public static final int FRAME_STRING = 1;
	public static final int FRAME_DOUBLE = 2;
	public static final int FRAME_INT    = 3;
	
	Vector framelist = new Vector();
	int valuetypes[] = null;
	Node nodes[] = null;
	Frame currentFrame = null;
	
	int colorType = -1, heightType = -1, nameType = -1, urlType = -1;
	
	
	/**
	 * Frameを1個追加生成し、それを返す
	 * @param frame
	 */
	public Frame addOneFrame() {
		Frame frame = new Frame(this);
		framelist.add((Object)frame);
		frame.setId(framelist.size());
		return frame;
	}
	
	
	
	/**
	 * Frameをセットする
	 * @param frame
	 */
	public void setFrame(Frame frame) {
		framelist.add((Object)frame);
		frame.setTreeFrame(this);
	}
	
	
	/**
	 * Frameをゲットする
	 * @param id
	 * @return
	 */
	public Frame getFrame(int id) {
		return (Frame)framelist.elementAt(id - 1);
	}
	

	/**
	 * Current Frameをセットする
	 * @param frame
	 */
	public void setCurrentFrame(Frame frame) {
		currentFrame = frame;
	}
	
	
	/**
	 * CurrentFrameをゲットする
	 * @param id
	 * @return
	 */
	public Frame getCurrentFrame() {
		return currentFrame;
	}
	
	
	/**
	 * Frameの総数をゲットする
	 * @return
	 */
	public int getNumFrames() {
		if(framelist == null) return 0;
		return framelist.size();
	}
	
	/**
	 * 変数の数をセットする
	 * @param num
	 */
	public void setNumValues(int num) {
		valuetypes = new int[num];
		
		// デフォルトはdouble型ということにする
		for(int i = 0; i < num; i++)
			valuetypes[i] = FRAME_DOUBLE;
	}
	
	
	/**
	 * 変数の数をゲットする
	 * @return
	 */
	public int getNumValues() {
		if(valuetypes == null) return 0;
		return valuetypes.length;
	}
	
	
	/**
	 * 変数のタイプをセットする
	 * @param id
	 * @param type
	 */
	public void setValueType(int id, int type) {
		valuetypes[id - 1] = type;
	}
	
	
	/**
	 * 変数のタイプをゲットする
	 * @param id
	 * @return
	 */
	public int getValueType(int id) {
		return valuetypes[id - 1];
	}
	
	
	/**
	 * Nodeの数をセットする
	 * @param num
	 */
	public void setNumNodes(int num) {
		nodes = new Node[num];
	}
	
	
	/**
	 * Nodeの数をゲットする
	 * @return
	 */
	public int getNumNodes() {
		if(nodes == null) return 0;
		return nodes.length;
	}
	
	
	/**
	 * Nodeをセットする
	 * @param id
	 * @param type
	 */
	public void setNode(int id, Node node) {
		nodes[id - 1] = node;
	}
	
	
	/**
	 * Nodeをゲットする
	 * @param id
	 * @return
	 */
	public Node getNode(int id) {
		return nodes[id - 1];
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
	 * Nodeの色を算出する
	 * @param node Node
	 * @return Nodeの色
	 */
	public Color calcNodeColor(Node node, ColorCalculator cc) {
		double value = -1.0;
		Color color = Color.gray;

		int nodeId = node.getFrameNodeId();
		value = currentFrame.getAppearanceValue(colorType, nodeId);
		color = cc.calculate((float) value);

		return color;
	}

	
	
	/**
	 * Nodeの色算出のための値（0.0～1.0）を算出する
	 * @param node Node
	 * @return Nodeの色算出のための値（0.0～1.0）
	 */
	public double calcNodeColorValue(Node node) {
		double value = 0.5;
		if(currentFrame == null)
			return 0.5;
		if(colorType <= 0 || colorType > getNumValues())
			return 0.5;
		
		int nodeId = node.getFrameNodeId();
		value = currentFrame.getAppearanceValue(colorType, nodeId);
		return value;
	}

	
	/**
	 * Nodeの高さ算出のための値（0.0～1.0）を算出する
	 * @param node Node
	 * @return Nodeの高さ算出のための値（0.0～1.0）
	 */
	public double calcNodeHeightValue(Node node) {
		double value = 0.5;
		if(currentFrame == null)
			return 0.5;
		if(heightType <= 0 || heightType > getNumValues())
			return 0.5;
		
		int nodeId = node.getFrameNodeId();
		value = currentFrame.getAppearanceValue(heightType, nodeId);
		return value;
	}
	
	
	/**
	 * Frameを参照しながらNodeの名前を返す
	 * @param node Node
	 * @param type 参照するTableのID
	 * @return Nodeの名前
	 */
	public String getNodeAttributeName(Node node, int type) {
		String name = null;
		int nodeId = node.getFrameNodeId();

		if (valuetypes[type - 1] == FRAME_STRING) {
			name = currentFrame.getString(type, nodeId);
		}
		if (valuetypes[type - 1] == FRAME_DOUBLE) {
			name = Double.toString(currentFrame.getDouble(type, nodeId));
		}
		if (valuetypes[type - 1] == FRAME_INT) {
			name = Integer.toString(currentFrame.getInt(type, nodeId));
		}

		return name;
	}

}
