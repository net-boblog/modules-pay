/**
 * 
 */
package com.alipay.bean;

/**
 * @author YRee
 * 
 */
public class Trade {
	/**
	 * 商户订单号，商户网站订单系统中唯一订单号，必填
	 */
	private String WIDout_trade_no;
	/**
	 * 订单名称，必填
	 */
	private String WIDsubject;
	/**
	 * 付款金额，必填
	 */
	private String WIDtotal_fee;
	/**
	 * 商品描述，可空
	 */
	private String WIDbody;

	public String getWIDout_trade_no() {
		return WIDout_trade_no;
	}

	public void setWIDout_trade_no(String wIDout_trade_no) {
		WIDout_trade_no = wIDout_trade_no;
	}

	public String getWIDsubject() {
		return WIDsubject;
	}

	public void setWIDsubject(String wIDsubject) {
		WIDsubject = wIDsubject;
	}

	public String getWIDtotal_fee() {
		return WIDtotal_fee;
	}

	public void setWIDtotal_fee(String wIDtotal_fee) {
		WIDtotal_fee = wIDtotal_fee;
	}

	public String getWIDbody() {
		return WIDbody;
	}

	public void setWIDbody(String wIDbody) {
		WIDbody = wIDbody;
	}

}
