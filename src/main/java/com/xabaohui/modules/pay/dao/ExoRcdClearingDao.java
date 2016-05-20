/**
 * 
 */
package com.xabaohui.modules.pay.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;

import com.xabaohui.modules.pay.bean.ExoRcdClearing;

/**
 * @author YRee
 * 
 */
public interface ExoRcdClearingDao {

	// property constants
	public static final String EXO_REF = "exoRef";
	public static final String OUR_RCD_ID = "ourRcdId";
	public static final String INSTRUCTION_ID = "instructionId";
	public static final String PROTOCOL_ID = "protocolId";
	public static final String TX_MONEY = "txMoney";
	public static final String TX_TYPE = "txType";
	public static final String TX_CHANNEL = "txChannel";
	public static final String VERSION = "version";

	public abstract void save(ExoRcdClearing transientInstance);

	public abstract ExoRcdClearing findById(java.lang.Integer id);

	public abstract List findByExample(ExoRcdClearing instance);

	public abstract List findByExoRef(Object exoRef);

	public abstract List findByOurRcdId(Object ourRcdId);

	public abstract List findByInstructionId(Object instructionId);

	public abstract List findByProtocolId(Object protocolId);

	public abstract List findByCriteria(DetachedCriteria criteria);

	public abstract void update(ExoRcdClearing exoRcdClearing);

}