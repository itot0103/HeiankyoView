package org.heiankyoview2.core.gldraw;

import org.heiankyoview2.core.tree.Branch;
import org.heiankyoview2.core.tree.Node;
import org.heiankyoview2.core.tree.Tree;


/**
 * 描画の視点操作（拡大縮小、回転、平行移動）のパラメータを管理するクラス
 * @author itot
 */
public class Transformer {
	double viewShift[] = new double[3];
	double viewRotate[] = new double[16];
	double viewScale;
	double viewShiftBak[] = new double[3];
	double viewScaleBak;
	double Xrotate, Yrotate, XrotateBak, YrotateBak;

	double treeMin[] = new double[3];
	double treeMax[] = new double[3];
	double treeCenter[] = new double[3];
	double treeSize;

	double shiftX, shiftZ;
	

	/**
	 * Constructor
	 */
	public Transformer() {
		setDefaultValue();
	}

	/**
	 * 視点パラメータをリセットする
	 */
	public void viewReset() {
		for (int i = 0; i < 16; i++) {
			if (i % 5 == 0)
				viewRotate[i] = 1.0;
			else
				viewRotate[i] = 0.0;
		}
		viewScale = viewScaleBak = 1.0;
		viewShift[0] = viewShiftBak[0] = 0.0;
		viewShift[1] = viewShiftBak[1] = 0.0;
		viewShift[2] = viewShiftBak[2] = 0.0;
		Xrotate = XrotateBak = 0.0;
		Yrotate = YrotateBak = 0.0;

	}


	/**
	 * Treeをセットする
	 * @param tree Tree
	 */
	public void setTree(Tree tree) {

		treeMin[0] = treeMin[1] = treeMin[2] = 1.0e+20;
		treeMax[0] = treeMax[1] = treeMax[2] = -1.0e+20;

		double tmp;
		Branch rootBranch = tree.getRootBranch();

		for (int i = 1; i <= rootBranch.getNodeList().size(); i++) {
			Node node = rootBranch.getNodeAt(i);
			double x = node.getX();
			double y = node.getY();
			double z = node.getZ();
			double width = node.getWidth();
			double height = node.getHeight();
			double depth = node.getDepth();

			tmp = x + width;
			if (treeMax[0] < tmp)
				treeMax[0] = tmp;
			tmp = y + height;
			if (treeMax[1] < tmp)
				treeMax[1] = tmp;
			tmp = z + depth;
			if (treeMax[2] < tmp)
				treeMax[2] = tmp;

			tmp = x - width;
			if (treeMin[0] > tmp)
				treeMin[0] = tmp;
			tmp = y - height;
			if (treeMin[1] > tmp)
				treeMin[1] = tmp;
			tmp = z - depth;
			if (treeMin[2] > tmp)
				treeMin[2] = tmp;

		}

		treeCenter[0] = (treeMin[0] + treeMax[0]) * 0.5;
		treeCenter[1] = (treeMin[1] + treeMax[1]) * 0.5;
		treeCenter[2] = (treeMin[2] + treeMax[2]) * 0.5;
		treeSize = treeMax[0] - treeMin[0];
		tmp = treeMax[1] - treeMin[1];
		if (treeSize < tmp)
			treeSize = tmp;
		treeSize *= 0.5;

		//System.out.println("   center[" + treeCenter[0] + "," + treeCenter[1] + "," + treeCenter[2] + "] size=" + treeSize);
	}
	
	
	/**
	 * マウスボタンが押されたモードを設定する
	 */
	public void mousePressed() {
		viewScaleBak = viewScale;
		viewShiftBak[0] = viewShift[0];
		viewShiftBak[1] = viewShift[1];
		viewShiftBak[2] = viewShift[2];
		XrotateBak = Xrotate;
		YrotateBak = Yrotate;
	}

	/**
	 * マウスのドラッグ操作に応じてパラメータを制御する
	 * @param x マウスポインタのx座標値
	 * @param y マウスポインタのy座標値
	 * @param width 画面領域の幅
	 * @param height 画面領域の高さ
	 * @param dragMode ドラッグモード（1:ZOOM, 2:SHIFT, 3:ROTATE）
	 */
	public void drag(int x, int y, int width, int height, int dragMode, Drawer d) {
		
		if(dragMode == 1) { // ZOOM
			
			if (y > 0) {
				viewScale =
					viewScaleBak * (1 + (double) (2 * y) / (double) height);
			} else {
				viewScale = viewScaleBak * (1 + (double) y / (double) height);
			}
		}
		
		if (dragMode == 2) { // SHIFT
			
			 float diffX = (float)x * 3.0f / width;
             float diffY = (-3.0f) * (float)y / height;
           
            viewShift[0] = viewShiftBak[0] + diffX / viewScale;
 			viewShift[1] = viewShiftBak[1] + diffY / viewScale;
		}
		
		if (dragMode == 3) { // ROTATE
			Xrotate = XrotateBak + (double) x * Math.PI / (double) width;
			Yrotate = YrotateBak + (double) y * Math.PI / (double) height;
			double cosX = Math.cos(Yrotate);
			double sinX = Math.sin(Yrotate);
			double cosY = Math.cos(Xrotate);
			double sinY = Math.sin(Xrotate);

			viewRotate[0] = cosY;
			viewRotate[1] = 0;
			viewRotate[2] = -sinY;
			viewRotate[4] = sinX * sinY;
			viewRotate[5] = cosX;
			viewRotate[6] = sinX * cosY;
			viewRotate[8] = cosX * sinY;
			viewRotate[9] = -sinX;
			viewRotate[10] = cosX * cosY;
		}

	}
	
	
	/**
	 * 視点パラメータを初期化する
	 */
	public void setDefaultValue() {

		viewRotate[0] = 0.8827;
		viewRotate[1] = 0.0;
		viewRotate[2] = -0.4699;
		viewRotate[3] = 0.0;
		viewRotate[4] = -0.2659;
		viewRotate[5] = 0.8244;
		viewRotate[6] = -0.4997;
		viewRotate[7] = 0.0;
		viewRotate[8] = 0.3873;
		viewRotate[9] = 0.5661;
		viewRotate[10] = 0.7277;
		viewRotate[11] = 0.0;
		viewRotate[12] = 0.0;
		viewRotate[13] = 0.0;
		viewRotate[14] = 0.0;
		viewRotate[15] = 1.0;

		viewShift[0] = 0.8819;
		viewShift[1] = -0.2209;
		viewShift[2] = 0.0;
		viewScale = 1.1417;
	}


	/**
	 * 表示の拡大度を返す
	 * @return 表示の拡大度
	 */
	public double getViewScale() {
		return viewScale;
	}
	
	
	/**
	 * 表示の拡大度をセットする
	 * @param v 表示の拡大度
	 */
	public void setViewScale(double v) {
		viewScale = v;
	}
	

	/**
	 * 表示の回転角度を返す
	 * @return 表示の回転角度
	 */
	public double getViewRotateX() {
		return Xrotate;
	}
	
	/**
	 * 表示の回転角度を返す
	 * @return 表示の回転角度
	 */
	public double getViewRotateY() {
		return Yrotate;
	}
	
	
	/**
	 * Treeのサイズ値を返す
	 * @return Treeのサイズ値
	 */
	public double getTreeSize() {
		return treeSize;
	}
	
	/**
	 * Treeのサイズ値をセットする
	 * @param t Treeのサイズ値
	 */
	public void setTreeSize(double t) {
		treeSize = t;
	}

	/**
	 * counterの中心座標値を返す
	 * @param i 座標軸(1:X, 2:Y, 3:Z)
	 * @return 中心座標値
	 */
	public double getCenter(int i) {
		return treeCenter[i];
	}
	
	/**
	 * counterの中心座標値をセットする
	 * @param g 中心座標値
	 * @param i 座標軸(1:X, 2:Y, 3:Z)
	 */
	public void setCenter(double g, int i) {
		treeCenter[i] = g;
	}

	/**
	 * 視点の回転の行列値を返す
	 * @param i 行列中の要素の位置
	 * @return 行列値
	 */
	public double getViewRotate(int i) {
		return viewRotate[i];
	}
	
	/**
	 * 視点の回転の行列値をセットする
	 * @param v 行列値
	 * @param i 行列中の要素の位置
	 */
	public void setViewRotate(double v, int i) {
		viewRotate[i] = v;
	}

	/**
	 * 視点の平行移動量を返す
	 * @param i 座標軸 (0:X, 1:Y, 2:Z)
	 * @return 平行移動量
	 */
	public double getViewShift(int i) {
		return viewShift[i];
	}
	
	/**
	 * 視点の平行移動量をセットする
	 * @param v 平行移動量
	 * @param i 座標軸 (1:X, 2:Y, 3:Z)
	 */
	public void setViewShift(double v, int i) {
		viewShift[i] = v;
	}

}
