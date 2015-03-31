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
    
    //カット、コピー、ペースト用アクション
    private Action cutAction = new CutAction();
    private Action copyAction = new CopyAction();
    private Action pasteAction = new PasteAction();

    //	それぞれのボタン
    private JButton open = new JButton("開く");
    private JButton save = new JButton("保存");
    private JButton close = new JButton("閉じる");
    private JButton cut = new JButton(cutAction);
    private JButton copy = new JButton(copyAction);
    private JButton paste = new JButton(pasteAction);

    //	任意のファイルを選ぶために用意
    private JFileChooser chooser = new JFileChooser();


    //コンストラクタ
    public TextFileOpenrer() {
    	super("Log");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 300);
        move(500, 0);
        setVisible(true);

        //	パネルを上中下に分ける。真中はtextArea
        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        getContentPane().add(panel1, BorderLayout.NORTH);
        getContentPane().add(new JScrollPane(textArea), BorderLayout.CENTER);
        getContentPane().add(panel2, BorderLayout.SOUTH);

        //	開くと保存と閉じるは上部のパネルに、カット・コピー・ペーストは下部のパネルに追加
        panel1.add(open);
        panel1.add(save);
        panel1.add(close);
        panel2.add(cut);
        panel2.add(copy);
        panel2.add(paste);

        //	openとsaveとcloseにアクションリスナーをつける
        open.addActionListener(this);
        save.addActionListener(this);
        close.addActionListener(this);

    }
	
    //	それぞれのボタンにアクションが起こったとき
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(open)) {	//	開くボタンが押されたら
            openFile();
        }
		else if(e.getSource().equals(save)){	//	保存ボタンが押されたら
			saveFile();
		}
		else if(e.getSource().equals(close)){	//	閉じるボタンが押されたら
			this.hide();
		}
	}
	
	//	ファイルを開くときの処理
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
	 
	//	ファイルを保存するときの処理
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