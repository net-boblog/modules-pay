package com.xabaohui.modules.pay.bean.channel;

public class PaymentInstructionChannel {
	/**
	 * ���֧��
	 */
	public static final String BALANCE = "balance";

	/**
	 * ֧����֧��
	 */
	public static final String ALIPAY = "alipay";

	public static boolean isInnerChannel(String channel) {
		if (BALANCE.equals(channel)) {
			return true;
		}
		return false;
	}

	public static boolean isOuterChannel(String channel) {
		return !isInnerChannel(channel);
	}
}
