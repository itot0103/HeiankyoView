package org.heiankyoview2.core.table;

/**
 * 実数情報を表形式で格納するクラス
 */
public class TableDouble {
	double array[] = null;
	int size = 0;
	final int INITIAL_SIZE = 100;
	
	double minDValue = 1.0e+30;
	double maxDValue = -1.0e+30;

	/**
	 * double形式で値を設定する
	 */
	void setValue(int id, double value) {

		if(id <= 0) {
			System.out.println("  TableDouble: id(" + id + ") is out of range.");
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

		if (maxDValue < value)
			maxDValue = value;
		if (minDValue > value)
			minDValue = value;
	}
	
	/**
	 * double形式で値を返す
	 */
	double getDouble(int id) {
		if(id <= 0 || id > size) {
			System.out.println("  TableDouble: id(" + id + "/" + size + ") is out of range.");
			return 0.0;
		}
		return array[id - 1];
	}
	
	/**
	 * テーブルの初期化
	 */
	void clear() {
		array = null;
		size = 0;
		minDValue = 1.0e+30;
		maxDValue = -1.0e+30;
	}
	
	/**
	 * 格納する値の個数をセットする
	 * @param length 値の個数
	 */
	void setSize(int length) {
		size = length;
		if(length < INITIAL_SIZE)
			length = INITIAL_SIZE;
		array = new double[length];
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
		double newarray[] = new double[length];
		for(int i = 0; i < array.length; i++)
			newarray[i] = array[i];
		array = newarray;
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
		if(id < 0)
			return 0.5;
		
		double value = (array[id] - minDValue) / (maxDValue - minDValue);
		if (value < 0.0)
			value = 0.0;
		if (value > 1.0)
			value = 1.0;

		return value;
	}
}
