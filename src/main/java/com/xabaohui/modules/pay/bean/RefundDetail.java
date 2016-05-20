package com.xabaohui.modules.pay.bean;

import java.util.Date;

/**
 * RefundDetail entity. @author MyEclipse Persistence Tools
 */

public class RefundDetail implements java.io.Serializable {

	// Fields

	private Integer refundDetailId;
	private Integer buyerId;
	private Integer sellerId;
	private Integer protocolId;
	private String refundInstructions;
	private Double refundMoney;
	private String status;
	private Date gmtCreate;
	private Date gmtModify;
	private Integer version;

	// Constructors

	/** default constructor */
	public RefundDetail() {
	}

	/** full constructor */
	public RefundDetail(Integer buyerId, Integer sellerId, Integer protocolId,
			String refundInstructions, Double refundMoney, String status,
			Date gmtCreate, Date gmtModify, Integer version) {
		this.buyerId = buyerId;
		this.sellerId = sellerId;
		this.protocolId = protocolId;
		this.refundInstructions = refundInstructions;
		this.refundMoney = refundMoney;
		this.status = status;
		this.gmtCreate = gmtCreate;
		this.gmtModify = gmtModify;
		this.version = version;
	}

	// Property accessors

	public Integer getRefundDetailId() {
		return this.refundDetailId;
	}

	public void setRefundDetailId(Integer refundDetailId) {
		this.refundDetailId = refundDetailId;
	}

	public Integer getBuyerId() {
		return this.buyerId;
	}

	public void setBuyerId(Integer buyerId) {
		this.buyerId = buyerId;
	}

	public Integer getSellerId() {
		return this.sellerId;
	}

	public void setSellerId(Integer sellerId) {
		this.sellerId = sellerId;
	}

	public Integer getProtocolId() {
		return this.protocolId;
	}

	public void setProtocolId(Integer protocolId) {
		this.protocolId = protocolId;
	}

	public String getRefundInstructions() {
		return this.refundInstructions;
	}

	public void setRefundInstructions(String refundInstructions) {
		this.refundInstructions = refundInstructions;
	}

	public Double getRefundMoney() {
		return this.refundMoney;
	}

	public void setRefundMoney(Double refundMoney) {
		this.refundMoney = refundMoney;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
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