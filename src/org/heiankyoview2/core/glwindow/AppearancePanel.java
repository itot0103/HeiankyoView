package org.heiankyoview2.core.glwindow;

import org.heiankyoview2.core.draw.Canvas;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * HeianViewの表示設定に関する操作をするパネル
 * @author itot
 */
public class AppearancePanel extends JDialog {

	/* var */
	public Container container;
	public JButton okButton, cancelButton, applyButton;
	public JSlider linewSlider, redSlider, greenSlider, blueSlider;
	public JRadioButton annotationOnRadio, annotationOffRadio;
	JTextField linewText, redText, greenText, blueText;
	JTextField cfilterText, hfilterText, ccoefText, hcoefText;

	Font font = null;

	Canvas canvas;

	/* Action listener */
	SliderListener sl = null;
	ButtonListener bl = null;
	RadioButtonListener rbl = null;

	/**
	 * Constructor
	 */
	public AppearancePanel() {
		// super class init
		super();

		// this setup
		setTitle("Appearance for 3D rendering");
		setSize(550, 300);
		makeWindowCloseCheckBox();
		font = new Font("Serif", Font.ITALIC, 16);

		container = getContentPane();
		container.setLayout(new BorderLayout());

		Panel pul = new Panel();
		pul.setLayout(new GridLayout(6, 1));
		Label l1dummy = new Label("");
		Label l2dummy = new Label("");
		Label l3dummy = new Label("");
		Label l2r = new Label("R");
		Label l2g = new Label("G");
		Label l2b = new Label("B");
		pul.add(l1dummy);
		pul.add(l2dummy);
		pul.add(l3dummy);
		pul.add(l2r);
		pul.add(l2g);
		pul.add(l2b);

		Panel put = new Panel();
		put.setLayout(new GridLayout(6, 1));
		Label t1dummy = new Label("");
		linewText = new JTextField("1.0", 4);
		Label t2dummy = new Label("");
		redText = new JTextField("0.0", 4);
		greenText = new JTextField("0.0", 4);
		blueText = new JTextField("0.0", 4);
		put.add(t1dummy);
		put.add(linewText);
		put.add(t2dummy);
		put.add(redText);
		put.add(greenText);
		put.add(blueText);

		Panel pus = new Panel();
		pus.setLayout(new GridLayout(6, 1));
		Label s1wtitle = new Label("Line width");
		linewSlider = new JSlider(JSlider.HORIZONTAL, 100, 500, 100);
		Label s2title = new Label("Background color");
		redSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 0);
		greenSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 0);
		blueSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 0);
		pus.add(s1wtitle);
		pus.add(linewSlider);
		pus.add(s2title);
		pus.add(redSlider);
		pus.add(greenSlider);
		pus.add(blueSlider);

		Panel pft = new Panel();
		pft.setLayout(new GridLayout(8, 1));
		Label t1ftdummy = new Label("");
		ccoefText = new JTextField("1.0", 4);
		Label t2ftdummy = new Label("");
		cfilterText = new JTextField("0.0", 4);
		Label t3ftdummy = new Label("");
		hcoefText = new JTextField("1.0", 4);
		Label t4ftdummy = new Label("");
		hfilterText = new JTextField("0.0", 4);
		pft.add(t1ftdummy);
		pft.add(ccoefText);
		pft.add(t2ftdummy);
		pft.add(cfilterText);
		pft.add(t3ftdummy);
		pft.add(hcoefText);
		pft.add(t4ftdummy);
		pft.add(hfilterText);

		Panel panel = new Panel();
		panel.setLayout(new FlowLayout());
		panel.add(pul);
		panel.add(put);
		panel.add(pus);
		panel.add(pft);
		
		Panel southPanel = new Panel();
		okButton = new JButton("Ok");
		cancelButton = new JButton("Cancel");
		applyButton = new JButton("Apply");

		annotationOnRadio = new JRadioButton("Annotation ON");
		annotationOffRadio = new JRadioButton("Annotation OFF");

		ButtonGroup bg2 = new ButtonGroup();
		bg2.add(annotationOnRadio);
		bg2.add(annotationOffRadio);

		Panel plr = new Panel();
		plr.setLayout(new GridLayout(1, 2));
		plr.add(annotationOnRadio);
		plr.add(annotationOffRadio);

		southPanel.add(okButton);
		southPanel.add(cancelButton);
		southPanel.add(applyButton);
		southPanel.add(plr);

		container.add(panel, "Center");
		container.add(southPanel, "South");
		setVisible(true);

		if (sl == null)
			sl = new SliderListener();
		addSliderListener(sl);

		if (bl == null)
			bl = new ButtonListener();
		addButtonListener(bl);

		if (rbl == null)
			rbl = new RadioButtonListener();
		addRadioButtonListener(rbl);
	}

	/**
	 * Canvas をセットする
	 * @param c Canvas
	 */
	public void setCanvas(Object c) {
		canvas = (Canvas) c;
	}

	public void actionPerformed(ActionEvent evt) { // override
		;
	}

	protected void makeWindowCloseCheckBox() {
		addWindowListener(new WindowAdapter() { // inner class
			public void windowClosing(WindowEvent e) {
				setVisible(false);
			}
		});
	}

	/**
	 * スライダのアクションの検出を設定する
	 * @param actionListener ActionListener
	 */
	public void addSliderListener(ChangeListener changeListener) {
		linewSlider.addChangeListener(changeListener);
		redSlider.addChangeListener(changeListener);
		greenSlider.addChangeListener(changeListener);
		blueSlider.addChangeListener(changeListener);
	}

	/**
	 * ラジオボタンのアクションの検出を設定する
	 * @param actionListener ActionListener
	 */
	public void addRadioButtonListener(ActionListener actionListener) {
		annotationOnRadio.addActionListener(actionListener);
		annotationOffRadio.addActionListener(actionListener);
	}

	/**
	 * ボタンのアクションの検出を設定する
	 * @param actionListener ActionListener
	 */
	public void addButtonListener(ActionListener actionListener) {
		okButton.addActionListener(actionListener);
		cancelButton.addActionListener(actionListener);
		applyButton.addActionListener(actionListener);
	}

	/**
	 * パネルに表示されている値をセットする
	 */
	public void setValues() {
		setLinewidthValue();
		setBgcolorValue();
	}

	/**
	 * 線の太さの値をパネルからセットする
	 */
	void setLinewidthValue() {
		double linew = (double) linewSlider.getValue() / 100.0;
		linewText.setText(enshortDoubleText(linew));
		canvas.setLinewidth(linew);
	}

	/**
	 * 背景色の値をパネルからセットする
	 */
	void setBgcolorValue() {
		double r = (double) redSlider.getValue() / 255.0;
		double g = (double) greenSlider.getValue() / 255.0;
		double b = (double) blueSlider.getValue() / 255.0;
		redText.setText(enshortDoubleText(r));
		greenText.setText(enshortDoubleText(g));
		blueText.setText(enshortDoubleText(b));
		canvas.setBackground(r, g, b);
	}


	/**
	 * 浮動小数値を短い文字列に変換する
	 * @param value 浮動小数値
	 * @return 小数を表す文字列
	 */
	String enshortDoubleText(double value) {
		String s = Double.toString(value);
		if (s.length() > 4)
			s = s.substring(0, 4);
		return s;
	}

	/**
	 * ボタンのアクションを検知するActionListener
	 * @author itot
	 */
	class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JButton buttonPushed = (JButton) e.getSource();
			if (buttonPushed == okButton) {
				setValues();
				setVisible(false);
				canvas.display();
			}
			if (buttonPushed == cancelButton) {
				setVisible(false);
			}
			if (buttonPushed == applyButton) {
				setValues();
				canvas.display();
			}
		}
	}

	/**
	 * ラジオボタンのアクションを検知するActionListener
	 * @author itot
	 */
	class RadioButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JRadioButton buttonPushed = (JRadioButton) e.getSource();

			if (buttonPushed == annotationOnRadio) {
				canvas.setAnnotationSwitch(true);
			}
			if (buttonPushed == annotationOffRadio) {
				canvas.setAnnotationSwitch(false);
			}

			canvas.display();
		}
	}

	/**
	 * スライダのアクションを検知するActionListener
	 * @author itot
	 */
	class SliderListener implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			JSlider sliderChanged = (JSlider) e.getSource();
			if (sliderChanged == linewSlider) {
				setLinewidthValue();
			}
			if (sliderChanged == redSlider) {
				setBgcolorValue();
			}
			if (sliderChanged == greenSlider) {
				setBgcolorValue();
			}
			if (sliderChanged == blueSlider) {
				setBgcolorValue();
			}
		}
	}

}
