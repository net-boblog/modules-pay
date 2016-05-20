/**
 * 
 */
package com.xabaohui.modules.pay.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;

import com.xabaohui.modules.pay.bean.OurRcdClearing;

/**
 * @author YRee
 * 
 */
public interface OurRcdClearingDao {

	// property constants
	public static final String INSTRUCTION_ID = "instructionId";
	public static final String PROTOCOL_ID = "protocolId";
	public static final String TX_MONEY = "txMoney";
	public static final String TX_TYPE = "txType";
	public static final String TX_CHANNEL = "txChannel";
	public static final String VERSION = "version";

	public abstract void save(OurRcdClearing transientInstance);

	public abstract OurRcdClearing findById(java.lang.Integer id);

	public abstract List findByExample(OurRcdClearing instance);

	public abstract OurRcdClearing findByInstructionId(Object instructionId);

	public abstract List findByProtocolId(Object protocolId);

	public abstract List findByCriteria(DetachedCriteria criteria);

	public abstract void update(OurRcdClearing ourRcdClearing);

}