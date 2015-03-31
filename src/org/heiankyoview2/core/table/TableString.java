package org.heiankyoview2.core.table;


/**
 * 文字列情報を表形式で格納するクラス
 */
public class TableString {
	String array[] = null;
	int size = 0;
	final int INITIAL_SIZE = 100;
	
	
	/**
	 * String形式で値を設定する
	 */
	void setValue(int id, String value) {
		if(id <= 0) {
			System.out.println("  TableString: id(" + id + ") is out of range.");
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
		
	}
	
	
	/**
	 * String形式で値を返す
	 */
	String getString(int id) {
		if(id <= 0 || id > size) {
			System.out.println("  TableString: id(" + id + "/" + size + ") is out of range.");
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
	}
	
	/**
	 * 格納する値の個数をセットする
	 * @param length 値の個数
	 */
	void setSize(int length) {
		size = length;
		if(length < INITIAL_SIZE)
			length = INITIAL_SIZE;
		array = new String[length];
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
		String newarray[] = new String[length];
		for(int i = 0; i < array.length; i++)
			newarray[i] = array[i];
		array = newarray;
	}
	
	
	/**
	 * Nodeの色や高さを算出するための値を返す
	 * @param id 値を格納しているID
	 * @return Nodeの色や高さを算出するための値（原則として0.0～1.0、不正なIDの場合は負値）
	 */
	double getAppearanceValue(int id) {
		double value = (double) id / (double) size;
		return value;
	}
}
