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
	 * 创建账户传参userId为空
	 */
	@Test(expected = RuntimeException.class)
	public void createAccountTestForUserId() {
		int userId = (Integer) null;
		String accountType = AccountType.CASH;
		accountServiceBO.createAccount(userId, accountType);
	}

	@Test
	/**
	 * 创建账户成功
	 */
	public void createAccountTestSuccess() {
		int userId = testUtil.createRandomUserId();
		String accountType = AccountType.CASH;
		Account account = accountServiceBO.createAccount(userId, accountType);

		Account account2 = accountServiceBO.findByAccountId(account
				.getAccountId());
		if (account2 == null) {
			Assert.fail("创建账户失败");
		}
	}

	/**
	 * 重复的创建账户
	 */
	@Test
	public void createAccountTestForRepeat() {
		int userId = testUtil.createRandomUserId();
		String accountType = AccountType.CASH;
		// 创建一次账户
		accountServiceBO.createAccount(userId, accountType);

		try {
			// 再次创建账户
			accountServiceBO.createAccount(userId, accountType);
		} catch (RuntimeException e) {
			if (!"创建用户失败:该用户已经拥有该类型的账户".equals(e.getMessage())) {
				Assert.fail("用户已经创建却没有报错");
			}
		}

	}

	@Test
	/**
	 * 没有的账户id查询账户
	 */
	public void findBlanceByAccountIdTestForUserIdNull() {
		int accountId = -100;
		try {
			accountServiceBO.findBlanceByAccountId(accountId);
		} catch (RuntimeException e) {
			if (!"传来的accountId并没有查询到账户".equals(e.getMessage())) {
				Assert.fail("传来的accountId并没有吵到账户却没有报错");
			}
		}
	}

	/**
	 * 查找用户余额成功
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
	 * 查询账户流水
	 */
	@Test
	public void findIOByAccountIdTest() {
		// 创建账户
		int userId = testUtil.createRandomUserId();
		String accountType = AccountType.CASH;
		Account account = accountServiceBO.createAccount(userId, accountType);
		// 新创建的协议
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		// 支付
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		cpiDTOs.add(cpiDTO);
		// 支付
		List<PaymentInstruction> instructions = payServiceBO
				.processGetInstructions(cpiDTOs);
		payServiceBO.process(instructions);

		// 确定收款
		cpiDTO.setType(PaymentInstructionType.CONFIRM);
		payServiceBO.confirmGetMoney(protocol.getPaymentProtocolId());
		// 查找流水
		List<AccountIoLog> list = accountServiceBO.findIOByAccountId(account
				.getAccountId());
		Assert.assertEquals(1, list.size());
	}

	@Test
	/**
	 *  查找总支出,或者总收入
	 */
	public void findIncomeOrOutcomeTotalByAccountIdTest() {
		// 创建账户
		int userId = testUtil.createRandomUserId();
		String accountType = AccountType.CASH;
		Account account = accountServiceBO.createAccount(userId, accountType);
		// 新创建的协议 交易金额默认20
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		// 支付 交易金额默认20
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		cpiDTOs.add(cpiDTO);
		// 支付
		List<PaymentInstruction> instructions = payServiceBO
				.processGetInstructions(cpiDTOs);
		payServiceBO.process(instructions);

		// 确定收款
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
	 * 通过userId和type查找账户，type为错误值时
	 */
	public void findAccountByUserIdAndTypeTestNoType() {
		// 创建账户
		int userId = testUtil.createRandomUserId();
		String accountType = AccountType.CASH;
		accountServiceBO.createAccount(userId, accountType);

		Account account = accountServiceBO.findAccountByUserIdAndType(userId,
				"错误的type");
		if (account != null) {
			Assert.fail("没有类型却找到了account");
		}
	}

	@Test
	/**
	 *  查找账户通过用户id和type成功
	 */
	public void findAccountByUserIdAndTypeTestSuccess() {
		// 创建账户
		int userId = testUtil.createRandomUserId();
		String accountType = AccountType.CASH;
		accountServiceBO.createAccount(userId, accountType);

		Account account = accountServiceBO.findAccountByUserIdAndType(userId,
				accountType);
		if (account == null) {
			Assert.fail("查找成功查找到account");
		}
	}

	/**
	 * 查找通过userId查找账户
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
			Assert.fail("应查找出两个account却只查找到了一个账户");
		}
	}

	@Test
	/**
	 * 通过账户id查找账户
	 */
	public void findByAccountIdTest() {
		int userId = testUtil.createRandomUserId();
		String accountType = AccountType.CASH;
		Account account = accountServiceBO.createAccount(userId, accountType);
		Account account2 = accountServiceBO.findByAccountId(account
				.getAccountId());

		if (account2 == null) {
			Assert.fail("没有通过accountId查找出account");
		}
	}

	@Test
	/**
	 *  增加、减少账户余额
	 */
	public void addAccountAmount() {
		int userId = testUtil.createRandomUserId();
		String accountType = AccountType.CASH;
		Account account = accountServiceBO.createAccount(userId, accountType);
		// 新创建的协议 交易金额默认20
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// 设置协议类型为转账
		cppDTO.setType(PaymentProtocolType.TRANSFER);
		PaymentProtocol protocol = transferAccountMoneyBO
				.createPaymentProtocol(cppDTO);
		// 创建默认的支付指令 交易金额默认20
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		// 设置类型为转账
		cpiDTO.setType(PaymentInstructionType.TRANSFER);
		PaymentInstruction instruction = transferAccountMoneyBO
				.createPaymentInstruction(cpiDTO);
		// 增加、减少余额
		accountServiceBO.addAccountAmount(instruction);
		accountServiceBO.reduceAccountAmount(instruction);

		// 查找出增加钱的账户
		Account accountReceiver = accountServiceBO.findByAccountId(account
				.getAccountId());
		Assert.assertEquals((Double) 20.0, accountReceiver.getBalance());
		Account accountPayer = accountServiceBO.findAccountByUserIdAndType(
				cppDTO.getPayerId(), AccountType.CASH);
		Assert.assertEquals((Double) 80.0, accountPayer.getBalance());

	}

}
