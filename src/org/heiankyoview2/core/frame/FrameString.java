package org.heiankyoview2.core.frame;

public class FrameString {
	String array[];

	
	/**
	 * Constructor
	 * @param numnodes
	 */
	public FrameString(int numnodes) {
		array = new String[numnodes];
	}
	
	
	/**
	 * 値を1個セットする
	 * @param nodeid
	 * @param value
	 */
	public void set(int nodeid, String value) {
		if(nodeid <= 0 || nodeid > array.length) {
			System.out.println("  FrameString: nodeid " + nodeid + " is out of range.");
			return;
		}
		array[nodeid - 1] = value;
	}
	
	
	/**
	 * 値を1個ゲットする
	 * @param nodeid
	 * @return
	 */
	public String get(int nodeid) {
		if(nodeid <= 0 || nodeid > array.length) {
			System.out.println("  FrameString: nodeid " + nodeid + " is out of range.");
			return null;
		}
		return array[nodeid - 1];
	}
	
	
	/**
	 * Nodeの色や高さを算出するための値を返す
	 * @param id 値を格納しているID
	 * @return Nodeの色や高さを算出するための値（原則として0.0～1.0、不正なIDの場合は負値）
	 */
	double getAppearanceValue(int id) {
		if(id <= 0 || id > array.length)
			return -1.0;
		double value = (double) (id - 1) / (double) array.length;
		return value;
	}
}
