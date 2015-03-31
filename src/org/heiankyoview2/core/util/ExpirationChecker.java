package org.heiankyoview2.core.util;
import java.util.Calendar;


public class ExpirationChecker {
	
	// 有効期限の年と月を記述する
	static int yearlimit = 3000;
	static int monthlimit = 11;
	static int limit = calcLimit(yearlimit, monthlimit);
	
	/**
	 * 有効期限内か否かをチェックする
	 */
	public static boolean isValid() {
		
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int value = calcLimit(year, month);
		
		if(value > limit) return false;
		return true;
	}
	
	/**
	 * 使用期限を判定するための整数値を算出する
	 */
	static int calcLimit(int y, int m) {
		return (y - 2000) * 12 + m;
	}
	
}
