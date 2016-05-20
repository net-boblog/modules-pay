package com.xabaohui.modules.pay.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;

import com.xabaohui.modules.pay.bean.AccountIoLog;

public interface AccountIoLogDao {

	// property constants
	public static final String ACCOUNT_ID = "accountId";
	public static final String OPPOSITE_ID = "oppositeId";
	public static final String TRADE_MONEY = "tradeMoney";
	public static final String BIZ_REF = "bizRef";
	public static final String IO_FLAG = "ioFlag";
	public static final String VERSION = "version";

	public abstract void save(AccountIoLog transientInstance);

	public abstract AccountIoLog findById(java.lang.Integer id);

	public abstract List findByExample(AccountIoLog instance);

	public abstract List findByAccountId(Object accountId);

	public abstract List findByOppositeId(Object oppositeId);

	public abstract List findByBizRef(Object bizRef);

	public abstract List findByIoFlag(Object ioFlag);

	public abstract List findByCriteria(DetachedCriteria criteria);

	public abstract void update(AccountIoLog accountIoLog);
}