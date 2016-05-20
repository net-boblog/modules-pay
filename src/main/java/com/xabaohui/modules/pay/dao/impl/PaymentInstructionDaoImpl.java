package com.xabaohui.modules.pay.dao.impl;

import java.util.List;

import org.hibernate.LockMode;
import org.hibernate.criterion.DetachedCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.xabaohui.modules.pay.bean.PaymentInstruction;
import com.xabaohui.modules.pay.dao.PaymentInstructionDao;

/**
 * A data access object (DAO) providing persistence and search support for
 * PaymentInstruction entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see com.xabaohui.modules.pay.bean.PaymentInstruction
 * @author MyEclipse Persistence Tools
 */
public class PaymentInstructionDaoImpl extends HibernateDaoSupport implements
		PaymentInstructionDao {
	private static final Logger log = LoggerFactory
			.getLogger(PaymentInstructionDaoImpl.class);

	protected void initDao() {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.xabaohui.modules.pay.dao.impl.PaymentInstructionDao#save(com.xabaohui
	 * .modules.pay.bean .PaymentInstruction)
	 */
	@Override
	public void save(PaymentInstruction transientInstance) {
		log.debug("saving PaymentInstruction instance");
		try {
			getHibernateTemplate().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(PaymentInstruction persistentInstance) {
		log.debug("deleting PaymentInstruction instance");
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
	 * com.xabaohui.modules.pay.dao.impl.PaymentInstructionDao#findById(java
	 * .lang.Integer )
	 */
	@Override
	public PaymentInstruction findById(java.lang.Integer id) {
		log.debug("getting PaymentInstruction instance with id: " + id);
		try {
			PaymentInstruction instance = (PaymentInstruction) getHibernateTemplate()
					.get("com.xabaohui.modules.pay.bean.PaymentInstruction", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed++++++++++==", re);
			throw re;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.xabaohui.modules.pay.dao.impl.PaymentInstructionDao#findByExample
	 * (com.zis. payment.bean.PaymentInstruction)
	 */
	@Override
	public List findByExample(PaymentInstruction instance) {
		log.debug("finding PaymentInstruction instance by example");
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
		log.debug("finding PaymentInstruction instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from PaymentInstruction as model where model."
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
	 * com.xabaohui.modules.pay.dao.impl.PaymentInstructionDao#findByProtocolId
	 * (java. lang.Object)
	 */
	@Override
	public List findByProtocolId(Object protocolId) {
		return findByProperty(PROTOCOL_ID, protocolId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.xabaohui.modules.pay.dao.impl.PaymentInstructionDao#findByPayerId
	 * (java.lang .Object)
	 */
	@Override
	public List findByPayerId(Object payerId) {
		return findByProperty(PAYER_ID, payerId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.xabaohui.modules.pay.dao.impl.PaymentInstructionDao#findByReceiverId
	 * (java. lang.Object)
	 */
	@Override
	public List findByReceiverId(Object receiverId) {
		return findByProperty(RECEIVER_ID, receiverId);
	}

	public List findByType(Object type) {
		return findByProperty(TYPE, type);
	}

	public List findByChannel(Object channel) {
		return findByProperty(CHANNEL, channel);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.xabaohui.modules.pay.dao.impl.PaymentInstructionDao#findByStatus(
	 * java.lang .Object)
	 */
	@Override
	public List findByStatus(Object status) {
		return findByProperty(STATUS, status);
	}

	public List findByPayMoney(Object payMoney) {
		return findByProperty(PAY_MONEY, payMoney);
	}

	public List findByRefundMoney(Object refundMoney) {
		return findByProperty(REFUND_MONEY, refundMoney);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.xabaohui.modules.pay.dao.impl.PaymentInstructionDao#findByVersion
	 * (java.lang .Object)
	 */
	@Override
	public List findByVersion(Object version) {
		return findByProperty(VERSION, version);
	}

	public List findAll() {
		log.debug("finding all PaymentInstruction instances");
		try {
			String queryString = "from PaymentInstruction";
			return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.xabaohui.modules.pay.dao.impl.PaymentInstructionDao#merge(com.xabaohui
	 * .modules.pay. bean.PaymentInstruction)
	 */
	@Override
	public PaymentInstruction merge(PaymentInstruction detachedInstance) {
		log.debug("merging PaymentInstruction instance");
		try {
			PaymentInstruction result = (PaymentInstruction) getHibernateTemplate()
					.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(PaymentInstruction instance) {
		log.debug("attaching dirty PaymentInstruction instance");
		try {
			getHibernateTemplate().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(PaymentInstruction instance) {
		log.debug("attaching clean PaymentInstruction instance");
		try {
			getHibernateTemplate().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public static PaymentInstructionDao getFromApplicationContext(
			ApplicationContext ctx) {
		return (PaymentInstructionDao) ctx.getBean("PaymentInstructionDAO");
	}

	@Override
	public void update(PaymentInstruction instruction) {
		log.debug("update PaymentInstruction÷∏¡Ó instance");
		try {
			getHibernateTemplate().update(instruction);
			log.debug("update ÷∏¡Ósuccessful");
		} catch (RuntimeException re) {
			log.error("update ÷∏¡Ófailed", re);
			throw re;
		}

	}

	@Override
	public List findByCriteria(DetachedCriteria criteria) {
		return getHibernateTemplate().findByCriteria(criteria);
	}
}