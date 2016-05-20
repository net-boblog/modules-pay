package com.xabaohui.modules.pay.dao.impl;

import java.util.List;

import org.hibernate.LockMode;
import org.hibernate.criterion.DetachedCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.xabaohui.modules.pay.bean.Account;
import com.xabaohui.modules.pay.dao.AccountDao;

/**
 * A data access object (DAO) providing persistence and search support for
 * Account entities. Transaction control of the save(), update() and delete()
 * operations can directly support Spring container-managed transactions or they
 * can be augmented to handle user-managed Spring transactions. Each of these
 * methods provides additional information for how to configure it for the
 * desired type of transaction control.
 * 
 * @see com.xabaohui.modules.pay.bean.Account
 * @author MyEclipse Persistence Tools
 */
public class AccountDaoImpl extends HibernateDaoSupport implements AccountDao {
	private static final Logger log = LoggerFactory
			.getLogger(AccountDaoImpl.class);

	protected void initDao() {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.xabaohui.modules.pay.service.dao.impl.AccountDao#save(com.xabaohui
	 * .modules.pay.bean .Account)
	 */
	@Override
	public void save(Account transientInstance) {
		log.debug("saving Account instance");
		try {
			getHibernateTemplate().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(Account persistentInstance) {
		log.debug("deleting Account instance");
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
	 * com.xabaohui.modules.pay.service.dao.impl.AccountDao#findById(java.lang
	 * .Integer)
	 */
	@Override
	public Account findById(java.lang.Integer id) {
		log.debug("getting Account instance with id: " + id);
		try {
			Account instance = (Account) getHibernateTemplate().get(
					"com.xabaohui.modules.pay.bean.Account", id);
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
	 * com.xabaohui.modules.pay.service.dao.impl.AccountDao#findByExample(com
	 * .xabaohui.modules.pay .bean.Account)
	 */
	@Override
	public List findByExample(Account instance) {
		log.debug("finding Account instance by example");
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
		log.debug("finding Account instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from Account as model where model."
					+ propertyName + "= ?";
			return getHibernateTemplate().find(queryString, value);
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findByBalance(Object balance) {
		return findByProperty(BALANCE, balance);
	}

	public List findByFrozenMoney(Object frozenMoney) {
		return findByProperty(FROZEN_MONEY, frozenMoney);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.xabaohui.modules.pay.service.dao.impl.AccountDao#findByType(java.
	 * lang.Object)
	 */
	@Override
	public List findByType(Object type) {
		return findByProperty(TYPE, type);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.xabaohui.modules.pay.service.dao.impl.AccountDao#findByStatus(java
	 * .lang.Object )
	 */
	@Override
	public List findByStatus(Object status) {
		return findByProperty(STATUS, status);
	}

	public List findByVersion(Object version) {
		return findByProperty(VERSION, version);
	}

	public List findAll() {
		log.debug("finding all Account instances");
		try {
			String queryString = "from Account";
			return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public Account merge(Account detachedInstance) {
		log.debug("merging Account instance");
		try {
			Account result = (Account) getHibernateTemplate().merge(
					detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(Account instance) {
		log.debug("attaching dirty Account instance");
		try {
			getHibernateTemplate().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Account instance) {
		log.debug("attaching clean Account instance");
		try {
			getHibernateTemplate().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public List findByUserid(Object userid) {
		return findByProperty(USERID, userid);
	}

	public static AccountDao getFromApplicationContext(ApplicationContext ctx) {
		return (AccountDao) ctx.getBean("AccountDAO");
	}

	@Override
	public List findByCriteria(DetachedCriteria criteria) {
		return getHibernateTemplate().findByCriteria(criteria);
	}

	@Override
	public void update(Account account) {
		getHibernateTemplate().update(account);
	}
}