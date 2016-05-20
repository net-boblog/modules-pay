package com.xabaohui.modules.pay.dto;

public class CreateAccountIOLogDTO {
	private Integer accountId;
	private Integer oppositeId;
	private Double tradeMoney;
	private Integer bizRef;
	private String ioFlag;
	private String instructionType;

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	public Integer getOppositeId() {
		return oppositeId;
	}

	public void setOppositeId(Integer oppositeId) {
		this.oppositeId = oppositeId;
	}

	public Double getTradeMoney() {
		return tradeMoney;
	}

	public void setTradeMoney(Double tradeMoney) {
		this.tradeMoney = tradeMoney;
	}

	public Integer getBizRef() {
		return bizRef;
	}

	public void setBizRef(Integer bizRef) {
		this.bizRef = bizRef;
	}

	public String getIoFlag() {
		return ioFlag;
	}

	public void setIoFlag(String ioFlag) {
		this.ioFlag = ioFlag;
	}

	public String getInstructionType() {
		return instructionType;
	}

	public void setInstructionType(String instructionType) {
		this.instructionType = instructionType;
	}
}
