package org.heiankyoview2.core.table;

import java.util.*;
import java.text.SimpleDateFormat;

/**
 * Treeの属性を表現するための表形式データを格納するクラス
 * @author itot
 */
public class Table {

	public static final int TABLE_NONE = 0;
	public static final int TABLE_STRING = 1;
	public static final int TABLE_DOUBLE = 2;
	public static final int TABLE_INT = 3;
	public static final int TABLE_DATE = 4;

	String name;
	int type = TABLE_NONE;
	
	TableString ts;
	TableDouble td;
	TableInt    ti;
	TableDate   ta;
	

	/**
	 * Constructor
	 */
	public Table(int type) {
		setType(type);
	}

	/**
	 * Constructor
	 */
	public Table() {
		this.type = TABLE_NONE;
	}
		
	/**
	 * テーブルの初期化
	 */
	public void clear() {
		switch(type) {
		case TABLE_STRING:
			ts.clear();
			break;
		case TABLE_DOUBLE:
			td.clear();
			break;
		case TABLE_INT:
			ti.clear();
			break;
		case TABLE_DATE:
			ta.clear();
			break;
		}
	}

	
	/**
	 * 格納する値のデータ型をセットする
	 * @param type データ型 （1:String, 2:double, 3:int）
	 */
	public void setType(int type) {
		this.type = type;
		switch(type) {
		case TABLE_STRING:
			ts = new TableString();
			break;
		case TABLE_DOUBLE:
			td = new TableDouble();
			break;
		case TABLE_INT:
			ti = new TableInt();
			break;
		case TABLE_DATE:
			ta = new TableDate();
			break;
		}
	}

	/**
	 * 格納する値のデータ型を返す
	 * @return データ型 （1:String, 2:double, 3:int）
	 */
	public int getType() {
		return type;
	}

	/**
	 * テーブルの名前をセットする
	 * @param name 名前
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * テーブルの名前を返す
	 * @return 名前
	 */
	public String getName() {
		return name;
	}


	/**
	 * テーブルに値をセットする
	 * @param id テーブル中のID
	 * @param value セットする値
	 */
	public void set(int id, String value) {
			ts.setValue(id, value);		
	}

	/**
	 * テーブルに値をセットする
	 * @param id テーブル中のID
	 * @param value セットする値
	 */
	public void set(int id, double value) {
			td.setValue(id, value);		
	}
	
	/**
	 * テーブルに値をセットする
	 * @param id テーブル中のID
	 * @param value セットする値
	 */
	public void set(int id, int value) {
			ti.setValue(id, value);		
	}
	
	/**
	 * テーブルに値をセットする
	 * @param id テーブル中のID
	 * @param value セットする値
	 */
	public void set(int id, Date value) {
			ta.setValue(id, value);		
	}
	
	/**
	 * String形式のテーブルから値を得る
	 * @param Id テーブル中のID
	 * @return String形式の値
	 */
	public String getString(int id) {
		return ts.getString(id);
	}

	/**
	 * double形式のテーブルから値を得る
	 * @param Id テーブル中のID
	 * @return double形式の値
	 */
	public double getDouble(int id) {
		return td.getDouble(id);
	}

	/**
	 * int形式のテーブルから値を得る
	 * @param Id テーブル中のID
	 * @return int形式の値
	 */
	public int getInt(int id) {
		return ti.getInt(id);
	}

	/**
	 * Date形式のテーブルから値を得る
	 * @param Id テーブル中のID
	 * @return String形式の値
	 */
	public Date getDate(int id) {
		return ta.getDate(id);
	}

	

	/**
	 * 格納する値の個数をセットする
	 * @param type 値の個数
	 */
	public void setSize(int size) {
		switch(type) {
		case TABLE_STRING:
			ts.setSize(size);
			break;
		case TABLE_DOUBLE:
			td.setSize(size);
			break;
		case TABLE_INT:
			ti.setSize(size);
			break;
		case TABLE_DATE:
			ta.setSize(size);
			break;
		}
	}

	/**
	 * 格納する値の個数を返す
	 * @return 値の個数
	 */
	public int getSize() {
		int size = 0;
		
		switch(type) {
		case TABLE_STRING:
			size = ts.getSize();
			break;
		case TABLE_DOUBLE:
			size = td.getSize();
			break;
		case TABLE_INT:
			size = ti.getSize();
			break;
		case TABLE_DATE:
			size = ta.getSize();
			break;
		}
		
		return size;
	}

	
	/**
	 * サイズを再変更する
	 */
	public void resize(int newsize) {
		switch(type) {
		case TABLE_STRING:
			ts.resize(newsize);
			break;
		case TABLE_DOUBLE:
			td.resize(newsize);
			break;
		case TABLE_INT:
			ti.resize(newsize);
			break;
		case TABLE_DATE:
			ta.resize(newsize);
			break;
		}
	}
	
	
	
	/**
	 * Nodeの色や高さを算出するための値を返す
	 * @param id 値を格納しているID
	 * @return Nodeの色や高さを算出するための値（原則として0.0～1.0、不正なIDの場合は負値）
	 */
	public double getAppearanceValue(int id) {
		double value = -1.0;
		
		switch(type) {
		case TABLE_STRING:
			value = ts.getAppearanceValue(id);
			break;
		case TABLE_DOUBLE:
			value = td.getAppearanceValue(id);
			break;
		case TABLE_INT:
			value = ti.getAppearanceValue(id);
			break;
		case TABLE_DATE:
			value = ta.getAppearanceValue(id);
			break;
		}

		return value;
	}

}
