package org.heiankyoview2.core.glwindow;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultEditorKit.CopyAction;
import javax.swing.text.DefaultEditorKit.CutAction;
import javax.swing.text.DefaultEditorKit.PasteAction;

public class TextFileOpenrer extends JFrame implements ActionListener{

private JTextArea textArea = new JTextArea();
    
    //�J�b�g�A�R�s�[�A�y�[�X�g�p�A�N�V����
    private Action cutAction = new CutAction();
    private Action copyAction = new CopyAction();
    private Action pasteAction = new PasteAction();

    //	���ꂼ��̃{�^��
    private JButton open = new JButton("�J��");
    private JButton save = new JButton("�ۑ�");
    private JButton close = new JButton("����");
    private JButton cut = new JButton(cutAction);
    private JButton copy = new JButton(copyAction);
    private JButton paste = new JButton(pasteAction);

    //	�C�ӂ̃t�@�C����I�Ԃ��߂ɗp��
    private JFileChooser chooser = new JFileChooser();


    //�R���X�g���N�^
    public TextFileOpenrer() {
    	super("Log");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 300);
        move(500, 0);
        setVisible(true);

        //	�p�l�����㒆���ɕ�����B�^����textArea
        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        getContentPane().add(panel1, BorderLayout.NORTH);
        getContentPane().add(new JScrollPane(textArea), BorderLayout.CENTER);
        getContentPane().add(panel2, BorderLayout.SOUTH);

        //	�J���ƕۑ��ƕ���͏㕔�̃p�l���ɁA�J�b�g�E�R�s�[�E�y�[�X�g�͉����̃p�l���ɒǉ�
        panel1.add(open);
        panel1.add(save);
        panel1.add(close);
        panel2.add(cut);
        panel2.add(copy);
        panel2.add(paste);

        //	open��save��close�ɃA�N�V�������X�i�[������
        open.addActionListener(this);
        save.addActionListener(this);
        close.addActionListener(this);

    }
	
    //	���ꂼ��̃{�^���ɃA�N�V�������N�������Ƃ�
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(open)) {	//	�J���{�^���������ꂽ��
            openFile();
        }
		else if(e.getSource().equals(save)){	//	�ۑ��{�^���������ꂽ��
			saveFile();
		}
		else if(e.getSource().equals(close)){	//	����{�^���������ꂽ��
			this.hide();
		}
	}
	
	//	�t�@�C�����J���Ƃ��̏���
	private void openFile() {
		int returnVal = chooser.showOpenDialog(this);
	    try {
	    	if (returnVal == JFileChooser.APPROVE_OPTION) {
	    		File file = chooser.getSelectedFile();
	            FileReader reader = new FileReader(file);
	            textArea.read(reader, null);
	            setTitle(file.getAbsolutePath());
	            reader.close();
	        }
	    } catch (FileNotFoundException e) {
	    	e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	 }
	 
	//	�t�@�C����ۑ�����Ƃ��̏���
	 private void saveFile(){
		 int returnVal = chooser.showSaveDialog(this);
		 try{
			 if(returnVal == JFileChooser.APPROVE_OPTION){
				 File file = chooser.getSelectedFile();
				 FileWriter writer = new FileWriter(file);
				 textArea.write(writer);
				 writer.close();
			 }
		} catch (FileNotFoundException e) {
			 e.printStackTrace();
		} catch (IOException e) {
	            e.printStackTrace();
	    }
	}
}