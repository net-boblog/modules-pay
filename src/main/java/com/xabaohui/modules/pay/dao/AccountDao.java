package com.xabaohui.modules.pay.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;

import com.xabaohui.modules.pay.bean.Account;

public interface AccountDao {

	// property constants
	public static final String USERID = "userId";
	public static final String BALANCE = "balance";
	public static final String FROZEN_MONEY = "frozenMoney";
	public static final String TYPE = "type";
	public static final String STATUS = "status";
	public static final String VERSION = "version";

	public abstract void save(Account transientInstance);

	public abstract Account findById(java.lang.Integer id);

	public abstract List findByExample(Account instance);

	public abstract List findByType(Object type);

	public abstract List findByStatus(Object status);

	public List findByUserid(Object userid);

	public abstract List findByCriteria(DetachedCriteria criteria);

	public abstract void update(Account account);
}