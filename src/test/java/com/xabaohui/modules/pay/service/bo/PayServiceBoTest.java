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
import com.xabaohui.modules.pay.bean.SpecialAccount;
import com.xabaohui.modules.pay.bean.channel.PaymentInstructionChannel;
import com.xabaohui.modules.pay.bean.status.PaymentInstructionStatus;
import com.xabaohui.modules.pay.bean.status.PaymentProtocolStatus;
import com.xabaohui.modules.pay.bean.type.AccountType;
import com.xabaohui.modules.pay.bean.type.PaymentInstructionType;
import com.xabaohui.modules.pay.bean.type.PaymentProtocolType;
import com.xabaohui.modules.pay.dto.CreatePaymentInstructionDTO;
import com.xabaohui.modules.pay.dto.CreatePaymentProtocolDTO;
import com.xabaohui.modules.pay.service.PayService;

@TransactionConfiguration(transactionManager = "transactionManagerH", defaultRollback = false)
// @Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:payment-servlet.xml")
public class PayServiceBoTest extends
		AbstractTransactionalJUnit4SpringContextTests {
	protected static Logger logger = LoggerFactory
			.getLogger(PayServiceBoTest.class);
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

	/**
	 * ����Э�鶩��id�ظ�
	 */
	@Test(expected = RuntimeException.class)
	public void createPaymentProtocolForOrderIdException() {
		// ׼������
		int userId = testUtil.createRandomUserId();
		// �����տ����˻�
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		payServiceBO.createPaymentProtocol(cppDTO);

		// ִ�в���
		// ͬһ��cppDTO���Զ���id�ظ�
		CreatePaymentProtocolDTO cppDTO2 = cppDTO;
		payServiceBO.createPaymentProtocol(cppDTO2);

	}

	@Test
	// ͨ��Э��id��ѯЭ��
	public void findProtocolByProtocolIdTest() {
		// ׼������
		int userId = testUtil.createRandomUserId();
		// �����տ����˻�
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);

		PaymentProtocol protocol2 = payServiceBO
				.findProtocolByProtocolId(protocol.getPaymentProtocolId());
		Assert.assertEquals(protocol.getOrderId(), protocol2.getOrderId());
		if (!protocol.getOrderId().equals(protocol2.getOrderId())
				&& !protocol.getReceiverId().equals(protocol2.getReceiverId())
				&& !protocol.getPayerId().equals(protocol2.getPayerId())
				&& !protocol.getPayMoney().equals(protocol2.getPayMoney())) {
			Assert.fail("Э����ҳ��Ĳ���ȷ");
		}
	}

	@Test
	// ����֧��Э��ɹ�
	public void createPaymentProtocolSucess() {
		// ׼������
		int userId = testUtil.createRandomUserId();
		// �����տ����˻�
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// �´�����Э��
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		// �õ��մ�����Э��
		PaymentProtocol protocol2 = payServiceBO
				.findProtocolByProtocolId(protocol.getPaymentProtocolId());
		Assert.assertNotNull(protocol2);
		Assert.assertEquals(PaymentProtocolStatus.INIT, protocol2.getStatus());
	}

	@Test
	// ����֧��ָ��������쳣����
	public void createPaymentInstructionTestForException() {
		// ׼������
		int userId = testUtil.createRandomUserId();
		// �����տ����˻�
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// �´�����Э��
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		// ָ������Ϊת��
		cpiDTO.setType(PaymentInstructionType.TRANSFER);
		try {
			payServiceBO.createPaymentInstruction(cpiDTO);
		} catch (RuntimeException e) {
			if (!"ָ�����Ͳ�Ϊ֧������ȷ�ϸ������֧����Ĵ���ָ���".equals(e.getMessage())) {
				Assert.fail("ָ�����Ͳ���ȷȴû�б���");
			}
		}
	}

	@Test
	// ����֧��ָ��
	public void createPaymentInstructionTestForPay() {
		int userId = testUtil.createRandomUserId();
		// �����տ����˻�
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// �´�����Э��
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		// Ĭ��ָ������Ϊpay
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		PaymentInstruction instruction = payServiceBO
				.createPaymentInstruction(cpiDTO);
		// ֧��ָ����տ����ǵ�����
		Assert.assertEquals(SpecialAccount.guaranteeId,
				instruction.getReceiverId());
	}

	@Test
	// ����ȷ���տ�ָ��
	public void createPaymentInstructionTestForConfirm() {
		int userId = testUtil.createRandomUserId();
		// �����տ����˻�
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// �´�����Э��
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		cpiDTO.setType(PaymentInstructionType.CONFIRM);
		PaymentInstruction instruction = payServiceBO
				.createPaymentInstruction(cpiDTO);
		// ȷ��ָ���֧�����ǵ�����
		Assert.assertEquals(SpecialAccount.guaranteeId,
				instruction.getPayerId());
	}

	@Test
	// ��֤�ѹرյ�֧������ת��Э��
	public void validatePayOrTransferTestForClose() {
		int userId = testUtil.createRandomUserId();
		// �����տ����˻�
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// �´�����Э��
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		payServiceBO.setProtocolStatus(protocol.getPaymentProtocolId(),
				PaymentProtocolStatus.CLOSE);
		// ����ָ��
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		try {
			payServiceBO.validateCpiDTO(cpiDTO);
		} catch (RuntimeException e) {
			if (!"û�и�Э�����Э���Ѿ��ر�".equals(e.getMessage())) {
				Assert.fail("Э��ر���ȴδ��֤����");
			}
		}
	}

	@Test
	// ��֤��ʼ״̬����INIT��֧������ת��Э��
	public void validatePayOrTransferTestForNotInit() {
		int userId = testUtil.createRandomUserId();
		// �����տ����˻�
		accountServiceBO.createAccount(userId, AccountType.CASH);

		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// �´�����Э��
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		payServiceBO.setProtocolStatus(protocol.getPaymentProtocolId(),
				PaymentProtocolStatus.PAID);
		// ����ָ��
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		try {
			payServiceBO.validateCpiDTO(cpiDTO);
		} catch (RuntimeException e) {
			if (!"Э���״̬����INIT������".equals(e.getMessage())) {
				Assert.fail("Э��ر���ȴδ��֤����");
			}
		}
	}

	@Test
	// ��֤֧������ת��Э��ɹ�
	public void validatePayOrTransferTestSuccess() {
		int userId = testUtil.createRandomUserId();
		// �����տ����˻�
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// �´�����Э��
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		// ����ָ��
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		PaymentProtocol protocol2 = payServiceBO.validateCpiDTO(cpiDTO);
		if (protocol2 == null) {
			Assert.fail("��֤��û��ȡ�����ص�Э��");
		}
	}

	@Test
	// ����֧��ָ�����֤��֧��Э���Ѿ��ر�
	public void validateCreatePaymentInstructionTestClose() {
		int userId = testUtil.createRandomUserId();
		// �����տ����˻�
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// �´�����Э��
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		payServiceBO.setProtocolStatus(protocol.getPaymentProtocolId(),
				PaymentProtocolStatus.CLOSE);
		// ����ָ��
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		try {
			payServiceBO.validateCreatePaymentInstruction(cpiDTO);
		} catch (RuntimeException e) {
			if (!"Э���Ѿ��ر�".equals(e.getMessage())) {
				Assert.fail("Э��ر���ȴδ��֤����");
			}
		}
	}

	@Test
	// ͨ��Э��idȥ����ָ��
	public void findInstructionsByProtocolIdTest() {
		int userId = testUtil.createRandomUserId();
		// �����տ����˻�
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// �´�����Э��
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		// Ĭ��ָ������Ϊpay
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		payServiceBO.createPaymentInstruction(cpiDTO);
		CreatePaymentInstructionDTO cpiDTO2 = cpiDTO;
		// ȷ���տ����͵�
		cpiDTO2.setType(PaymentInstructionType.CONFIRM);
		payServiceBO.createPaymentInstruction(cpiDTO2);
		// �ȽϽ��
		List<PaymentInstruction> list = payServiceBO
				.findInstructionsByProtocolId(protocol.getPaymentProtocolId());
		Assert.assertEquals(2, list.size());
	}

	@Test
	// ת�ƽ��ʱ��ָ��״̬ΪFAILURE
	public void transferMoneyTestFailure() {
		int userId = testUtil.createRandomUserId();
		// �����տ����˻�
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// �´�����Э��
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		// Ĭ��ָ������Ϊpay
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		// �½�ָ��
		PaymentInstruction instruction = payServiceBO
				.createPaymentInstruction(cpiDTO);
		instruction.setStatus(PaymentInstructionStatus.FAILURE);
		payServiceBO.updatePaymentInstruction(instruction);
		try {
			payServiceBO.transferMoney(instruction);
		} catch (RuntimeException e) {
			if (!"ָ���processing��״̬������ִ��ת�ƽ��".equals(e.getMessage())) {
				Assert.fail("ָ���Ѿ�ʧЧ��ȴ��Ȼ��ִ��");
			}
		}
	}

	@Test
	// ת��ʱ����ָ���Ǯ�Ѿ�������
	public void transferMoneyTestForAllRefund() {
		int userId = testUtil.createRandomUserId();
		// �����տ����˻�
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// �´�����Э��
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		// Ĭ��ָ������Ϊpay
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		cpiDTOs.add(cpiDTO);
		// ֧��
		List<PaymentInstruction> instructions = payServiceBO
				.processGetInstructions(cpiDTOs);
		payServiceBO.process(instructions);

		// ��ȫ��
		payService.refundMoney(protocol.getPaymentProtocolId(),
				protocol.getPayMoney());

		PaymentInstruction instruction = payServiceBO
				.findInstructionsByProtocolIdAndType(
						protocol.getPaymentProtocolId(),
						PaymentInstructionType.PAY).get(0);
		Assert.assertEquals((Integer) 3, instruction.getVersion());
	}

	@Test(expected = RuntimeException.class)
	// ��֤֧������Ϊ��
	public void payTestForExceptionNull() {
		payServiceBO.process(null);
	}

	@Test
	// �˻�����֧��
	public void transferMoneyForNoEnoughToPaid() {
		int userId = testUtil.createRandomUserId();
		// �����տ����˻�,������һ���н��100��֧���˻�
		accountServiceBO.createAccount(userId, AccountType.CASH);

		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// Ҫ֧��200
		cppDTO.setPayMoney(200.0);
		// �´�����Э��
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		// Ĭ��ָ������Ϊpay
		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		// Ĭ��ָ������Ϊpay
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		cpiDTOs.add(cpiDTO);
		// ֧��
		try {
			// ֧��
			List<PaymentInstruction> instructions = payServiceBO
					.processGetInstructions(cpiDTOs);
			payServiceBO.process(instructions);

		} catch (RuntimeException e) {
			if (!"����֧��".equals(e.getMessage())) {
				Assert.fail("Ǯ����֧��ȴû�б���");
			}
		}
	}

	@Test(expected = RuntimeException.class)
	// ֧�������쳣
	public void payTestForException() {
		// Ĭ��ָ������Ϊpay
		// ֧��
		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		CreatePaymentInstructionDTO cpiDTO = new CreatePaymentInstructionDTO();
		cpiDTO.setChannel("");

		cpiDTOs.add(cpiDTO);
		// ֧��
		List<PaymentInstruction> instructions = payServiceBO
				.processGetInstructions(cpiDTOs);
		payServiceBO.process(instructions);

	}

	@Test(expected = RuntimeException.class)
	// ֧��--�����쳣
	public void payTestForTypeException() {
		// ׼������
		int userId = testUtil.createRandomUserId();
		// �����տ����˻�,������һ���н��100��֧���˻�
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		cppDTO.setType(PaymentProtocolType.RECHARGE);
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);

		CreatePaymentInstructionDTO cpiDTO = new CreatePaymentInstructionDTO();
		cpiDTO.setChannel(PaymentInstructionChannel.BALANCE);
		cpiDTO.setPayMoney(20.0);
		cpiDTO.setProtocolId(protocol.getPaymentProtocolId());
		cpiDTO.setType(PaymentProtocolType.RECHARGE);
		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		cpiDTOs.add(cpiDTO);
		// ֧��
		List<PaymentInstruction> instructions = payServiceBO
				.processGetInstructions(cpiDTOs);
		payServiceBO.process(instructions);

	}

	@Test
	// ֧����Ǯû�й�Э��Ľ��
	public void payTestForNotEnoughMoneyToProtocol() {
		int userId = testUtil.createRandomUserId();
		// �����տ����˻�,������һ���н��100��֧���˻�
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);

		CreatePaymentInstructionDTO cpiDTO = new CreatePaymentInstructionDTO();
		cpiDTO.setChannel(PaymentInstructionChannel.BALANCE);
		cpiDTO.setPayMoney(10.0);
		cpiDTO.setProtocolId(protocol.getPaymentProtocolId());
		cpiDTO.setType(PaymentProtocolType.PAY);
		logger.info("Э��id��" + protocol.getPaymentProtocolId());
		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		cpiDTOs.add(cpiDTO);
		// ֧��
		List<PaymentInstruction> instructions = payServiceBO
				.processGetInstructions(cpiDTOs);
		payServiceBO.process(instructions);

		// �ȽϽ��
		PaymentProtocol protocol2 = payServiceBO
				.findProtocolByProtocolId(protocol.getPaymentProtocolId());
		Assert.assertEquals(PaymentProtocolStatus.PROCESSING,
				protocol2.getStatus());
	}

	@Test
	// ֧����Ǯ�㹻Э��Ľ��
	public void payTestForEnoughMoneyToProtocol() {
		int userId = testUtil.createRandomUserId();
		// �����տ����˻�,������һ���н��100��֧���˻�
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);

		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);

		CreatePaymentInstructionDTO cpiDTO = new CreatePaymentInstructionDTO();
		cpiDTO.setChannel(PaymentInstructionChannel.BALANCE);
		cpiDTO.setPayMoney(20.0);
		cpiDTO.setProtocolId(protocol.getPaymentProtocolId());
		cpiDTO.setType(PaymentProtocolType.PAY);
		logger.info("Э��id��" + protocol.getPaymentProtocolId());
		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		cpiDTOs.add(cpiDTO);
		// ִ�в���
		// ֧��
		List<PaymentInstruction> instructions = payServiceBO
				.processGetInstructions(cpiDTOs);
		payServiceBO.process(instructions);

		// �ȽϽ��
		PaymentProtocol protocol2 = payServiceBO
				.findProtocolByProtocolId(protocol.getPaymentProtocolId());
		Assert.assertEquals(PaymentProtocolStatus.PAID, protocol2.getStatus());
	}

	@Test
	// �˻���Ǯ����֧��
	public void isEnoughMoneyToPaidProtocolTestForNotEnough() {
		int userId = testUtil.createRandomUserId();
		// �����տ����˻�,������һ���н��100��֧���˻�
		accountServiceBO.createAccount(userId, AccountType.CASH);

		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// Ҫ֧��200
		cppDTO.setPayMoney(200.0);
		// �´�����Э��
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);

		Assert.assertEquals(false, payServiceBO.isEnoughMoneyToPaidProtocol(
				protocol.getPaymentProtocolId(), PaymentProtocolType.PAY));
	}

	/**
	 * �ж�֧�����ÿ��
	 */
	@Test
	public void isEnoughMoneyToPaidProtocolForPayEnough() {
		int userId = testUtil.createRandomUserId();
		// �����տ����˻�,������һ���н��100��֧���˻�
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// �´�����Э��
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		cpiDTOs.add(cpiDTO);
		// ֧��
		List<PaymentInstruction> instructions = payServiceBO
				.processGetInstructions(cpiDTOs);
		payServiceBO.process(instructions);

		Assert.assertEquals(true, payServiceBO.isEnoughMoneyToPaidProtocol(
				protocol.getPaymentProtocolId(), PaymentInstructionType.PAY));
	}

	/**
	 * �ж�֧�����ÿ��
	 */
	@Test
	public void isEnoughMoneyToPaidProtocolForPayNotEnough() {
		int userId = testUtil.createRandomUserId();
		// �����տ����˻�,������һ���н��100��֧���˻�
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// �´�����Э��
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		cpiDTO.setPayMoney(5.00);
		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		cpiDTOs.add(cpiDTO);
		// ֧��
		List<PaymentInstruction> instructions = payServiceBO
				.processGetInstructions(cpiDTOs);
		payServiceBO.process(instructions);

		Assert.assertEquals(false, payServiceBO.isEnoughMoneyToPaidProtocol(
				protocol.getPaymentProtocolId(), PaymentInstructionType.PAY));
	}

	@Test
	/**
	 * ֧����ʱ���˿�����˵�Ǯ����
	 */
	public void isEnoughMoneyToConfirmProtocolForRefund() {
		int userId = testUtil.createRandomUserId();
		// �����տ����˻�,������һ���н��100��֧���˻�
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// �´�����Э��
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		cpiDTOs.add(cpiDTO);
		// ֧��
		List<PaymentInstruction> instructions = payServiceBO
				.processGetInstructions(cpiDTOs);
		payServiceBO.process(instructions);
		// �˿��
		payService.refundMoney(protocol.getPaymentProtocolId(), 5);

		payServiceBO.confirmGetMoney(protocol.getPaymentProtocolId());
		Assert.assertEquals(true,
				payServiceBO.isEnoughMoneyToPaidProtocol(
						protocol.getPaymentProtocolId(),
						PaymentInstructionType.CONFIRM));
	}

	@Test
	/**
	 * ͨ��ָ��idȥ��ѯָ��
	 */
	public void findByInstructionIdTest() {
		int userId = testUtil.createRandomUserId();
		// �����տ����˻�,������һ���н��100��֧���˻�
		accountServiceBO.createAccount(userId, AccountType.CASH);

		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);

		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		PaymentInstruction instruction = this.payServiceBO
				.createPaymentInstruction(cpiDTO);
		PaymentInstruction instruction2 = payServiceBO
				.findByInstructionId(instruction.getPaymentInstructionId());
		if (instruction2 == null) {
			Assert.fail("û�в��ҵ�ָ��");
		}
	}

	@Test
	/**
	 * ͨ��Э��id�����Ͳ���ָ��
	 */
	public void findInstructionsByProtocolIdAndTypeTestSuccess() {
		int userId = testUtil.createRandomUserId();
		// �����տ����˻�,������һ���н��100��֧���˻�
		accountServiceBO.createAccount(userId, AccountType.CASH);

		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);

		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		this.payServiceBO.createPaymentInstruction(cpiDTO);

		List<PaymentInstruction> instructions = payServiceBO
				.findInstructionsByProtocolIdAndType(
						protocol.getPaymentProtocolId(),
						PaymentInstructionType.PAY);
		if (instructions.size() != 1) {
			Assert.fail("û���ҵ������ҵ���ָ����������");
		}
	}

	@Test
	// �޸�Э��״̬
	public void setProtocolStatusTestSuccess() {
		int userId = testUtil.createRandomUserId();
		// �����տ����˻�,������һ���н��100��֧���˻�
		accountServiceBO.createAccount(userId, AccountType.CASH);

		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);

		payServiceBO.setProtocolStatus(protocol.getPaymentProtocolId(),
				PaymentProtocolStatus.CLOSE);

		PaymentProtocol protocol2 = payServiceBO
				.findProtocolByProtocolId(protocol.getPaymentProtocolId());
		Assert.assertEquals(PaymentProtocolStatus.CLOSE, protocol2.getStatus());
	}

	@Test
	/**
	 * ȷ���տ�ʱ���˿�
	 */
	public void confirmGetMoneyHadRefund() {
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
		// ֧��
		List<PaymentInstruction> instructions = payServiceBO
				.processGetInstructions(cpiDTOs);
		payServiceBO.process(instructions);

		// ��5Ԫ
		payService.refundMoney(protocol.getPaymentProtocolId(), 5);
		// ȷ���տ�
		payServiceBO.confirmGetMoney(protocol.getPaymentProtocolId());
		// ȷ���տ�ָ��
		PaymentInstruction instruction = payServiceBO
				.findInstructionsByProtocolIdAndType(
						protocol.getPaymentProtocolId(),
						PaymentInstructionType.CONFIRM).get(0);
		Assert.assertEquals((Double) (20.0 - 5.0), instruction.getPayMoney());
	}

	@Test
	/**
	 * ȷ���տ�ʱû�����˿����Ǯ��֧��Э����
	 */
	public void confirmGetMoneNoRefund() {
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
		// ֧��
		List<PaymentInstruction> instructions = payServiceBO
				.processGetInstructions(cpiDTOs);
		payServiceBO.process(instructions);

		// ȷ���տ�
		payServiceBO.confirmGetMoney(protocol.getPaymentProtocolId());
		// ȷ���տ�ָ��
		PaymentInstruction instruction = payServiceBO
				.findInstructionsByProtocolIdAndType(
						protocol.getPaymentProtocolId(),
						PaymentInstructionType.CONFIRM).get(0);
		Assert.assertEquals((Double) 20.0, instruction.getPayMoney());
		PaymentProtocol protocol2 = payServiceBO
				.findProtocolByProtocolId(protocol.getPaymentProtocolId());
		Assert.assertEquals(PaymentProtocolStatus.SUCCESS,
				protocol2.getStatus());
	}

	/**
	 * ͨ��orderid����Э��
	 */
	@Test
	public void findProtocolByOrderIdSuccess() {
		int userId = testUtil.createRandomUserId();
		// �����տ����˻�,������һ���н��100��֧���˻�
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// �´�����Э�� 20Ԫ
		payServiceBO.createPaymentProtocol(cppDTO);
		PaymentProtocol paymentProtocol = payServiceBO
				.findProtocolByOrderId(cppDTO.getOrderId());
		if (paymentProtocol == null) {
			Assert.fail("û��ͨ������id���ҵ�Э��");
		}
	}

	@Test
	/**
	 * �޸�Э����ɹ�
	 */
	public void modifyProtocolTestSuccess() {
		int userId = testUtil.createRandomUserId();
		// �����տ����˻�,������һ���н��100��֧���˻�
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// �´�����Э�� 20Ԫ
		PaymentProtocol paymentProtocol = payServiceBO
				.createPaymentProtocol(cppDTO);
		// ��Э�����Ϊ40.0
		payServiceBO.modifyProtocol(paymentProtocol.getPaymentProtocolId(),
				40.0);
		PaymentProtocol paymentProtocol2 = payServiceBO
				.findProtocolByProtocolId(paymentProtocol
						.getPaymentProtocolId());
		Assert.assertEquals((Double) 40.0, paymentProtocol2.getPayMoney());
	}

	@Test
	/**
	 * �޸�Э����ʧ����Ϊ�Ѿ�֧���ˣ�״̬��Ϊinit
	 */
	public void modifyProtocolTestFailByPaid() {
		int userId = testUtil.createRandomUserId();
		// �����տ����˻�,������һ���н��100��֧���˻�
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// �´�����Э�� 20Ԫ
		PaymentProtocol paymentProtocol = payServiceBO
				.createPaymentProtocol(cppDTO);
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(paymentProtocol.getPaymentProtocolId());
		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		cpiDTOs.add(cpiDTO);
		// ֧��
		List<PaymentInstruction> instructions = payServiceBO
				.processGetInstructions(cpiDTOs);
		payServiceBO.process(instructions);

		// ��Э�����Ϊ40.0

		try {
			payServiceBO.modifyProtocol(paymentProtocol.getPaymentProtocolId(),
					40.0);
		} catch (RuntimeException e) {
			if (!"Э���Ѿ������������޸�".equals(e.getMessage())) {
				Assert.fail("֧����Ͳ����޸�Э���ȴ�޸���");
			}
		}
	}

	@Test
	/**
	 * ����ָ��ʱ�����Ͳ��ԣ���Ϊ֧������ȷ���տ�
	 */
	public void createPaymentInstructionFailForErrorType() {
		int userId = testUtil.createRandomUserId();
		// �����տ����˻�,������һ���н��100��֧���˻�
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// �´�����Э�� 20Ԫ
		PaymentProtocol paymentProtocol = payServiceBO
				.createPaymentProtocol(cppDTO);
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(paymentProtocol.getPaymentProtocolId());
		// ��ֵ����
		cpiDTO.setType(PaymentInstructionType.RECHARGE);
		try {
			// ����֧��Э��
			payServiceBO.createPaymentInstruction(cpiDTO);
		} catch (RuntimeException e) {
			if (!"ָ�����Ͳ�Ϊ֧������ȷ�ϸ������֧����Ĵ���ָ���".equals(e.getMessage())) {
				Assert.fail("ָ�����Ͳ��ԣ�ȴ�ܹ�����ָ��");
			}
		}

	}

	@Test
	/**
	 * ����֧��ָ��
	 */
	public void createPaymentInstructionSuccessForPay() {
		int userId = testUtil.createRandomUserId();
		// �����տ����˻�,������һ���н��100��֧���˻�
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// �´�����Э�� 20Ԫ
		PaymentProtocol paymentProtocol = payServiceBO
				.createPaymentProtocol(cppDTO);
		// ֧��20
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(paymentProtocol.getPaymentProtocolId());
		PaymentInstruction instruction = payServiceBO
				.createPaymentInstruction(cpiDTO);
		PaymentInstruction instruction2 = payServiceBO
				.findByInstructionId(instruction.getPaymentInstructionId());
		Assert.assertEquals(SpecialAccount.guaranteeId,
				instruction2.getReceiverId());

	}

	@Test
	/**
	 * ����ȷ���տ�ָ��
	 */
	public void createPaymentInstructionSuccessForConfirm() {
		int userId = testUtil.createRandomUserId();
		// �����տ����˻�,������һ���н��100��֧���˻�
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// �´�����Э�� 20Ԫ
		PaymentProtocol paymentProtocol = payServiceBO
				.createPaymentProtocol(cppDTO);
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(paymentProtocol.getPaymentProtocolId());
		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		cpiDTOs.add(cpiDTO);
		// ֧��20
		// ֧��
		List<PaymentInstruction> instructions = payServiceBO
				.processGetInstructions(cpiDTOs);
		payServiceBO.process(instructions);

		CreatePaymentInstructionDTO cpiDTO2 = cpiDTO;
		cpiDTO2.setType(PaymentInstructionType.CONFIRM);
		// ����ȷ���տ�ָ��
		PaymentInstruction instruction = payServiceBO
				.createPaymentInstruction(cpiDTO2);
		// �Ƚ�
		PaymentInstruction instruction2 = payServiceBO
				.findByInstructionId(instruction.getPaymentInstructionId());
		Assert.assertEquals(SpecialAccount.guaranteeId,
				instruction2.getPayerId());

	}

	/**
	 * ������;��֧���ڵȴ�
	 */
	@Test
	public void payForOtherChannelTest() {
		int userId = testUtil.createRandomUserId();
		// �����տ����˻�,������һ���н��100��֧���˻�
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// �´�����Э�� 20Ԫ
		PaymentProtocol paymentProtocol = payServiceBO
				.createPaymentProtocol(cppDTO);
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(paymentProtocol.getPaymentProtocolId());
		// ����֧������Ϊ����֧��
		cpiDTO.setChannel(PaymentInstructionChannel.ALIPAY);
		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		cpiDTOs.add(cpiDTO);
		// ֧��20
		// ֧��
		List<PaymentInstruction> instructions = payServiceBO
				.processGetInstructions(cpiDTOs);
		payServiceBO.process(instructions);

		// ����Э��
		PaymentProtocol paymentProtocol2 = payServiceBO
				.findProtocolByProtocolId(paymentProtocol
						.getPaymentProtocolId());

		Assert.assertEquals(PaymentProtocolStatus.PROCESSING,
				paymentProtocol2.getStatus());
		// ���ҳ���Ӧ��֧��ָ��
		PaymentInstruction instruction = payServiceBO
				.findInstructionsByProtocolIdAndType(
						paymentProtocol.getPaymentProtocolId(),
						PaymentProtocolType.PAY).get(0);
		Assert.assertEquals(PaymentInstructionStatus.PROCESSING,
				instruction.getStatus());
	}

	/**
	 * �ӵ�����;��֧�����˶Գɹ�
	 */
	@Test
	public void payForOtherChannelMatchSuccessTest() {
		int userId = testUtil.createRandomUserId();
		// �����տ����˻�,������һ���н��100��֧���˻�
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// �´�����Э�� 20Ԫ
		PaymentProtocol paymentProtocol = payServiceBO
				.createPaymentProtocol(cppDTO);
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(paymentProtocol.getPaymentProtocolId());
		// ����֧������Ϊ����֧��
		cpiDTO.setChannel(PaymentInstructionChannel.ALIPAY);
		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		cpiDTOs.add(cpiDTO);
		// ֧��20
		// ֧��
		List<PaymentInstruction> instructions = payServiceBO
				.processGetInstructions(cpiDTOs);
		payServiceBO.process(instructions);
		// ���ҳ���Ӧ��֧��ָ��
		PaymentInstruction instruction = payServiceBO
				.findInstructionsByProtocolIdAndType(
						paymentProtocol.getPaymentProtocolId(),
						PaymentProtocolType.PAY).get(0);
		// һ����ҵ����ˮƥ��ɹ�
		payServiceBO.matchRcdSuccess(instruction.getProtocolId(),
				PaymentInstructionType.PAY);

		// ����Э��
		PaymentProtocol paymentProtocol2 = payServiceBO
				.findProtocolByProtocolId(paymentProtocol
						.getPaymentProtocolId());

		Assert.assertEquals(PaymentProtocolStatus.PAID,
				paymentProtocol2.getStatus());

		Assert.assertEquals(PaymentInstructionStatus.SUCCESS,
				instruction.getStatus());
	}

	/**
	 * �ӵ�����;��֧�����˶Գɹ�֮��ȷ���տ�
	 */
	@Test
	public void payForOtherChannelMatchSuccessAndConfirmTest() {
		int userId = testUtil.createRandomUserId();
		// �����տ����˻�,������һ���н��100��֧���˻�
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// �´�����Э�� 20Ԫ
		PaymentProtocol paymentProtocol = payServiceBO
				.createPaymentProtocol(cppDTO);
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(paymentProtocol.getPaymentProtocolId());
		// ����֧������Ϊ����֧��
		cpiDTO.setChannel(PaymentInstructionChannel.ALIPAY);
		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		cpiDTOs.add(cpiDTO);
		// ֧��20
		// ֧��
		List<PaymentInstruction> instructions = payServiceBO
				.processGetInstructions(cpiDTOs);
		payServiceBO.process(instructions);
		// ���ҳ���Ӧ��֧��ָ��
		PaymentInstruction instruction = payServiceBO
				.findInstructionsByProtocolIdAndType(
						paymentProtocol.getPaymentProtocolId(),
						PaymentProtocolType.PAY).get(0);
		// һ����ҵ����ˮƥ��ɹ�
		payServiceBO.matchRcdSuccess(paymentProtocol.getPaymentProtocolId(),
				PaymentInstructionType.PAY);
		// ȷ���ջ�
		payServiceBO.confirmGetMoney(paymentProtocol.getPaymentProtocolId());

		// ����Э��
		PaymentProtocol paymentProtocol2 = payServiceBO
				.findProtocolByProtocolId(paymentProtocol
						.getPaymentProtocolId());

		// ���״̬
		Assert.assertEquals(PaymentProtocolStatus.SUCCESS,
				paymentProtocol2.getStatus());
		Assert.assertEquals(PaymentInstructionStatus.SUCCESS,
				instruction.getStatus());

		Account accountPayer = accountServiceBO.findAccountByUserIdAndType(
				cppDTO.getPayerId(), AccountType.CASH);
		Account accountRecevier = accountServiceBO.findAccountByUserIdAndType(
				cppDTO.getReceiverId(), AccountType.CASH);

		// ���������
		Assert.assertEquals((Double) 100.0, accountServiceBO
				.findAvailableByAccountId(accountPayer.getAccountId()));
		Assert.assertEquals((Double) 20.0, accountServiceBO
				.findAvailableByAccountId(accountRecevier.getAccountId()));
	}

	/**
	 * �����͵�����;��֧�����˶Գɹ�֮��ȷ���տ�
	 */
	@Test
	public void payForBlanceAndOtherChannelMatchSuccessAndConfirmTest() {
		int userId = testUtil.createRandomUserId();
		// �����տ����˻�,������һ���н��100��֧���˻�
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// �´�����Э�� 20Ԫ
		PaymentProtocol paymentProtocol = payServiceBO
				.createPaymentProtocol(cppDTO);
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(paymentProtocol.getPaymentProtocolId());
		// ����֧������Ϊ����֧��
		cpiDTO.setChannel(PaymentInstructionChannel.ALIPAY);
		// ���õ�����֧�����Ϊ10Ԫ
		cpiDTO.setPayMoney(10.0);
		// ���֧��
		CreatePaymentInstructionDTO cpiDTO2 = testUtil
				.createInstructionDTO(paymentProtocol.getPaymentProtocolId());
		// ���õ�����֧�����Ϊ10Ԫ
		cpiDTO2.setPayMoney(10.0);
		// �������֧������
		cpiDTO2.setChannel(PaymentInstructionChannel.BALANCE);

		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		cpiDTOs.add(cpiDTO);
		cpiDTOs.add(cpiDTO2);
		// ֧��20
		List<PaymentInstruction> instructions = payServiceBO
				.processGetInstructions(cpiDTOs);
		payServiceBO.process(instructions);

		// ���ҳ���Ӧ��֧��ָ��
		PaymentInstruction instruction = payServiceBO
				.findInstructionsByProtocolIdAndType(
						paymentProtocol.getPaymentProtocolId(),
						PaymentProtocolType.PAY).get(0);
		// һ����ҵ����ˮƥ��ɹ�
		payServiceBO.matchRcdSuccess(paymentProtocol.getPaymentProtocolId(),
				PaymentInstructionType.PAY);
		// ȷ���ջ�
		payServiceBO.confirmGetMoney(paymentProtocol.getPaymentProtocolId());

		// ����Э��
		PaymentProtocol paymentProtocol2 = payServiceBO
				.findProtocolByProtocolId(paymentProtocol
						.getPaymentProtocolId());

		// ���״̬
		Assert.assertEquals(PaymentProtocolStatus.SUCCESS,
				paymentProtocol2.getStatus());
		Assert.assertEquals(PaymentInstructionStatus.SUCCESS,
				instruction.getStatus());

		Account accountPayer = accountServiceBO.findAccountByUserIdAndType(
				cppDTO.getPayerId(), AccountType.CASH);
		Account accountRecevier = accountServiceBO.findAccountByUserIdAndType(
				cppDTO.getReceiverId(), AccountType.CASH);

		// ���������
		Assert.assertEquals((Double) (100.0 - 10.0), accountServiceBO
				.findAvailableByAccountId(accountPayer.getAccountId()));
		Assert.assertEquals((Double) 20.0, accountServiceBO
				.findAvailableByAccountId(accountRecevier.getAccountId()));
	}

}
