/**
 * 
 */
package com.xabaohui.modules.pay.dto;

/**
 * @author YRee
 * 
 */
public class CreateExoFsClearingDTO {
	private String exoRef;
	private Integer ourRcdId;
	private Integer instructionId;
	private Integer protocolId;
	private Double txMoney;
	private String txType;
	private String txChannel;
	private Double exoFee;

	public Double getExoFee() {
		return exoFee;
	}

	public void setExoFee(Double exoFee) {
		this.exoFee = exoFee;
	}

	public String getExoRef() {
		return exoRef;
	}

	public void setExoRef(String exoRef) {
		this.exoRef = exoRef;
	}

	public Integer getOurRcdId() {
		return ourRcdId;
	}

	public void setOurRcdId(Integer ourRcdId) {
		this.ourRcdId = ourRcdId;
	}

	public Integer getInstructionId() {
		return instructionId;
	}

	public void setInstructionId(Integer instructionId) {
		this.instructionId = instructionId;
	}

	public Integer getProtocolId() {
		return protocolId;
	}

	public void setProtocolId(Integer protocolId) {
		this.protocolId = protocolId;
	}

	public Double getTxMoney() {
		return txMoney;
	}

	public void setTxMoney(Double txMoney) {
		this.txMoney = txMoney;
	}

	public String getTxType() {
		return txType;
	}

	public void setTxType(String txType) {
		this.txType = txType;
	}

	public String getTxChannel() {
		return txChannel;
	}

	public void setTxChannel(String txChannel) {
		this.txChannel = txChannel;
	}

}
