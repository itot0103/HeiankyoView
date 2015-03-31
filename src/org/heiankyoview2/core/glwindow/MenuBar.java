package org.heiankyoview2.core.glwindow;

import java.awt.event.*;
import javax.swing.*;

/**
 * MenuBarを構築するクラスのためのインタフェース
 * @author itot
 */
public interface MenuBar {


	/**
	 * 選択されたメニューアイテムを返す
	 * @param name 選択されたメニュー名
	 * @return JMenuItem 選択されたメニューアイテム
	 */
	public JMenuItem getMenuItem(String name);
		
	/**
	 * メニューに関するアクションの検知を設定する
	 * @param actionListener ActionListener
	 */
	public void addMenuListener(ActionListener actionListener);

}
