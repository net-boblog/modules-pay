package com.xabaohui.modules.pay.bean;

import java.util.Date;

/**
 * ExoFsClearing entity. @author MyEclipse Persistence Tools
 */

public class ExoFsClearing implements java.io.Serializable {

	// Fields

	private Integer exoFsId;
	private String exoRef;
	private Integer ourFsId;
	private Integer instructionId;
	private Integer protocolId;
	private Double txMoney;
	private String txType;
	private String txChannel;
	private Double exoFee;
	private Date gmtCreate;
	private Date gmtModify;
	private Integer version;

	// Constructors

	/** default constructor */
	public ExoFsClearing() {
	}

	/** full constructor */
	public ExoFsClearing(String exoRef, Integer ourFsId, Integer instructionId,
			Integer protocolId, Double txMoney, String txType,
			String txChannel, Double exoFee, Date gmtCreate, Date gmtModify,
			Integer version) {
		this.exoRef = exoRef;
		this.ourFsId = ourFsId;
		this.instructionId = instructionId;
		this.protocolId = protocolId;
		this.txMoney = txMoney;
		this.txType = txType;
		this.txChannel = txChannel;
		this.exoFee = exoFee;
		this.gmtCreate = gmtCreate;
		this.gmtModify = gmtModify;
		this.version = version;
	}

	// Property accessors

	public Integer getExoFsId() {
		return this.exoFsId;
	}

	public void setExoFsId(Integer exoFsId) {
		this.exoFsId = exoFsId;
	}

	public String getExoRef() {
		return this.exoRef;
	}

	public void setExoRef(String exoRef) {
		this.exoRef = exoRef;
	}

	public Integer getOurFsId() {
		return this.ourFsId;
	}

	public void setOurFsId(Integer ourFsId) {
		this.ourFsId = ourFsId;
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

	public Double getExoFee() {
		return this.exoFee;
	}

	public void setExoFee(Double exoFee) {
		this.exoFee = exoFee;
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