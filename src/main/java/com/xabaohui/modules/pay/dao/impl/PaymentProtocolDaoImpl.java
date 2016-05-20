package com.xabaohui.modules.pay.dao.impl;

import java.util.List;

import org.hibernate.LockMode;
import org.hibernate.criterion.DetachedCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.xabaohui.modules.pay.bean.PaymentProtocol;
import com.xabaohui.modules.pay.dao.PaymentProtocolDao;

/**
 * A data access object (DAO) providing persistence and search support for
 * PaymentProtocol entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see com.xabaohui.modules.pay.bean.PaymentProtocol
 * @author MyEclipse Persistence Tools
 */
public class PaymentProtocolDaoImpl extends HibernateDaoSupport implements
		PaymentProtocolDao {
	private static final Logger log = LoggerFactory
			.getLogger(PaymentProtocolDaoImpl.class);

	protected void initDao() {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.xabaohui.modules.pay.dao.impl.PaymentProtocol#save(com.xabaohui.modules
	 * .pay.bean. PaymentProtocol)
	 */

	public void delete(PaymentProtocol persistentInstance) {
		log.debug("deleting PaymentProtocol instance");
		try {
			getHibernateTemplate().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	@Override
	public PaymentProtocol findById(java.lang.Integer id) {
		log.debug("getting PaymentProtocol instance with id: " + id);
		try {
			PaymentProtocol instance = (PaymentProtocol) getHibernateTemplate()
					.get("com.xabaohui.modules.pay.bean.PaymentProtocol", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	@Override
	public List findByExample(PaymentProtocol instance) {
		log.debug("finding PaymentProtocol instance by example");
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
		log.debug("finding PaymentProtocol instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from PaymentProtocol as model where model."
					+ propertyName + "= ?";
			return getHibernateTemplate().find(queryString, value);
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	@Override
	public List findByPayerId(Object payerId) {
		return findByProperty(PAYER_ID, payerId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.xabaohui.modules.pay.dao.impl.PaymentProtocol#findByReceiverId(java
	 * .lang.Object )
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.xabaohui.modules.pay.dao.impl.PaymentProtocolDao#findByReceiverId
	 * (java.lang .Object)
	 */
	@Override
	public List findByReceiverId(Object receiverId) {
		return findByProperty(RECEIVER_ID, receiverId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.xabaohui.modules.pay.dao.impl.PaymentProtocol#findByOrderId(java.
	 * lang.Object)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.xabaohui.modules.pay.dao.impl.PaymentProtocolDao#findByOrderId(java
	 * .lang.Object )
	 */
	@Override
	public PaymentProtocol findByOrderId(Object orderId) {
		List<PaymentProtocol> listPaymentProtocols = findByProperty(ORDER_ID,
				orderId);
		if (listPaymentProtocols.isEmpty()) {
			return null;
		} else
			// FIXME Ö±½Óget(0)£¿
			return listPaymentProtocols.get(0);
	}

	public List findByPayMoney(Object payMoney) {
		return findByProperty(PAY_MONEY, payMoney);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.xabaohui.modules.pay.dao.impl.PaymentProtocol#findByStatus(java.lang
	 * .Object)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.xabaohui.modules.pay.dao.impl.PaymentProtocolDao#findByStatus(java
	 * .lang.Object )
	 */
	@Override
	public List findByStatus(Object status) {
		return findByProperty(STATUS, status);
	}

	public List findByType(Object type) {
		return findByProperty(TYPE, type);
	}

	public List findByVersion(Object version) {
		return findByProperty(VERSION, version);
	}

	public List findAll() {
		log.debug("finding all PaymentProtocol instances");
		try {
			String queryString = "from PaymentProtocol";
			return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public PaymentProtocol merge(PaymentProtocol detachedInstance) {
		log.debug("merging PaymentProtocol instance");
		try {
			PaymentProtocol result = (PaymentProtocol) getHibernateTemplate()
					.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(PaymentProtocol instance) {
		log.debug("attaching dirty PaymentProtocol instance");
		try {
			getHibernateTemplate().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(PaymentProtocol instance) {
		log.debug("attaching clean PaymentProtocol instance");
		try {
			getHibernateTemplate().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public static PaymentProtocolDao getFromApplicationContext(
			ApplicationContext ctx) {
		return (PaymentProtocolDao) ctx.getBean("PaymentProtocolDAO");
	}

	@Override
	public void save(PaymentProtocol transientInstance) {
		log.debug("saving PaymentProtocol instance");
		try {
			getHibernateTemplate().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}

	}

	@Override
	public void update(PaymentProtocol protocol) {
		getHibernateTemplate().update(protocol);

	}

	@Override
	public List findByCriteria(DetachedCriteria criteria) {
		return getHibernateTemplate().findByCriteria(criteria);
	}
}