package org.heiankyoview2.core.xmlio;

import org.heiankyoview2.core.tree.Tree;
import org.heiankyoview2.core.tree.Branch;
import org.heiankyoview2.core.tree.Node;
import org.heiankyoview2.core.frame.Frame;
import org.heiankyoview2.core.frame.TreeFrame;

import java.io.*;
import java.util.StringTokenizer;
import javax.xml.parsers.*;
import org.w3c.dom.*;


/**
 * xmlファイルを読み込む
 * 
 * @author itot
 */
public class XmlFrameFileReader {

	File xmlFile;
	Document doc;
	Tree tree;
	int numnode = 0, numdimension = 0;
	
	/**
	 * Constructor
	 * 
	 * @param inputFile
	 *            読み込みファイル
	 */
	public XmlFrameFileReader(File inputFile) {

		try {
			// ドキュメントビルダーファクトリを生成
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory
					.newInstance();
			// ドキュメントビルダーを生成
			DocumentBuilder builder = dbfactory.newDocumentBuilder();
			// パースを実行してDocumentオブジェクトを取得
			doc = builder.parse(inputFile);
			// ルート要素を取得（タグ名：message）

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * DOM構造をparseして、Treeを構成するデータを構築する
	 * @param tree Tree
	 * @return 成功すればtrue
	 */
	/**
	 * DOM構造を読み込み、生成したFrameをTreeに設定する
	 * @param tree
	 */
	public void getFrame(Tree tree) {
		org.w3c.dom.Element element;
		
		/*
		 * Documentの先頭ノードを得る
		 */
		org.w3c.dom.Node framesNode = doc.getFirstChild();
		String framesName = framesNode.getNodeName();
		if(framesName.compareTo("frames") != 0) return;
		
		/*
		 * TreeFrameを確保する
		 */
		TreeFrame tf = new TreeFrame();
		tree.setTreeFrame(tf);
		
		/*
		 * framesノードからnumnodeとnumdimensionを得る
		 */
		element = (org.w3c.dom.Element)framesNode;
		numnode = Integer.parseInt(element.getAttribute("numnode"));
		numdimension = Integer.parseInt(element.getAttribute("numdimension"));
		tf.setNumNodes(numnode);
		tf.setNumValues(numdimension);
		
		/*
		 * framesノードからframeノードを得る
		 */
		org.w3c.dom.NodeList  nodelist = framesNode.getChildNodes();
		for(int i = 0; i < nodelist.getLength(); i++) {
			org.w3c.dom.Node frameNode = nodelist.item(i);
			if(frameNode.getNodeName().compareTo("frame") != 0) continue;
			Frame frame = tf.addOneFrame();
			element = (org.w3c.dom.Element)frameNode;
			String time = element.getAttribute("name");
			frame.setTime(Double.parseDouble(time));
			
			/*
			 * frameノードからframenodeノードを得る
			 */
			org.w3c.dom.NodeList  nodelist2 = frameNode.getChildNodes();
			for(int j = 0; j < nodelist2.getLength(); j++) {
				org.w3c.dom.Node node = nodelist2.item(j);
				if(node.getNodeName().compareTo("framenode") != 0) continue;
				element = (org.w3c.dom.Element)frameNode;
				int nodeId = Integer.parseInt(element.getAttribute("id"));
				StringTokenizer st = new StringTokenizer(
						element.getAttribute("value"), ",");
				for(int k = 1; k <= numdimension; k++) {
					double dvalue = Double.parseDouble(st.nextToken());
					frame.set(k, nodeId, dvalue);
				}
			}
		}
		
		
		return;
	}
	
}