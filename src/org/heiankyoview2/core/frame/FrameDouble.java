package org.heiankyoview2.core.frame;

public class FrameDouble {
	double array[];
	double minDValue = 1.0e+30;
	double maxDValue = -1.0e+30;
	
	/**
	 * Constructor
	 * @param numnodes
	 */
	public FrameDouble(int numnodes) {
		array = new double[numnodes];
	}
	
	
	/**
	 * 値を1個セットする
	 * @param nodeid
	 * @param value
	 */
	public void set(int nodeid, double value) {
		if(nodeid <= 0 || nodeid > array.length) {
			System.out.println("  FrameDouble: nodeid " + nodeid + " is out of range.");
			return;
		}
		array[nodeid - 1] = value;
		
		if (maxDValue < value)
			maxDValue = value;
		if (minDValue > value)
			minDValue = value;
	}
	
	
	/**
	 * 値を1個ゲットする
	 * @param nodeid
	 * @return
	 */
	public double get(int nodeid) {
		if(nodeid <= 0 || nodeid > array.length) {
			System.out.println("  FrameDouble: nodeid " + nodeid + " is out of range.");
			return 0.0;
		}
		return array[nodeid - 1];
	}
	
	/**
	 * 格納する値の最大値を double 形式で指定する
	 * @param maxd 最大値
	 */
	public void setMaxDValue(double maxd) {
		maxDValue = maxd;
	}

	/**
	 * 格納する値の最大値を double 形式で返す
	 * @return 最大値
	 */
	public double getMaxDValue() {
		return maxDValue;
	}

	
	/**
	 * 格納する値の最小値を double 形式で指定する
	 * @param mind 最小値
	 */
	public void setMinDValue(double mind) {
		minDValue = mind;
	}

	/**
	 * 格納する値の最小値を double 形式で返す
	 * @return 最小値
	 */
	public double getMinDValue() {
		return minDValue;
	}

	
	/**
	 * Nodeの色や高さを算出するための値を返す
	 * @param id 値を格納しているID
	 * @return Nodeの色や高さを算出するための値（原則として0.0～1.0、不正なIDの場合は負値）
	 */
	double getAppearanceValue(int id) {
		if (maxDValue <= minDValue)
			return 0.5;
		if(id <= 0 || id > array.length)
			return -1.0;
		
		double value = (array[id - 1] - minDValue) / (maxDValue - minDValue);
		if (value < 0.0)
			value = 0.0;
		if (value > 1.0)
			value = 1.0;

		return value;
	}
}
