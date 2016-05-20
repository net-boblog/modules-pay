package com.xabaohui.modules.pay.service.bo;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xabaohui.modules.pay.bean.Account;
import com.xabaohui.modules.pay.bean.PaymentInstruction;
import com.xabaohui.modules.pay.bean.PaymentProtocol;
import com.xabaohui.modules.pay.bean.RefundDetail;
import com.xabaohui.modules.pay.bean.SpecialAccount;
import com.xabaohui.modules.pay.bean.channel.PaymentInstructionChannel;
import com.xabaohui.modules.pay.bean.status.PaymentInstructionStatus;
import com.xabaohui.modules.pay.bean.status.PaymentProtocolStatus;
import com.xabaohui.modules.pay.bean.type.AccountType;
import com.xabaohui.modules.pay.bean.type.PaymentInstructionType;
import com.xabaohui.modules.pay.bean.type.PaymentProtocolType;
import com.xabaohui.modules.pay.dto.CreatePaymentInstructionDTO;
import com.xabaohui.modules.pay.dto.CreatePaymentProtocolDTO;
import com.xabaohui.modules.pay.util.Time;
import com.xabaohui.modules.pay.util.Validation;

/**
 * 
 * @author YRee
 * 
 */
public class PayServiceBO extends PaymentBOu {
	protected static Logger logger = LoggerFactory
			.getLogger(PayServiceBO.class);

	private RefundDetailBO refundDetailBO;

	/**
	 * 生成支付协议
	 * 
	 * @param orderId
	 * @param payerId
	 * @param receiverId
	 * @param money
	 * @return
	 */
	public PaymentProtocol createPayProtocol(Integer orderId, Integer payerId,
			Integer receiverId, double money) {

		if (orderId == null || payerId == null || receiverId == null
				|| !Validation.isValidMoney(money)) {
			throw new RuntimeException("创建支付协议失败，请检查创建的参数是否正确");
		}
		// 如果是支付类型的业务，针对orderId判重
		if (protocolDao.findByOrderId(orderId) != null) {
			throw new RuntimeException("定单" + orderId + "已经创建过协议，请确定");
		}

		CreatePaymentProtocolDTO cppDTO = new CreatePaymentProtocolDTO();
		cppDTO.setOrderId(orderId);
		cppDTO.setPayerId(payerId);
		cppDTO.setReceiverId(receiverId);
		cppDTO.setPayMoney(money);
		cppDTO.setType(PaymentProtocolType.PAY);
		return super.createPaymentProtocol(cppDTO);
	}

	// /**
	// * 余额支付
	// *
	// * @param cpiDTO
	// */
	// public void pay(List<CreatePaymentInstructionDTO> cpiDTOs) {
	// if (cpiDTOs == null || cpiDTOs.isEmpty()) {
	// throw new RuntimeException("支付失败：传来的指令为空");
	// }
	// // 首先执行的指令
	// PaymentInstruction outerChannelInstruction = null;
	// // 是否验证过协议状态
	// boolean isValidateProtocolStatus = false;
	// List<PaymentInstruction> instructions = new
	// ArrayList<PaymentInstruction>();
	// for (CreatePaymentInstructionDTO cpiDTO : cpiDTOs) {
	// // 检查对象
	// PaymentProtocol protocol = this.validatePayOrTransfer(cpiDTO);
	// if (!PaymentInstructionType.PAY.equals(cpiDTO.getType())) {
	// throw new RuntimeException("支付失败：支付指令的类型不为支付");
	// }
	// // 没有验证过协议状态的话，检查协议状态
	// if (!isValidateProtocolStatus
	// && !PaymentProtocolStatus.INIT.equals(protocol.getStatus())) {
	// throw new RuntimeException("支付失败:指令的初始状态不为init");
	// }
	// // 没有验证过协议状态的话，设置协议状态为processing（只第一次设置）
	// if (!isValidateProtocolStatus) {
	// super.setProtocolStatus(protocol.getPaymentProtocolId(),
	// PaymentProtocolStatus.PROCESSING);
	// isValidateProtocolStatus = true;
	// }
	// // 生成支付指令
	// PaymentInstruction instruction = this
	// .createPaymentInstruction(cpiDTO);
	// instructions.add(instruction);
	// // 排序并将外部支付放在最前
	// if (!PaymentInstructionChannel.BALANCE.equals(instruction
	// .getChannel())) {
	// outerChannelInstruction = instruction;
	// }
	// }
	// // 存在外部渠道指令
	// if (outerChannelInstruction != null) {
	// super.processOne(outerChannelInstruction);
	// } else {// 只有内部渠道指令
	// for (PaymentInstruction instruction : instructions) {
	// super.processOne(instruction);
	// }
	// }
	//
	// // 每一条指令逐条执行
	// logger.info("生成支付指令【{}】",
	// outerChannelInstruction.getPaymentInstructionId());
	// }

	// /**
	// * 余额支付
	// *
	// * @param cpiDTO
	// */
	// public void pay2(List<CreatePaymentInstructionDTO> cpiDTOs) {
	// // 判断只有余额支付的标志
	// boolean isOnlyBlance = true;
	// PaymentInstruction onlyBlanceInstruction = null;
	// if (cpiDTOs == null || cpiDTOs.isEmpty()) {
	// throw new RuntimeException("支付失败：传来的指令为空");
	// }
	// // 是否验证过协议状态
	// boolean isValidateProtocolStatus = false;
	// for (CreatePaymentInstructionDTO cpiDTO : cpiDTOs) {
	// // 检查对象
	// PaymentProtocol protocol = this.validatePayOrTransfer(cpiDTO);
	// if (!PaymentInstructionType.PAY.equals(cpiDTO.getType())) {
	// logger.error("支付指令的类型不为支付");
	// throw new RuntimeException("支付失败：支付指令的类型不为支付");
	// }
	// // 没有验证过协议状态的话，检查协议状态
	// if (!isValidateProtocolStatus
	// && !PaymentProtocolStatus.INIT.equals(protocol.getStatus())) {
	// throw new RuntimeException("支付失败:指令的初始状态不为init");
	// }
	// // 没有验证过协议状态的话，设置协议状态为processing（只第一次设置）
	// if (!isValidateProtocolStatus) {
	// super.setProtocolStatus(protocol.getPaymentProtocolId(),
	// PaymentProtocolStatus.PROCESSING);
	// isValidateProtocolStatus = true;
	// }
	// // 生成支付指令
	// PaymentInstruction instruction = this
	// .createPaymentInstruction(cpiDTO);
	// logger.info("生成支付指令【{}】", instruction.getPaymentInstructionId());
	// // 设置协议状态为processing
	// instruction.setStatus(PaymentInstructionStatus.PROCESSING);
	// super.updatePaymentInstruction(instruction);
	//
	// // 如果是第三方支付，就生成业务流水并且等待
	// if (!PaymentInstructionChannel.BALANCE.equals(instruction
	// .getChannel())) {
	// // 不是或者不只是余额支付了
	// isOnlyBlance = false;
	// // 不做充值。充值的话，返回的会是充值协议的id，不是支付协议的id
	// // TODO invoke outer API
	// // 直接生成一方业务流水
	// super.createOurRcdClearing(instruction,
	// PaymentInstructionChannel.BALANCE);
	// // 充值操作，生成了一方业务流水
	//
	// } else {
	// // 唯一的余额支付指令
	// onlyBlanceInstruction = instruction;
	// }
	// }
	// // 有且只有一条余额支付指令
	// if (cpiDTOs.size() == 1 && isOnlyBlance) {
	// // 余额支付直接执行
	// this.processForPay(onlyBlanceInstruction);
	//
	// }
	// }

	// /**
	// * 支付成功
	// *
	// * @param instruction
	// */
	// private void processForPay(PaymentInstruction instruction) {
	// logger.info("转移金钱");
	// // 转移金钱
	// super.transferMoney(instruction);
	// PaymentProtocol protocol = super.findProtocolByProtocolId(instruction
	// .getProtocolId());
	// if (protocol == null) {
	// throw new RuntimeException("支付成功后的操作失败：没有找到协议");
	// }
	// // 但是协议状态不为processing
	// if (!PaymentProtocolStatus.PROCESSING.equals(protocol.getStatus())) {
	// logger.info("协议的状态是{}", protocol.getStatus());
	// throw new RuntimeException("支付成功后的操作失败：协议状态不为processing");
	// }
	// // 如果协议的金额够了，且状态位仍然是processing,就变成paid状态
	// if (super.isEnoughMoneyToPaidProtocol(instruction.getProtocolId(),
	// PaymentInstructionType.PAY)
	// && PaymentProtocolStatus.PROCESSING
	// .equals(protocol.getStatus())) {
	// super.setProtocolStatus(instruction.getProtocolId(),
	// PaymentProtocolStatus.PAID);
	// // 唤醒 trade
	// }
	// }

	/**
	 * 确定收货
	 * 
	 * @param protocolId
	 */
	public void confirmGetMoney(Integer protocolId) {
		if (protocolId == null) {
			throw new RuntimeException("传来的协议id不能为空");
		}
		// 拿到协议
		PaymentProtocol protocol = findProtocolByProtocolId(protocolId);
		if (protocol == null) {
			throw new RuntimeException("没有找到这个协议，请检查");
		}
		if (PaymentProtocolStatus.SUCCESS.equals(protocol.getStatus())
				|| PaymentProtocolStatus.INIT.equals(protocol.getStatus())
				|| PaymentProtocolStatus.CLOSE.equals(protocol.getStatus())) {
			logger.info("协议此时状态不正确，状态为:" + protocol.getStatus());
			throw new RuntimeException("请检查协议状态");
		}
		// 支付的总额
		double payMoney = protocol.getPayMoney();
		// 已经退过款的流水
		List<RefundDetail> listRefundDetails = refundDetailBO
				.findRefundDetailsByProtocolId(protocolId);
		// 如果找到有退款元素
		if (listRefundDetails != null && !listRefundDetails.isEmpty()) {
			for (RefundDetail refundDetail : listRefundDetails) {
				payMoney = payMoney - refundDetail.getRefundMoney(); // 退款金额不断减少
			}
		}
		// 生成确定收款指令。转移金钱
		CreatePaymentInstructionDTO cpiDTO = new CreatePaymentInstructionDTO();
		cpiDTO.setChannel(PaymentInstructionChannel.BALANCE);
		cpiDTO.setProtocolId(protocolId);
		cpiDTO.setPayMoney(payMoney);
		cpiDTO.setType(PaymentInstructionType.CONFIRM);
		// 确认收款的指令
		PaymentInstruction instruction = this.createPaymentInstruction(cpiDTO);
		// 修改指令状态
		instruction.setStatus(PaymentInstructionStatus.PROCESSING);
		super.updatePaymentInstruction(instruction);
		super.transferMoney(instruction);
		// 如果确定收款的钱够了
		if (isEnoughMoneyToPaidProtocol(protocol.getPaymentProtocolId(),
				PaymentInstructionType.CONFIRM)) {
			this.setProtocolStatus(protocol.getPaymentProtocolId(),
					PaymentProtocolStatus.SUCCESS);
		}
	}

	/**
	 * 通过orderId 查询支付协议id
	 * 
	 * @param orderId
	 * @return
	 */
	public PaymentProtocol findProtocolByOrderId(Integer orderId) {
		if (orderId == null) {
			throw new RuntimeException("传来的orderId有误");
		}

		return protocolDao.findByOrderId(orderId);
	}

	/**
	 * 修改支付协议金额
	 * 
	 * @param mppDTO
	 */
	public void modifyProtocol(Integer protocolId, double money) {
		if (protocolId == null || !Validation.isValidMoney(money)) {
			throw new RuntimeException("请检查传来的数据是否有效");
		}

		// 修改协议
		PaymentProtocol protocol = this.findProtocolByProtocolId(protocolId);
		if (protocol == null) {
			throw new RuntimeException("传来的协议id并没有找到对象");
		}
		// 检查协议状态
		if (!PaymentProtocolStatus.INIT.equals(protocol.getStatus())) {
			throw new RuntimeException("协议已经被处理，不能修改");
		}
		// 修改协议金额
		protocol.setPayMoney(money);

		protocol.setGmtModify(Time.getNow());
		protocol.setVersion(protocol.getVersion() + 1);
		protocolDao.update(protocol);
	}

	@Override
	protected PaymentInstruction createPaymentInstruction(
			CreatePaymentInstructionDTO cpiDTO) {
		// 检验并且 拿到协议
		PaymentProtocol protocol = super
				.validateCreatePaymentInstruction(cpiDTO);
		logger.info("指令的类型为" + cpiDTO.getType());
		if (!PaymentInstructionType.PAY.equals(cpiDTO.getType())
				&& !PaymentInstructionType.CONFIRM.equals(cpiDTO.getType())) {
			throw new RuntimeException("指令类型不为支付或者确认付款不能用支付里的创建指令方法");
		}
		Account payer = null;
		Account receiver = null;
		// 支付类型
		if (PaymentInstructionType.PAY.equals(cpiDTO.getType())) {
			// 支付人是卖家
			payer = accountServiceBO.findAccountByUserIdAndType(
					protocol.getPayerId(), AccountType.CASH);
			// 收款人是担保户
			receiver = accountServiceBO
					.findByAccountId(SpecialAccount.guaranteeId);
		}
		if (PaymentInstructionType.CONFIRM.equals(cpiDTO.getType())) {

			// 支付人是担保户
			payer = accountServiceBO
					.findByAccountId(SpecialAccount.guaranteeId);
			// 收款人是卖家
			receiver = accountServiceBO.findAccountByUserIdAndType(
					protocol.getReceiverId(), AccountType.CASH);
		}
		logger.info("payerId【{}】,receiverId【{}】,指令交易金额{},交易渠道为【{}】",
				payer.getAccountId(), receiver.getAccountId(),
				cpiDTO.getPayMoney(), cpiDTO.getChannel());

		// 调用父类重构方法
		return super.createPaymentInstruction(cpiDTO, payer.getAccountId(),
				receiver.getAccountId());
	}

	// @Override
	// protected void matchRcdSuccess(Integer protocolId) {
	// // 验证并查找指令
	// List<PaymentInstruction> instructions = super
	// .validateProtocolAndReturnInstruction(protocolId,
	// PaymentInstructionType.PAY);
	// for (PaymentInstruction instruction : instructions) {
	// // 第三方支付或者已经支付成功的就跳过
	// if (!PaymentInstructionChannel.isInnerChannel(instruction
	// .getChannel())
	// || PaymentInstructionStatus.SUCCESS.equals(instruction
	// .getStatus())) {
	// continue;
	// }
	// // 对内部渠道指令执行应有的操作
	// super.processOne(instruction);
	// }
	//
	// }

	@Override
	protected void processForConcreteBusiness(PaymentInstruction instruction) {
		logger.info("转移金钱");
		PaymentProtocol protocol = super.findProtocolByProtocolId(instruction
				.getProtocolId());
		if (protocol == null) {
			throw new RuntimeException("支付成功后的操作失败：没有找到协议");
		}
		// 但是协议状态不为processing
		if (!PaymentProtocolStatus.PROCESSING.equals(protocol.getStatus())) {
			logger.info("协议的状态是{}", protocol.getStatus());
			throw new RuntimeException("支付成功后的操作失败：协议状态不为processing");
		}
		// 转移金钱
		super.transferMoney(instruction);
		// 如果协议的金额够了，且状态位仍然是processing,就变成paid状态
		if (super.isEnoughMoneyToPaidProtocol(instruction.getProtocolId(),
				PaymentInstructionType.PAY)
				&& PaymentProtocolStatus.PROCESSING
						.equals(protocol.getStatus())) {
			super.setProtocolStatus(instruction.getProtocolId(),
					PaymentProtocolStatus.PAID);
			// 唤醒 trade
		}
	}

	public RefundDetailBO getRefundDetailBO() {
		return refundDetailBO;
	}

	public void setRefundDetailBO(RefundDetailBO refundDetailBO) {
		this.refundDetailBO = refundDetailBO;
	}

}
