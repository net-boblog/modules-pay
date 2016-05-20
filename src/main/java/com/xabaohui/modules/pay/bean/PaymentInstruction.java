package com.xabaohui.modules.pay.bean;

import java.util.Date;

/**
 * PaymentInstruction entity. @author MyEclipse Persistence Tools
 */

public class PaymentInstruction implements java.io.Serializable {

	// Fields

	private Integer paymentInstructionId;
	private Integer protocolId;
	private Integer payerId;
	private Integer receiverId;
	private String type;
	private String channel;
	private String status;
	private Double payMoney;
	private Date payTime;
	private Double refundMoney;
	private Date gmtCreate;
	private Date gmtModify;
	private Integer version;

	// Constructors

	/** default constructor */
	public PaymentInstruction() {
	}

	/** minimal constructor */
	public PaymentInstruction(Integer protocolId, Integer payerId,
			Integer receiverId, String type, String channel, String status,
			Double payMoney, Double refundMoney, Date gmtCreate,
			Date gmtModify, Integer version) {
		this.protocolId = protocolId;
		this.payerId = payerId;
		this.receiverId = receiverId;
		this.type = type;
		this.channel = channel;
		this.status = status;
		this.payMoney = payMoney;
		this.refundMoney = refundMoney;
		this.gmtCreate = gmtCreate;
		this.gmtModify = gmtModify;
		this.version = version;
	}

	/** full constructor */
	public PaymentInstruction(Integer protocolId, Integer payerId,
			Integer receiverId, String type, String channel, String status,
			Double payMoney, Date payTime, Double refundMoney, Date gmtCreate,
			Date gmtModify, Integer version) {
		this.protocolId = protocolId;
		this.payerId = payerId;
		this.receiverId = receiverId;
		this.type = type;
		this.channel = channel;
		this.status = status;
		this.payMoney = payMoney;
		this.payTime = payTime;
		this.refundMoney = refundMoney;
		this.gmtCreate = gmtCreate;
		this.gmtModify = gmtModify;
		this.version = version;
	}

	// Property accessors

	public Integer getPaymentInstructionId() {
		return this.paymentInstructionId;
	}

	public void setPaymentInstructionId(Integer paymentInstructionId) {
		this.paymentInstructionId = paymentInstructionId;
	}

	public Integer getProtocolId() {
		return this.protocolId;
	}

	public void setProtocolId(Integer protocolId) {
		this.protocolId = protocolId;
	}

	public Integer getPayerId() {
		return this.payerId;
	}

	public void setPayerId(Integer payerId) {
		this.payerId = payerId;
	}

	public Integer getReceiverId() {
		return this.receiverId;
	}

	public void setReceiverId(Integer receiverId) {
		this.receiverId = receiverId;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getChannel() {
		return this.channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Double getPayMoney() {
		return this.payMoney;
	}

	public void setPayMoney(Double payMoney) {
		this.payMoney = payMoney;
	}

	public Date getPayTime() {
		return this.payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

	public Double getRefundMoney() {
		return this.refundMoney;
	}

	public void setRefundMoney(Double refundMoney) {
		this.refundMoney = refundMoney;
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