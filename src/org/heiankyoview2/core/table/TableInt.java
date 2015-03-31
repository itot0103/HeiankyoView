package org.heiankyoview2.core.table;

/**
 * 整数情報を表形式で格納するクラス
 */
public class TableInt {
	int array[] = null;
	int size = 0;
	final int INITIAL_SIZE = 100;
	
	int minIValue = (int) 1.0e+30;
	int maxIValue = (int) -1.0e+30;
	
	/**
	 * int形式で値を設定する
	 * @param value 値
	 */
	void setValue(int id, int value) {
		if(id <= 0) {
			System.out.println("  TableInt(1): id(" + id + ") is out of range.");
			return;
		}
		else if(array == null) {
			setSize(id);
			size = id;
		}
		else if(id > size && id <= array.length) {
			size = id;
		}
		else if(id > array.length) {
			resize(array.length * 2);
			size = id;
		}
		array[id - 1] = value;

		if (maxIValue < value)
			maxIValue = value;
		if (minIValue > value)
			minIValue = value;
		
		//System.out.println("   id=" + id + " value=" + value + " min=" + minIValue  + " max=" + maxIValue);
	}

	/**
	 * int形式で値を返す
	 */
	int getInt(int id) {
		if(id <= 0 || id > size) {
			System.out.println("  TableInt(2): id(" + id + ") is out of range.");
			return 0;
		}
		return array[id - 1];
	}
	
	/**
	 * テーブルの初期化
	 */
	void clear() {
		array = null;
		size = 0;
		minIValue = (int) 1.0e+30;
		maxIValue = (int) -1.0e+30;
		
	}
	
	/**
	 * 格納する値の個数をセットする
	 * @param length 値の個数
	 */
	void setSize(int length) {
		size = length;
		if(length < INITIAL_SIZE)
			length = INITIAL_SIZE;
		array = new int[length];
	}
	
	
	/**
	 * 格納する値の個数をゲットする
	 */
	int getSize() {
		return size;
	}
	
	
	/**
	 * 格納する値の個数を再セットする
	 * @param length 値の個数
	 */
	void resize(int length) {
		if(length < array.length) return;
		if(length < (array.length * 2))
			length = array.length * 2;
		int newarray[] = new int[length];
		for(int i = 0; i < array.length; i++)
			newarray[i] = array[i];
		array = newarray;
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
		if(id < 0)
			return 0.5;
		
		double value =	(double) (array[id] - minIValue)
				/ (double) (maxIValue - minIValue);
		if(value > 0.5)
		//System.out.println("      id=" + id + " value=" + value);
		if (value < 0.0)
			value = 0.0;
		if (value > 1.0)
			value = 1.0;

		return value;
	}

}
