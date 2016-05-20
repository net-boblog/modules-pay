/**
 * 
 */
package com.xabaohui.modules.pay.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;

import com.xabaohui.modules.pay.bean.ExoFsClearing;

/**
 * @author YRee
 * 
 */
public interface ExoFsClearingDao {

	// property constants
	public static final String EXO_REF = "exoRef";
	public static final String OUR_FS_ID = "ourFsId";
	public static final String INSTRUCTION_ID = "instructionId";
	public static final String PROTOCOL_ID = "protocolId";
	public static final String TX_MONEY = "txMoney";
	public static final String TX_TYPE = "txType";
	public static final String TX_CHANNEL = "txChannel";
	public static final String EXO_FEE = "exoFee";
	public static final String VERSION = "version";

	public abstract void save(ExoFsClearing transientInstance);

	public abstract ExoFsClearing findById(java.lang.Integer id);

	public abstract List findByExample(ExoFsClearing instance);

	public abstract List findByOurFsId(Object ourFsId);

	public abstract List findByExoRef(Object exoRef);

	public abstract List findByInstructionId(Object instructionId);

	public abstract List findByProtocolId(Object protocolId);

	public abstract List findByCriteria(DetachedCriteria criteria);

	public abstract void update(ExoFsClearing exoFsClearing);

}