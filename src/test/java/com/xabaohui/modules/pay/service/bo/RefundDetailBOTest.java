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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.xabaohui.modules.pay.bean.Account;
import com.xabaohui.modules.pay.bean.PaymentInstruction;
import com.xabaohui.modules.pay.bean.PaymentProtocol;
import com.xabaohui.modules.pay.bean.RefundDetail;
import com.xabaohui.modules.pay.bean.SpecialAccount;
import com.xabaohui.modules.pay.bean.status.PaymentProtocolStatus;
import com.xabaohui.modules.pay.bean.status.RefundDetailStatus;
import com.xabaohui.modules.pay.bean.type.AccountType;
import com.xabaohui.modules.pay.bean.type.PaymentInstructionType;
import com.xabaohui.modules.pay.dto.CreatePaymentInstructionDTO;
import com.xabaohui.modules.pay.dto.CreatePaymentProtocolDTO;
import com.xabaohui.modules.pay.service.PayService;

/**
 * @author YRee
 * 
 */
@TransactionConfiguration(transactionManager = "transactionManagerH", defaultRollback = false)
// @Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:payment-servlet.xml")
public class RefundDetailBOTest extends
		AbstractTransactionalJUnit4SpringContextTests {
	@Resource
	private PayServiceBO payServiceBO;
	@Resource
	private AccountServiceBO accountServiceBO;
	@Resource
	private PayService payService;
	@Resource
	private RefundDetailBO refundDetailBO;
	@Resource
	private TestUtil testUtil;

	@Test
	/**
	 * 不是全退款
	 */
	public void refundMoneyTestSucessForNotAllRefund() {
		int userId = testUtil.createRandomUserId();
		// 创建收款人账户,并创建一个有金额100的支付账户
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// 新创建的协议 20元
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		cpiDTOs.add(cpiDTO);
		// 支付20
		List<PaymentInstruction> instructions = payServiceBO
				.processGetInstructions(cpiDTOs);
		payServiceBO.process(instructions);

		// 退款5元
		List<PaymentInstruction> refundInstructions = refundDetailBO
				.getRefundInstructions(protocol.getPaymentProtocolId(), 5);
		refundDetailBO.process(refundInstructions);

		PaymentProtocol protocol2 = payServiceBO
				.findProtocolByProtocolId(protocol.getPaymentProtocolId());
		Assert.assertEquals(PaymentProtocolStatus.PAID, protocol2.getStatus());
	}

	@Test
	/**
	 * 退全款
	 */
	public void refundMoneyTestSucessForAllRefund() {
		int userId = testUtil.createRandomUserId();
		// 创建收款人账户,并创建一个有金额100的支付账户
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// 新创建的协议 20元
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		cpiDTOs.add(cpiDTO);
		// 支付
		List<PaymentInstruction> instructions = payServiceBO
				.processGetInstructions(cpiDTOs);
		payServiceBO.process(instructions);
		// 退款
		List<PaymentInstruction> refundInstructions = refundDetailBO
				.getRefundInstructions(protocol.getPaymentProtocolId(),
						protocol.getPayMoney());
		refundDetailBO.process(refundInstructions);

		PaymentProtocol protocol2 = payServiceBO
				.findProtocolByProtocolId(protocol.getPaymentProtocolId());
		Assert.assertEquals(PaymentProtocolStatus.CLOSE, protocol2.getStatus());
	}

	@Test
	// 查询退款明细 通过状态和协议id
	public void findRefundDetailByProcolIdAndStatusTest() {
		int userId = testUtil.createRandomUserId();
		// 创建收款人账户,并创建一个有金额100的支付账户
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// 新创建的协议 20元
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		cpiDTOs.add(cpiDTO);

		// 支付
		List<PaymentInstruction> instructions = payServiceBO
				.processGetInstructions(cpiDTOs);
		payServiceBO.process(instructions);
		// 退款
		List<PaymentInstruction> refundInstructions = refundDetailBO
				.getRefundInstructions(protocol.getPaymentProtocolId(), 5.0);
		refundDetailBO.process(refundInstructions);
		// 退款
		List<PaymentInstruction> refundInstructions2 = refundDetailBO
				.getRefundInstructions(protocol.getPaymentProtocolId(), 5.0);
		refundDetailBO.process(refundInstructions2);

		List<RefundDetail> list = refundDetailBO
				.findRefundDetailByProcolIdAndStatus(
						protocol.getPaymentProtocolId(),
						RefundDetailStatus.SUCCESS);

		Assert.assertEquals(2, list.size());
	}

	@Test
	// 查询退款明细 通过协议id
	public void findRefundDetailsByProtocolIdTest() {
		int userId = testUtil.createRandomUserId();
		// 创建收款人账户,并创建一个有金额100的支付账户
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// 新创建的协议 20元
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		cpiDTOs.add(cpiDTO);
		// 支付
		List<PaymentInstruction> instructions = payServiceBO
				.processGetInstructions(cpiDTOs);
		payServiceBO.process(instructions);
		// 退款
		List<PaymentInstruction> refundInstructions = refundDetailBO
				.getRefundInstructions(protocol.getPaymentProtocolId(), 5.0);
		refundDetailBO.process(refundInstructions);
		// 退款
		List<PaymentInstruction> refundInstructions2 = refundDetailBO
				.getRefundInstructions(protocol.getPaymentProtocolId(), 5.0);
		refundDetailBO.process(refundInstructions2);

		List<RefundDetail> list = refundDetailBO
				.findRefundDetailByProcolIdAndStatus(
						protocol.getPaymentProtocolId(),
						RefundDetailStatus.SUCCESS);

		Assert.assertEquals(2, list.size());
	}

	@Test
	/**
	 * 创建退款指令失败，因为类型不为退款
	 */
	public void createPaymentInstructionTestFailForType() {
		int userId = testUtil.createRandomUserId();
		// 创建收款人账户,并创建一个有金额100的支付账户
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// 新创建的协议 20元
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		// 创建支付cpiDTO
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());

		try {
			refundDetailBO.createPaymentInstruction(cpiDTO);
		} catch (RuntimeException e) {
			if (!"指令类型不为退款不能用退款里的创建指令方法".equals(e.getMessage())) {
				Assert.fail("不是退款类型不能使用退款里的创建指令方法");
			}
		}
	}

	@Test
	/**
	 * 创建退款指令,在支付阶段
	 */
	public void createPaymentInstructionTestForPay() {
		int userId = testUtil.createRandomUserId();
		// 创建收款人账户,并创建一个有金额100的支付账户
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// 新创建的协议 20元
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		// 创建支付cpiDTO
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		cpiDTOs.add(cpiDTO);
		// 支付
		List<PaymentInstruction> instructions = payServiceBO
				.processGetInstructions(cpiDTOs);
		payServiceBO.process(instructions);
		// 退款
		List<PaymentInstruction> refundInstructions = refundDetailBO
				.getRefundInstructions(protocol.getPaymentProtocolId(), 5.0);
		refundDetailBO.process(refundInstructions);

		// 退款人是担保户
		List<PaymentInstruction> listRefundinsInstructions = refundDetailBO
				.findInstructionsByProtocolIdAndType(
						protocol.getPaymentProtocolId(),
						PaymentInstructionType.REFUND);
		for (PaymentInstruction instruction : listRefundinsInstructions) {
			if (!SpecialAccount.guaranteeId.equals(instruction.getPayerId())) {
				Assert.fail("在支付阶段的退款人应该是担保户");
			}
		}
	}

	@Test
	/**
	 * 创建退款指令,在已确定收款后
	 */
	public void createPaymentInstructionTestForConfirm() {
		int userId = testUtil.createRandomUserId();
		// 创建收款人账户,并创建一个有金额100的支付账户
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// 新创建的协议 20元
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		// 创建支付cpiDTO
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		cpiDTOs.add(cpiDTO);
		// 支付
		List<PaymentInstruction> instructions = payServiceBO
				.processGetInstructions(cpiDTOs);
		payServiceBO.process(instructions);
		// 确定收款
		payServiceBO.confirmGetMoney(protocol.getPaymentProtocolId());
		// 退款
		List<PaymentInstruction> refundInstructions = refundDetailBO
				.getRefundInstructions(protocol.getPaymentProtocolId(), 5.0);
		refundDetailBO.process(refundInstructions);
		// 退款人是卖家
		List<PaymentInstruction> listRefundinsInstructions = refundDetailBO
				.findInstructionsByProtocolIdAndType(
						protocol.getPaymentProtocolId(),
						PaymentInstructionType.REFUND);
		// 卖家
		Account accountPayer = accountServiceBO.findAccountByUserIdAndType(
				protocol.getReceiverId(), AccountType.CASH);

		for (PaymentInstruction refundInstruction : listRefundinsInstructions) {
			// 支付人
			if (!accountPayer.getAccountId().equals(
					refundInstruction.getPayerId())) {
				Assert.fail("在交易完成阶段的退款人应该是卖家");
			}
		}
	}
}
