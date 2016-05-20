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
	 * 创建协议订单id重复
	 */
	@Test(expected = RuntimeException.class)
	public void createPaymentProtocolForOrderIdException() {
		// 准备数据
		int userId = testUtil.createRandomUserId();
		// 创建收款人账户
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		payServiceBO.createPaymentProtocol(cppDTO);

		// 执行测试
		// 同一个cppDTO所以订单id重复
		CreatePaymentProtocolDTO cppDTO2 = cppDTO;
		payServiceBO.createPaymentProtocol(cppDTO2);

	}

	@Test
	// 通过协议id查询协议
	public void findProtocolByProtocolIdTest() {
		// 准备数据
		int userId = testUtil.createRandomUserId();
		// 创建收款人账户
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
			Assert.fail("协议查找出的不正确");
		}
	}

	@Test
	// 创建支付协议成功
	public void createPaymentProtocolSucess() {
		// 准备数据
		int userId = testUtil.createRandomUserId();
		// 创建收款人账户
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// 新创建的协议
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		// 拿到刚创建的协议
		PaymentProtocol protocol2 = payServiceBO
				.findProtocolByProtocolId(protocol.getPaymentProtocolId());
		Assert.assertNotNull(protocol2);
		Assert.assertEquals(PaymentProtocolStatus.INIT, protocol2.getStatus());
	}

	@Test
	// 创建支付指令的类型异常测试
	public void createPaymentInstructionTestForException() {
		// 准备数据
		int userId = testUtil.createRandomUserId();
		// 创建收款人账户
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// 新创建的协议
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		// 指令类型为转账
		cpiDTO.setType(PaymentInstructionType.TRANSFER);
		try {
			payServiceBO.createPaymentInstruction(cpiDTO);
		} catch (RuntimeException e) {
			if (!"指令类型不为支付或者确认付款不能用支付里的创建指令方法".equals(e.getMessage())) {
				Assert.fail("指令类型不正确却没有报错");
			}
		}
	}

	@Test
	// 创建支付指令
	public void createPaymentInstructionTestForPay() {
		int userId = testUtil.createRandomUserId();
		// 创建收款人账户
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// 新创建的协议
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		// 默认指令类型为pay
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		PaymentInstruction instruction = payServiceBO
				.createPaymentInstruction(cpiDTO);
		// 支付指令的收款人是担保户
		Assert.assertEquals(SpecialAccount.guaranteeId,
				instruction.getReceiverId());
	}

	@Test
	// 创建确定收款指令
	public void createPaymentInstructionTestForConfirm() {
		int userId = testUtil.createRandomUserId();
		// 创建收款人账户
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// 新创建的协议
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		cpiDTO.setType(PaymentInstructionType.CONFIRM);
		PaymentInstruction instruction = payServiceBO
				.createPaymentInstruction(cpiDTO);
		// 确人指令的支付人是担保户
		Assert.assertEquals(SpecialAccount.guaranteeId,
				instruction.getPayerId());
	}

	@Test
	// 验证已关闭的支付或者转账协议
	public void validatePayOrTransferTestForClose() {
		int userId = testUtil.createRandomUserId();
		// 创建收款人账户
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// 新创建的协议
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		payServiceBO.setProtocolStatus(protocol.getPaymentProtocolId(),
				PaymentProtocolStatus.CLOSE);
		// 创建指令
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		try {
			payServiceBO.validateCpiDTO(cpiDTO);
		} catch (RuntimeException e) {
			if (!"没有该协议或者协议已经关闭".equals(e.getMessage())) {
				Assert.fail("协议关闭了却未验证出来");
			}
		}
	}

	@Test
	// 验证初始状态不是INIT的支付或者转账协议
	public void validatePayOrTransferTestForNotInit() {
		int userId = testUtil.createRandomUserId();
		// 创建收款人账户
		accountServiceBO.createAccount(userId, AccountType.CASH);

		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// 新创建的协议
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		payServiceBO.setProtocolStatus(protocol.getPaymentProtocolId(),
				PaymentProtocolStatus.PAID);
		// 创建指令
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		try {
			payServiceBO.validateCpiDTO(cpiDTO);
		} catch (RuntimeException e) {
			if (!"协议的状态不是INIT，请检查".equals(e.getMessage())) {
				Assert.fail("协议关闭了却未验证出来");
			}
		}
	}

	@Test
	// 验证支付或者转账协议成功
	public void validatePayOrTransferTestSuccess() {
		int userId = testUtil.createRandomUserId();
		// 创建收款人账户
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// 新创建的协议
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		// 创建指令
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		PaymentProtocol protocol2 = payServiceBO.validateCpiDTO(cpiDTO);
		if (protocol2 == null) {
			Assert.fail("验证后没有取到返回的协议");
		}
	}

	@Test
	// 创建支付指令的验证，支付协议已经关闭
	public void validateCreatePaymentInstructionTestClose() {
		int userId = testUtil.createRandomUserId();
		// 创建收款人账户
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// 新创建的协议
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		payServiceBO.setProtocolStatus(protocol.getPaymentProtocolId(),
				PaymentProtocolStatus.CLOSE);
		// 创建指令
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		try {
			payServiceBO.validateCreatePaymentInstruction(cpiDTO);
		} catch (RuntimeException e) {
			if (!"协议已经关闭".equals(e.getMessage())) {
				Assert.fail("协议关闭了却未验证出来");
			}
		}
	}

	@Test
	// 通过协议id去查找指令
	public void findInstructionsByProtocolIdTest() {
		int userId = testUtil.createRandomUserId();
		// 创建收款人账户
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// 新创建的协议
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		// 默认指令类型为pay
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		payServiceBO.createPaymentInstruction(cpiDTO);
		CreatePaymentInstructionDTO cpiDTO2 = cpiDTO;
		// 确定收款类型的
		cpiDTO2.setType(PaymentInstructionType.CONFIRM);
		payServiceBO.createPaymentInstruction(cpiDTO2);
		// 比较结果
		List<PaymentInstruction> list = payServiceBO
				.findInstructionsByProtocolId(protocol.getPaymentProtocolId());
		Assert.assertEquals(2, list.size());
	}

	@Test
	// 转移金额时候指令状态为FAILURE
	public void transferMoneyTestFailure() {
		int userId = testUtil.createRandomUserId();
		// 创建收款人账户
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// 新创建的协议
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		// 默认指令类型为pay
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		// 新建指令
		PaymentInstruction instruction = payServiceBO
				.createPaymentInstruction(cpiDTO);
		instruction.setStatus(PaymentInstructionStatus.FAILURE);
		payServiceBO.updatePaymentInstruction(instruction);
		try {
			payServiceBO.transferMoney(instruction);
		} catch (RuntimeException e) {
			if (!"指令不是processing化状态，不能执行转移金额".equals(e.getMessage())) {
				Assert.fail("指令已经失效，却任然能执行");
			}
		}
	}

	@Test
	// 转账时发现指令的钱已经退完了
	public void transferMoneyTestForAllRefund() {
		int userId = testUtil.createRandomUserId();
		// 创建收款人账户
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// 新创建的协议
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		// 默认指令类型为pay
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		cpiDTOs.add(cpiDTO);
		// 支付
		List<PaymentInstruction> instructions = payServiceBO
				.processGetInstructions(cpiDTOs);
		payServiceBO.process(instructions);

		// 退全款
		payService.refundMoney(protocol.getPaymentProtocolId(),
				protocol.getPayMoney());

		PaymentInstruction instruction = payServiceBO
				.findInstructionsByProtocolIdAndType(
						protocol.getPaymentProtocolId(),
						PaymentInstructionType.PAY).get(0);
		Assert.assertEquals((Integer) 3, instruction.getVersion());
	}

	@Test(expected = RuntimeException.class)
	// 验证支付传参为空
	public void payTestForExceptionNull() {
		payServiceBO.process(null);
	}

	@Test
	// 账户余额不够支付
	public void transferMoneyForNoEnoughToPaid() {
		int userId = testUtil.createRandomUserId();
		// 创建收款人账户,并创建一个有金额100的支付账户
		accountServiceBO.createAccount(userId, AccountType.CASH);

		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// 要支付200
		cppDTO.setPayMoney(200.0);
		// 新创建的协议
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		// 默认指令类型为pay
		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		// 默认指令类型为pay
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		cpiDTOs.add(cpiDTO);
		// 支付
		try {
			// 支付
			List<PaymentInstruction> instructions = payServiceBO
					.processGetInstructions(cpiDTOs);
			payServiceBO.process(instructions);

		} catch (RuntimeException e) {
			if (!"余额不够支付".equals(e.getMessage())) {
				Assert.fail("钱不够支付却没有报错");
			}
		}
	}

	@Test(expected = RuntimeException.class)
	// 支付渠道异常
	public void payTestForException() {
		// 默认指令类型为pay
		// 支付
		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		CreatePaymentInstructionDTO cpiDTO = new CreatePaymentInstructionDTO();
		cpiDTO.setChannel("");

		cpiDTOs.add(cpiDTO);
		// 支付
		List<PaymentInstruction> instructions = payServiceBO
				.processGetInstructions(cpiDTOs);
		payServiceBO.process(instructions);

	}

	@Test(expected = RuntimeException.class)
	// 支付--类型异常
	public void payTestForTypeException() {
		// 准备数据
		int userId = testUtil.createRandomUserId();
		// 创建收款人账户,并创建一个有金额100的支付账户
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
		// 支付
		List<PaymentInstruction> instructions = payServiceBO
				.processGetInstructions(cpiDTOs);
		payServiceBO.process(instructions);

	}

	@Test
	// 支付，钱没有够协议的金额
	public void payTestForNotEnoughMoneyToProtocol() {
		int userId = testUtil.createRandomUserId();
		// 创建收款人账户,并创建一个有金额100的支付账户
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);

		CreatePaymentInstructionDTO cpiDTO = new CreatePaymentInstructionDTO();
		cpiDTO.setChannel(PaymentInstructionChannel.BALANCE);
		cpiDTO.setPayMoney(10.0);
		cpiDTO.setProtocolId(protocol.getPaymentProtocolId());
		cpiDTO.setType(PaymentProtocolType.PAY);
		logger.info("协议id是" + protocol.getPaymentProtocolId());
		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		cpiDTOs.add(cpiDTO);
		// 支付
		List<PaymentInstruction> instructions = payServiceBO
				.processGetInstructions(cpiDTOs);
		payServiceBO.process(instructions);

		// 比较结果
		PaymentProtocol protocol2 = payServiceBO
				.findProtocolByProtocolId(protocol.getPaymentProtocolId());
		Assert.assertEquals(PaymentProtocolStatus.PROCESSING,
				protocol2.getStatus());
	}

	@Test
	// 支付，钱足够协议的金额
	public void payTestForEnoughMoneyToProtocol() {
		int userId = testUtil.createRandomUserId();
		// 创建收款人账户,并创建一个有金额100的支付账户
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);

		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);

		CreatePaymentInstructionDTO cpiDTO = new CreatePaymentInstructionDTO();
		cpiDTO.setChannel(PaymentInstructionChannel.BALANCE);
		cpiDTO.setPayMoney(20.0);
		cpiDTO.setProtocolId(protocol.getPaymentProtocolId());
		cpiDTO.setType(PaymentProtocolType.PAY);
		logger.info("协议id是" + protocol.getPaymentProtocolId());
		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		cpiDTOs.add(cpiDTO);
		// 执行测试
		// 支付
		List<PaymentInstruction> instructions = payServiceBO
				.processGetInstructions(cpiDTOs);
		payServiceBO.process(instructions);

		// 比较结果
		PaymentProtocol protocol2 = payServiceBO
				.findProtocolByProtocolId(protocol.getPaymentProtocolId());
		Assert.assertEquals(PaymentProtocolStatus.PAID, protocol2.getStatus());
	}

	@Test
	// 账户的钱不够支付
	public void isEnoughMoneyToPaidProtocolTestForNotEnough() {
		int userId = testUtil.createRandomUserId();
		// 创建收款人账户,并创建一个有金额100的支付账户
		accountServiceBO.createAccount(userId, AccountType.CASH);

		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// 要支付200
		cppDTO.setPayMoney(200.0);
		// 新创建的协议
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);

		Assert.assertEquals(false, payServiceBO.isEnoughMoneyToPaidProtocol(
				protocol.getPaymentProtocolId(), PaymentProtocolType.PAY));
	}

	/**
	 * 判断支付付得款够了
	 */
	@Test
	public void isEnoughMoneyToPaidProtocolForPayEnough() {
		int userId = testUtil.createRandomUserId();
		// 创建收款人账户,并创建一个有金额100的支付账户
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// 新创建的协议
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		cpiDTOs.add(cpiDTO);
		// 支付
		List<PaymentInstruction> instructions = payServiceBO
				.processGetInstructions(cpiDTOs);
		payServiceBO.process(instructions);

		Assert.assertEquals(true, payServiceBO.isEnoughMoneyToPaidProtocol(
				protocol.getPaymentProtocolId(), PaymentInstructionType.PAY));
	}

	/**
	 * 判断支付付得款不够
	 */
	@Test
	public void isEnoughMoneyToPaidProtocolForPayNotEnough() {
		int userId = testUtil.createRandomUserId();
		// 创建收款人账户,并创建一个有金额100的支付账户
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// 新创建的协议
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		cpiDTO.setPayMoney(5.00);
		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		cpiDTOs.add(cpiDTO);
		// 支付
		List<PaymentInstruction> instructions = payServiceBO
				.processGetInstructions(cpiDTOs);
		payServiceBO.process(instructions);

		Assert.assertEquals(false, payServiceBO.isEnoughMoneyToPaidProtocol(
				protocol.getPaymentProtocolId(), PaymentInstructionType.PAY));
	}

	@Test
	/**
	 * 支付的时候退款加上退的钱不够
	 */
	public void isEnoughMoneyToConfirmProtocolForRefund() {
		int userId = testUtil.createRandomUserId();
		// 创建收款人账户,并创建一个有金额100的支付账户
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// 新创建的协议
		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(protocol.getPaymentProtocolId());
		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		cpiDTOs.add(cpiDTO);
		// 支付
		List<PaymentInstruction> instructions = payServiceBO
				.processGetInstructions(cpiDTOs);
		payServiceBO.process(instructions);
		// 退款部分
		payService.refundMoney(protocol.getPaymentProtocolId(), 5);

		payServiceBO.confirmGetMoney(protocol.getPaymentProtocolId());
		Assert.assertEquals(true,
				payServiceBO.isEnoughMoneyToPaidProtocol(
						protocol.getPaymentProtocolId(),
						PaymentInstructionType.CONFIRM));
	}

	@Test
	/**
	 * 通过指令id去查询指令
	 */
	public void findByInstructionIdTest() {
		int userId = testUtil.createRandomUserId();
		// 创建收款人账户,并创建一个有金额100的支付账户
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
			Assert.fail("没有查找到指令");
		}
	}

	@Test
	/**
	 * 通过协议id和类型查找指令
	 */
	public void findInstructionsByProtocolIdAndTypeTestSuccess() {
		int userId = testUtil.createRandomUserId();
		// 创建收款人账户,并创建一个有金额100的支付账户
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
			Assert.fail("没有找到或者找到的指令数量不对");
		}
	}

	@Test
	// 修改协议状态
	public void setProtocolStatusTestSuccess() {
		int userId = testUtil.createRandomUserId();
		// 创建收款人账户,并创建一个有金额100的支付账户
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
	 * 确定收款时有退款
	 */
	public void confirmGetMoneyHadRefund() {
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
		// 支付
		List<PaymentInstruction> instructions = payServiceBO
				.processGetInstructions(cpiDTOs);
		payServiceBO.process(instructions);

		// 退5元
		payService.refundMoney(protocol.getPaymentProtocolId(), 5);
		// 确定收款
		payServiceBO.confirmGetMoney(protocol.getPaymentProtocolId());
		// 确定收款指令
		PaymentInstruction instruction = payServiceBO
				.findInstructionsByProtocolIdAndType(
						protocol.getPaymentProtocolId(),
						PaymentInstructionType.CONFIRM).get(0);
		Assert.assertEquals((Double) (20.0 - 5.0), instruction.getPayMoney());
	}

	@Test
	/**
	 * 确定收款时没有有退款，并且钱够支付协议了
	 */
	public void confirmGetMoneNoRefund() {
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
		// 支付
		List<PaymentInstruction> instructions = payServiceBO
				.processGetInstructions(cpiDTOs);
		payServiceBO.process(instructions);

		// 确定收款
		payServiceBO.confirmGetMoney(protocol.getPaymentProtocolId());
		// 确定收款指令
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
	 * 通过orderid查找协议
	 */
	@Test
	public void findProtocolByOrderIdSuccess() {
		int userId = testUtil.createRandomUserId();
		// 创建收款人账户,并创建一个有金额100的支付账户
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// 新创建的协议 20元
		payServiceBO.createPaymentProtocol(cppDTO);
		PaymentProtocol paymentProtocol = payServiceBO
				.findProtocolByOrderId(cppDTO.getOrderId());
		if (paymentProtocol == null) {
			Assert.fail("没有通过订单id查找到协议");
		}
	}

	@Test
	/**
	 * 修改协议金额成功
	 */
	public void modifyProtocolTestSuccess() {
		int userId = testUtil.createRandomUserId();
		// 创建收款人账户,并创建一个有金额100的支付账户
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// 新创建的协议 20元
		PaymentProtocol paymentProtocol = payServiceBO
				.createPaymentProtocol(cppDTO);
		// 将协议金额该为40.0
		payServiceBO.modifyProtocol(paymentProtocol.getPaymentProtocolId(),
				40.0);
		PaymentProtocol paymentProtocol2 = payServiceBO
				.findProtocolByProtocolId(paymentProtocol
						.getPaymentProtocolId());
		Assert.assertEquals((Double) 40.0, paymentProtocol2.getPayMoney());
	}

	@Test
	/**
	 * 修改协议金额失败因为已经支付了，状态不为init
	 */
	public void modifyProtocolTestFailByPaid() {
		int userId = testUtil.createRandomUserId();
		// 创建收款人账户,并创建一个有金额100的支付账户
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// 新创建的协议 20元
		PaymentProtocol paymentProtocol = payServiceBO
				.createPaymentProtocol(cppDTO);
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(paymentProtocol.getPaymentProtocolId());
		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		cpiDTOs.add(cpiDTO);
		// 支付
		List<PaymentInstruction> instructions = payServiceBO
				.processGetInstructions(cpiDTOs);
		payServiceBO.process(instructions);

		// 将协议金额该为40.0

		try {
			payServiceBO.modifyProtocol(paymentProtocol.getPaymentProtocolId(),
					40.0);
		} catch (RuntimeException e) {
			if (!"协议已经被处理，不能修改".equals(e.getMessage())) {
				Assert.fail("支付后就不能修改协议金额，却修改了");
			}
		}
	}

	@Test
	/**
	 * 生成指令时，类型不对，不为支付或者确定收款
	 */
	public void createPaymentInstructionFailForErrorType() {
		int userId = testUtil.createRandomUserId();
		// 创建收款人账户,并创建一个有金额100的支付账户
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// 新创建的协议 20元
		PaymentProtocol paymentProtocol = payServiceBO
				.createPaymentProtocol(cppDTO);
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(paymentProtocol.getPaymentProtocolId());
		// 充值类型
		cpiDTO.setType(PaymentInstructionType.RECHARGE);
		try {
			// 生成支付协议
			payServiceBO.createPaymentInstruction(cpiDTO);
		} catch (RuntimeException e) {
			if (!"指令类型不为支付或者确认付款不能用支付里的创建指令方法".equals(e.getMessage())) {
				Assert.fail("指令类型不对，却能够创建指令");
			}
		}

	}

	@Test
	/**
	 * 生成支付指令
	 */
	public void createPaymentInstructionSuccessForPay() {
		int userId = testUtil.createRandomUserId();
		// 创建收款人账户,并创建一个有金额100的支付账户
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// 新创建的协议 20元
		PaymentProtocol paymentProtocol = payServiceBO
				.createPaymentProtocol(cppDTO);
		// 支付20
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
	 * 生成确定收款指令
	 */
	public void createPaymentInstructionSuccessForConfirm() {
		int userId = testUtil.createRandomUserId();
		// 创建收款人账户,并创建一个有金额100的支付账户
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// 新创建的协议 20元
		PaymentProtocol paymentProtocol = payServiceBO
				.createPaymentProtocol(cppDTO);
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(paymentProtocol.getPaymentProtocolId());
		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		cpiDTOs.add(cpiDTO);
		// 支付20
		// 支付
		List<PaymentInstruction> instructions = payServiceBO
				.processGetInstructions(cpiDTOs);
		payServiceBO.process(instructions);

		CreatePaymentInstructionDTO cpiDTO2 = cpiDTO;
		cpiDTO2.setType(PaymentInstructionType.CONFIRM);
		// 创建确定收款指令
		PaymentInstruction instruction = payServiceBO
				.createPaymentInstruction(cpiDTO2);
		// 比较
		PaymentInstruction instruction2 = payServiceBO
				.findByInstructionId(instruction.getPaymentInstructionId());
		Assert.assertEquals(SpecialAccount.guaranteeId,
				instruction2.getPayerId());

	}

	/**
	 * 从其他途径支付在等待
	 */
	@Test
	public void payForOtherChannelTest() {
		int userId = testUtil.createRandomUserId();
		// 创建收款人账户,并创建一个有金额100的支付账户
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// 新创建的协议 20元
		PaymentProtocol paymentProtocol = payServiceBO
				.createPaymentProtocol(cppDTO);
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(paymentProtocol.getPaymentProtocolId());
		// 设置支付渠道为阿里支付
		cpiDTO.setChannel(PaymentInstructionChannel.ALIPAY);
		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		cpiDTOs.add(cpiDTO);
		// 支付20
		// 支付
		List<PaymentInstruction> instructions = payServiceBO
				.processGetInstructions(cpiDTOs);
		payServiceBO.process(instructions);

		// 查找协议
		PaymentProtocol paymentProtocol2 = payServiceBO
				.findProtocolByProtocolId(paymentProtocol
						.getPaymentProtocolId());

		Assert.assertEquals(PaymentProtocolStatus.PROCESSING,
				paymentProtocol2.getStatus());
		// 查找出对应的支付指令
		PaymentInstruction instruction = payServiceBO
				.findInstructionsByProtocolIdAndType(
						paymentProtocol.getPaymentProtocolId(),
						PaymentProtocolType.PAY).get(0);
		Assert.assertEquals(PaymentInstructionStatus.PROCESSING,
				instruction.getStatus());
	}

	/**
	 * 从第三方途径支付并核对成功
	 */
	@Test
	public void payForOtherChannelMatchSuccessTest() {
		int userId = testUtil.createRandomUserId();
		// 创建收款人账户,并创建一个有金额100的支付账户
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// 新创建的协议 20元
		PaymentProtocol paymentProtocol = payServiceBO
				.createPaymentProtocol(cppDTO);
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(paymentProtocol.getPaymentProtocolId());
		// 设置支付渠道为阿里支付
		cpiDTO.setChannel(PaymentInstructionChannel.ALIPAY);
		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		cpiDTOs.add(cpiDTO);
		// 支付20
		// 支付
		List<PaymentInstruction> instructions = payServiceBO
				.processGetInstructions(cpiDTOs);
		payServiceBO.process(instructions);
		// 查找出对应的支付指令
		PaymentInstruction instruction = payServiceBO
				.findInstructionsByProtocolIdAndType(
						paymentProtocol.getPaymentProtocolId(),
						PaymentProtocolType.PAY).get(0);
		// 一三方业务流水匹配成功
		payServiceBO.matchRcdSuccess(instruction.getProtocolId(),
				PaymentInstructionType.PAY);

		// 查找协议
		PaymentProtocol paymentProtocol2 = payServiceBO
				.findProtocolByProtocolId(paymentProtocol
						.getPaymentProtocolId());

		Assert.assertEquals(PaymentProtocolStatus.PAID,
				paymentProtocol2.getStatus());

		Assert.assertEquals(PaymentInstructionStatus.SUCCESS,
				instruction.getStatus());
	}

	/**
	 * 从第三方途径支付并核对成功之后，确定收款
	 */
	@Test
	public void payForOtherChannelMatchSuccessAndConfirmTest() {
		int userId = testUtil.createRandomUserId();
		// 创建收款人账户,并创建一个有金额100的支付账户
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// 新创建的协议 20元
		PaymentProtocol paymentProtocol = payServiceBO
				.createPaymentProtocol(cppDTO);
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(paymentProtocol.getPaymentProtocolId());
		// 设置支付渠道为阿里支付
		cpiDTO.setChannel(PaymentInstructionChannel.ALIPAY);
		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		cpiDTOs.add(cpiDTO);
		// 支付20
		// 支付
		List<PaymentInstruction> instructions = payServiceBO
				.processGetInstructions(cpiDTOs);
		payServiceBO.process(instructions);
		// 查找出对应的支付指令
		PaymentInstruction instruction = payServiceBO
				.findInstructionsByProtocolIdAndType(
						paymentProtocol.getPaymentProtocolId(),
						PaymentProtocolType.PAY).get(0);
		// 一三方业务流水匹配成功
		payServiceBO.matchRcdSuccess(paymentProtocol.getPaymentProtocolId(),
				PaymentInstructionType.PAY);
		// 确定收货
		payServiceBO.confirmGetMoney(paymentProtocol.getPaymentProtocolId());

		// 查找协议
		PaymentProtocol paymentProtocol2 = payServiceBO
				.findProtocolByProtocolId(paymentProtocol
						.getPaymentProtocolId());

		// 检测状态
		Assert.assertEquals(PaymentProtocolStatus.SUCCESS,
				paymentProtocol2.getStatus());
		Assert.assertEquals(PaymentInstructionStatus.SUCCESS,
				instruction.getStatus());

		Account accountPayer = accountServiceBO.findAccountByUserIdAndType(
				cppDTO.getPayerId(), AccountType.CASH);
		Account accountRecevier = accountServiceBO.findAccountByUserIdAndType(
				cppDTO.getReceiverId(), AccountType.CASH);

		// 检测可用余额
		Assert.assertEquals((Double) 100.0, accountServiceBO
				.findAvailableByAccountId(accountPayer.getAccountId()));
		Assert.assertEquals((Double) 20.0, accountServiceBO
				.findAvailableByAccountId(accountRecevier.getAccountId()));
	}

	/**
	 * 从余额和第三方途径支付并核对成功之后，确定收款
	 */
	@Test
	public void payForBlanceAndOtherChannelMatchSuccessAndConfirmTest() {
		int userId = testUtil.createRandomUserId();
		// 创建收款人账户,并创建一个有金额100的支付账户
		accountServiceBO.createAccount(userId, AccountType.CASH);
		CreatePaymentProtocolDTO cppDTO = testUtil.createProtocolDTO(userId);
		// 新创建的协议 20元
		PaymentProtocol paymentProtocol = payServiceBO
				.createPaymentProtocol(cppDTO);
		CreatePaymentInstructionDTO cpiDTO = testUtil
				.createInstructionDTO(paymentProtocol.getPaymentProtocolId());
		// 设置支付渠道为阿里支付
		cpiDTO.setChannel(PaymentInstructionChannel.ALIPAY);
		// 设置第三方支付金额为10元
		cpiDTO.setPayMoney(10.0);
		// 余额支付
		CreatePaymentInstructionDTO cpiDTO2 = testUtil
				.createInstructionDTO(paymentProtocol.getPaymentProtocolId());
		// 设置第三方支付金额为10元
		cpiDTO2.setPayMoney(10.0);
		// 设置余额支付渠道
		cpiDTO2.setChannel(PaymentInstructionChannel.BALANCE);

		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		cpiDTOs.add(cpiDTO);
		cpiDTOs.add(cpiDTO2);
		// 支付20
		List<PaymentInstruction> instructions = payServiceBO
				.processGetInstructions(cpiDTOs);
		payServiceBO.process(instructions);

		// 查找出对应的支付指令
		PaymentInstruction instruction = payServiceBO
				.findInstructionsByProtocolIdAndType(
						paymentProtocol.getPaymentProtocolId(),
						PaymentProtocolType.PAY).get(0);
		// 一三方业务流水匹配成功
		payServiceBO.matchRcdSuccess(paymentProtocol.getPaymentProtocolId(),
				PaymentInstructionType.PAY);
		// 确定收货
		payServiceBO.confirmGetMoney(paymentProtocol.getPaymentProtocolId());

		// 查找协议
		PaymentProtocol paymentProtocol2 = payServiceBO
				.findProtocolByProtocolId(paymentProtocol
						.getPaymentProtocolId());

		// 检测状态
		Assert.assertEquals(PaymentProtocolStatus.SUCCESS,
				paymentProtocol2.getStatus());
		Assert.assertEquals(PaymentInstructionStatus.SUCCESS,
				instruction.getStatus());

		Account accountPayer = accountServiceBO.findAccountByUserIdAndType(
				cppDTO.getPayerId(), AccountType.CASH);
		Account accountRecevier = accountServiceBO.findAccountByUserIdAndType(
				cppDTO.getReceiverId(), AccountType.CASH);

		// 检测可用余额
		Assert.assertEquals((Double) (100.0 - 10.0), accountServiceBO
				.findAvailableByAccountId(accountPayer.getAccountId()));
		Assert.assertEquals((Double) 20.0, accountServiceBO
				.findAvailableByAccountId(accountRecevier.getAccountId()));
	}

}
