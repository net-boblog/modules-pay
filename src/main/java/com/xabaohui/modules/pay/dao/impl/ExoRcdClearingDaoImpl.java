package com.xabaohui.modules.pay.dao.impl;

import java.util.List;

import org.hibernate.LockMode;
import org.hibernate.criterion.DetachedCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.xabaohui.modules.pay.bean.ExoRcdClearing;
import com.xabaohui.modules.pay.dao.ExoRcdClearingDao;

/**
 * A data access object (DAO) providing persistence and search support for
 * ExoRcdClearing entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see com.xabaohui.modules.pay.bean.ExoRcdClearing
 * @author MyEclipse Persistence Tools
 */
public class ExoRcdClearingDaoImpl extends HibernateDaoSupport implements
		ExoRcdClearingDao {
	private static final Logger log = LoggerFactory
			.getLogger(ExoRcdClearingDaoImpl.class);

	protected void initDao() {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.xabaohui.modules.pay.dao.impl.ExoRcdClearingDao#save(com.xabaohui
	 * .modules.pay.bean.ExoRcdClearing)
	 */
	@Override
	public void save(ExoRcdClearing transientInstance) {
		log.debug("saving ExoRcdClearing instance");
		try {
			getHibernateTemplate().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(ExoRcdClearing persistentInstance) {
		log.debug("deleting ExoRcdClearing instance");
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
	 * com.xabaohui.modules.pay.dao.impl.ExoRcdClearingDao#findById(java.lang
	 * .Integer)
	 */
	@Override
	public ExoRcdClearing findById(java.lang.Integer id) {
		log.debug("getting ExoRcdClearing instance with id: " + id);
		try {
			ExoRcdClearing instance = (ExoRcdClearing) getHibernateTemplate()
					.get("com.xabaohui.modules.pay.bean.ExoRcdClearing", id);
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
	 * com.xabaohui.modules.pay.dao.impl.ExoRcdClearingDao#findByExample(com
	 * .xabaohui.modules.pay.bean.ExoRcdClearing)
	 */
	@Override
	public List findByExample(ExoRcdClearing instance) {
		log.debug("finding ExoRcdClearing instance by example");
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
		log.debug("finding ExoRcdClearing instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from ExoRcdClearing as model where model."
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
	 * com.xabaohui.modules.pay.dao.impl.ExoRcdClearingDao#findByExoRef(java
	 * .lang.Object)
	 */
	@Override
	public List findByExoRef(Object exoRef) {
		return findByProperty(EXO_REF, exoRef);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.xabaohui.modules.pay.dao.impl.ExoRcdClearingDao#findByOurRcdId(java
	 * .lang.Object)
	 */
	@Override
	public List findByOurRcdId(Object ourRcdId) {
		return findByProperty(OUR_RCD_ID, ourRcdId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.xabaohui.modules.pay.dao.impl.ExoRcdClearingDao#findByInstructionId
	 * (java.lang.Object)
	 */
	@Override
	public List findByInstructionId(Object instructionId) {
		return findByProperty(INSTRUCTION_ID, instructionId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.xabaohui.modules.pay.dao.impl.ExoRcdClearingDao#findByProtocolId(
	 * java.lang.Object)
	 */
	@Override
	public List findByProtocolId(Object protocolId) {
		return findByProperty(PROTOCOL_ID, protocolId);
	}

	public List findByTxMoney(Object txMoney) {
		return findByProperty(TX_MONEY, txMoney);
	}

	public List findByTxType(Object txType) {
		return findByProperty(TX_TYPE, txType);
	}

	public List findByTxChannel(Object txChannel) {
		return findByProperty(TX_CHANNEL, txChannel);
	}

	public List findByVersion(Object version) {
		return findByProperty(VERSION, version);
	}

	public List findAll() {
		log.debug("finding all ExoRcdClearing instances");
		try {
			String queryString = "from ExoRcdClearing";
			return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public ExoRcdClearing merge(ExoRcdClearing detachedInstance) {
		log.debug("merging ExoRcdClearing instance");
		try {
			ExoRcdClearing result = (ExoRcdClearing) getHibernateTemplate()
					.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(ExoRcdClearing instance) {
		log.debug("attaching dirty ExoRcdClearing instance");
		try {
			getHibernateTemplate().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(ExoRcdClearing instance) {
		log.debug("attaching clean ExoRcdClearing instance");
		try {
			getHibernateTemplate().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public static ExoRcdClearingDao getFromApplicationContext(
			ApplicationContext ctx) {
		return (ExoRcdClearingDao) ctx.getBean("ExoRcdClearingDAO");
	}

	@Override
	public List findByCriteria(DetachedCriteria criteria) {
		return getHibernateTemplate().findByCriteria(criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.xabaohui.modules.pay.dao.ExoRcdClearingDao#update(com.xabaohui.modules
	 * .pay.bean.ExoRcdClearing)
	 */
	@Override
	public void update(ExoRcdClearing exoRcdClearing) {
		getHibernateTemplate().update(exoRcdClearing);

	}
}