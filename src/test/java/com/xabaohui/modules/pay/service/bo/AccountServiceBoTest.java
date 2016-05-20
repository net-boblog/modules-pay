/**
 * 
 */
package com.xabaohui.modules.pay.service.bo;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.xabaohui.modules.pay.bean.Account;
import com.xabaohui.modules.pay.bean.AccountIoLog;
import com.xabaohui.modules.pay.bean.PaymentInstruction;
import com.xabaohui.modules.pay.bean.PaymentProtocol;
import com.xabaohui.modules.pay.bean.type.AccountIoLogType;
import com.xabaohui.modules.pay.bean.type.AccountType;
import com.xabaohui.modules.pay.bean.type.PaymentInstructionType;
import com.xabaohui.modules.pay.bean.type.PaymentProtocolType;
import com.xabaohui.modules.pay.dto.CreatePaymentInstructionDTO;
import com.xabaohui.modules.pay.dto.CreatePaymentProtocolDTO;

/**
 * @author YRee
 * 
 */
@TransactionConfiguration(transactionManager = "transactionManagerH", defaultRollback = false)
// @Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:payment-servlet.xml")
public class AccountServiceBoTest extends
		AbstractTransactionalJUnit4SpringContextTests {
	protected static Logger logger = LoggerFactory
			.getLogger(AccountServiceBoTest.class);
	@Resource
	AccountServiceBO accountServiceBO;

	@Resource
	PayServiceBO payServiceBO;
	@Resource
	TransferAccountMoneyBO transferAccountMoneyBO;
	@Resource
	private TestUtil testUtil;

	/**
	 * �����˻�����userIdΪ��
	 */
	@Test(expected = RuntimeException.class)
	public void createAccountTestForUserId() {
		int userId = (Integer) null;
		String accountType = AccountType.CASH;
		accountServiceBO.createAccount(userId, accountType);
	}

	@Test
	/**
	 * �����˻��ɹ�
	 */
	public void createAccountTestSuccess() {
		int userId = testUtil.createRandomUserId();
		String accountType = AccountType.CASH;
		Account account = accountServiceBO.createAccount(userId, accountType);

		Account account2 = accountServiceBO.findByAccountId(account
				.getAccountId());
		if (account2 == null) {
			Assert.fail("�����˻�ʧ��");
		}
	}

	/**
	 * �ظ��Ĵ����˻�
	 */
	@Test
	public void createAccountTestForRepeat() {
		int userId = testUtil.createRandomUserId();
		String accountType = AccountType.CASH;
		// ����һ���˻�
		accountServiceBO.createAccount(userId, accountType);

		try {
			// �ٴδ����˻�
			accountServiceBO.createAccount(userId, accountType);
		} catch (RuntimeException e) {
			if (!"�����û�ʧ��:���û��Ѿ�ӵ�и����͵��˻�".equals(e.getMessage())) {
				Assert.fail("�û��Ѿ�����ȴû�б���");
			}
		}

	}

	@Test
	/**
	 * û�е��˻�id��ѯ�˻�
	 */
	public void findBlanceByAccountIdTestForUserIdNull() {
		int accountId = -100;
		try {
			accountServiceBO.findBlanceByAccountId(accountId);
		} catch (RuntimeException e) {
			if (!"������accountId��û�в�ѯ���˻�".equals(e.getMessage())) {
				Assert.fail("������accountId��û�г����˻�ȴû�б���");
			}
		}
	}

	/**
	 * �����û����ɹ�
	 */
	@Test
	public void findAvailableByAccountIdSuccess() {
		int userId = testUtil.createRandomUserId();
		String accountType = AccountType.CASH;
		Account account = accountServiceBO.createAccount(userId, accountType);
		double balance = accountServiceBO.findAvailableByAccountId(account
				.getAccountId());
		Assert.assertEquals((Double) 0.0, (Double) balance);
	}

	/**
	 * ��ѯ�˻���ˮ
	 */
	@Test
	public void findIOByAccountIdTest() {
		// �����˻�
		int userId = testUtil.createRandomUserId();
		String accountType = AccountType.CASH;
		Account account = accountServiceBO.createAccount(userId, accountType);
		// �´�����Э��
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		// ֧��
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		cpiDTOs.add(cpiDTO);
		// ֧��
		List<PaymentInstruction> instructions = payServiceBO
				.processGetInstructions(cpiDTOs);
		payServiceBO.process(instructions);

		// ȷ���տ�
		cpiDTO.setType(PaymentInstructionType.CONFIRM);
		payServiceBO.confirmGetMoney(protocol.getPaymentProtocolId());
		// ������ˮ
		List<AccountIoLog> list = accountServiceBO.findIOByAccountId(account
				.getAccountId());
		Assert.assertEquals(1, list.size());
	}

	@Test
	/**
	 *  ������֧��,����������
	 */
	public void findIncomeOrOutcomeTotalByAccountIdTest() {
		// �����˻�
		int userId = testUtil.createRandomUserId();
		String accountType = AccountType.CASH;
		Account account = accountServiceBO.createAccount(userId, accountType);
		// �´�����Э�� ���׽��Ĭ��20
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		// ֧�� ���׽��Ĭ��20
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		cpiDTOs.add(cpiDTO);
		// ֧��
		List<PaymentInstruction> instructions = payServiceBO
				.processGetInstructions(cpiDTOs);
		payServiceBO.process(instructions);

		// ȷ���տ�
		cpiDTO.setType(PaymentInstructionType.CONFIRM);
		payServiceBO.confirmGetMoney(protocol.getPaymentProtocolId());

		double outComeMoney = accountServiceBO
				.findIncomeOrOutcomeTotalByAccountId(account.getAccountId(),
						AccountIoLogType.OUTCOME);
		Assert.assertEquals((Double) 0.0, (Double) outComeMoney);

		double inComeMoney = accountServiceBO
				.findIncomeOrOutcomeTotalByAccountId(account.getAccountId(),
						AccountIoLogType.INCOME);
		Assert.assertEquals((Double) 20.0, (Double) inComeMoney);
	}

	@Test
	/**
	 * ͨ��userId��type�����˻���typeΪ����ֵʱ
	 */
	public void findAccountByUserIdAndTypeTestNoType() {
		// �����˻�
		int userId = testUtil.createRandomUserId();
		String accountType = AccountType.CASH;
		accountServiceBO.createAccount(userId, accountType);

		Account account = accountServiceBO.findAccountByUserIdAndType(userId,
				"�����type");
		if (account != null) {
			Assert.fail("û������ȴ�ҵ���account");
		}
	}

	@Test
	/**
	 *  �����˻�ͨ���û�id��type�ɹ�
	 */
	public void findAccountByUserIdAndTypeTestSuccess() {
		// �����˻�
		int userId = testUtil.createRandomUserId();
		String accountType = AccountType.CASH;
		accountServiceBO.createAccount(userId, accountType);

		Account account = accountServiceBO.findAccountByUserIdAndType(userId,
				accountType);
		if (account == null) {
			Assert.fail("���ҳɹ����ҵ�account");
		}
	}

	/**
	 * ����ͨ��userId�����˻�
	 */
	@Test
	public void findAccountsByUserIdTestSuccess() {
		int userId = testUtil.createRandomUserId();
		String accountType = AccountType.CASH;
		accountServiceBO.createAccount(userId, accountType);
		String accountType2 = AccountType.BANKPAY;
		accountServiceBO.createAccount(userId, accountType2);

		List<Account> listAccounts = accountServiceBO
				.findAccountsByUserId(userId);
		if (listAccounts.size() != 2) {
			Assert.fail("Ӧ���ҳ�����accountȴֻ���ҵ���һ���˻�");
		}
	}

	@Test
	/**
	 * ͨ���˻�id�����˻�
	 */
	public void findByAccountIdTest() {
		int userId = testUtil.createRandomUserId();
		String accountType = AccountType.CASH;
		Account account = accountServiceBO.createAccount(userId, accountType);
		Account account2 = accountServiceBO.findByAccountId(account
				.getAccountId());

		if (account2 == null) {
			Assert.fail("û��ͨ��accountId���ҳ�account");
		}
	}

	@Test
	/**
	 *  ���ӡ������˻����
	 */
	public void addAccountAmount() {
		int userId = testUtil.createRandomUserId();
		String accountType = AccountType.CASH;
		Account account = accountServiceBO.createAccount(userId, accountType);
		// �´�����Э�� ���׽��Ĭ��20
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// ����Э������Ϊת��
		cppDTO.setType(PaymentProtocolType.TRANSFER);
		PaymentProtocol protocol = transferAccountMoneyBO
				.createPaymentProtocol(cppDTO);
		// ����Ĭ�ϵ�֧��ָ�� ���׽��Ĭ��20
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		// ��������Ϊת��
		cpiDTO.setType(PaymentInstructionType.TRANSFER);
		PaymentInstruction instruction = transferAccountMoneyBO
				.createPaymentInstruction(cpiDTO);
		// ���ӡ��������
		accountServiceBO.addAccountAmount(instruction);
		accountServiceBO.reduceAccountAmount(instruction);

		// ���ҳ�����Ǯ���˻�
		Account accountReceiver = accountServiceBO.findByAccountId(account
				.getAccountId());
		Assert.assertEquals((Double) 20.0, accountReceiver.getBalance());
		Account accountPayer = accountServiceBO.findAccountByUserIdAndType(
				cppDTO.getPayerId(), AccountType.CASH);
		Assert.assertEquals((Double) 80.0, accountPayer.getBalance());

	}

}
