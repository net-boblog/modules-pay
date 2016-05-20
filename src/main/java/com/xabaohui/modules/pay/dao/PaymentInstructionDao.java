package com.xabaohui.modules.pay.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;

import com.xabaohui.modules.pay.bean.PaymentInstruction;

public interface PaymentInstructionDao {

	// property constants
	public static final String PROTOCOL_ID = "protocolId";
	public static final String PAYER_ID = "payerId";
	public static final String RECEIVER_ID = "receiverId";
	public static final String TYPE = "type";
	public static final String CHANNEL = "channel";
	public static final String STATUS = "status";
	public static final String PAY_MONEY = "payMoney";
	public static final String VERSION = "version";
	public static final String REFUND_MONEY = "refundMoney";

	public abstract void save(PaymentInstruction transientInstance);

	public abstract PaymentInstruction findById(java.lang.Integer id);

	public abstract List findByExample(PaymentInstruction instance);

	public abstract List findByProtocolId(Object protocolId);

	public abstract List findByPayerId(Object payerId);

	public abstract List findByReceiverId(Object receiverId);

	public abstract List findByStatus(Object status);

	public abstract List findByVersion(Object version);

	public abstract PaymentInstruction merge(PaymentInstruction detachedInstance);

	public abstract void update(PaymentInstruction instruction);

	public abstract List findByCriteria(DetachedCriteria criteria);
}