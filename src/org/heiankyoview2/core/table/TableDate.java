package org.heiankyoview2.core.table;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 時刻情報を表形式で格納するクラス
 */
public class TableDate {
	Date array[] = null;
	int size = 0;
	final int INITIAL_SIZE = 100;

	long minAValue = (long) 1.0e+30;
	long maxAValue = (long) -1.0e+30;

	SimpleDateFormat sdf = new SimpleDateFormat();

	
	
	/**
	 * Date形式で値を設定する
	 * @param value 値
	 */
	void setValue(int id, Date value) {

		if(id <= 0) {
			System.out.println("  TableDate: id(" + id + ") is out of range.");
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

		long ivalue = value.getTime();
		if (maxAValue < ivalue)
			maxAValue = ivalue;
		if (minAValue > ivalue)
			minAValue = ivalue;
	}
	
	
	/**
	 * Date形式で値を返す
	 */
	Date getDate(int id) {
		if(id <= 0 || id > size) {
			System.out.println("  TableDate: id(" + id + ") is out of range.");
			return null;
		}
		return array[id - 1];
	}
	
	
	/**
	 * テーブルの初期化
	 */
	void clear() {
		array = null;
		size = 0;
		minAValue = (long) 1.0e+30;
		maxAValue = (long) -1.0e+30;
	}
	
	/**
	 * 格納する値の個数をセットする
	 * @param length 値の個数
	 */
	void setSize(int length) {
		size = length;
		if(length < INITIAL_SIZE)
			length = INITIAL_SIZE;
		array = new Date[length];
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
		Date newarray[] = new Date[length];
		for(int i = 0; i < array.length; i++)
			newarray[i] = array[i];
		array = newarray;
	}

	/**
	 * 格納する値の最大値を long 形式で指定する
	 * @param maxi 最大値
	 */
	public void setMaxAValue(long maxa) {
		maxAValue = maxa;
	}

	/**
	 * 格納する値の最大値を long 形式で返す
	 * @return 最大値
	 */
	public long getMaxAValue() {
		return maxAValue;
	}

	/**
	 * 格納する値の最小値を long 形式で指定する
	 * @param mini 最小値
	 */
	public void setMinAValue(long mina) {
		minAValue = mina;
	}

	/**
	 * 格納する値の最小値を long 形式で返す
	 * @return 最小値
	 */
	public long getMinAValue() {
		return minAValue;
	}


	/**
	 * Nodeの色や高さを算出するための値を返す
	 * @param id 値を格納しているID
	 * @return Nodeの色や高さを算出するための値（原則として0.0～1.0、不正なIDの場合は負値）
	 */
	double getAppearanceValue(int id) {
		if (maxAValue <= minAValue)
			return 0.5;

		long ivalue = array[id].getTime();
		double value = (double) (ivalue - minAValue)
				/ (double) (maxAValue - minAValue);
		if (value < 0.0)
			value = 0.0;
		if (value > 1.0)
			value = 1.0;
		
		return value;
	}
}
