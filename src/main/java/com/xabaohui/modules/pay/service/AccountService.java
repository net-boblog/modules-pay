package com.xabaohui.modules.pay.service;

import java.util.List;

import com.xabaohui.modules.pay.bean.Account;
import com.xabaohui.modules.pay.bean.AccountIoLog;
import com.xabaohui.modules.pay.service.bo.AccountServiceBO;

//@Service
public class AccountService {
	private AccountServiceBO accountServiceBO;

	/**
	 * 创建账户
	 * 
	 * @param userId
	 * @param accountType
	 * @return
	 */
	public Account createAccount(Integer userId, String accountType) {
		return accountServiceBO.createAccount(userId, accountType);
	}

	/**
	 * 查询账户余额
	 * 
	 * @param accountId
	 * @return
	 */
	public double findBlanceByAccountId(Integer accountId) {
		return accountServiceBO.findBlanceByAccountId(accountId);
	}

	/**
	 * 查询账户可用余额
	 * 
	 * @param accountId
	 * @return
	 */
	public double findAvailableByAccountId(Integer accountId) {
		return accountServiceBO.findAvailableByAccountId(accountId);
	}

	/**
	 * 查询账户流水
	 * 
	 * @param accountId
	 * @return
	 */
	public List<AccountIoLog> findIOByAccountId(Integer accountId) {
		return accountServiceBO.findIOByAccountId(accountId);
	}

	/**
	 * 查找账户的总收入或者总支出
	 * 
	 * @param accountId
	 * @return
	 */
	public double findIncomeOrOutcomeTotalByAccountId(Integer accountId,
			String IOtype) {
		return accountServiceBO.findIncomeOrOutcomeTotalByAccountId(accountId,
				IOtype);
	}

	/**
	 * 通过用户id和类型来查询账户
	 * 
	 * @param userId
	 * @param Type
	 * @return
	 */
	public Account findAccountByUserIdAndType(int userId, String Type) {
		return accountServiceBO.findAccountByUserIdAndType(userId, Type);
	}

	/**
	 * 查询用户id下的所有账户
	 * 
	 * @param userId
	 * @return
	 */
	public List<Account> findAccountsByUserId(Integer userId) {
		return accountServiceBO.findAccountsByUserId(userId);
	}

	/**
	 * 通过accountID查找账户
	 * 
	 * @param id
	 * @return
	 */
	public Account findByAccountId(Integer id) {
		return accountServiceBO.findByAccountId(id);
	}

	public AccountServiceBO getAccountServiceBO() {
		return accountServiceBO;
	}

	public void setAccountServiceBO(AccountServiceBO accountServiceBO) {
		this.accountServiceBO = accountServiceBO;
	}

}
