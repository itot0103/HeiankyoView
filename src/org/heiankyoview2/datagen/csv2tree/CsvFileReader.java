package org.heiankyoview2.datagen.csv2tree;

import org.heiankyoview2.core.tree.Branch;
import org.heiankyoview2.core.tree.Tree;
import org.heiankyoview2.core.tree.Node;
import org.heiankyoview2.core.table.TreeTable;
import org.heiankyoview2.core.table.NodeTablePointer;
import org.heiankyoview2.core.table.Table;
import org.heiankyoview2.core.fileio.TreeFileWriter;

import java.io.*;
import java.util.*;


/**
 * CSV�t�@�C����ǂݍ����Tree���\�z����
 * @author itot
 */
public class CsvFileReader{
	Tree tree = null;
	int numlevel = 0;
	int numvalue = 0;
	String labels[] = null;
	
	File csvFile;
	BufferedReader reader;
	
	/**
	 * Constructor
	 * @param c2n Csv2Tree
	 */
	public CsvFileReader(File csvFile) {
		// �t�@�C�����J��
		try {
			reader = new BufferedReader(new FileReader(csvFile));
		} catch (FileNotFoundException e) {
			System.err.println(e);
		}
	}
	
	
	/**
	 * �ŏ���1�s�ŁA�K�w�̐[����A�ϐ��̎�������ǂݎ��
	 */
	void consumeFirstLine(StringTokenizer tokenBuffer) {
		int numtoken = tokenBuffer.countTokens();
		labels = new String[numtoken];
		Table table = null;
		int counter = 0;
		
		// �g�[�N����1�����߂���
		while(tokenBuffer.countTokens() > 0) {
			String word = tokenBuffer.nextToken();
			labels[counter++] = word;
			
			// �K�w�̐[�����X�V����
			if(word.startsWith("Level")) {
				StringTokenizer tokenBuffer2 = new StringTokenizer(word, " ");
				tokenBuffer2.nextToken();
				String snum = tokenBuffer2.nextToken();
				int level = Integer.parseInt(snum);
				if(numlevel < level) numlevel = level;
			}
			
			// �ϐ��̎��������X�V����
			if(word.startsWith("Value")) {
				StringTokenizer tokenBuffer2 = new StringTokenizer(word, " ");
				tokenBuffer2.nextToken();
				String snum = tokenBuffer2.nextToken();
				int value = Integer.parseInt(snum);
				if(numvalue < value) numvalue = value;
			}
		}
		
		// �l���s���ł����tree��null��������
		if(numtoken != (numlevel + numvalue + 1)) {
			tree = null;  return;
		}
		if(numlevel <= 0 || numvalue <= 0) {
			tree = null;  return;
		}
		
		// Tree���m�ۂ���
		tree = new Tree();
		
		// 1����Branch��L����\�����\�z����
		tree.setNumBranch(1);
		Branch rootBranch = (Branch) tree.getBranchList().elementAt(0);
		Node rootNode = tree.getRootNode();
		tree.setRootBranch(rootBranch);
		rootBranch.setParentNode(rootNode);
		rootNode.setChildBranch(rootBranch);
		
		// �e�[�u����p�ӂ���
		tree.table.setNumTable(numtoken);
		
		// Node���̃e�[�u��
		table = new Table();
		table.setType(table.TABLE_STRING);
		table.setName("Name");
		tree.table.setTable(1, table);
		
		// �K�w���̃e�[�u��
		for(int i = 1; i <= numlevel; i++) {
			table = new Table();
			table.setType(table.TABLE_STRING);
			table.setName("Level " + i);
			tree.table.setTable((i + 1), table);
		}
		
		// �ϐ����̃e�[�u��
		for(int i = 1; i <= numvalue; i++) {
			table = new Table();
			table.setType(table.TABLE_DOUBLE);
			table.setName("Value " + i);
			tree.table.setTable((i + numlevel + 1), table);
		}
		
	}
	
	
	/**
	 * ���ꂩ��o�^���ׂ�Table��ID��Ԃ�
	 */
	int getTableId(int counter) {
		String label = labels[counter];
		
		// ���O
		if(label.startsWith("Name") == true) return 1;
		
		// �K�w�̐[��
		if(label.startsWith("Level") == true) {
			StringTokenizer token = new StringTokenizer(label, " ");
			token.nextToken();
			String snum = token.nextToken();
			int level = Integer.parseInt(snum);
			return (1 + level);
		}
		
		// �ϐ��̎�����
		if(label.startsWith("Value") == true) {
			StringTokenizer token = new StringTokenizer(label, " ");
			token.nextToken();
			String snum = token.nextToken();
			int value = Integer.parseInt(snum);
			return (1 + numlevel + value);
		}
		
		return -1;
	}
	

	/**
	 * ���ꂩ��o�^����Node����Table�ւ̃|�C���^��ID��Ԃ�
	 */
	int getNodePointerId(Table table, String word) {
		int id = 0;
		
		// �����^
		if(table.getType() == table.TABLE_STRING) {
			
			// ���������^�����ɓo�^����Ă��邩��������
			for(int i = 1; i <= table.getSize(); i++) {
				String w = table.getString(i);
				if(w.compareTo(word) == 0) {
					id = i;  break;
				}
			}
			
			// �o�^����Ă��Ȃ���΁A�V�����o�^����
			if(id <= 0) {
				id = table.getSize() + 1;
				table.set(id, word);
			}
		}
		
		// �����^
		if(table.getType() == table.TABLE_DOUBLE) {
			double value = Double.parseDouble(word);
			id = table.getSize() + 1;
			table.set(id, value);
		}
		
		// ID��Ԃ�
		return id;
	}
	
	
	/**
	 * Node��1�ǉ�����
	 * @param tree
	 * @param values
	 */
	public void addOneNode(StringTokenizer tokenBuffer) {
		int counter = 0;
		
		// Node��1�m�ۂ���
		Branch branch = tree.getRootBranch();
		Node node = branch.getOneNewNode();
		node.setCurrentBranch(branch);
		node.table.setNumId(labels.length);
		
		// �g�[�N����1�����߂���
		while(tokenBuffer.countTokens() > 0) {
			String word = tokenBuffer.nextToken();
			int tableId = getTableId(counter++);
			Table table = tree.table.getTable(tableId);
			int nid = getNodePointerId(table, word);
			node.table.setId(tableId, nid);
		}
		
	}

	
	/**
	 * Node�𕪊�����
	 * @param branch
	 * @param level
	 */
	public void divideNodes(Branch branch, int level) {
		int tid = -1;
		Table table = null;
		Node nodearray[] = new Node[branch.getNodeList().size()];
		
		// Node�̃R�s�[���Ƃ�A���X�g���炢������폜����
		for(int i = 0; i < nodearray.length; i++) {
			Node node = (Node)branch.getNodeList().elementAt(0);
			nodearray[i] = node;
			branch.getNodeList().remove(node);
		}
		
		// ���x���ɑΉ�����Table��T��
		for(int i = 1; i <= tree.table.getNumTable(); i++) {
			table = tree.table.getTable(i);
			String tname = table.getName();
			if(tname.startsWith("Level") == false) continue;
			StringTokenizer token = new StringTokenizer(tname," ");
			token.nextToken();
			String sl = token.nextToken();
			if(Integer.parseInt(sl) == level) {
				tid = i;    break;
			}
		}
		//System.out.println("   numtable=" + tree.table.getNumTable() + " tid=" + tid);
		if(tid < 0) return;
		
		// ���XBranch�����ɂ������e�X��Node�ɂ���
		for(int i = 0; i < nodearray.length; i++) {
			Node node = nodearray[i];
			int nodeid1 = node.table.getId(tid);
			String nname = table.getString(nodeid1);
			//System.out.println("  level=" + level + " i=" + i + " nname=" + nname);
			
			// ���ɐ�������Ă���qBranch�Ƃ̔�r
			int j = 0;
			for(j = 0; j < branch.getNodeList().size(); j++) {
				Node cnode = (Node)branch.getNodeList().elementAt(j);
				Branch cbranch = cnode.getChildBranch();
				if(cbranch == null) continue;
				String bname = cbranch.getName();
				
				// ���YNode�ƎqBranch����v����悤�Ȃ�A�qBranch�ɓo�^����
				if(bname.compareTo(nname) == 0) {
					cbranch.addOneNode(node);
					node.setCurrentBranch(cbranch);
					break;
				}
			}
			
			// ���YNode���qBranch�ɓo�^����Ȃ��Ȃ�A�V�����qBranch�𐶐�����
			if(j >= branch.getNodeList().size()) {
				Node cnode = branch.getOneNewNode();
				Branch cbranch = tree.getOneNewBranch();
				cnode.setChildBranch(cbranch);
				cbranch.setParentNode(cnode);
				cbranch.addOneNode(node);
				cbranch.setName(nname);
				node.setCurrentBranch(cbranch);
				cbranch.setName(nname);
			}
			
		}
		
		
		// �V�����qBranch�ɑ΂��ē��l�ȏ������ċA�I�ɓK�p����
		for(int i = 0; i < branch.getNodeList().size(); i++) {
			Node node = (Node)branch.getNodeList().elementAt(i);
			Branch cbranch = node.getChildBranch();
			if(cbranch != null && level < numlevel)
				divideNodes(cbranch, (level + 1));
		}
		
		
	}
	
	
	
	/**
	 * CSV�t�@�C�����J��
	 * @param csvFilename ����CSV�t�@�C����
	 * @return �������ꂽTree
	 */
	public Tree getTree() {
		int count = -1;
		
		// �t�@�C����1�s���ǂ�
		try {
			reader.ready();
			while (true) {
				String line = reader.readLine();
				//System.out.println(" line=" + line);
				if (line == null || line.length() <= 0)
					break;
				StringTokenizer tokenBuffer = new StringTokenizer(line, ",");

				// �ŏ���1�s�ŁA�K�w�̐[����A�ϐ��̎�������ǂݎ��
				if(count < 0) {
					consumeFirstLine(tokenBuffer);
					if(tree == null) return null;
				}
				
				// 2�s�ڈȍ~�ŁA�m�[�h��1���ǉ�����
				else
					addOneNode(tokenBuffer);
				
				// �s�����J�E���g����
				count++;
			}
			
			// �t�@�C�������
			reader.close();

		} catch (IOException e) {
			System.err.println(e);
			return null;
		}

		// �ċA�I��Node�𕪊�����
		divideNodes(tree.getRootBranch(), 1);
		
		// Node��ID������U�蒼��
		for(int j = 0; j < tree.getBranchList().size(); j++) {
			Branch branch = (Branch)tree.getBranchList().elementAt(j);
		
			for(int i = 0; i < branch.getNodeList().size(); i++) {
				Node node = (Node)branch.getNodeList().elementAt(i);
				node.setCurrentBranch(branch);
				node.setId(i + 1);
				//System.out.println("   branchID=" + branch.getId() + " i=" + i + " nodeId=" + node.getId());
			}
		}
		
		// Tree��Ԃ�
		return tree;
	}

	/**
	 * main�֐�
	 * @param args ����
	 */
	public static void main(String[] args) {
		
		// �t�@�C��������肷��
		String csvFilename = "data/csv/sample20081210.csv";
		String treeFilename = "tmp.tree";
		if(args.length >= 2) {
			csvFilename = args[0];
			treeFilename = args[1];
		}
		
		// �t�@�C����ǂ�
		CsvFileReader fcfile = new CsvFileReader(new File(csvFilename));
		Tree tree = fcfile.getTree();
		
		// �ϊ����ʂ�ʃt�@�C���ɏ���
		TreeFileWriter output = new TreeFileWriter(new File(treeFilename), tree);
		output.writeTree();
	}
}