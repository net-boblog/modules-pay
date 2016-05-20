package com.xabaohui.modules.pay.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;

import com.xabaohui.modules.pay.bean.FreezeLog;

public interface FreezeLogDao {

	// property constants
	public static final String ACCOUNT_ID = "accountId";
	public static final String FROZEN_MONEY = "frozenMoney";
	public static final String BIZ_REF = "bizRef";
	public static final String OPERATE_TYPE = "operateType";
	public static final String VERSION = "version";

	public abstract void save(FreezeLog transientInstance);

	public abstract FreezeLog findById(java.lang.Integer id);

	public abstract List findByAccountId(Object accountId);

	public abstract List findByFrozenMoney(Object frozenMoney);

	public abstract List findByBizRef(Object bizRef);

	public abstract List findByOperateType(Object operateType);

	public abstract List findByCriteria(DetachedCriteria criteria);

	public abstract void update(FreezeLog freezeLog);
}