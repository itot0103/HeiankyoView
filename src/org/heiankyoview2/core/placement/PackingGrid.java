package org.heiankyoview2.core.placement;

import java.util.Vector;


/**
 * 画面配置状況を管理する格子分割データを格納するクラス
 * @author itot
 */
public class PackingGrid {
	public double xPositions[] = null, yPositions[] = null;
	public int xyFlags[] = null;
	public int xSize = 0, ySize = 0;
	public Vector candidate[] = new Vector[5];

	public PackingGrid() {
		for (int i = 0; i < 5; i++)
			candidate[i] = new Vector();
	}

	public void initGrid() {
		xPositions = new double[2];
		yPositions = new double[2];
		xyFlags = new int[1];
		xSize = ySize = 1;

		for (int i = 0; i < 5; i++)
			candidate[i].clear();
	}

	public int getNumX() {
		return xSize;
	}

	public int getNumY() {
		return ySize;
	}

	public void setXPos(int id, double value) {
		xPositions[id] = value;
	}

	public double getXPos(int id) {
		return xPositions[id];
	}

	public void setYPos(int id, double value) {
		yPositions[id] = value;
	}

	public double getYPos(int id) {
		return yPositions[id];
	}

	public void setFlag(int id, int value) {
		xyFlags[id] = value;
	}

	public int getFlag(int id) {
		return xyFlags[id];
	}

}
