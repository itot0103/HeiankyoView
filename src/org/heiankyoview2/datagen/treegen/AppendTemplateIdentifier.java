package org.heiankyoview2.datagen.treegen;

import org.heiankyoview2.core.tree.*;
import org.heiankyoview2.core.table.*;
import org.heiankyoview2.core.fileio.TreeFileReader;
import org.heiankyoview2.core.fileio.TreeFileWriter;

import java.io.*;

public class AppendTemplateIdentifier {
	static int totalNum = 0;
	static int counter = 0;
	static Tree tree = null;
	
	
	/**
	 * Node��Branch�̑������J�E���g����
	 */
	static void countTotalNum(Branch branch) {

		// ���YBranch�ɑ΂���1�����Z����
		totalNum++;
		
		// �e�X��Node�ɑ΂���
		for(int i = 0; i < branch.getNodeList().size(); i++) {
			Node node = (Node)branch.getNodeList().elementAt(i);
			if(node.getChildBranch() != null)
				countTotalNum(node.getChildBranch());
			else
				totalNum++;
		}
		
	}
	
	
	/**
	 * �ʂ��ԍ��̂��߂�Table��1�ǉ�����
	 */
	static void addTable() {
		Table itable = new Table();
		
		// Table��1���₷
		TreeTable tt = tree.table;
		int numt = tt.getNumTable();
		Table tarray[] = new Table[numt];
		for(int i = 1; i <= numt; i++)
			tarray[i - 1] = tt.getTable(i);
		tt.setNumTable(numt + 1);
		tt.setTable(1, itable);
		for(int i = 0; i < numt; i++)
			tt.setTable((i + 2), tarray[i]);
		
		// �ʂ��ԍ��̃e�[�u�����\�z����
		itable.setName("identifier");
		itable.setType(itable.TABLE_STRING);
		itable.setSize(totalNum);
		for(int i = 1; i < totalNum; i++)
			itable.set(i, Integer.toString(i));
		
	}
	
	/**
	 * 1��Node�ɒʂ��ԍ�������U��
	 */
	static void assignIdentifierOneNode(Node node) {
		
		// NodeTablePointer��1���₷
		NodeTablePointer ntp = node.table;
		int numt = ntp.getNumId();
		int tarray[] = new int[numt];
		for(int i = 1; i <= numt; i++)
			tarray[i - 1] = ntp.getId(i);
		ntp.setNumId(numt + 1);
		ntp.setId(1, ++counter);
		for(int i = 0; i < numt; i++)
			ntp.setId((i + 2), tarray[i]);
	}
	
	
	/**
	 * Node��Branch�ɒʂ��ԍ�������U��
	 */
	static void assignIdentifier(Branch branch) {
		
		// ����Branch���g�ɒʂ��ԍ�������U��
		Node pnode = branch.getParentNode();
		assignIdentifierOneNode(pnode);
		
		// �e�X��Node�ɂ���
		for(int i = 0; i < branch.getNodeList().size(); i++) {
			Node node = (Node)branch.getNodeList().elementAt(i);
			if(node.getChildBranch() != null)
				assignIdentifier(node.getChildBranch());
			else
				assignIdentifierOneNode(node);
		}
		
	}
	
	
	/**
	 * main�֐�
	 */
	public static void main(String[] args) {
		
		String filename1 = "C:/itot/projects/HeiankyoView2/data/template/jiali-min10max100_4.tree"; // tree�t�@�C����
		String filename2 = "C:/itot/projects/HeiankyoView2/data/template/jiali-min10max100_4t.tree";
		
		// tree�t�@�C����ǂ�
		TreeFileReader tfr = new TreeFileReader(new File(filename1));
		tree = tfr.getTree();
		
		// Node��Branch�̑������J�E���g����
		totalNum = 1;
		countTotalNum(tree.getRootBranch());
		
		// Table��1�m�ۂ���
		addTable();
		
		// Node��Branch�ɒʂ��ԍ�������
		counter = 0;
		assignIdentifier(tree.getRootBranch());
		
		// tree�t�@�C��������
		TreeFileWriter tnf = new TreeFileWriter(new File(filename2), tree);
		tnf.writeTree();
		
	}
}
