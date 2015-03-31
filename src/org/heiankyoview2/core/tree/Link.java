package org.heiankyoview2.core.tree;

import java.awt.Color;


/**
 * 2つのNodeを連結するLinkを定義する
 * 
 * @author itot
 */
public class Link {

	Node node1, node2;
	String name;
	Color color;

	/**
	 * Constructor
	 * @param name 名前
	 * @param node1 1個目のNode
	 * @param node2 2個目のNode
	 */
	public Link(String name, Node node1, Node node2) {
		// set up nodes on the both ends
		this.name = name;
		this.node1 = node1;
		this.node2 = node2;
	}

	/**
	 * Constructor
	 * @param name 名前
	 */
	public Link(String name) {
		this(name, null, null);
	}

	/**
	 * Constructor
	 * @param LinkID ID
	 */
	public Link(int LinkID) {
	}

	/**
	 * 名前をセットする
	 * @param name 名前
	 */
	public void setIndex(String name) {
		this.name = name;
	}

	/**
	 * 名前をゲットする
	 * @return 名前
	 */
	public String getName() {
		return name;
	}

	/**
	 * 2個のNodeを同時に登録する
	 * @param Node
	 */
	public void setNodes(Node n1, Node n2) {
		node1 = n1;    node2 = n2;
	}
	
	/**
	 * 1番目のNodeを登録する
	 * @param Node
	 */
	public void setNode1(Node node) {
		node1 = node;
	}

	/**
	 * 2番目のNodeを登録する
	 * @param Node
	 */
	public void setNode2(Node node) {
		node2 = node;
	}

	/**
	 * 1番目のNodeを返す
	 * @return Node
	 */
	public Node getNode1() {
		return node1;
	}
	
	/**
	 * 2番目のNodeを返す
	 * @return Node
	 */
	public Node getNode2() {
		return node2;
	}


	/**
	 * アークの色を設定する
	 * @param アークの色
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * アークの色を返す
	 * @return アークの色
	 */
	public Color getColor() {
		return color;
	}

}
