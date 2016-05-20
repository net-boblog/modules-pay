package com.xabaohui.modules.pay.bean;

import java.util.Date;

/**
 * AccountIoLog entity. @author MyEclipse Persistence Tools
 */

public class AccountIoLog implements java.io.Serializable {

	// Fields

	private Integer accountIoLogId;
	private Integer accountId;
	private Integer oppositeId;
	private Double tradeMoney;
	private Integer bizRef;
	private String ioFlag;
	private Date gmtCreate;
	private Date gmtModify;
	private Integer version;

	// Constructors

	/** default constructor */
	public AccountIoLog() {
	}

	/** full constructor */
	public AccountIoLog(Integer accountId, Integer oppositeId,
			Double tradeMoney, Integer bizRef, String ioFlag, Date gmtCreate,
			Date gmtModify, Integer version) {
		this.accountId = accountId;
		this.oppositeId = oppositeId;
		this.tradeMoney = tradeMoney;
		this.bizRef = bizRef;
		this.ioFlag = ioFlag;
		this.gmtCreate = gmtCreate;
		this.gmtModify = gmtModify;
		this.version = version;
	}

	// Property accessors

	public Integer getAccountIoLogId() {
		return this.accountIoLogId;
	}

	public void setAccountIoLogId(Integer accountIoLogId) {
		this.accountIoLogId = accountIoLogId;
	}

	public Integer getAccountId() {
		return this.accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	public Integer getOppositeId() {
		return this.oppositeId;
	}

	public void setOppositeId(Integer oppositeId) {
		this.oppositeId = oppositeId;
	}

	public Double getTradeMoney() {
		return this.tradeMoney;
	}

	public void setTradeMoney(Double tradeMoney) {
		this.tradeMoney = tradeMoney;
	}

	public Integer getBizRef() {
		return this.bizRef;
	}

	public void setBizRef(Integer bizRef) {
		this.bizRef = bizRef;
	}

	public String getIoFlag() {
		return this.ioFlag;
	}

	public void setIoFlag(String ioFlag) {
		this.ioFlag = ioFlag;
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