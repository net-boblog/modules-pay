/**
 * 
 */
package com.xabaohui.modules.pay.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;

import com.xabaohui.modules.pay.bean.OurFsClearing;

/**
 * @author YRee
 * 
 */
public interface OurFsClearingDao {

	// property constants
	public static final String INSTRUCTION_ID = "instructionId";
	public static final String PROTOCOL_ID = "protocolId";
	public static final String TX_MONEY = "txMoney";
	public static final String TX_TYPE = "txType";
	public static final String TX_CHANNEL = "txChannel";
	public static final String VERSION = "version";

	public abstract void save(OurFsClearing transientInstance);

	public abstract OurFsClearing findById(java.lang.Integer id);

	public abstract List findByExample(OurFsClearing instance);

	public abstract List findByInstructionId(Object instructionId);

	public abstract List findByProtocolId(Object protocolId);

	public abstract List findByCriteria(DetachedCriteria criteria);

	public abstract void update(OurFsClearing ourFsClearing);

}