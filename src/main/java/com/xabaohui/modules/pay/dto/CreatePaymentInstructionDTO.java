package com.xabaohui.modules.pay.dto;

public class CreatePaymentInstructionDTO {
	private Integer protocolId; // Э��id
	private String type; // ֧����ȷ���տת��,�˿�ȵ�
	private String channel; // ����
	private Double payMoney; // ֧�����

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
