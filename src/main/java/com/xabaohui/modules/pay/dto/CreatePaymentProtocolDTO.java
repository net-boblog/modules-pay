package com.xabaohui.modules.pay.dto;

public class CreatePaymentProtocolDTO {
	private Integer payerId; // �û�id
	private Integer receiverId; // �տ����û�id
	private Integer orderId; // ����id
	private Double payMoney;// ֧�����
	private String type; // ֧������

	public Integer getPayerId() {
		return payerId;
	}

	public void setPayerId(Integer payerId) {
		this.payerId = payerId;
	}

	public Integer getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(Integer receiverId) {
		this.receiverId = receiverId;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public Double getPayMoney() {
		return payMoney;
	}

	public void setPayMoney(Double payMoney) {
		this.payMoney = payMoney;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
