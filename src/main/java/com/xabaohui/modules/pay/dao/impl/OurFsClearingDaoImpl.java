package com.xabaohui.modules.pay.dao.impl;

import java.util.List;

import org.hibernate.LockMode;
import org.hibernate.criterion.DetachedCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.xabaohui.modules.pay.bean.OurFsClearing;
import com.xabaohui.modules.pay.dao.OurFsClearingDao;

/**
 * A data access object (DAO) providing persistence and search support for
 * OurFsClearing entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see com.xabaohui.modules.pay.bean.OurFsClearing
 * @author MyEclipse Persistence Tools
 */
public class OurFsClearingDaoImpl extends HibernateDaoSupport implements
		OurFsClearingDao {
	private static final Logger log = LoggerFactory
			.getLogger(OurFsClearingDaoImpl.class);

	protected void initDao() {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.xabaohui.modules.pay.dao.impl.OurFsClearingDao#save(com.xabaohui.
	 * modules.pay.bean.OurFsClearing)
	 */
	@Override
	public void save(OurFsClearing transientInstance) {
		log.debug("saving OurFsClearing instance");
		try {
			getHibernateTemplate().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(OurFsClearing persistentInstance) {
		log.debug("deleting OurFsClearing instance");
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
	 * com.xabaohui.modules.pay.dao.impl.OurFsClearingDao#findById(java.lang
	 * .Integer)
	 */
	@Override
	public OurFsClearing findById(java.lang.Integer id) {
		log.debug("getting OurFsClearing instance with id: " + id);
		try {
			OurFsClearing instance = (OurFsClearing) getHibernateTemplate()
					.get("com.xabaohui.modules.pay.bean.OurFsClearing", id);
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
	 * com.xabaohui.modules.pay.dao.impl.OurFsClearingDao#findByExample(com.
	 * xabaohui.modules.pay.bean.OurFsClearing)
	 */
	@Override
	public List findByExample(OurFsClearing instance) {
		log.debug("finding OurFsClearing instance by example");
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
		log.debug("finding OurFsClearing instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from OurFsClearing as model where model."
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
	 * com.xabaohui.modules.pay.dao.impl.OurFsClearingDao#findByInstructionId
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
	 * com.xabaohui.modules.pay.dao.impl.OurFsClearingDao#findByProtocolId(java
	 * .lang.Object)
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
		log.debug("finding all OurFsClearing instances");
		try {
			String queryString = "from OurFsClearing";
			return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public OurFsClearing merge(OurFsClearing detachedInstance) {
		log.debug("merging OurFsClearing instance");
		try {
			OurFsClearing result = (OurFsClearing) getHibernateTemplate()
					.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(OurFsClearing instance) {
		log.debug("attaching dirty OurFsClearing instance");
		try {
			getHibernateTemplate().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(OurFsClearing instance) {
		log.debug("attaching clean OurFsClearing instance");
		try {
			getHibernateTemplate().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public static OurFsClearingDao getFromApplicationContext(
			ApplicationContext ctx) {
		return (OurFsClearingDao) ctx.getBean("OurFsClearingDAO");
	}

	@Override
	public List findByCriteria(DetachedCriteria criteria) {
		return getHibernateTemplate().findByCriteria(criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.xabaohui.modules.pay.dao.OurFsClearingDao#update(com.xabaohui.modules
	 * .pay.bean.OurFsClearing)
	 */
	@Override
	public void update(OurFsClearing ourFsClearing) {
		getHibernateTemplate().update(ourFsClearing);

	}
}