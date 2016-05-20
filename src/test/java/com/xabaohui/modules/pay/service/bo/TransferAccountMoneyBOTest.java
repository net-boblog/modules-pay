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
public class TransferAccountMoneyBOTest extends
		AbstractTransactionalJUnit4SpringContextTests {
	protected static Logger logger = LoggerFactory
			.getLogger(TransferAccountMoneyBOTest.class);
	@Resource
	private PayServiceBO payServiceBO;
	@Resource
	private AccountServiceBO accountServiceBO;
	@Resource
	private TransferAccountMoneyBO transferAccountMoneyBO;
	@Resource
	private TestUtil testUtil;

	// /**
	// * 用cpiDTO对象当参数转账,类型错误
	// */
	// @Test
	// public void transferAccountMoneyForCpiDTOFailByType() {
	// int userId = testUtil.createRandomUserId();
	// // 创建收款人账户,并创建一个有金额100的支付账户
	// accountServiceBO.createAccount(userId, AccountType.CASH);
	// CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
	// // 设置为转账
	// cppDTO.setType(PaymentProtocolType.TRANSFER);
	// // 新创建的协议 20元
	// PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
	// CreatePaymentInstructionDTO cpiDTO = testUtil
	// .createInstructionDTO(protocol.getPaymentProtocolId());
	// List<CreatePaymentInstructionDTO> cpiDTOs = new
	// ArrayList<CreatePaymentInstructionDTO>();
	// cpiDTOs.add(cpiDTO);
	// try {
	// // 支付类型
	// List<PaymentInstruction> instructions = transferAccountMoneyBO
	// .processGetInstructions(cpiDTOs);
	// transferAccountMoneyBO.process(instructions);
	// } catch (RuntimeException e) {
	// if (!"转账失败：支付指令的类型不为转账".equals(e.getMessage())) {
	// Assert.fail("类型不为转账却任然执行了");
	// }
	// }
	// }

	// @Test
	// /**
	// * 创建支付指令失败，因为类型错
	// */
	// public void createPaymentInstructionTestFailForType() {
	// int userId = testUtil.createRandomUserId();
	// // 创建收款人账户,并创建一个有金额100的支付账户
	// accountServiceBO.createAccount(userId, AccountType.CASH);
	// CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
	// // 设置为转账
	// cppDTO.setType(PaymentProtocolType.TRANSFER);
	// // 新创建的协议 20元
	// PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
	// CreatePaymentInstructionDTO cpiDTO = testUtil
	// .createInstructionDTO(protocol.getPaymentProtocolId());
	// try {
	// transferAccountMoneyBO.createPaymentInstruction(cpiDTO);
	// } catch (RuntimeException e) {
	// if (!"指令类型不为转账不能用转账里的创建指令方法".equals(e.getMessage())) {
	// Assert.fail("不是转账类型不能用转账类型的创建协议");
	// }
	// }
	// }

	@Test
	/**
	 * 创建指令成功
	 */
	public void createPaymentInstructionTestSuccess() {
		int userId = testUtil.createRandomUserId();
		// 创建收款人账户,并创建一个有金额100的支付账户
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// 设置为转账
		cppDTO.setType(PaymentProtocolType.TRANSFER);
		// 新创建的协议 20元
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		cpiDTO.setType(PaymentInstructionType.TRANSFER);
		transferAccountMoneyBO.createPaymentInstruction(cpiDTO);

	}

	/**
	 * 用cpiDTO对象当参数转账,余额支付
	 */
	@Test
	public void transferAccountMoneyForCpiDTOSuccess() {
		int userId = testUtil.createRandomUserId();
		// 创建收款人账户,并创建一个有金额100的支付账户
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// 设置为转账
		cppDTO.setType(PaymentProtocolType.TRANSFER);
		// 新创建的协议 20元
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		cpiDTO.setType(PaymentInstructionType.TRANSFER);
		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		cpiDTOs.add(cpiDTO);
		// 执行转账
		List<PaymentInstruction> instructions = transferAccountMoneyBO
				.processGetInstructions(cpiDTOs);
		transferAccountMoneyBO.process(instructions);

		Account accountPayer = accountServiceBO.findAccountByUserIdAndType(
				cppDTO.getPayerId(), AccountType.CASH);
		Account accountRecevier = accountServiceBO.findAccountByUserIdAndType(
				userId, AccountType.CASH);
		Assert.assertEquals((Double) 80.0, accountPayer.getBalance());
		Assert.assertEquals((Double) 20.0, accountRecevier.getBalance());
	}

	/**
	 * 第三方支付
	 */
	@Test
	public void transferForOtherChannelSuccess() {
		int userId = testUtil.createRandomUserId();
		// 创建收款人账户,并创建一个有金额100的支付账户
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// 设置为转账
		cppDTO.setType(PaymentProtocolType.TRANSFER);
		// 新创建的协议 20元
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		cpiDTO.setType(PaymentInstructionType.TRANSFER);
		// 设置第三方支付途径
		cpiDTO.setChannel(PaymentInstructionChannel.ALIPAY);

		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		cpiDTOs.add(cpiDTO);
		// 转账
		List<PaymentInstruction> instructions = transferAccountMoneyBO
				.processGetInstructions(cpiDTOs);
		transferAccountMoneyBO.process(instructions);
		// 核对一三方业务流水后
		transferAccountMoneyBO.matchRcdSuccess(protocol.getPaymentProtocolId(),
				PaymentInstructionType.TRANSFER);

		Account accountPayer = accountServiceBO.findAccountByUserIdAndType(
				cppDTO.getPayerId(), AccountType.CASH);
		Account accountRecevier = accountServiceBO.findAccountByUserIdAndType(
				userId, AccountType.CASH);
		Assert.assertEquals((Double) 100.0, accountPayer.getBalance());
		Assert.assertEquals((Double) 20.0, accountRecevier.getBalance());
	}

	/**
	 * 余额和第三方一起支付
	 */
	@Test
	public void transferForBlanceAndOtherChannelSuccess() {
		int userId = testUtil.createRandomUserId();
		// 创建收款人账户,并创建一个有金额100的支付账户
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// 设置为转账
		cppDTO.setType(PaymentProtocolType.TRANSFER);
		// 新创建的协议 20元
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		cpiDTO.setType(PaymentInstructionType.TRANSFER);
		// 设置第三方支付途径
		cpiDTO.setChannel(PaymentInstructionChannel.ALIPAY);
		cpiDTO.setPayMoney(10.0);

		// 设置第余额支付途径
		CreatePaymentInstructionDTO cpiDTO2 = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		cpiDTO2.setType(PaymentInstructionType.TRANSFER);
		cpiDTO2.setChannel(PaymentInstructionChannel.BALANCE);
		cpiDTO2.setPayMoney(10.0);

		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		cpiDTOs.add(cpiDTO);
		cpiDTOs.add(cpiDTO2);
		// 转账
		List<PaymentInstruction> instructions = transferAccountMoneyBO
				.processGetInstructions(cpiDTOs);
		transferAccountMoneyBO.process(instructions);
		// 核对一三方业务流水后
		transferAccountMoneyBO.matchRcdSuccess(protocol.getPaymentProtocolId(),
				PaymentInstructionType.TRANSFER);

		Account accountPayer = accountServiceBO.findAccountByUserIdAndType(
				cppDTO.getPayerId(), AccountType.CASH);
		Account accountRecevier = accountServiceBO.findAccountByUserIdAndType(
				userId, AccountType.CASH);
		Assert.assertEquals((Double) 90.0, accountPayer.getBalance());
		Assert.assertEquals((Double) 20.0, accountRecevier.getBalance());
	}

}
