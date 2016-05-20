package com.xabaohui.modules.pay.util;

public class Validation {

	/**
	 * 判断金额是不是正确
	 * 
	 * @param money
	 * @return
	 */
	public static boolean isValidMoney(Double money) {
		if (money == null) {
			return false;
		}
		if (money < 0) {
			return false;
		}
		return true;
	}
}
