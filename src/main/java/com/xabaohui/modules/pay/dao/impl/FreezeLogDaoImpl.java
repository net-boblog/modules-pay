package com.xabaohui.modules.pay.dao.impl;

import java.util.List;

import org.hibernate.LockMode;
import org.hibernate.criterion.DetachedCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.xabaohui.modules.pay.bean.FreezeLog;
import com.xabaohui.modules.pay.dao.FreezeLogDao;

/**
 * A data access object (DAO) providing persistence and search support for
 * FreezeLog entities. Transaction control of the save(), update() and delete()
 * operations can directly support Spring container-managed transactions or they
 * can be augmented to handle user-managed Spring transactions. Each of these
 * methods provides additional information for how to configure it for the
 * desired type of transaction control.
 * 
 * @see com.xabaohui.modules.pay.bean.FreezeLog
 * @author MyEclipse Persistence Tools
 */
public class FreezeLogDaoImpl extends HibernateDaoSupport implements
		FreezeLogDao {
	private static final Logger log = LoggerFactory
			.getLogger(FreezeLogDaoImpl.class);

	protected void initDao() {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.xabaohui.modules.pay.service.dao.impl.FreezeLogDao#save(com.xabaohui
	 * .modules.pay.bean .FreezeLog)
	 */
	@Override
	public void save(FreezeLog transientInstance) {
		log.debug("saving FreezeLog instance");
		try {
			getHibernateTemplate().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(FreezeLog persistentInstance) {
		log.debug("deleting FreezeLog instance");
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
	 * com.xabaohui.modules.pay.service.dao.impl.FreezeLogDao#findById(java.
	 * lang.Integer)
	 */
	@Override
	public FreezeLog findById(java.lang.Integer id) {
		log.debug("getting FreezeLog instance with id: " + id);
		try {
			FreezeLog instance = (FreezeLog) getHibernateTemplate().get(
					"com.xabaohui.modules.pay.bean.FreezeLog", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByExample(FreezeLog instance) {
		log.debug("finding FreezeLog instance by example");
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
		log.debug("finding FreezeLog instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from FreezeLog as model where model."
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
	 * com.xabaohui.modules.pay.service.dao.impl.FreezeLogDao#findByAccountId
	 * (java.lang .Object)
	 */
	@Override
	public List findByAccountId(Object accountId) {
		return findByProperty(ACCOUNT_ID, accountId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.xabaohui.modules.pay.service.dao.impl.FreezeLogDao#findByFrozenMoney
	 * (java. lang.Object)
	 */
	@Override
	public List findByFrozenMoney(Object frozenMoney) {
		return findByProperty(FROZEN_MONEY, frozenMoney);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.xabaohui.modules.pay.service.dao.impl.FreezeLogDao#findByBizRef(java
	 * .lang. Object)
	 */
	@Override
	public List findByBizRef(Object bizRef) {
		return findByProperty(BIZ_REF, bizRef);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.xabaohui.modules.pay.service.dao.impl.FreezeLogDao#findByOperateType
	 * (java. lang.Object)
	 */
	@Override
	public List findByOperateType(Object operateType) {
		return findByProperty(OPERATE_TYPE, operateType);
	}

	public List findByVersion(Object version) {
		return findByProperty(VERSION, version);
	}

	public List findAll() {
		log.debug("finding all FreezeLog instances");
		try {
			String queryString = "from FreezeLog";
			return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public FreezeLog merge(FreezeLog detachedInstance) {
		log.debug("merging FreezeLog instance");
		try {
			FreezeLog result = (FreezeLog) getHibernateTemplate().merge(
					detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(FreezeLog instance) {
		log.debug("attaching dirty FreezeLog instance");
		try {
			getHibernateTemplate().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(FreezeLog instance) {
		log.debug("attaching clean FreezeLog instance");
		try {
			getHibernateTemplate().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public static FreezeLogDao getFromApplicationContext(ApplicationContext ctx) {
		return (FreezeLogDao) ctx.getBean("FreezeLogDAO");
	}

	@Override
	public List findByCriteria(DetachedCriteria criteria) {
		return getHibernateTemplate().findByCriteria(criteria);
	}

	@Override
	public void update(FreezeLog freezeLog) {
		getHibernateTemplate().update(freezeLog);
	}
}
