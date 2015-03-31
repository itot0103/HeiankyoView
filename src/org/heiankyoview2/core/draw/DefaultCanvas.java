package org.heiankyoview2.core.draw; 

import org.heiankyoview2.core.tree.Tree;
import org.heiankyoview2.core.table.TreeTable;

import java.awt.*;


/**
 * HeianView ‚Ì‚½‚ß‚Ì•`‰æ—Ìˆæ‚ğŠÇ—‚·‚é
 * @author itot
 */
public class DefaultCanvas extends MinimumCanvas {

	/* var */
	Tree tree;
	TreeTable tg;
	Transformer view;
	DefaultDrawer drawer;
	DefaultBuffer dbuf;
	ViewFile vfile = null;



	/**
	 * Constructor
	 * @param width ‰æ–Ê‚Ì•
	 * @param height ‰æ–Ê‚Ì‚‚³
	 * @param foregroundColor ‰æ–Ê‚Ì‘O–ÊF 
	 * @param backgroundColor ‰æ–Ê‚Ì”wŒiF
	 */
	public DefaultCanvas(
		int width,
		int height,
		Color foregroundColor,
		Color backgroundColor) {

		super(width, height, foregroundColor, backgroundColor);

		DefaultDrawer drawer = new DefaultDrawer(width, height, this);
		Transformer view = new Transformer();
		DefaultBuffer dbuf = new DefaultBuffer();
		drawer.setTransformer(view);
		drawer.setBuffer(dbuf);

		setDrawer((Drawer)drawer);
		setBuffer((Buffer)dbuf);
		setTransformer((Transformer)view);

		view.setDefaultValue();
	}

	/**
	 * Constructor
	 * @param width ‰æ–Ê‚Ì•
	 * @param height ‰æ–Ê‚Ì‚‚³
	 */
	public DefaultCanvas(int width, int height) {
		this(width, height, Color.white, Color.black);
	}


}
