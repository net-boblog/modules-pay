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
	 * ��������Э��ɹ�
	 */
	public void createWithdrawProtocolTestSuccess() {
		// ����һ���µ��˻�
		int userId = testUtil.createRandomUserId();
		accountServiceBO.createAccount(userId, AccountType.CASH);
		// ��ֵ50
		PaymentProtocol withdrawProtocol = withdrawBo.createWithdrawProtocol(
				userId, 50.0);
		Assert.assertEquals((Double) 50.0, withdrawProtocol.getPayMoney());

	}

	/**
	 * ����
	 */
	@Test
	public void withdrawTestSuccess() {
		// ����һ���µ���100���˻������õ�userId
		int userId = testUtil.createOwnMoneyAccount();
		// ��������Э��
		PaymentProtocol withdrawProtocol = withdrawBo.createWithdrawProtocol(
				userId, 20.0);
		PaymentProtocol withdrawProtocol2 = withdrawBo.createWithdrawProtocol(
				userId, 20.0);

		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		// ����20ָ��
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(withdrawProtocol.getPaymentProtocolId());
		cpiDTO.setChannel(PaymentInstructionChannel.ALIPAY);
		cpiDTO.setType(PaymentInstructionType.WITHDRAW);
		cpiDTOs.add(cpiDTO);
		// ����20ָ��
		CreatePaymentInstructionDTO cpiDTO2 = testUtil
				.createInstructionDTO(withdrawProtocol.getPaymentProtocolId());
		cpiDTO2.setChannel(PaymentInstructionChannel.ALIPAY);
		cpiDTO2.setType(PaymentInstructionType.WITHDRAW);
		cpiDTOs.add(cpiDTO2);
		// ���ֲ���
		List<PaymentInstruction> instructions = withdrawBo
				.processGetInstructions(cpiDTOs);
		withdrawBo.process(instructions);

		// // ����20
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
	 * ȷ�����ֳɹ�
	 * 
	 * @param protocolId
	 */
	@Test
	public void confirmWithdrawTestSuccess() {
		// ����һ���µ��˻�
		// ����һ���µ���100���˻������õ�userId
		int userId = testUtil.createOwnMoneyAccount();
		// ��������Э��
		PaymentProtocol withdrawProtocol = withdrawBo.createWithdrawProtocol(
				userId, 20.0);
		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		// ����20ָ��
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(withdrawProtocol.getPaymentProtocolId());
		cpiDTO.setChannel(PaymentInstructionChannel.ALIPAY);
		cpiDTO.setType(PaymentInstructionType.WITHDRAW);
		cpiDTOs.add(cpiDTO);
		// ���ֲ���
		List<PaymentInstruction> instructions = withdrawBo
				.processGetInstructions(cpiDTOs);
		withdrawBo.process(instructions);

		// // ����20
		// withdrawBo.withdraw(withdrawProtocol.getPaymentProtocolId(),
		// PaymentInstructionChannel.ALIPAY);

		// һ����ҵ��˶Գɹ���ȷ�����ֳɹ�
		withdrawBo.matchRcdSuccess(withdrawProtocol.getPaymentProtocolId(),
				PaymentInstructionType.WITHDRAW);
		Account account = accountServiceBO.findAccountByUserIdAndType(userId,
				AccountType.CASH);
		Assert.assertEquals((Double) (80.0), account.getBalance());
		Assert.assertEquals((Double) (0.0), account.getFrozenMoney());
	}
}
