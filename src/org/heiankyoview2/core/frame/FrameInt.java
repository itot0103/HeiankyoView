package org.heiankyoview2.core.frame;

public class FrameInt {
	int array[];
	
	int minIValue = (int) 1.0e+30;
	int maxIValue = (int) -1.0e+30;
	
	/**
	 * Constructor
	 * @param numnodes
	 */
	public FrameInt(int numnodes) {
		array = new int[numnodes];
	}
	
	
	/**
	 * 値を1個セットする
	 * @param nodeid
	 * @param value
	 */
	public void set(int nodeid, int value) {
		if(nodeid <= 0 || nodeid > array.length) {
			System.out.println("  FrameInt: nodeid " + nodeid + " is out of range.");
			return;
		}
		array[nodeid - 1] = value;
		
		if (maxIValue < value)
			maxIValue = value;
		if (minIValue > value)
			minIValue = value;
	}
	
	
	/**
	 * 値を1個ゲットする
	 * @param nodeid
	 * @return
	 */
	public int get(int nodeid) {
		if(nodeid <= 0 || nodeid > array.length) {
			System.out.println("  FrameInt: nodeid " + nodeid + " is out of range.");
			return 0;
		}
		return array[nodeid - 1];
	}
	
	
	/**
	 * 格納する値の最大値を int 形式で指定する
	 * @param maxi 最大値
	 */
	public void setMaxIValue(int maxi) {
		maxIValue = maxi;
	}

	/**
	 * 格納する値の最大値を int 形式で返す
	 * @return 最大値
	 */
	public int getMaxIValue() {
		return maxIValue;
	}

	
	/**
	 * 格納する値の最小値を int 形式で指定する
	 * @param mini 最小値
	 */
	public void setMinIValue(int mini) {
		minIValue = mini;
	}

	/**
	 * 格納する値の最小値を int 形式で返す
	 * @return 最小値
	 */
	public int getMinIValue() {
		return minIValue;
	}
	
	/**
	 * Nodeの色や高さを算出するための値を返す
	 * @param id 値を格納しているID
	 * @return Nodeの色や高さを算出するための値（原則として0.0～1.0、不正なIDの場合は負値）
	 */
	double getAppearanceValue(int id) {
		if (maxIValue <= minIValue)
			return 0.5;
		if(id <= 0 || id > array.length)
			return -1.0;

		double value =	(double) ((double)array[id] - minIValue)
				/ (double) (maxIValue - minIValue);
		if (value < 0.0)
			value = 0.0;
		if (value > 1.0)
			value = 1.0;

		return value;
	}
}
