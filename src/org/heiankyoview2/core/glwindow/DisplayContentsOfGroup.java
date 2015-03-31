package org.heiankyoview2.core.glwindow;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.*;

import org.heiankyoview2.core.table.NodeTablePointer;
import org.heiankyoview2.core.table.Table;
import org.heiankyoview2.core.table.TreeTable;
import org.heiankyoview2.core.tree.Branch;
import org.heiankyoview2.core.tree.Node;
import org.heiankyoview2.core.tree.Tree;

public class DisplayContentsOfGroup extends JFrame implements ActionListener{

	private JTextArea textArea = new JTextArea();
	Node node = null;
	Tree tree = null;
	
	//	ボタン
	private JButton close = new JButton("閉じる");
	
	//	ラジオボタンを作る
	private JRadioButton rb1 = new JRadioButton("DATATYP", true);
	private JRadioButton rb2 = new JRadioButton("HOSTRCVDT");
	private JRadioButton rb3 = new JRadioButton("HOSTRRCVTM");
	private JRadioButton rb4 = new JRadioButton("TRDTYP");
	private JRadioButton rb5 = new JRadioButton("SYSMENUDIV");
	private JRadioButton rb6 = new JRadioButton("PAYDIV");
	private JRadioButton rb7 = new JRadioButton("SID");
	private JRadioButton rb8 = new JRadioButton("FAMCD");
	private JRadioButton rb9 = new JRadioButton("REISSCD");
	private JRadioButton rb10 = new JRadioButton("RSLTST");
	private JRadioButton rb11 = new JRadioButton("MCTCD");
	private JRadioButton rb12 = new JRadioButton("POSINPUTFORM");
	private JRadioButton rb13 = new JRadioButton("SEX");
	private JRadioButton rb14 = new JRadioButton("ENTRYDT");
	private JRadioButton rb15 = new JRadioButton("VARIDTERM");
	private JRadioButton rb16 = new JRadioButton("LMTAMTS");
	private JRadioButton rb17 = new JRadioButton("LMTAMTC");
	private JRadioButton rb18 = new JRadioButton("POSAMTS");
	private JRadioButton rb19 = new JRadioButton("POSAMTC");
	private JRadioButton rb20 = new JRadioButton("INCRELMTAMT");
	private JRadioButton rb21 = new JRadioButton("MCTCTGCD");
	private JRadioButton rb22 = new JRadioButton("USEAMT");
	private JRadioButton rb23 = new JRadioButton("MCDCD");
	private JRadioButton rb24 = new JRadioButton("TERMNO");
	private JRadioButton rb25 = new JRadioButton("USEDT");
	private JRadioButton rb26 = new JRadioButton("INDCTRYCD");
	private JRadioButton rb27 = new JRadioButton("TRDCRCYCD");
	private JRadioButton rb28 = new JRadioButton("TRDAMT");
	private JRadioButton rb29 = new JRadioButton("DMDAMT");
	private JRadioButton rb30 = new JRadioButton("FRNMCTID");
	private JRadioButton rb31 = new JRadioButton("SECUREFLG");
	private JRadioButton rb32 = new JRadioButton("SFICATRDFLG");
	private JRadioButton rb33 = new JRadioButton("CTG");
	private JRadioButton rb34 = new JRadioButton("CITRINSCORE");
	private JRadioButton rb35 = new JRadioButton("COPETYP");
	private JRadioButton rb36 = new JRadioButton("INJSTTYP");
	private JRadioButton rb37 = new JRadioButton("CANSELFLG");
	private JRadioButton rb38 = new JRadioButton("RULE");
	private JRadioButton rb39 = new JRadioButton("AMT");
	private JRadioButton rb40 = new JRadioButton("DOW");
	private JRadioButton rb41 = new JRadioButton("AMTZONE");
	private JRadioButton rb42 = new JRadioButton("TIMEZONE");
	
	//	treeとnodeを引数に取るコンストラクタ
	public DisplayContentsOfGroup(Tree ntree, Node nnode) {
    	super("グループの中身");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setVisible(true);
        
        //	引数のtreeとnodeをそれぞれ静的変数に入れる
		tree = ntree;
		node = nnode;

        //	パネルを上中下に分ける。真中はtextArea
        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        getContentPane().add(panel1, BorderLayout.NORTH);
        getContentPane().add(new JScrollPane(textArea), BorderLayout.CENTER);
        getContentPane().add(panel2, BorderLayout.SOUTH);

    	//ボタングループにラジオボタンを追加=同時に二つ以上選択できない
    	ButtonGroup bg = new ButtonGroup();
    	bg.add(rb1);
    	bg.add(rb2);
    	bg.add(rb3);
    	bg.add(rb4);
    	bg.add(rb5);
    	bg.add(rb6);
    	bg.add(rb7);
    	bg.add(rb8);
    	bg.add(rb9);
    	bg.add(rb10);
    	bg.add(rb11);
    	bg.add(rb12);
    	bg.add(rb13);
    	bg.add(rb14);
    	bg.add(rb15);
    	bg.add(rb16);
    	bg.add(rb17);
    	bg.add(rb18);
    	bg.add(rb19);
    	bg.add(rb20);
    	bg.add(rb21);
    	bg.add(rb22);
    	bg.add(rb23);
    	bg.add(rb24);
    	bg.add(rb25);
    	bg.add(rb26);
    	bg.add(rb27);
    	bg.add(rb28);
    	bg.add(rb29);
    	bg.add(rb30);
    	bg.add(rb31);
    	bg.add(rb32);
    	bg.add(rb33);
    	bg.add(rb34);
    	bg.add(rb35);
    	bg.add(rb36);
    	bg.add(rb37);
    	bg.add(rb38);
    	bg.add(rb39);
    	bg.add(rb40);
    	bg.add(rb41);
    	bg.add(rb42);
    	   	
        //	上のパネルにラジオボタンを追加
    	panel1.setLayout(new GridLayout(8,6));	//ボタンの配置の仕方は8*6
    	panel1.add(rb1);
    	panel1.add(rb2);
    	panel1.add(rb3);
    	panel1.add(rb4);
    	panel1.add(rb5);
    	panel1.add(rb6);
    	panel1.add(rb7);
    	panel1.add(rb8);
    	panel1.add(rb9);
    	panel1.add(rb10);
    	panel1.add(rb11);
    	panel1.add(rb12);
    	panel1.add(rb13);
    	panel1.add(rb14);
    	panel1.add(rb15);
    	panel1.add(rb16);
    	panel1.add(rb17);
    	panel1.add(rb18);
    	panel1.add(rb19);
    	panel1.add(rb20);
    	panel1.add(rb21);
    	panel1.add(rb22);
    	panel1.add(rb23);
    	panel1.add(rb24);
    	panel1.add(rb25);
    	panel1.add(rb26);
    	panel1.add(rb27);
    	panel1.add(rb28);
    	panel1.add(rb29);
    	panel1.add(rb30);
    	panel1.add(rb31);
    	panel1.add(rb32);
    	panel1.add(rb33);
    	panel1.add(rb34);
    	panel1.add(rb35);
    	panel1.add(rb36);
    	panel1.add(rb37);
    	panel1.add(rb38);
    	panel1.add(rb39);
    	panel1.add(rb40);
    	panel1.add(rb41);
    	panel1.add(rb42);
    	
    	// イベントリスナの登録。
        rb1.addActionListener(this);
        rb2.addActionListener(this);
        rb3.addActionListener(this);
        rb4.addActionListener(this);
        rb5.addActionListener(this);
        rb6.addActionListener(this);
        rb7.addActionListener(this);
        rb8.addActionListener(this);
        rb9.addActionListener(this);
        rb10.addActionListener(this);
        rb11.addActionListener(this);
        rb12.addActionListener(this);
        rb13.addActionListener(this);
        rb14.addActionListener(this);
        rb15.addActionListener(this);
        rb16.addActionListener(this);
        rb17.addActionListener(this);
        rb18.addActionListener(this);
        rb19.addActionListener(this);
        rb20.addActionListener(this);
        rb21.addActionListener(this);
        rb22.addActionListener(this);
        rb23.addActionListener(this);
        rb24.addActionListener(this);
        rb25.addActionListener(this);
        rb26.addActionListener(this);
        rb27.addActionListener(this);
        rb28.addActionListener(this);
        rb29.addActionListener(this);
        rb30.addActionListener(this);
        rb31.addActionListener(this);
        rb32.addActionListener(this);
        rb33.addActionListener(this);
        rb34.addActionListener(this);
        rb35.addActionListener(this);
        rb36.addActionListener(this);
        rb37.addActionListener(this);
        rb38.addActionListener(this);
        rb39.addActionListener(this);
        rb40.addActionListener(this);
        rb41.addActionListener(this);
        rb42.addActionListener(this);
    	
        //	閉じるボタンを下のパネルに追加
        panel2.add(close);

        //	closeにアクションリスナーをつける
        close.addActionListener(this);
	}
	
	//	それぞれのボタンにアクションが起こったとき
	public void actionPerformed(ActionEvent e) {
		int attribute = 0;
		if (e.getSource().equals(close)) {	//	閉じるボタンが押されたら
            closewindow();
        }
		//	ラジオボタンが押されたら、attributeにそれぞれの属性番号を入れる
		else if(e.getSource().equals(rb1)){
			attribute = 1;
		}
		else if(e.getSource().equals(rb2)){
			attribute = 2;
		}
		else if(e.getSource().equals(rb3)){
			attribute = 3;
		}
		else if(e.getSource().equals(rb4)){
			attribute = 4;
		}
		else if(e.getSource().equals(rb5)){
			attribute = 5;
		}
		else if(e.getSource().equals(rb6)){
			attribute = 6;
		}
		else if(e.getSource().equals(rb7)){
			attribute = 7;
		}
		else if(e.getSource().equals(rb8)){
			attribute = 8;
		}
		else if(e.getSource().equals(rb9)){
			attribute = 9;
		}
		else if(e.getSource().equals(rb10)){
			attribute = 10;
		}
		else if(e.getSource().equals(rb11)){
			attribute = 11;
		}
		else if(e.getSource().equals(rb12)){
			attribute = 12;
		}
		else if(e.getSource().equals(rb13)){
			attribute = 13;
		}
		else if(e.getSource().equals(rb14)){
			attribute = 14;
		}
		else if(e.getSource().equals(rb15)){
			attribute = 15;
		}
		else if(e.getSource().equals(rb16)){
			attribute = 16;
		}
		else if(e.getSource().equals(rb17)){
			attribute = 17;
		}
		else if(e.getSource().equals(rb18)){
			attribute = 18;
		}
		else if(e.getSource().equals(rb19)){
			attribute = 19;
		}
		else if(e.getSource().equals(rb20)){
			attribute = 20;
		}
		else if(e.getSource().equals(rb21)){
			attribute = 21;
		}
		else if(e.getSource().equals(rb22)){
			attribute = 22;
		}
		else if(e.getSource().equals(rb23)){
			attribute = 23;
		}
		else if(e.getSource().equals(rb24)){
			attribute = 24;
		}
		else if(e.getSource().equals(rb25)){
			attribute = 25;
		}
		else if(e.getSource().equals(rb26)){
			attribute = 26;
		}
		else if(e.getSource().equals(rb27)){
			attribute = 27;
		}
		else if(e.getSource().equals(rb28)){
			attribute = 28;
		}
		else if(e.getSource().equals(rb29)){
			attribute = 29;
		}
		else if(e.getSource().equals(rb30)){
			attribute = 30;
		}
		else if(e.getSource().equals(rb31)){
			attribute = 31;
		}
		else if(e.getSource().equals(rb32)){
			attribute = 32;
		}
		else if(e.getSource().equals(rb33)){
			attribute = 33;
		}
		else if(e.getSource().equals(rb34)){
			attribute = 34;
		}
		else if(e.getSource().equals(rb35)){
			attribute = 35;
		}
		else if(e.getSource().equals(rb36)){
			attribute = 36;
		}
		else if(e.getSource().equals(rb37)){
			attribute = 37;
		}
		else if(e.getSource().equals(rb38)){
			attribute = 38;
		}
		else if(e.getSource().equals(rb39)){
			attribute = 39;
		}
		else if(e.getSource().equals(rb40)){
			attribute = 40;
		}
		else if(e.getSource().equals(rb41)){
			attribute = 41;
		}
		else if(e.getSource().equals(rb42)){
			attribute = 42;
		}
		if(attribute != 0){	//	閉じるボタンが押された時はこれやっちゃうとエラー
			displayAttribute(attribute);
		}
	}
	
	//	ウィンドウを閉じる
	public void closewindow(){
		this.hide();
	}
	
	//	ダブルクリックしたノードを取ってきて、それが属すグループ(という名のbranch)を返す
	public Branch getNode(Node node){
		System.out.println(node + "っていうノードがとれたよ！");
		Node nnode = node;
		Branch branch = nnode.getCurrentBranch();	//	nodeの所属グループを返す
		System.out.println("↑のノードは" + branch + "のブランチに属しています");
		return branch;
	}
	
	//	引数：どのラジオボタンが選択されているか
	//	ここで
	public void displayAttribute(int attribute){
		
		TreeTable tg = (TreeTable)tree.table;	//	treeをtreetableに変換		
		int numTable = tg.getNumTable();	//tgが保有するtableの総数＝属性数
		//	ダブルクリックしたノードが属しているグループ（branch）をとってくる
        Branch branch = getNode(node);
		//	グループに属するノードのリストを返す
		Vector vector = branch.getNodeList();
		//	取れた値のリスト
		Vector valuevector = new Vector();
		
		//	ベクトルの中身を順に見ていく
		for(int j = 0; j < vector.size(); j++){
			node = (Node) vector.elementAt(j);
			//	以下ベクトルに入っているノードの特定の属性の値を求める
			if (node == null || node.getChildBranch() != null) {
				textArea.setText("ノード入ってないか子がnullだよ");
				continue;
			}
			Table table = tg.getTable(attribute);
			int tabletype = table.getType();
			NodeTablePointer tn = node.table;
			int id = tn.getId(attribute);
			String value = "";
			if (tabletype == table.TABLE_STRING) { /* STRING */
        
				value = table.getString(id);
			}
			if (tabletype == table.TABLE_DOUBLE) { /* DOUBLE */
				value = Double.toString(table.getDouble(id));
			}
			if (tabletype == table.TABLE_INT) { /* INT */
				value = Integer.toString(table.getInt(id));
			}
			//	valueに特定の値の属性値を格納完了
			if(j == 0){	//	最初のベクトルなら
				textArea.setText(value);
			}
			else{	//	それ以外のベクトルなら
				String text = textArea.getText();	//	まず今入力されているtextを取得
				textArea.setText(text + "," + value);	//	次いで新しく取得したvalueを追加
			}
			valuevector.addElement(value);
			//	次はvaluevectorの中に入ってるベクトルが何が何種類あるのか調べて表示したい
		}
	}
}
