package com.xabaohui.modules.pay.dao.impl;

import java.util.List;

import org.hibernate.LockMode;
import org.hibernate.criterion.DetachedCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.xabaohui.modules.pay.bean.AccountIoLog;
import com.xabaohui.modules.pay.dao.AccountIoLogDao;

/**
 * A data access object (DAO) providing persistence and search support for
 * AccountIoLog entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see com.xabaohui.modules.pay.bean.AccountIoLog
 * @author MyEclipse Persistence Tools
 */
public class AccountIoLogDaoImpl extends HibernateDaoSupport implements
		AccountIoLogDao {
	private static final Logger log = LoggerFactory
			.getLogger(AccountIoLogDaoImpl.class);

	protected void initDao() {
		// do nothing
	}

	public void save(AccountIoLog transientInstance) {
		log.debug("saving AccountIoLog instance");
		try {
			getHibernateTemplate().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(AccountIoLog persistentInstance) {
		log.debug("deleting AccountIoLog instance");
		try {
			getHibernateTemplate().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.xabaohui.modules.pay.service.dao.impl.AccountIoLogDao#findById(java
	 * .lang.Integer )
	 */
	@Override
	public AccountIoLog findById(java.lang.Integer id) {
		log.debug("getting AccountIoLog instance with id: " + id);
		try {
			AccountIoLog instance = (AccountIoLog) getHibernateTemplate().get(
					"com.xabaohui.modules.pay.bean.AccountIoLog", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.xabaohui.modules.pay.service.dao.impl.AccountIoLogDao#findByExample
	 * (com.zis .payment.bean.AccountIoLog)
	 */
	@Override
	public List findByExample(AccountIoLog instance) {
		log.debug("finding AccountIoLog instance by example");
		try {
			List results = getHibernateTemplate().findByExample(instance);
			log.debug("find by example successful, result size: "
					+ results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}

	public List findByProperty(String propertyName, Object value) {
		log.debug("finding AccountIoLog instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from AccountIoLog as model where model."
					+ propertyName + "= ?";
			return getHibernateTemplate().find(queryString, value);
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.xabaohui.modules.pay.service.dao.impl.AccountIoLogDao#findByAccountId
	 * (java .lang.Object)
	 */
	@Override
	public List findByAccountId(Object accountId) {
		return findByProperty(ACCOUNT_ID, accountId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.xabaohui.modules.pay.service.dao.impl.AccountIoLogDao#findByOppositeId
	 * (java .lang.Object)
	 */
	@Override
	public List findByOppositeId(Object oppositeId) {
		return findByProperty(OPPOSITE_ID, oppositeId);
	}

	public List findByPayMoney(Object payMoney) {
		return findByProperty(TRADE_MONEY, payMoney);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.xabaohui.modules.pay.service.dao.impl.AccountIoLogDao#findByBizRef
	 * (java.lang .Object)
	 */
	@Override
	public List findByBizRef(Object bizRef) {
		return findByProperty(BIZ_REF, bizRef);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.xabaohui.modules.pay.service.dao.impl.AccountIoLogDao#findByIoFlag
	 * (java.lang .Object)
	 */
	@Override
	public List findByIoFlag(Object ioFlag) {
		return findByProperty(IO_FLAG, ioFlag);
	}

	public List findByVersion(Object version) {
		return findByProperty(VERSION, version);
	}

	public List findAll() {
		log.debug("finding all AccountIoLog instances");
		try {
			String queryString = "from AccountIoLog";
			return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public AccountIoLog merge(AccountIoLog detachedInstance) {
		log.debug("merging AccountIoLog instance");
		try {
			AccountIoLog result = (AccountIoLog) getHibernateTemplate().merge(
					detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(AccountIoLog instance) {
		log.debug("attaching dirty AccountIoLog instance");
		try {
			getHibernateTemplate().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(AccountIoLog instance) {
		log.debug("attaching clean AccountIoLog instance");
		try {
			getHibernateTemplate().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public static AccountIoLogDao getFromApplicationContext(
			ApplicationContext ctx) {
		return (AccountIoLogDao) ctx.getBean("AccountIoLogDAO");
	}

	@Override
	public List findByCriteria(DetachedCriteria criteria) {
		return getHibernateTemplate().findByCriteria(criteria);
	}

	@Override
	public void update(AccountIoLog accountIoLog) {
		getHibernateTemplate().update(accountIoLog);

	}
}