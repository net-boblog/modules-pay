package com.xabaohui.modules.pay.bean;

import java.util.Date;

/**
 * PaymentProtocol entity. @author MyEclipse Persistence Tools
 */

public class PaymentProtocol implements java.io.Serializable {

	// Fields

	private Integer paymentProtocolId;
	private Integer payerId;
	private Integer receiverId;
	private Integer orderId;
	private Double payMoney;
	private Date payTime;
	private Date deliveryTime;
	private Date confirmTime;
	private String status;
	private String type;
	private Date gmtCreate;
	private Date gmtModify;
	private Integer version;

	// Constructors

	/** default constructor */
	public PaymentProtocol() {
	}

	/** minimal constructor */
	public PaymentProtocol(Double payMoney, String status, String type,
			Date gmtCreate, Date gmtModify, Integer version) {
		this.payMoney = payMoney;
		this.status = status;
		this.type = type;
		this.gmtCreate = gmtCreate;
		this.gmtModify = gmtModify;
		this.version = version;
	}

	/** full constructor */
	public PaymentProtocol(Integer payerId, Integer receiverId,
			Integer orderId, Double payMoney, Date payTime, Date deliveryTime,
			Date confirmTime, String status, String type, Date gmtCreate,
			Date gmtModify, Integer version) {
		this.payerId = payerId;
		this.receiverId = receiverId;
		this.orderId = orderId;
		this.payMoney = payMoney;
		this.payTime = payTime;
		this.deliveryTime = deliveryTime;
		this.confirmTime = confirmTime;
		this.status = status;
		this.type = type;
		this.gmtCreate = gmtCreate;
		this.gmtModify = gmtModify;
		this.version = version;
	}

	// Property accessors

	public Integer getPaymentProtocolId() {
		return this.paymentProtocolId;
	}

	public void setPaymentProtocolId(Integer paymentProtocolId) {
		this.paymentProtocolId = paymentProtocolId;
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

	public Integer getOrderId() {
		return this.orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
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

	public Date getDeliveryTime() {
		return this.deliveryTime;
	}

	public void setDeliveryTime(Date deliveryTime) {
		this.deliveryTime = deliveryTime;
	}

	public Date getConfirmTime() {
		return this.confirmTime;
	}

	public void setConfirmTime(Date confirmTime) {
		this.confirmTime = confirmTime;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
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