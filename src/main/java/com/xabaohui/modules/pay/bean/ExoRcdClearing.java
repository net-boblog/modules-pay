package com.xabaohui.modules.pay.bean;

import java.util.Date;

/**
 * ExoRcdClearing entity. @author MyEclipse Persistence Tools
 */

public class ExoRcdClearing implements java.io.Serializable {

	// Fields

	private Integer exoRcdId;
	private String exoRef;
	private Integer ourRcdId;
	private Integer instructionId;
	private Integer protocolId;
	private Double txMoney;
	private String txType;
	private String txChannel;
	private Date gmtCreate;
	private Date gmtModify;
	private Integer version;

	// Constructors

	/** default constructor */
	public ExoRcdClearing() {
	}

	/** full constructor */
	public ExoRcdClearing(String exoRef, Integer ourRcdId,
			Integer instructionId, Integer protocolId, Double txMoney,
			String txType, String txChannel, Date gmtCreate, Date gmtModify,
			Integer version) {
		this.exoRef = exoRef;
		this.ourRcdId = ourRcdId;
		this.instructionId = instructionId;
		this.protocolId = protocolId;
		this.txMoney = txMoney;
		this.txType = txType;
		this.txChannel = txChannel;
		this.gmtCreate = gmtCreate;
		this.gmtModify = gmtModify;
		this.version = version;
	}

	// Property accessors

	public Integer getExoRcdId() {
		return this.exoRcdId;
	}

	public void setExoRcdId(Integer exoRcdId) {
		this.exoRcdId = exoRcdId;
	}

	public String getExoRef() {
		return this.exoRef;
	}

	public void setExoRef(String exoRef) {
		this.exoRef = exoRef;
	}

	public Integer getOurRcdId() {
		return this.ourRcdId;
	}

	public void setOurRcdId(Integer ourRcdId) {
		this.ourRcdId = ourRcdId;
	}

	public Integer getInstructionId() {
		return this.instructionId;
	}

	public void setInstructionId(Integer instructionId) {
		this.instructionId = instructionId;
	}

	public Integer getProtocolId() {
		return this.protocolId;
	}

	public void setProtocolId(Integer protocolId) {
		this.protocolId = protocolId;
	}

	public Double getTxMoney() {
		return this.txMoney;
	}

	public void setTxMoney(Double txMoney) {
		this.txMoney = txMoney;
	}

	public String getTxType() {
		return this.txType;
	}

	public void setTxType(String txType) {
		this.txType = txType;
	}

	public String getTxChannel() {
		return this.txChannel;
	}

	public void setTxChannel(String txChannel) {
		this.txChannel = txChannel;
	}

	public Date getGmtCreate() {
		return this.gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	public Date getGmtModify() {
		return this.gmtModify;
	}

	public void setGmtModify(Date gmtModify) {
		this.gmtModify = gmtModify;
	}

	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

}