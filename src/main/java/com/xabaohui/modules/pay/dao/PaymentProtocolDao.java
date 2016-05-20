package com.xabaohui.modules.pay.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;

import com.xabaohui.modules.pay.bean.PaymentProtocol;

public interface PaymentProtocolDao {

	// property constants
	// public static final String USER_ID = "userId";
	public static final String PAYER_ID = "payerId";
	public static final String RECEIVER_ID = "receiverId";
	public static final String ORDER_ID = "orderId";
	public static final String PAY_MONEY = "payMoney";
	public static final String STATUS = "status";
	public static final String TYPE = "type";
	public static final String VERSION = "version";

	public abstract void save(PaymentProtocol transientInstance);

	public abstract PaymentProtocol findById(java.lang.Integer id);

	public abstract List findByExample(PaymentProtocol instance);

	public abstract List findByPayerId(Object payerId);

	public abstract List findByReceiverId(Object receiverId);

	public abstract PaymentProtocol findByOrderId(Object orderId);

	public abstract List findByStatus(Object status);

	public abstract void update(PaymentProtocol protocol);

	public abstract List findByCriteria(DetachedCriteria criteria);
}