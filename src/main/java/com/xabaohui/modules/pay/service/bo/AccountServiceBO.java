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
	 * 初始化常量特殊账户id
	 */
	public void initSpecialAccount() {
		// 判断得钱账户是否存在
		Account bankGetAcc = this.findByAccountId(SpecialAccount.bankget);
		if (bankGetAcc == null) {
			// create
			Account account = this.getInitAccount(AccountType.BANKGET);
			account.setAccountId(SpecialAccount.bankget);
			accountDao.save(account);
			logger.info("新增{}账户成功", SpecialAccount.bankget);
		} else if (!AccountType.BANKGET.equals(bankGetAcc.getType())) {
			throw new RuntimeException("数据库中bankget类型的账户id被其他类型所占用");
		}
		logger.info("{}账户存在", AccountType.BANKGET);

		// 判断付款账户是否存在
		Account bankPayAcc = this.findByAccountId(SpecialAccount.bankpay);
		if (bankPayAcc == null) {
			// create
			Account account = this.getInitAccount(AccountType.BANKGET);
			account.setAccountId(SpecialAccount.bankpay);
			accountDao.save(account);
			logger.info("新增{}账户成功", SpecialAccount.bankpay);
		} else if (!AccountType.BANKPAY.equals(bankPayAcc.getType())) {
			throw new RuntimeException("数据库中bankget类型的账户id被其他类型所占用");
		}
		logger.info("{}账户存在", AccountType.BANKPAY);
		// 判断担保户账户是否存在
		Account guarantee = this.findByAccountId(SpecialAccount.guaranteeId);
		if (guarantee == null) {
			// create
			Account account = this.getInitAccount(AccountType.CASH);
			account.setAccountId(SpecialAccount.guaranteeId);
			accountDao.save(account);
			logger.info("新增{}账户成功", SpecialAccount.guaranteeId);
		} else if (!AccountType.BANKPAY.equals(bankPayAcc.getType())) {
			throw new RuntimeException("数据库中bankget类型的账户id被其他类型所占用");
		}
		logger.info("{}账户存在", SpecialAccount.guaranteeId);
	}

	/**
	 * 创建账户
	 * 
	 * @param userId
	 * @param accountType
	 * @return
	 */
	public Account createAccount(Integer userId, String accountType) {
		// 验证userID和accountType
		this.validatecreateAccount(userId, accountType);

		// 创建账户
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
		logger.info("新建账户 " + account.getAccountId() + " 成功");
		return account;
	}

	/**
	 * 初始化某种类型的账户
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
	 * 验证战鼓id和Type是否为空
	 * 
	 * @param userId
	 * @param accountType
	 */
	private void validatecreateAccount(Integer userId, String accountType) {
		if (userId == null) {
			throw new RuntimeException("创建用户失败:userId是空");
		}
		if (StringUtils.isBlank(accountType)) {
			throw new RuntimeException("创建用户失败:账户类型不能为空");
		}
		// 判断该用户是否已经常见了该类型的账户
		if (this.findAccountByUserIdAndType(userId, accountType) != null) {
			throw new RuntimeException("创建用户失败:该用户已经拥有该类型的账户");
		}
	}

	/**
	 * 查询账户的余额
	 * <p/>
	 * <ul>
	 * <li>totalBalance 总金额</li>
	 * <li>availableBalance 可用余额</li>
	 * <li>freezeBalance 冻结金额</li>
	 * </ul>
	 * 
	 * @param accountId
	 * @return
	 */
	public double findBlanceByAccountId(Integer accountId) {
		Account account = this.findByAccountId(accountId);
		if (account == null) {
			throw new RuntimeException("传来的accountId并没有查询到账户");
		}
		return account.getBalance();
	}

	/**
	 * 查询账户可以用余额
	 * 
	 * @param accountId
	 * @return
	 */
	public Double findAvailableByAccountId(Integer accountId) {
		Account account = this.findByAccountId(accountId);
		if (account == null) {
			throw new RuntimeException("传来的accountId并没有查询到账户");
		}
		return account.getBalance() - account.getFrozenMoney();
	}

	/**
	 * 查询账户的交易流水
	 * 
	 * @param accountId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<AccountIoLog> findIOByAccountId(Integer accountId) {
		if (accountId == null) {
			throw new RuntimeException("传来的accountId为空");
		}
		List<AccountIoLog> listIOLog = accountIoLogDao
				.findByAccountId(accountId);
		logger.info("获得的流水有{}条", listIOLog.size());
		return listIOLog;
	}

	/**
	 * 查找账户的总收入或者总支出
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
		logger.info("账户" + accountId + "的" + IOtype + "金额为:" + money);
		return money;
	}

	/**
	 * 通过用户id和类型来查询账户
	 * 
	 * @param userId
	 * @param Type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Account findAccountByUserIdAndType(Integer userId, String type) {
		if (StringUtils.isBlank(type)) {
			throw new RuntimeException("请检查你的类型是否为空");
		}
		if (userId == null) {
			throw new RuntimeException("userId不能为空");
		}
		// List<Account> list = this.findAccountsByUserId(userId);
		// findByExample
		// findByCriteria

		DetachedCriteria criteria = DetachedCriteria.forClass(Account.class);
		criteria.add(Restrictions.eq("userId", userId));
		criteria.add(Restrictions.eq("type", type));
		criteria.add(Restrictions.ne("status", AccountStatus.ABANDON));
		List<Account> accounts = accountDao.findByCriteria(criteria);
		// 没有找到
		if (accounts.isEmpty()) {
			return null;
		} else if (accounts.size() > 1) {
			throw new RuntimeException("用户同类型的账户不能大于2个");
		}
		return accounts.get(0);
		// // 通过id没找到
		// if (list == null || list.isEmpty()) {
		// return null;
		// }
		// // 通过id和type找到了
		// for (Account account : list) {
		// if (type.equals(account.getType())) {
		// return account;
		// }
		// }
		// // 有id,没有type
		// return null;
	}

	/**
	 * 通过userId查找账户
	 * 
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Account> findAccountsByUserId(Integer userId) {
		if (userId == null) {
			throw new RuntimeException("检查userId是否正确");
		}
		DetachedCriteria criteria = DetachedCriteria.forClass(Account.class);
		criteria.add(Restrictions.eq("userId", userId));
		// 不查冻结 状态的
		criteria.add(Restrictions.ne("status", AccountStatus.FROZEN));
		return accountDao.findByCriteria(criteria);
	}

	/**
	 * 通过accountID查找账户
	 * 
	 * @param id
	 * @return
	 */
	public Account findByAccountId(Integer id) {
		if (id == null) {
			throw new RuntimeException("accountId异常");
		}
		Account account = accountDao.findById(id);
		if (account == null) {
			throw new RuntimeException("传来的accountId并没有查询到账户");
		}
		if (AccountStatus.ABANDON.equals(account.getStatus())) {
			account = null;
		}
		return account;
	}

	/**
	 * 更新账户信息
	 * 
	 * @param account
	 */
	protected void updateAccount(Account account) {
		if (account == null) {
			throw new RuntimeException("传来的account对象是空");
		}
		if (account.getAccountId() == null) {
			throw new RuntimeException("请检查accountId是否正确");
		}
		account.setGmtModify(Time.getNow());
		account.setVersion(account.getVersion() + 1);
		accountDao.update(account);
	}

	/**
	 * 增加账户余额
	 * 
	 * @param uamDTO
	 */
	protected void addAccountAmount(PaymentInstruction instruction) {
		// 验证参数
		this.validateInstruction(instruction);
		Account account = this.findByAccountId(instruction.getReceiverId());
		// 提现账户金额不加 直接返回
		if (SpecialAccount.bankget.equals(account.getAccountId())) {
			return;
		}
		// 退款，且向第三方退
		if (!PaymentInstructionChannel.BALANCE.equals(instruction.getChannel())
				&& PaymentInstructionType.REFUND.equals(instruction.getType())) {
			// 不处理，打日志
			logger.info("在指令【{}】为退款指令，账户【{}】的钱为第三方，所以其账户的钱不增加",
					instruction.getPaymentInstructionId(),
					account.getAccountId());
		} else {
			account.setBalance(account.getBalance() + instruction.getPayMoney());
			// 更新用户
			this.updateAccount(account);
		}
		// 打流水
		CreateAccountIOLogDTO caIODTO = new CreateAccountIOLogDTO();
		caIODTO.setAccountId(instruction.getReceiverId());
		caIODTO.setOppositeId(instruction.getPayerId());
		caIODTO.setBizRef(instruction.getPaymentInstructionId());
		caIODTO.setIoFlag(AccountIoLogType.INCOME);
		caIODTO.setTradeMoney(instruction.getPayMoney());
		caIODTO.setInstructionType(instruction.getType());
		logger.info("账户{}总余额增加了{}", account.getAccountId(),
				instruction.getPayMoney());
		this.createAccountIOLog(caIODTO);
	}

	/**
	 * 减少账户余额
	 * 
	 * @param uamDTO
	 */
	protected void reduceAccountAmount(PaymentInstruction instruction) {
		// 验证参数
		this.validateInstruction(instruction);

		Account account = this.findByAccountId(instruction.getPayerId());

		// 充值账户金额不减少,直接返回
		if (SpecialAccount.bankpay.equals(account.getAccountId())) {
			return;
		}
		// 检测余额是否够支付
		if (account.getBalance() < instruction.getPayMoney()) {
			throw new RuntimeException("余额不够支付");
		}
		// 第三方渠道付款,且为指令类型为不为退款/提现，就不进行减少,直接跳过
		if (PaymentInstructionChannel.isOuterChannel(instruction.getChannel())
				&& !PaymentInstructionType.REFUND.equals(instruction.getType())
				&& !PaymentInstructionType.WITHDRAW.equals(instruction
						.getType())) {
			// 不进行账户余额操作，但是还有打日志
			logger.info("在指令【{}】中操作中，账户【{}】的钱为第三方，所以其账户的钱不减少",
					instruction.getPaymentInstructionId(),
					account.getAccountId());
		} else {
			account.setBalance(account.getBalance() - instruction.getPayMoney());
			// 更新用户
			this.updateAccount(account);
		}
		// 打流水
		CreateAccountIOLogDTO caIODTO = new CreateAccountIOLogDTO();
		caIODTO.setAccountId(instruction.getPayerId());
		caIODTO.setOppositeId(instruction.getReceiverId());
		caIODTO.setBizRef(instruction.getPaymentInstructionId());
		caIODTO.setIoFlag(AccountIoLogType.OUTCOME);
		caIODTO.setTradeMoney(instruction.getPayMoney());
		caIODTO.setInstructionType(instruction.getType());
		logger.info("账户" + account.getAccountId() + "账户金额减少了"
				+ instruction.getPayMoney());
		this.createAccountIOLog(caIODTO);

	}

	/**
	 * 验证 指令
	 * 
	 * @param instruction
	 */
	private void validateInstruction(PaymentInstruction instruction) {
		if (instruction == null) {
			throw new RuntimeException("传来的指令为空");
		}
		if (instruction.getPayerId() == null
				|| instruction.getReceiverId() == null
				|| !Validation.isValidMoney(instruction.getPayMoney())) {
			throw new RuntimeException("money或者accountId值不正确");
		}
	}

	//
	// /**
	// * 冻结账户余额
	// *
	// * @param instruction
	// */
	// public void freezeAccountMoney(PaymentInstruction instruction) {
	// Account account = this.chooseAccountForFreeMoney(instruction);
	// // 设置冻结金额
	// logger.info(instruction.getPaymentInstructionId() + "号指令执行了从账户"
	// + account.getAccountId() + "冻结" + instruction.getPayMoney()
	// + "元");
	//
	// account.setFrozenMoney(instruction.getPayMoney());
	// // 更新账户
	// this.updateAccount(account);
	// // 打冻结流水
	// CreateFreezeLogDTO cflDTO = new CreateFreezeLogDTO();
	// cflDTO.setAccountId(account.getAccountId());
	// cflDTO.setBizRef(instruction.getPaymentInstructionId());
	// cflDTO.setFrozenMoney(instruction.getPayMoney());
	// cflDTO.setOperateType(FreezeLogType.FREEZE);
	// this.createFreezeLog(cflDTO);
	// }
	//
	// /**
	// * 解冻账户余额
	// *
	// * @param instruction
	// */
	// public void unfreezeAccountMoney(PaymentInstruction instruction) {
	// Account account = this.chooseAccountForFreeMoney(instruction);
	// // 设置冻结金额
	// account.setFrozenMoney(account.getFrozenMoney()
	// - instruction.getPayMoney());
	// // 更新账户
	// this.updateAccount(account);
	// // 打冻结流水
	// CreateFreezeLogDTO cflDTO = new CreateFreezeLogDTO();
	// cflDTO.setAccountId(account.getAccountId());
	// cflDTO.setBizRef(instruction.getPaymentInstructionId());
	// cflDTO.setFrozenMoney(instruction.getPayMoney());
	// cflDTO.setOperateType(FreezeLogType.UNFREEZE);
	// this.createFreezeLog(cflDTO);
	// }

	/**
	 * 对冻结金额操作
	 * 
	 * @param instruction
	 * @param freezeType
	 * 
	 */
	public void freezeMoneyOperation(PaymentInstruction instruction,
			String freezeType) {
		Account account = this.chooseAccountForFreeMoney(instruction);

		// 解冻操作
		if (FreezeLogType.UNFREEZE.equals(freezeType)) {
			// 设置冻结金额
			account.setFrozenMoney(account.getFrozenMoney()
					- instruction.getPayMoney());

		} else if (FreezeLogType.FREEZE.equals(freezeType)) {// 冻结操作
			account.setFrozenMoney(account.getFrozenMoney()
					+ instruction.getPayMoney());
		} else {
			throw new RuntimeException("非冻结解冻类型，不能使用操作冻结金额");
		}

		logger.info("指令{}执行了从账户{}的操作{}了{}元",
				instruction.getPaymentInstructionId(), account.getAccountId(),
				freezeType, instruction.getPayMoney());

		// 更新账户
		this.updateAccount(account);
		// 打冻结流水
		CreateFreezeLogDTO cflDTO = new CreateFreezeLogDTO();
		cflDTO.setOperateType(freezeType);
		cflDTO.setAccountId(account.getAccountId());
		cflDTO.setBizRef(instruction.getPaymentInstructionId());
		cflDTO.setFrozenMoney(instruction.getPayMoney());
		this.createFreezeLog(cflDTO);
	}

	/**
	 * 选择冻结操作账户
	 * 
	 * @param instruction
	 * @return
	 */
	private Account chooseAccountForFreeMoney(PaymentInstruction instruction) {
		this.validateInstruction(instruction);
		// 如果不是充值提现，那么不能冻结金额
		logger.info("指令" + instruction.getPaymentInstructionId() + "的操作是:"
				+ instruction.getType());
		if (!PaymentInstructionType.REFUND.equals(instruction.getType())
				&& !PaymentInstructionType.WITHDRAW.equals(instruction
						.getType())) {
			throw new RuntimeException("不是退款/提现，不能对冻结金额进行操作");
		}
		Account account = this.findByAccountId(instruction.getPayerId());
		return account;
	}

	/**
	 * 生成冻结明细
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
	 * 通过指令生成流水
	 * 
	 * @param instruction
	 */
	private void createAccountIOLog(CreateAccountIOLogDTO caIODTO) {
		if (caIODTO == null) {
			throw new RuntimeException("传来的caIODTO对象是空");
		}
		if (caIODTO.getBizRef() == null || caIODTO.getAccountId() == null
				|| caIODTO.getOppositeId() == null
				|| !Validation.isValidMoney(caIODTO.getTradeMoney())
				|| StringUtils.isBlank(caIODTO.getIoFlag())
				|| StringUtils.isBlank(caIODTO.getInstructionType())) {
			throw new RuntimeException("请检查传来 数据是否正确无误	");
		}

		// 判断是否为转账或者提现
		// 如果是充值
		if (PaymentInstructionType.RECHARGE
				.equals(caIODTO.getInstructionType())
				&& AccountIoLogType.OUTCOME.equals(caIODTO.getBizRef())) {
			return;
		}
		// 如果是提现
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
