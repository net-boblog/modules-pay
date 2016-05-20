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
	 * ����ȫ�˿�
	 */
	public void refundMoneyTestSucessForNotAllRefund() {
		int userId = testUtil.createRandomUserId();
		// �����տ����˻�,������һ���н��100��֧���˻�
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// �´�����Э�� 20Ԫ
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		cpiDTOs.add(cpiDTO);
		// ֧��20
		List<PaymentInstruction> instructions = payServiceBO
				.processGetInstructions(cpiDTOs);
		payServiceBO.process(instructions);

		// �˿�5Ԫ
		List<PaymentInstruction> refundInstructions = refundDetailBO
				.getRefundInstructions(protocol.getPaymentProtocolId(), 5);
		refundDetailBO.process(refundInstructions);

		PaymentProtocol protocol2 = payServiceBO
				.findProtocolByProtocolId(protocol.getPaymentProtocolId());
		Assert.assertEquals(PaymentProtocolStatus.PAID, protocol2.getStatus());
	}

	@Test
	/**
	 * ��ȫ��
	 */
	public void refundMoneyTestSucessForAllRefund() {
		int userId = testUtil.createRandomUserId();
		// �����տ����˻�,������һ���н��100��֧���˻�
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// �´�����Э�� 20Ԫ
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		cpiDTOs.add(cpiDTO);
		// ֧��
		List<PaymentInstruction> instructions = payServiceBO
				.processGetInstructions(cpiDTOs);
		payServiceBO.process(instructions);
		// �˿�
		List<PaymentInstruction> refundInstructions = refundDetailBO
				.getRefundInstructions(protocol.getPaymentProtocolId(),
						protocol.getPayMoney());
		refundDetailBO.process(refundInstructions);

		PaymentProtocol protocol2 = payServiceBO
				.findProtocolByProtocolId(protocol.getPaymentProtocolId());
		Assert.assertEquals(PaymentProtocolStatus.CLOSE, protocol2.getStatus());
	}

	@Test
	// ��ѯ�˿���ϸ ͨ��״̬��Э��id
	public void findRefundDetailByProcolIdAndStatusTest() {
		int userId = testUtil.createRandomUserId();
		// �����տ����˻�,������һ���н��100��֧���˻�
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// �´�����Э�� 20Ԫ
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		cpiDTOs.add(cpiDTO);

		// ֧��
		List<PaymentInstruction> instructions = payServiceBO
				.processGetInstructions(cpiDTOs);
		payServiceBO.process(instructions);
		// �˿�
		List<PaymentInstruction> refundInstructions = refundDetailBO
				.getRefundInstructions(protocol.getPaymentProtocolId(), 5.0);
		refundDetailBO.process(refundInstructions);
		// �˿�
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
	// ��ѯ�˿���ϸ ͨ��Э��id
	public void findRefundDetailsByProtocolIdTest() {
		int userId = testUtil.createRandomUserId();
		// �����տ����˻�,������һ���н��100��֧���˻�
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// �´�����Э�� 20Ԫ
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		cpiDTOs.add(cpiDTO);
		// ֧��
		List<PaymentInstruction> instructions = payServiceBO
				.processGetInstructions(cpiDTOs);
		payServiceBO.process(instructions);
		// �˿�
		List<PaymentInstruction> refundInstructions = refundDetailBO
				.getRefundInstructions(protocol.getPaymentProtocolId(), 5.0);
		refundDetailBO.process(refundInstructions);
		// �˿�
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
	 * �����˿�ָ��ʧ�ܣ���Ϊ���Ͳ�Ϊ�˿�
	 */
	public void createPaymentInstructionTestFailForType() {
		int userId = testUtil.createRandomUserId();
		// �����տ����˻�,������һ���н��100��֧���˻�
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// �´�����Э�� 20Ԫ
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		// ����֧��cpiDTO
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());

		try {
			refundDetailBO.createPaymentInstruction(cpiDTO);
		} catch (RuntimeException e) {
			if (!"ָ�����Ͳ�Ϊ�˿�����˿���Ĵ���ָ���".equals(e.getMessage())) {
				Assert.fail("�����˿����Ͳ���ʹ���˿���Ĵ���ָ���");
			}
		}
	}

	@Test
	/**
	 * �����˿�ָ��,��֧���׶�
	 */
	public void createPaymentInstructionTestForPay() {
		int userId = testUtil.createRandomUserId();
		// �����տ����˻�,������һ���н��100��֧���˻�
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// �´�����Э�� 20Ԫ
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		// ����֧��cpiDTO
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		cpiDTOs.add(cpiDTO);
		// ֧��
		List<PaymentInstruction> instructions = payServiceBO
				.processGetInstructions(cpiDTOs);
		payServiceBO.process(instructions);
		// �˿�
		List<PaymentInstruction> refundInstructions = refundDetailBO
				.getRefundInstructions(protocol.getPaymentProtocolId(), 5.0);
		refundDetailBO.process(refundInstructions);

		// �˿����ǵ�����
		List<PaymentInstruction> listRefundinsInstructions = refundDetailBO
				.findInstructionsByProtocolIdAndType(
						protocol.getPaymentProtocolId(),
						PaymentInstructionType.REFUND);
		for (PaymentInstruction instruction : listRefundinsInstructions) {
			if (!SpecialAccount.guaranteeId.equals(instruction.getPayerId())) {
				Assert.fail("��֧���׶ε��˿���Ӧ���ǵ�����");
			}
		}
	}

	@Test
	/**
	 * �����˿�ָ��,����ȷ���տ��
	 */
	public void createPaymentInstructionTestForConfirm() {
		int userId = testUtil.createRandomUserId();
		// �����տ����˻�,������һ���н��100��֧���˻�
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// �´�����Э�� 20Ԫ
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		// ����֧��cpiDTO
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		cpiDTOs.add(cpiDTO);
		// ֧��
		List<PaymentInstruction> instructions = payServiceBO
				.processGetInstructions(cpiDTOs);
		payServiceBO.process(instructions);
		// ȷ���տ�
		payServiceBO.confirmGetMoney(protocol.getPaymentProtocolId());
		// �˿�
		List<PaymentInstruction> refundInstructions = refundDetailBO
				.getRefundInstructions(protocol.getPaymentProtocolId(), 5.0);
		refundDetailBO.process(refundInstructions);
		// �˿���������
		List<PaymentInstruction> listRefundinsInstructions = refundDetailBO
				.findInstructionsByProtocolIdAndType(
						protocol.getPaymentProtocolId(),
						PaymentInstructionType.REFUND);
		// ����
		Account accountPayer = accountServiceBO.findAccountByUserIdAndType(
				protocol.getReceiverId(), AccountType.CASH);

		for (PaymentInstruction refundInstruction : listRefundinsInstructions) {
			// ֧����
			if (!accountPayer.getAccountId().equals(
					refundInstruction.getPayerId())) {
				Assert.fail("�ڽ�����ɽ׶ε��˿���Ӧ��������");
			}
		}
	}
}
