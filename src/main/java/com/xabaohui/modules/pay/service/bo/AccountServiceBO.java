package com.xabaohui.modules.pay.service.bo;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import com.xabaohui.modules.pay.bean.Account;
import com.xabaohui.modules.pay.bean.AccountIoLog;
import com.xabaohui.modules.pay.bean.FreezeLog;
import com.xabaohui.modules.pay.bean.PaymentInstruction;
import com.xabaohui.modules.pay.bean.SpecialAccount;
import com.xabaohui.modules.pay.bean.channel.PaymentInstructionChannel;
import com.xabaohui.modules.pay.bean.status.AccountStatus;
import com.xabaohui.modules.pay.bean.type.AccountIoLogType;
import com.xabaohui.modules.pay.bean.type.AccountType;
import com.xabaohui.modules.pay.bean.type.FreezeLogType;
import com.xabaohui.modules.pay.bean.type.PaymentInstructionType;
import com.xabaohui.modules.pay.dao.AccountDao;
import com.xabaohui.modules.pay.dao.AccountIoLogDao;
import com.xabaohui.modules.pay.dao.FreezeLogDao;
import com.xabaohui.modules.pay.dto.CreateAccountIOLogDTO;
import com.xabaohui.modules.pay.dto.CreateFreezeLogDTO;
import com.xabaohui.modules.pay.util.Time;
import com.xabaohui.modules.pay.util.Validation;

/**
 * 
 * @author YRee
 * 
 */
public class AccountServiceBO {
	static Logger logger = LoggerFactory.getLogger(AccountServiceBO.class);
	AccountDao accountDao;
	AccountIoLogDao accountIoLogDao;
	FreezeLogDao freezeLogDao;

	/**
	 * ��ʼ�����������˻�id
	 */
	public void initSpecialAccount() {
		// �жϵ�Ǯ�˻��Ƿ����
		Account bankGetAcc = this.findByAccountId(SpecialAccount.bankget);
		if (bankGetAcc == null) {
			// create
			Account account = this.getInitAccount(AccountType.BANKGET);
			account.setAccountId(SpecialAccount.bankget);
			accountDao.save(account);
			logger.info("����{}�˻��ɹ�", SpecialAccount.bankget);
		} else if (!AccountType.BANKGET.equals(bankGetAcc.getType())) {
			throw new RuntimeException("���ݿ���bankget���͵��˻�id������������ռ��");
		}
		logger.info("{}�˻�����", AccountType.BANKGET);

		// �жϸ����˻��Ƿ����
		Account bankPayAcc = this.findByAccountId(SpecialAccount.bankpay);
		if (bankPayAcc == null) {
			// create
			Account account = this.getInitAccount(AccountType.BANKGET);
			account.setAccountId(SpecialAccount.bankpay);
			accountDao.save(account);
			logger.info("����{}�˻��ɹ�", SpecialAccount.bankpay);
		} else if (!AccountType.BANKPAY.equals(bankPayAcc.getType())) {
			throw new RuntimeException("���ݿ���bankget���͵��˻�id������������ռ��");
		}
		logger.info("{}�˻�����", AccountType.BANKPAY);
		// �жϵ������˻��Ƿ����
		Account guarantee = this.findByAccountId(SpecialAccount.guaranteeId);
		if (guarantee == null) {
			// create
			Account account = this.getInitAccount(AccountType.CASH);
			account.setAccountId(SpecialAccount.guaranteeId);
			accountDao.save(account);
			logger.info("����{}�˻��ɹ�", SpecialAccount.guaranteeId);
		} else if (!AccountType.BANKPAY.equals(bankPayAcc.getType())) {
			throw new RuntimeException("���ݿ���bankget���͵��˻�id������������ռ��");
		}
		logger.info("{}�˻�����", SpecialAccount.guaranteeId);
	}

	/**
	 * �����˻�
	 * 
	 * @param userId
	 * @param accountType
	 * @return
	 */
	public Account createAccount(Integer userId, String accountType) {
		// ��֤userID��accountType
		this.validatecreateAccount(userId, accountType);

		// �����˻�
		Account account = this.getInitAccount(accountType);
		account.setUserId(userId);
		//
		// account.setBalance(0.0);
		// account.setFrozenMoney(0.0);
		// account.setStatus(AccountStatus.NORMAL);
		// account.setType(accountType);
		// Date now = Time.getNow();
		// account.setGmtCreate(now);
		// account.setGmtModify(now);
		// account.setVersion(1);
		accountDao.save(account);
		logger.info("�½��˻� " + account.getAccountId() + " �ɹ�");
		return account;
	}

	/**
	 * ��ʼ��ĳ�����͵��˻�
	 * 
	 * @param accountType
	 * @return
	 */
	private Account getInitAccount(String accountType) {
		Account account = new Account();
		account.setBalance(0.0);
		account.setFrozenMoney(0.0);
		account.setStatus(AccountStatus.NORMAL);
		account.setType(accountType);
		Date now = Time.getNow();
		account.setGmtCreate(now);
		account.setGmtModify(now);
		account.setVersion(1);
		return account;
	}

	/**
	 * ��֤ս��id��Type�Ƿ�Ϊ��
	 * 
	 * @param userId
	 * @param accountType
	 */
	private void validatecreateAccount(Integer userId, String accountType) {
		if (userId == null) {
			throw new RuntimeException("�����û�ʧ��:userId�ǿ�");
		}
		if (StringUtils.isBlank(accountType)) {
			throw new RuntimeException("�����û�ʧ��:�˻����Ͳ���Ϊ��");
		}
		// �жϸ��û��Ƿ��Ѿ������˸����͵��˻�
		if (this.findAccountByUserIdAndType(userId, accountType) != null) {
			throw new RuntimeException("�����û�ʧ��:���û��Ѿ�ӵ�и����͵��˻�");
		}
	}

	/**
	 * ��ѯ�˻������
	 * <p/>
	 * <ul>
	 * <li>totalBalance �ܽ��</li>
	 * <li>availableBalance �������</li>
	 * <li>freezeBalance ������</li>
	 * </ul>
	 * 
	 * @param accountId
	 * @return
	 */
	public double findBlanceByAccountId(Integer accountId) {
		Account account = this.findByAccountId(accountId);
		if (account == null) {
			throw new RuntimeException("������accountId��û�в�ѯ���˻�");
		}
		return account.getBalance();
	}

	/**
	 * ��ѯ�˻����������
	 * 
	 * @param accountId
	 * @return
	 */
	public Double findAvailableByAccountId(Integer accountId) {
		Account account = this.findByAccountId(accountId);
		if (account == null) {
			throw new RuntimeException("������accountId��û�в�ѯ���˻�");
		}
		return account.getBalance() - account.getFrozenMoney();
	}

	/**
	 * ��ѯ�˻��Ľ�����ˮ
	 * 
	 * @param accountId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<AccountIoLog> findIOByAccountId(Integer accountId) {
		if (accountId == null) {
			throw new RuntimeException("������accountIdΪ��");
		}
		List<AccountIoLog> listIOLog = accountIoLogDao
				.findByAccountId(accountId);
		logger.info("��õ���ˮ��{}��", listIOLog.size());
		return listIOLog;
	}

	/**
	 * �����˻��������������֧��
	 * 
	 * @param accountId
	 * @return
	 */
	public double findIncomeOrOutcomeTotalByAccountId(Integer accountId,
			String IOtype) {
		List<AccountIoLog> listIOLog = this.findIOByAccountId(accountId);
		double money = 0.0;
		for (AccountIoLog accountIoLog : listIOLog) {
			if (IOtype.equals(accountIoLog.getIoFlag())) {
				money = accountIoLog.getTradeMoney();
			}
		}
		logger.info("�˻�" + accountId + "��" + IOtype + "���Ϊ:" + money);
		return money;
	}

	/**
	 * ͨ���û�id����������ѯ�˻�
	 * 
	 * @param userId
	 * @param Type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Account findAccountByUserIdAndType(Integer userId, String type) {
		if (StringUtils.isBlank(type)) {
			throw new RuntimeException("������������Ƿ�Ϊ��");
		}
		if (userId == null) {
			throw new RuntimeException("userId����Ϊ��");
		}
		// List<Account> list = this.findAccountsByUserId(userId);
		// findByExample
		// findByCriteria

		DetachedCriteria criteria = DetachedCriteria.forClass(Account.class);
		criteria.add(Restrictions.eq("userId", userId));
		criteria.add(Restrictions.eq("type", type));
		criteria.add(Restrictions.ne("status", AccountStatus.ABANDON));
		List<Account> accounts = accountDao.findByCriteria(criteria);
		// û���ҵ�
		if (accounts.isEmpty()) {
			return null;
		} else if (accounts.size() > 1) {
			throw new RuntimeException("�û�ͬ���͵��˻����ܴ���2��");
		}
		return accounts.get(0);
		// // ͨ��idû�ҵ�
		// if (list == null || list.isEmpty()) {
		// return null;
		// }
		// // ͨ��id��type�ҵ���
		// for (Account account : list) {
		// if (type.equals(account.getType())) {
		// return account;
		// }
		// }
		// // ��id,û��type
		// return null;
	}

	/**
	 * ͨ��userId�����˻�
	 * 
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Account> findAccountsByUserId(Integer userId) {
		if (userId == null) {
			throw new RuntimeException("���userId�Ƿ���ȷ");
		}
		DetachedCriteria criteria = DetachedCriteria.forClass(Account.class);
		criteria.add(Restrictions.eq("userId", userId));
		// ���鶳�� ״̬��
		criteria.add(Restrictions.ne("status", AccountStatus.FROZEN));
		return accountDao.findByCriteria(criteria);
	}

	/**
	 * ͨ��accountID�����˻�
	 * 
	 * @param id
	 * @return
	 */
	public Account findByAccountId(Integer id) {
		if (id == null) {
			throw new RuntimeException("accountId�쳣");
		}
		Account account = accountDao.findById(id);
		if (account == null) {
			throw new RuntimeException("������accountId��û�в�ѯ���˻�");
		}
		if (AccountStatus.ABANDON.equals(account.getStatus())) {
			account = null;
		}
		return account;
	}

	/**
	 * �����˻���Ϣ
	 * 
	 * @param account
	 */
	protected void updateAccount(Account account) {
		if (account == null) {
			throw new RuntimeException("������account�����ǿ�");
		}
		if (account.getAccountId() == null) {
			throw new RuntimeException("����accountId�Ƿ���ȷ");
		}
		account.setGmtModify(Time.getNow());
		account.setVersion(account.getVersion() + 1);
		accountDao.update(account);
	}

	/**
	 * �����˻����
	 * 
	 * @param uamDTO
	 */
	protected void addAccountAmount(PaymentInstruction instruction) {
		// ��֤����
		this.validateInstruction(instruction);
		Account account = this.findByAccountId(instruction.getReceiverId());
		// �����˻����� ֱ�ӷ���
		if (SpecialAccount.bankget.equals(account.getAccountId())) {
			return;
		}
		// �˿�����������
		if (!PaymentInstructionChannel.BALANCE.equals(instruction.getChannel())
				&& PaymentInstructionType.REFUND.equals(instruction.getType())) {
			// ����������־
			logger.info("��ָ�{}��Ϊ�˿�ָ��˻���{}����ǮΪ���������������˻���Ǯ������",
					instruction.getPaymentInstructionId(),
					account.getAccountId());
		} else {
			account.setBalance(account.getBalance() + instruction.getPayMoney());
			// �����û�
			this.updateAccount(account);
		}
		// ����ˮ
		CreateAccountIOLogDTO caIODTO = new CreateAccountIOLogDTO();
		caIODTO.setAccountId(instruction.getReceiverId());
		caIODTO.setOppositeId(instruction.getPayerId());
		caIODTO.setBizRef(instruction.getPaymentInstructionId());
		caIODTO.setIoFlag(AccountIoLogType.INCOME);
		caIODTO.setTradeMoney(instruction.getPayMoney());
		caIODTO.setInstructionType(instruction.getType());
		logger.info("�˻�{}�����������{}", account.getAccountId(),
				instruction.getPayMoney());
		this.createAccountIOLog(caIODTO);
	}

	/**
	 * �����˻����
	 * 
	 * @param uamDTO
	 */
	protected void reduceAccountAmount(PaymentInstruction instruction) {
		// ��֤����
		this.validateInstruction(instruction);

		Account account = this.findByAccountId(instruction.getPayerId());

		// ��ֵ�˻�������,ֱ�ӷ���
		if (SpecialAccount.bankpay.equals(account.getAccountId())) {
			return;
		}
		// �������Ƿ�֧��
		if (account.getBalance() < instruction.getPayMoney()) {
			throw new RuntimeException("����֧��");
		}
		// ��������������,��Ϊָ������Ϊ��Ϊ�˿�/���֣��Ͳ����м���,ֱ������
		if (PaymentInstructionChannel.isOuterChannel(instruction.getChannel())
				&& !PaymentInstructionType.REFUND.equals(instruction.getType())
				&& !PaymentInstructionType.WITHDRAW.equals(instruction
						.getType())) {
			// �������˻������������ǻ��д���־
			logger.info("��ָ�{}���в����У��˻���{}����ǮΪ���������������˻���Ǯ������",
					instruction.getPaymentInstructionId(),
					account.getAccountId());
		} else {
			account.setBalance(account.getBalance() - instruction.getPayMoney());
			// �����û�
			this.updateAccount(account);
		}
		// ����ˮ
		CreateAccountIOLogDTO caIODTO = new CreateAccountIOLogDTO();
		caIODTO.setAccountId(instruction.getPayerId());
		caIODTO.setOppositeId(instruction.getReceiverId());
		caIODTO.setBizRef(instruction.getPaymentInstructionId());
		caIODTO.setIoFlag(AccountIoLogType.OUTCOME);
		caIODTO.setTradeMoney(instruction.getPayMoney());
		caIODTO.setInstructionType(instruction.getType());
		logger.info("�˻�" + account.getAccountId() + "�˻���������"
				+ instruction.getPayMoney());
		this.createAccountIOLog(caIODTO);

	}

	/**
	 * ��֤ ָ��
	 * 
	 * @param instruction
	 */
	private void validateInstruction(PaymentInstruction instruction) {
		if (instruction == null) {
			throw new RuntimeException("������ָ��Ϊ��");
		}
		if (instruction.getPayerId() == null
				|| instruction.getReceiverId() == null
				|| !Validation.isValidMoney(instruction.getPayMoney())) {
			throw new RuntimeException("money����accountIdֵ����ȷ");
		}
	}

	//
	// /**
	// * �����˻����
	// *
	// * @param instruction
	// */
	// public void freezeAccountMoney(PaymentInstruction instruction) {
	// Account account = this.chooseAccountForFreeMoney(instruction);
	// // ���ö�����
	// logger.info(instruction.getPaymentInstructionId() + "��ָ��ִ���˴��˻�"
	// + account.getAccountId() + "����" + instruction.getPayMoney()
	// + "Ԫ");
	//
	// account.setFrozenMoney(instruction.getPayMoney());
	// // �����˻�
	// this.updateAccount(account);
	// // �򶳽���ˮ
	// CreateFreezeLogDTO cflDTO = new CreateFreezeLogDTO();
	// cflDTO.setAccountId(account.getAccountId());
	// cflDTO.setBizRef(instruction.getPaymentInstructionId());
	// cflDTO.setFrozenMoney(instruction.getPayMoney());
	// cflDTO.setOperateType(FreezeLogType.FREEZE);
	// this.createFreezeLog(cflDTO);
	// }
	//
	// /**
	// * �ⶳ�˻����
	// *
	// * @param instruction
	// */
	// public void unfreezeAccountMoney(PaymentInstruction instruction) {
	// Account account = this.chooseAccountForFreeMoney(instruction);
	// // ���ö�����
	// account.setFrozenMoney(account.getFrozenMoney()
	// - instruction.getPayMoney());
	// // �����˻�
	// this.updateAccount(account);
	// // �򶳽���ˮ
	// CreateFreezeLogDTO cflDTO = new CreateFreezeLogDTO();
	// cflDTO.setAccountId(account.getAccountId());
	// cflDTO.setBizRef(instruction.getPaymentInstructionId());
	// cflDTO.setFrozenMoney(instruction.getPayMoney());
	// cflDTO.setOperateType(FreezeLogType.UNFREEZE);
	// this.createFreezeLog(cflDTO);
	// }

	/**
	 * �Զ��������
	 * 
	 * @param instruction
	 * @param freezeType
	 * 
	 */
	public void freezeMoneyOperation(PaymentInstruction instruction,
			String freezeType) {
		Account account = this.chooseAccountForFreeMoney(instruction);

		// �ⶳ����
		if (FreezeLogType.UNFREEZE.equals(freezeType)) {
			// ���ö�����
			account.setFrozenMoney(account.getFrozenMoney()
					- instruction.getPayMoney());

		} else if (FreezeLogType.FREEZE.equals(freezeType)) {// �������
			account.setFrozenMoney(account.getFrozenMoney()
					+ instruction.getPayMoney());
		} else {
			throw new RuntimeException("�Ƕ���ⶳ���ͣ�����ʹ�ò���������");
		}

		logger.info("ָ��{}ִ���˴��˻�{}�Ĳ���{}��{}Ԫ",
				instruction.getPaymentInstructionId(), account.getAccountId(),
				freezeType, instruction.getPayMoney());

		// �����˻�
		this.updateAccount(account);
		// �򶳽���ˮ
		CreateFreezeLogDTO cflDTO = new CreateFreezeLogDTO();
		cflDTO.setOperateType(freezeType);
		cflDTO.setAccountId(account.getAccountId());
		cflDTO.setBizRef(instruction.getPaymentInstructionId());
		cflDTO.setFrozenMoney(instruction.getPayMoney());
		this.createFreezeLog(cflDTO);
	}

	/**
	 * ѡ�񶳽�����˻�
	 * 
	 * @param instruction
	 * @return
	 */
	private Account chooseAccountForFreeMoney(PaymentInstruction instruction) {
		this.validateInstruction(instruction);
		// ������ǳ�ֵ���֣���ô���ܶ�����
		logger.info("ָ��" + instruction.getPaymentInstructionId() + "�Ĳ�����:"
				+ instruction.getType());
		if (!PaymentInstructionType.REFUND.equals(instruction.getType())
				&& !PaymentInstructionType.WITHDRAW.equals(instruction
						.getType())) {
			throw new RuntimeException("�����˿�/���֣����ܶԶ�������в���");
		}
		Account account = this.findByAccountId(instruction.getPayerId());
		return account;
	}

	/**
	 * ���ɶ�����ϸ
	 * 
	 * @param cflDTO
	 */
	private void createFreezeLog(CreateFreezeLogDTO cflDTO) {
		FreezeLog freezeLog = new FreezeLog();
		BeanUtils.copyProperties(cflDTO, freezeLog);
		Date now = Time.getNow();
		freezeLog.setGmtCreate(now);
		freezeLog.setGmtModify(now);
		freezeLog.setVersion(1);
		freezeLogDao.save(freezeLog);
	}

	/**
	 * ͨ��ָ��������ˮ
	 * 
	 * @param instruction
	 */
	private void createAccountIOLog(CreateAccountIOLogDTO caIODTO) {
		if (caIODTO == null) {
			throw new RuntimeException("������caIODTO�����ǿ�");
		}
		if (caIODTO.getBizRef() == null || caIODTO.getAccountId() == null
				|| caIODTO.getOppositeId() == null
				|| !Validation.isValidMoney(caIODTO.getTradeMoney())
				|| StringUtils.isBlank(caIODTO.getIoFlag())
				|| StringUtils.isBlank(caIODTO.getInstructionType())) {
			throw new RuntimeException("���鴫�� �����Ƿ���ȷ����	");
		}

		// �ж��Ƿ�Ϊת�˻�������
		// ����ǳ�ֵ
		if (PaymentInstructionType.RECHARGE
				.equals(caIODTO.getInstructionType())
				&& AccountIoLogType.OUTCOME.equals(caIODTO.getBizRef())) {
			return;
		}
		// ���������
		if (PaymentInstructionType.WITHDRAW
				.equals(caIODTO.getInstructionType())
				&& AccountIoLogType.INCOME.equals(caIODTO.getBizRef())) {
			return;
		}

		Date now = Time.getNow();

		AccountIoLog ioLog = new AccountIoLog();
		BeanUtils.copyProperties(caIODTO, ioLog);
		ioLog.setGmtCreate(now);
		ioLog.setGmtModify(now);
		ioLog.setVersion(1);

		accountIoLogDao.save(ioLog);
	}

	public AccountDao getAccountDao() {
		return accountDao;
	}

	public void setAccountDao(AccountDao accountDao) {
		this.accountDao = accountDao;
	}

	public AccountIoLogDao getAccountIoLogDao() {
		return accountIoLogDao;
	}

	public void setAccountIoLogDao(AccountIoLogDao accountIoLogDao) {
		this.accountIoLogDao = accountIoLogDao;
	}

	public FreezeLogDao getFreezeLogDao() {
		return freezeLogDao;
	}

	public void setFreezeLogDao(FreezeLogDao freezeLogDao) {
		this.freezeLogDao = freezeLogDao;
	}

}
