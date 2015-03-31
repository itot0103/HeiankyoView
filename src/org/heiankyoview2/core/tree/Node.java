package org.heiankyoview2.core.tree;

import java.awt.Color;
import java.awt.Image;
import org.heiankyoview2.core.table.NodeTablePointer;

/**
 * 階層型データの最小単位であるNodeを定義する
 * 
 * @author itot
 */
public class Node {

	// Node の位置、大きさ、色
	int id; 
	double x, y, z;
	double width, height, depth;
	Color color;
	String name = "";

	// Branch, Image など
	Branch childBranch;
	Branch currentBranch;
	String childFilename;
	String imageUrl;
	Image  image;
	Object texture;

	// Table へのポインタ
	public NodeTablePointer table;

	// Frame へのポインタ
	int frameNodeId = 0;
	
	// Template 関係の変数
	boolean isTemplateApplied = false;
	double nx, ny, nz;
	double nwidth, nheight, ndepth;
	boolean isPlaced = false;
	boolean isDefaultSize = true;
	double length;

	/**
	 * Constructor
	 * @param id
	 * @param currentBranch
	 * @param x
	 * @param y
	 * @param z
	 */
	public Node(int id, Branch currentBranch, double x, double y, double z) {
		setId(id);
		setCoordinate(x, y, z);
		setCurrentBranch(currentBranch);;
		width = height = depth = 1.0;
		nwidth = nheight = ndepth = 1.0;
		length = 0.0;
		childBranch = null;
		table = new NodeTablePointer();
	}

	/**
	 * Constructor
	 * @param id
	 * @param currentBranch
	 */
	public Node(int id, Branch currentBranch) {
		this(id, currentBranch, 0.0, 0.0, 0.0);
	}

	/**
	 * Constructor
	 *
	 */
	public Node() {
		this(-1, null, 0.0, 0.0, 0.0);
	}

	/**
	 * ノードの番号を設定する
	 * @param ノードの番号
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * ノードの番号を返す
	 * @return ノードの番号
	 */
	public int getId() {
		return id;
	}


	/**
	 * ノードの名前を設定する
	 * @param ノードの名前
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * ノードの名前を返す
	 * @return ノードの名前
	 */
	public String getName() {
		return name;
	}


	/**
	 * FrameのNodeIdを設定する
	 * @param id FrameのNodeId
	 */
	public void setFrameNodeId(int id) {
		frameNodeId = id;
	}
	

	/**
	 * FrameのNodeIdを返す
	 * @return FrameのNodeId
	 */
	public int getFrameNodeId() {
		return frameNodeId;
	}
	
	
	/**
	 * ノードの座標値を設定する
	 * @param ノードのx座標値
	 * @param ノードのy座標値
	 * @param ノードのz座標値
	 */
	public void setCoordinate(double x, double y, double z) {
		setX(x);
		setY(y);
		setZ(z);
	}

	/**
	 * ノードのx座標値を設定する
	 * @param ノードのx座標値
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * ノードのx座標値を返す
	 * @return ノードのx座標値
	 */
	public double getX() {
		return x;
	}

	/**
	 * ノードのy座標値を設定する
	 * @param ノードのy座標値
	 */
	public void setY(double y) {
		this.y = y;
	}

	/**
	 * ノードのy座標値を返す
	 * @return ノードのy座標値
	 * 
	 */
	public double getY() {
		return y;
	}

	/**
	 * ノードのz座標値を設定する
	 * @param ノードのz座標値
	 */
	public void setZ(double z) {
		this.z = z;
	}

	/**
	 * ノードのz座標値を返す
	 * @return ノードのz座標値
	 */
	public double getZ() {
		return z;
	}


	/**
	 * ノードの子グループを設定する
	 * @param ノードの子グループ
	 */
	public void setChildBranch(Branch childBranch) {
		this.childBranch = childBranch;
	}

	/**
	 * ノードの子グループを返す
	 * @return ノードの子グループ
	 */
	public Branch getChildBranch() {
		return childBranch;
	}

	/**
	 * ノードの所属グループを設定する
	 * @param ノードの所属グループ
	 */
	public void setCurrentBranch(Branch currentBranch) {
		this.currentBranch = currentBranch;
	}

	/**
	 * ノードの所属グループを返す	
	 * @return ノードの所属グループ
	 */
	public Branch getCurrentBranch() {
		return currentBranch;
	}

	/**
	 * ノードの色を設定する
	 * @param ノードの色
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * ノードの色を返す
	 * @return ノードの色
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * ノードのサイズを設定する
	 * @param ノードの横幅
	 * @param ノードの縦幅
	 * @param ノードの奥行き
	 */
	public void setSize(double width, double height, double depth) {
		this.width = width;
		this.height = height;
		this.depth = depth;
		isDefaultSize = false;
	}

	/**
	 * ノードの横幅を設定する
	 * @param ノードの横幅
	 */
	public void setWidth(double width) {
		this.width = width;
		isDefaultSize = false;
	}
	
	/**
	 * ノードの横幅を返す
	 * @return ノードの横幅
	 */
	public double getWidth() {
		return width;
	}


	/**
	 * ノードの縦幅を設定する
	 * @param ノードの縦幅
	 */
	public void setHeight(double height) {
		this.height = height;
		isDefaultSize = false;
	}
	
	/**
	 * ノードの縦幅を返す
	 * @return ノードの縦幅
	 */
	public double getHeight() {
		return height;
	}

	/**
	 * ノードの奥行きを設定する
	 * @param ノードの奥行き
	 */
	public void setDepth(double depth) {
		this.depth = depth;
		isDefaultSize = false;
	}

	/**
	 * ノードの奥行きを返す
	 * @return ノードの奥行き
	 */
	public double getDepth() {
		return depth;
	}


	/**
	 * ノードにデータファイルが関連付けられているとき、そのファイルを設定する
	 * @param 関連付けられたデータファイル
	 */
	public void setChildFilename(String filename) {
		this.childFilename = filename;
	}

	/**
	 * ノードにデータファイルが関連付けられているとき、そのファイルを返す
	 * @return 関連付けられたデータファイル
	 */
	public String getChildFilename() {
		return childFilename;
	}

	/**
	 * ノードに画像URLが関連付けられているとき、そのURLを設定する
	 * @param 関連付けられた画像URL
	 */
	public void setImageUrl(String url) {
		this.imageUrl = url;
	}

	/**
	 * ノードに画像URLが関連付けられているとき、そのURLを返す
	 * @param 関連付けられたアイコンファイル
	 */
	public String getImageUrl() {
		return imageUrl;
	}
	
	/**
	 * 画像を設定する
	 * @param 画像
	 */
	public void setImage(Image image) {
		this.image = image;
	}

	/**
	 * 画像を返す
	 * @return 画像
	 */
	public Image getImage() {
		return image;
	}
	
	/**
	 * テクスチャを設定する
	 * @param テクスチャ
	 */
	public void setTexture(Object texture) {
		this.texture = texture;
	}
	
	/**
	 * テクスチャを返す
	 * @return テクスチャ
	 */
	public Object getTexture() {
		return texture;
	}

	/**
	 * サイズが初期値であるかどうかを返す
	 */
	public boolean isDefaultSize() {
		return isDefaultSize;
	}
	
	/*
	 * 以下テンプレートグラフに固有のメソッド
	 */
	
	public void setNX(double nx) {
		this.nx = nx;
	}
	public double getNX() {
		return nx;
	}
	public void setNY(double ny) {
		this.ny = ny;
	}
	public double getNY() {
		return ny;
	}
	public void setNZ(double nz) {
		this.nz = nz;
	}
	public double getNZ() {
		return nz;
	}
	public void setNCoordinate(double nx, double ny, double nz) {
		this.nx = nx;
		this.ny = ny;
		this.nz = nz;
	}

	public void setNwidth(double nwidth) {
		this.nwidth = nwidth;
	}
	public double getNwidth() {
		return nwidth;
	}
	public void setNheight(double nheight) {
		this.nheight = nheight;
	}
	public double getNheight() {
		return nheight;
	}

	public void setNdepth(double ndepth) {
		this.ndepth = ndepth;
	}
	public double getNdepth() {
		return ndepth;
	}

	public void setNsize(double nwidth, double nheight, double ndepth) {
		this.nwidth = nwidth;
		this.nheight = nheight;
		this.ndepth = ndepth;
	}


	public void setTemplateFlag(boolean i) {
		this.isTemplateApplied = i;
	}
	public boolean getTemplateFlag() {
		return this.isTemplateApplied;
	}

	public void setPlaced(boolean i) {
		this.isPlaced = i;
	}
	public boolean getPlaced() {
		return this.isPlaced;
	}

	public double getLength() {
		return length;
	}
	public void setLength(double l) {
		this.length = Math.abs(l);
	}
	
	public void calcLength(Node maxNode) {
		double l = 0;

		l =
			Math.pow((nx - maxNode.getNX()), 2)
				+ Math.pow((ny - maxNode.getNY()), 2);

		this.length = Math.abs(l);
	}
}
