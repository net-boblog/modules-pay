package com.xabaohui.modules.pay.dao.impl;

import java.util.List;

import org.hibernate.LockMode;
import org.hibernate.criterion.DetachedCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.xabaohui.modules.pay.bean.ExoFsClearing;
import com.xabaohui.modules.pay.dao.ExoFsClearingDao;

/**
 * A data access object (DAO) providing persistence and search support for
 * ExoFsClearing entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see com.zis.payment.bean.ExoFsClearing
 * @author MyEclipse Persistence Tools
 */
public class ExoFsClearingDaoImpl extends HibernateDaoSupport implements
		ExoFsClearingDao {
	private static final Logger log = LoggerFactory
			.getLogger(ExoFsClearingDaoImpl.class);

	protected void initDao() {
		// do nothing
	}

	@Override
	public void save(ExoFsClearing transientInstance) {
		log.debug("saving ExoFsClearing instance");
		try {
			getHibernateTemplate().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(ExoFsClearing persistentInstance) {
		log.debug("deleting ExoFsClearing instance");
		try {
			getHibernateTemplate().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	@Override
	public ExoFsClearing findById(java.lang.Integer id) {
		log.debug("getting ExoFsClearing instance with id: " + id);
		try {
			ExoFsClearing instance = (ExoFsClearing) getHibernateTemplate()
					.get("com.zis.payment.bean.ExoFsClearing", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	@Override
	public List findByExample(ExoFsClearing instance) {
		log.debug("finding ExoFsClearing instance by example");
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
		log.debug("finding ExoFsClearing instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from ExoFsClearing as model where model."
					+ propertyName + "= ?";
			return getHibernateTemplate().find(queryString, value);
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findByExoRef(Object exoRef) {
		return findByProperty(EXO_REF, exoRef);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.xabaohui.modules.pay.dao.impl.ExoFsClearingDao#findByOurFsId(java
	 * .lang.Object)
	 */
	@Override
	public List findByOurFsId(Object ourFsId) {
		return findByProperty(OUR_FS_ID, ourFsId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.xabaohui.modules.pay.dao.impl.ExoFsClearingDao#findByInstructionId
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
	 * com.xabaohui.modules.pay.dao.impl.ExoFsClearingDao#findByProtocolId(java
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

	public List findByExoFee(Object exoFee) {
		return findByProperty(EXO_FEE, exoFee);
	}

	public List findByVersion(Object version) {
		return findByProperty(VERSION, version);
	}

	public List findAll() {
		log.debug("finding all ExoFsClearing instances");
		try {
			String queryString = "from ExoFsClearing";
			return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public ExoFsClearing merge(ExoFsClearing detachedInstance) {
		log.debug("merging ExoFsClearing instance");
		try {
			ExoFsClearing result = (ExoFsClearing) getHibernateTemplate()
					.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(ExoFsClearing instance) {
		log.debug("attaching dirty ExoFsClearing instance");
		try {
			getHibernateTemplate().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(ExoFsClearing instance) {
		log.debug("attaching clean ExoFsClearing instance");
		try {
			getHibernateTemplate().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public static ExoFsClearingDao getFromApplicationContext(
			ApplicationContext ctx) {
		return (ExoFsClearingDao) ctx.getBean("ExoFsClearingDAO");
	}

	@Override
	public List findByCriteria(DetachedCriteria criteria) {
		return getHibernateTemplate().findByCriteria(criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.xabaohui.modules.pay.dao.ExoFsClearingDao#update(com.xabaohui.modules
	 * .pay.bean.ExoFsClearing)
	 */
	@Override
	public void update(ExoFsClearing exoFsClearing) {
		getHibernateTemplate().update(exoFsClearing);
	}
}