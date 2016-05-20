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
	// * ��cpiDTO���󵱲���ת��,���ʹ���
	// */
	// @Test
	// public void transferAccountMoneyForCpiDTOFailByType() {
	// int userId = testUtil.createRandomUserId();
	// // �����տ����˻�,������һ���н��100��֧���˻�
	// accountServiceBO.createAccount(userId, AccountType.CASH);
	// CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
	// // ����Ϊת��
	// cppDTO.setType(PaymentProtocolType.TRANSFER);
	// // �´�����Э�� 20Ԫ
	// PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
	// CreatePaymentInstructionDTO cpiDTO = testUtil
	// .createInstructionDTO(protocol.getPaymentProtocolId());
	// List<CreatePaymentInstructionDTO> cpiDTOs = new
	// ArrayList<CreatePaymentInstructionDTO>();
	// cpiDTOs.add(cpiDTO);
	// try {
	// // ֧������
	// List<PaymentInstruction> instructions = transferAccountMoneyBO
	// .processGetInstructions(cpiDTOs);
	// transferAccountMoneyBO.process(instructions);
	// } catch (RuntimeException e) {
	// if (!"ת��ʧ�ܣ�֧��ָ������Ͳ�Ϊת��".equals(e.getMessage())) {
	// Assert.fail("���Ͳ�Ϊת��ȴ��Ȼִ����");
	// }
	// }
	// }

	// @Test
	// /**
	// * ����֧��ָ��ʧ�ܣ���Ϊ���ʹ�
	// */
	// public void createPaymentInstructionTestFailForType() {
	// int userId = testUtil.createRandomUserId();
	// // �����տ����˻�,������һ���н��100��֧���˻�
	// accountServiceBO.createAccount(userId, AccountType.CASH);
	// CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
	// // ����Ϊת��
	// cppDTO.setType(PaymentProtocolType.TRANSFER);
	// // �´�����Э�� 20Ԫ
	// PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
	// CreatePaymentInstructionDTO cpiDTO = testUtil
	// .createInstructionDTO(protocol.getPaymentProtocolId());
	// try {
	// transferAccountMoneyBO.createPaymentInstruction(cpiDTO);
	// } catch (RuntimeException e) {
	// if (!"ָ�����Ͳ�Ϊת�˲�����ת����Ĵ���ָ���".equals(e.getMessage())) {
	// Assert.fail("����ת�����Ͳ�����ת�����͵Ĵ���Э��");
	// }
	// }
	// }

	@Test
	/**
	 * ����ָ��ɹ�
	 */
	public void createPaymentInstructionTestSuccess() {
		int userId = testUtil.createRandomUserId();
		// �����տ����˻�,������һ���н��100��֧���˻�
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// ����Ϊת��
		cppDTO.setType(PaymentProtocolType.TRANSFER);
		// �´�����Э�� 20Ԫ
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		cpiDTO.setType(PaymentInstructionType.TRANSFER);
		transferAccountMoneyBO.createPaymentInstruction(cpiDTO);

	}

	/**
	 * ��cpiDTO���󵱲���ת��,���֧��
	 */
	@Test
	public void transferAccountMoneyForCpiDTOSuccess() {
		int userId = testUtil.createRandomUserId();
		// �����տ����˻�,������һ���н��100��֧���˻�
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// ����Ϊת��
		cppDTO.setType(PaymentProtocolType.TRANSFER);
		// �´�����Э�� 20Ԫ
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		cpiDTO.setType(PaymentInstructionType.TRANSFER);
		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		cpiDTOs.add(cpiDTO);
		// ִ��ת��
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
	 * ������֧��
	 */
	@Test
	public void transferForOtherChannelSuccess() {
		int userId = testUtil.createRandomUserId();
		// �����տ����˻�,������һ���н��100��֧���˻�
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// ����Ϊת��
		cppDTO.setType(PaymentProtocolType.TRANSFER);
		// �´�����Э�� 20Ԫ
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		cpiDTO.setType(PaymentInstructionType.TRANSFER);
		// ���õ�����֧��;��
		cpiDTO.setChannel(PaymentInstructionChannel.ALIPAY);

		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		cpiDTOs.add(cpiDTO);
		// ת��
		List<PaymentInstruction> instructions = transferAccountMoneyBO
				.processGetInstructions(cpiDTOs);
		transferAccountMoneyBO.process(instructions);
		// �˶�һ����ҵ����ˮ��
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
	 * ���͵�����һ��֧��
	 */
	@Test
	public void transferForBlanceAndOtherChannelSuccess() {
		int userId = testUtil.createRandomUserId();
		// �����տ����˻�,������һ���н��100��֧���˻�
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// ����Ϊת��
		cppDTO.setType(PaymentProtocolType.TRANSFER);
		// �´�����Э�� 20Ԫ
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		cpiDTO.setType(PaymentInstructionType.TRANSFER);
		// ���õ�����֧��;��
		cpiDTO.setChannel(PaymentInstructionChannel.ALIPAY);
		cpiDTO.setPayMoney(10.0);

		// ���õ����֧��;��
		CreatePaymentInstructionDTO cpiDTO2 = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		cpiDTO2.setType(PaymentInstructionType.TRANSFER);
		cpiDTO2.setChannel(PaymentInstructionChannel.BALANCE);
		cpiDTO2.setPayMoney(10.0);

		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		cpiDTOs.add(cpiDTO);
		cpiDTOs.add(cpiDTO2);
		// ת��
		List<PaymentInstruction> instructions = transferAccountMoneyBO
				.processGetInstructions(cpiDTOs);
		transferAccountMoneyBO.process(instructions);
		// �˶�һ����ҵ����ˮ��
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
