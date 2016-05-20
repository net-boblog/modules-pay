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
import com.xabaohui.modules.pay.bean.PaymentInstruction;
import com.xabaohui.modules.pay.bean.PaymentProtocol;
import com.xabaohui.modules.pay.bean.channel.PaymentInstructionChannel;
import com.xabaohui.modules.pay.bean.type.AccountType;
import com.xabaohui.modules.pay.bean.type.PaymentInstructionType;
import com.xabaohui.modules.pay.dto.CreatePaymentInstructionDTO;

/**
 * @author YRee
 * 
 */

@TransactionConfiguration(transactionManager = "transactionManagerH", defaultRollback = false)
// @Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:payment-servlet.xml")
public class WithdrawBOTest extends
		AbstractTransactionalJUnit4SpringContextTests {
	protected static Logger logger = LoggerFactory
			.getLogger(WithdrawBOTest.class);
	@Resource
	private PayServiceBO payServiceBO;
	@Resource
	private AccountServiceBO accountServiceBO;
	@Resource
	private TestUtil testUtil;
	@Resource
	private WithdrawBo withdrawBo;

	@Test
	/**
	 * 创建提现协议成功
	 */
	public void createWithdrawProtocolTestSuccess() {
		// 创建一个新的账户
		int userId = testUtil.createRandomUserId();
		accountServiceBO.createAccount(userId, AccountType.CASH);
		// 充值50
		PaymentProtocol withdrawProtocol = withdrawBo.createWithdrawProtocol(
				userId, 50.0);
		Assert.assertEquals((Double) 50.0, withdrawProtocol.getPayMoney());

	}

	/**
	 * 提现
	 */
	@Test
	public void withdrawTestSuccess() {
		// 创建一个新的有100的账户，并拿到userId
		int userId = testUtil.createOwnMoneyAccount();
		// 生成提现协议
		PaymentProtocol withdrawProtocol = withdrawBo.createWithdrawProtocol(
				userId, 20.0);
		PaymentProtocol withdrawProtocol2 = withdrawBo.createWithdrawProtocol(
				userId, 20.0);

		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		// 提现20指令
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(withdrawProtocol.getPaymentProtocolId());
		cpiDTO.setChannel(PaymentInstructionChannel.ALIPAY);
		cpiDTO.setType(PaymentInstructionType.WITHDRAW);
		cpiDTOs.add(cpiDTO);
		// 提现20指令
		CreatePaymentInstructionDTO cpiDTO2 = testUtil
				.createInstructionDTO(withdrawProtocol.getPaymentProtocolId());
		cpiDTO2.setChannel(PaymentInstructionChannel.ALIPAY);
		cpiDTO2.setType(PaymentInstructionType.WITHDRAW);
		cpiDTOs.add(cpiDTO2);
		// 提现操作
		List<PaymentInstruction> instructions = withdrawBo
				.processGetInstructions(cpiDTOs);
		withdrawBo.process(instructions);

		// // 提现20
		// withdrawBo.withdraw(withdrawProtocol.getPaymentProtocolId(),
		// PaymentInstructionChannel.ALIPAY);
		// withdrawBo.withdraw(withdrawProtocol2.getPaymentProtocolId(),
		// PaymentInstructionChannel.ALIPAY);
		Account account = accountServiceBO.findAccountByUserIdAndType(userId,
				AccountType.CASH);
		Assert.assertEquals((Double) (100.0), account.getBalance());
		Assert.assertEquals((Double) (20.0 + 20.0), account.getFrozenMoney());
	}

	/**
	 * 确定提现成功
	 * 
	 * @param protocolId
	 */
	@Test
	public void confirmWithdrawTestSuccess() {
		// 创建一个新的账户
		// 创建一个新的有100的账户，并拿到userId
		int userId = testUtil.createOwnMoneyAccount();
		// 生成提现协议
		PaymentProtocol withdrawProtocol = withdrawBo.createWithdrawProtocol(
				userId, 20.0);
		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		// 提现20指令
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(withdrawProtocol.getPaymentProtocolId());
		cpiDTO.setChannel(PaymentInstructionChannel.ALIPAY);
		cpiDTO.setType(PaymentInstructionType.WITHDRAW);
		cpiDTOs.add(cpiDTO);
		// 提现操作
		List<PaymentInstruction> instructions = withdrawBo
				.processGetInstructions(cpiDTOs);
		withdrawBo.process(instructions);

		// // 提现20
		// withdrawBo.withdraw(withdrawProtocol.getPaymentProtocolId(),
		// PaymentInstructionChannel.ALIPAY);

		// 一三方业务核对成功，确定提现成功
		withdrawBo.matchRcdSuccess(withdrawProtocol.getPaymentProtocolId(),
				PaymentInstructionType.WITHDRAW);
		Account account = accountServiceBO.findAccountByUserIdAndType(userId,
				AccountType.CASH);
		Assert.assertEquals((Double) (80.0), account.getBalance());
		Assert.assertEquals((Double) (0.0), account.getFrozenMoney());
	}
}
