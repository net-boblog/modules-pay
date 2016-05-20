package com.xabaohui.modules.pay.dto;

public class CreatePaymentInstructionDTO {
	private Integer protocolId; // 协议id
	private String type; // 支付，确定收款，转账,退款等等
	private String channel; // 渠道
	private Double payMoney; // 支付金额

	public Integer getProtocolId() {
		return protocolId;
	}

	public void setProtocolId(Integer protocolId) {
		this.protocolId = protocolId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public Double getPayMoney() {
		return payMoney;
	}

	public void setPayMoney(Double payMoney) {
		this.payMoney = payMoney;
	}

}
