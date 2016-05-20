package com.xabaohui.modules.pay.dao.impl;

import java.util.List;

import org.hibernate.LockMode;
import org.hibernate.criterion.DetachedCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.xabaohui.modules.pay.bean.RefundDetail;
import com.xabaohui.modules.pay.dao.RefundDetailDao;

/**
 * A data access object (DAO) providing persistence and search support for
 * RefundDetail entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see com.xabaohui.modules.pay.bean.RefundDetail
 * @author MyEclipse Persistence Tools
 */
public class RefundDetailDaoImpl extends HibernateDaoSupport implements
		RefundDetailDao {
	private static final Logger log = LoggerFactory
			.getLogger(RefundDetailDaoImpl.class);

	protected void initDao() {
		// do nothing
	}

	@Override
	public void save(RefundDetail transientInstance) {
		log.debug("saving RefundDetail instance");
		try {
			getHibernateTemplate().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(RefundDetail persistentInstance) {
		log.debug("deleting RefundDetail instance");
		try {
			getHibernateTemplate().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	@Override
	public RefundDetail findById(java.lang.Integer id) {
		log.debug("getting RefundDetail instance with id: " + id);
		try {
			RefundDetail instance = (RefundDetail) getHibernateTemplate().get(
					"com.xabaohui.modules.pay.bean.RefundDetail", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByExample(RefundDetail instance) {
		log.debug("finding RefundDetail instance by example");
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
		log.debug("finding RefundDetail instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from RefundDetail as model where model."
					+ propertyName + "= ?";
			return getHibernateTemplate().find(queryString, value);
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	@Override
	public List findByBuyerId(Object buyerId) {
		return findByProperty(BUYER_ID, buyerId);
	}

	@Override
	public List findBySellerId(Object sellerId) {
		return findByProperty(SELLER_ID, sellerId);
	}

	@Override
	public List findByProtocolId(Object protocolId) {
		return findByProperty(PROTOCOL_ID, protocolId);
	}

	@Override
	public List findByRefundMoney(Object refundMoney) {
		return findByProperty(REFUND_MONEY, refundMoney);
	}

	@Override
	public List findByStatus(Object status) {
		return findByProperty(STATUS, status);
	}

	@Override
	public List findByVersion(Object version) {
		return findByProperty(VERSION, version);
	}

	public List findByRefundInstructions(Object refundInstructions) {
		return findByProperty(REFUND_INSTRUCTIONS, refundInstructions);
	}

	public List findAll() {
		log.debug("finding all RefundDetail instances");
		try {
			String queryString = "from RefundDetail";
			return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public RefundDetail merge(RefundDetail detachedInstance) {
		log.debug("merging RefundDetail instance");
		try {
			RefundDetail result = (RefundDetail) getHibernateTemplate().merge(
					detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(RefundDetail instance) {
		log.debug("attaching dirty RefundDetail instance");
		try {
			getHibernateTemplate().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(RefundDetail instance) {
		log.debug("attaching clean RefundDetail instance");
		try {
			getHibernateTemplate().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public static RefundDetailDao getFromApplicationContext(
			ApplicationContext ctx) {
		return (RefundDetailDao) ctx.getBean("RefundDetailDAO");
	}

	@Override
	public List findByCriteria(DetachedCriteria criteria) {
		return getHibernateTemplate().findByCriteria(criteria);
	}

	@Override
	public void update(RefundDetail refundDetail) {
		getHibernateTemplate().update(refundDetail);

	}
}