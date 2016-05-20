/**
 * 
 */
package com.xabaohui.modules.pay.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;

import com.xabaohui.modules.pay.bean.RefundDetail;

/**
 * @author YRee
 * 
 */
public interface RefundDetailDao {

	// property constants
	public static final String BUYER_ID = "buyerId";
	public static final String SELLER_ID = "sellerId";
	public static final String PROTOCOL_ID = "protocolId";
	public static final String REFUND_MONEY = "refundMoney";
	public static final String STATUS = "status";
	public static final String VERSION = "version";
	public static final String REFUND_INSTRUCTIONS = "refundInstructions";

	public abstract void save(RefundDetail transientInstance);

	public abstract RefundDetail findById(java.lang.Integer id);

	public abstract List findByBuyerId(Object buyerId);

	public abstract List findBySellerId(Object sellerId);

	public abstract List findByProtocolId(Object protocolId);

	public abstract List findByRefundMoney(Object refundMoney);

	public abstract List findByStatus(Object status);

	public abstract List findByVersion(Object version);

	public abstract void update(RefundDetail refundDetail);

	public abstract List findByCriteria(DetachedCriteria criteria);

}