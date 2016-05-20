package com.xabaohui.modules.pay.service;

import java.util.List;

import com.xabaohui.modules.pay.bean.Account;
import com.xabaohui.modules.pay.bean.AccountIoLog;
import com.xabaohui.modules.pay.service.bo.AccountServiceBO;

//@Service
public class AccountService {
	private AccountServiceBO accountServiceBO;

	/**
	 * �����˻�
	 * 
	 * @param userId
	 * @param accountType
	 * @return
	 */
	public Account createAccount(Integer userId, String accountType) {
		return accountServiceBO.createAccount(userId, accountType);
	}

	/**
	 * ��ѯ�˻����
	 * 
	 * @param accountId
	 * @return
	 */
	public double findBlanceByAccountId(Integer accountId) {
		return accountServiceBO.findBlanceByAccountId(accountId);
	}

	/**
	 * ��ѯ�˻��������
	 * 
	 * @param accountId
	 * @return
	 */
	public double findAvailableByAccountId(Integer accountId) {
		return accountServiceBO.findAvailableByAccountId(accountId);
	}

	/**
	 * ��ѯ�˻���ˮ
	 * 
	 * @param accountId
	 * @return
	 */
	public List<AccountIoLog> findIOByAccountId(Integer accountId) {
		return accountServiceBO.findIOByAccountId(accountId);
	}

	/**
	 * �����˻��������������֧��
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
	 * ͨ���û�id����������ѯ�˻�
	 * 
	 * @param userId
	 * @param Type
	 * @return
	 */
	public Account findAccountByUserIdAndType(int userId, String Type) {
		return accountServiceBO.findAccountByUserIdAndType(userId, Type);
	}

	/**
	 * ��ѯ�û�id�µ������˻�
	 * 
	 * @param userId
	 * @return
	 */
	public List<Account> findAccountsByUserId(Integer userId) {
		return accountServiceBO.findAccountsByUserId(userId);
	}

	/**
	 * ͨ��accountID�����˻�
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
