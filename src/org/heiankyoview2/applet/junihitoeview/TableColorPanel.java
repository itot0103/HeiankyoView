/*
 * Created on 2005/10/25
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.heiankyoview2.applet.junihitoeview;

import java.awt.*;
import java.awt.geom.GeneralPath;

import javax.swing.*;



/**
 * @author itot
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TableColorPanel extends JPanel {
	Color color[] = new Color[4];
	int paintType = 2;
	
	/**
	 * Constructor
	 */
	public TableColorPanel() {
		super();
		setSize(80, 30);
	}
	

	/**
	 * ï`âÊÇé¿çsÇ∑ÇÈ
	 */
	public void draw(Color c[]) {
		for(int i = 0; i < c.length; i++)
			color[i] = c[i];
		Graphics g = getGraphics();
		paintComponent(g);
	}
	
	
	/**
	 * ï`âÊÉ^ÉCÉvÇÉZÉbÉgÇ∑ÇÈ
	 */
	public void setPaintType(int t) {
		paintType = t;
	}
	
		
	/**
	 * ï`âÊÇé¿çsÇ∑ÇÈ
	 * @param g Graphics
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g); // clear the background
		
		if(paintType == 1) paint1(g);
		if(paintType == 2) paint2(g);
	}
	
	
	/**
	 * ï`âÊÇªÇÃ1
	 */
	void paint1(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		
		GeneralPath polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 4);
		g2.setPaint(color[0]);
		polygon.moveTo(75, 10);
		polygon.lineTo(75, 20);
		polygon.lineTo(65, 20);
		polygon.lineTo(65, 10);
		polygon.closePath();
		g2.fill(polygon);
		
		polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 4);
		g2.setPaint(color[1]);
		polygon.moveTo(45, 10);
		polygon.lineTo(45, 20);
		polygon.lineTo(55, 20);
		polygon.lineTo(55, 10);
		polygon.closePath();
		g2.fill(polygon);
		
		polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 4);
		g2.setPaint(color[1]);
		polygon.moveTo(30, 10);
		polygon.lineTo(35, 15);
		polygon.lineTo(30, 20);
		polygon.lineTo(25, 15);
		polygon.closePath();
		g2.fill(polygon);
		
		polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 4);
		g2.setPaint(color[0]);
		polygon.moveTo(10, 10);
		polygon.lineTo(15, 15);
		polygon.lineTo(10, 20);
		polygon.lineTo( 5, 15);
		polygon.closePath();
		g2.fill(polygon);
		
	}
	
	
	/**
	 * ï`âÊÇªÇÃ2
	 */
	void paint2(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		
		GeneralPath polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 4);
		g2.setPaint(color[3]);
		polygon.moveTo(75, 10);
		polygon.lineTo(75, 20);
		polygon.lineTo(65, 20);
		polygon.lineTo(65, 10);
		polygon.closePath();
		g2.fill(polygon);
		
		polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 4);
		g2.setPaint(color[2]);
		polygon.moveTo(45, 10);
		polygon.lineTo(45, 20);
		polygon.lineTo(55, 20);
		polygon.lineTo(55, 10);
		polygon.closePath();
		g2.fill(polygon);
		
		polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 4);
		g2.setPaint(color[1]);
		polygon.moveTo(25, 10);
		polygon.lineTo(25, 20);
		polygon.lineTo(35, 20);
		polygon.lineTo(35, 10);
		polygon.closePath();
		g2.fill(polygon);
		
		polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 4);
		g2.setPaint(color[0]);
		polygon.moveTo( 5, 10);
		polygon.lineTo( 5, 20);
		polygon.lineTo(15, 20);
		polygon.lineTo(15, 10);
		polygon.closePath();
		g2.fill(polygon);
		
	}

}
