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
import com.xabaohui.modules.pay.bean.status.PaymentProtocolStatus;
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
public class RechargeBOTest extends
		AbstractTransactionalJUnit4SpringContextTests {
	protected static Logger logger = LoggerFactory
			.getLogger(RechargeBOTest.class);
	@Resource
	private PayServiceBO payServiceBO;
	@Resource
	private AccountServiceBO accountServiceBO;
	@Resource
	private TestUtil testUtil;
	@Resource
	private RechargeBo rechargeBo;

	@Test
	/**
	 * 创建充值协议成功
	 */
	public void createRechargeProtocolTestSuccess() {
		// 创建一个新的账户
		int userId = testUtil.createRandomUserId();
		accountServiceBO.createAccount(userId, AccountType.CASH);
		// 充值50
		PaymentProtocol rechargeProtocol = rechargeBo.createRechargeProtocol(
				userId, 50.0);
		Assert.assertEquals((Double) 50.0, rechargeProtocol.getPayMoney());

	}

	/**
	 * 充值
	 */
	@Test
	public void rechargeTestSuccess() {
		// 创建一个新的账户
		int userId = testUtil.createRandomUserId();
		accountServiceBO.createAccount(userId, AccountType.CASH);
		// 充值50
		PaymentProtocol rechargeProtocol = rechargeBo.createRechargeProtocol(
				userId, 50.0);

		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(rechargeProtocol.getPaymentProtocolId());
		cpiDTO.setChannel(PaymentInstructionChannel.ALIPAY);
		cpiDTO.setType(PaymentInstructionType.RECHARGE);
		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		cpiDTOs.add(cpiDTO);
		// 充值
		List<PaymentInstruction> instructions = rechargeBo
				.processGetInstructions(cpiDTOs);
		rechargeBo.process(instructions);

		Account account = accountServiceBO.findAccountByUserIdAndType(userId,
				AccountType.CASH);
		Assert.assertEquals((Double) (0.0), account.getBalance());
		Assert.assertEquals((Double) (0.0), account.getFrozenMoney());
		// 比较协议状态
		PaymentProtocol protocol = rechargeBo
				.findProtocolByProtocolId(rechargeProtocol
						.getPaymentProtocolId());
		Assert.assertEquals(PaymentProtocolStatus.PROCESSING,
				protocol.getStatus());
	}

	/**
	 * 确定充值成功
	 * 
	 * @param protocolId
	 */
	@Test
	public void confirmRechargeTestSuccess() {
		// 创建一个新的账户
		int userId = testUtil.createRandomUserId();
		accountServiceBO.createAccount(userId, AccountType.CASH);
		// 充值50
		PaymentProtocol rechargeProtocol = rechargeBo.createRechargeProtocol(
				userId, 20.0);
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(rechargeProtocol.getPaymentProtocolId());
		cpiDTO.setChannel(PaymentInstructionChannel.ALIPAY);
		cpiDTO.setType(PaymentInstructionType.RECHARGE);
		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		cpiDTOs.add(cpiDTO);
		// 充值
		List<PaymentInstruction> instructions = rechargeBo
				.processGetInstructions(cpiDTOs);
		rechargeBo.process(instructions); // 一三方核对成功后确定充值成功

		rechargeBo.matchRcdSuccess(rechargeProtocol.getPaymentProtocolId(),
				PaymentInstructionType.RECHARGE);

		Account account = accountServiceBO.findAccountByUserIdAndType(userId,
				AccountType.CASH);
		Assert.assertEquals((Double) (20.0), account.getBalance());
		Assert.assertEquals((Double) (0.0), account.getFrozenMoney());
	}

}
