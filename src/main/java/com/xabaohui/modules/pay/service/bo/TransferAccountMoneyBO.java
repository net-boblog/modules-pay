/**
 * 
 */
package com.xabaohui.modules.pay.service.bo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xabaohui.modules.pay.bean.Account;
import com.xabaohui.modules.pay.bean.PaymentInstruction;
import com.xabaohui.modules.pay.bean.PaymentProtocol;
import com.xabaohui.modules.pay.bean.status.PaymentProtocolStatus;
import com.xabaohui.modules.pay.bean.type.AccountType;
import com.xabaohui.modules.pay.bean.type.PaymentInstructionType;
import com.xabaohui.modules.pay.bean.type.PaymentProtocolType;
import com.xabaohui.modules.pay.dto.CreatePaymentInstructionDTO;
import com.xabaohui.modules.pay.dto.CreatePaymentProtocolDTO;
import com.xabaohui.modules.pay.util.Validation;

/**
 * 转账类
 * 
 * @author YRee
 * 
 */
public class TransferAccountMoneyBO extends PaymentBOu {
	protected static Logger logger = LoggerFactory
			.getLogger(TransferAccountMoneyBO.class);

	/**
	 * 创建转账协议
	 * 
	 * @param payerId
	 * @param receiverId
	 * @param money
	 * @return
	 */
	public PaymentProtocol createTransferAccountMoneyProtocol(Integer payerId,
			Integer receiverId, double money) {

		if (payerId == null || receiverId == null
				|| !Validation.isValidMoney(money)) {
			throw new RuntimeException("创建支付协议失败，请检查创建的参数是否正确");
		}

		CreatePaymentProtocolDTO cppDTO = new CreatePaymentProtocolDTO();
		cppDTO.setPayerId(payerId);
		cppDTO.setReceiverId(receiverId);
		cppDTO.setPayMoney(money);
		cppDTO.setType(PaymentProtocolType.TRANSFER);
		return this.createPaymentProtocol(cppDTO);
	}

	// /**
	// * 转账准备 如果包含有第三方的转账，那么就先充值，并返回充值指令id
	// *
	// * @param cpiDTO
	// * @param receiverChannel
	// */
	// public Integer transferPrepare(CreatePaymentInstructionDTO cpiDTO,
	// String receiverChannel) {
	// PaymentProtocol protocol = this.validatePayOrTransfer(cpiDTO);
	//
	// if (!PaymentProtocolStatus.INIT.equals(protocol.getStatus())) {
	// throw new RuntimeException("转账准备失败:指令的初始状态不为init");
	// }
	// // 设置协议状态为processing
	// super.setProtocolStatus(protocol.getPaymentProtocolId(),
	// PaymentProtocolStatus.PROCESSING);
	//
	// // 如果是从三方转账过来的话，应该先充值，在转账
	// if (!PaymentInstructionChannel.BALANCE.equals(cpiDTO.getChannel())) {
	// // 创建充值协议
	// PaymentProtocol rechargeProtocol = rechargeBo
	// .createRechargeProtocol(protocol.getPayerId(),
	// cpiDTO.getPayMoney());
	// // 充值操作，生成了一方业务流水
	// rechargeBo.recharge(rechargeProtocol.getPaymentProtocolId(),
	// cpiDTO.getChannel());
	//
	// // 查询出新生成的指令
	// List<PaymentInstruction> rechargeInstructions = super
	// .findInstructionsByProtocolIdAndType(
	// rechargeProtocol.getPaymentProtocolId(),
	// PaymentInstructionType.RECHARGE);
	// if (rechargeInstructions.isEmpty()) {
	// throw new RuntimeException("转账准备失败：生成的充值指令为空");
	// }
	// if (rechargeInstructions.size() > 1) {
	// throw new RuntimeException("转账准备失败：生成的充值指大于了一条");
	// }
	// PaymentInstruction rechargeInstruction = rechargeInstructions
	// .get(0);
	// return rechargeInstruction.getPaymentInstructionId();
	// }
	// return null;
	// }

	// /**
	// * 创建支付指令并且转账
	// *
	// * @param cpiDTO
	// */
	// public void transferAccountMoney(List<CreatePaymentInstructionDTO>
	// cpiDTOs,
	// String receiverChannel) {
	// // 判断只有余额支付的标志
	// boolean isOnlyBlance = true;
	// PaymentInstruction onlyBlanceInstruction = null;
	// if (cpiDTOs == null || cpiDTOs.isEmpty()) {
	// throw new RuntimeException("转账失败：传来的指令为空");
	// }
	// // 是否验证过协议状态
	// boolean isValidateProtocolStatus = false;
	// for (CreatePaymentInstructionDTO cpiDTO : cpiDTOs) {
	// // 验证并取到协议
	// PaymentProtocol protocol = this.validatePayOrTransfer(cpiDTO);
	// if (!isValidateProtocolStatus
	// && !PaymentProtocolStatus.INIT.equals(protocol.getStatus())) {
	// throw new RuntimeException("转账失败:指令的初始状态不为init");
	// }
	// // 没有验证过协议状态的话，设置协议状态为processing（只第一次设置）
	// if (!isValidateProtocolStatus) {
	// super.setProtocolStatus(protocol.getPaymentProtocolId(),
	// PaymentProtocolStatus.PROCESSING);
	// isValidateProtocolStatus = true;
	// }
	// if (!PaymentInstructionType.TRANSFER.equals(cpiDTO.getType())) {
	// throw new RuntimeException("转账失败：支付指令的类型不为转账");
	// }
	//
	// // 生成支付指令
	// PaymentInstruction instruction = this
	// .createPaymentInstruction(cpiDTO);
	// // 设置指令状态为processing
	// instruction.setStatus(PaymentInstructionStatus.PROCESSING);
	// this.updatePaymentInstruction(instruction);
	// // 如果是第三方渠道， 就生成指令并等待
	// // 如果是第三方支付，就生成业务流水并且等待
	// if (!PaymentInstructionChannel.BALANCE.equals(instruction
	// .getChannel())) {
	// // 不是或者不只是余额支付了
	// isOnlyBlance = false;
	// // 直接生成一方业务流水
	// super.createOurRcdClearing(instruction);
	// // 充值操作，生成了一方业务流水
	// } else {
	// // 唯一的余额支付指令
	// onlyBlanceInstruction = instruction;
	// }
	// // 有且只有一条余额支付指令
	// if (cpiDTOs.size() == 1 && isOnlyBlance) {
	// // 余额转账直接执行，且需要修改状态
	// this.transferAccountMoneySuccess(onlyBlanceInstruction, true);
	// }
	// }
	// }

	/**
	 * 转账成功
	 * 
	 * @param instruction
	 * @param isChengeStatus
	 * 
	 */
	// private void transferAccountMoneySuccess(PaymentInstruction instruction)
	// {
	// // 转移金钱
	// super.transferMoney(instruction);
	// PaymentProtocol protocol = super.findProtocolByProtocolId(instruction
	// .getProtocolId());
	// if (protocol == null) {
	// throw new RuntimeException("转账成功后的操作失败：没有找到协议");
	// }
	// // 需要修改状态，但是协议状态不为processing
	// if (PaymentProtocolStatus.PROCESSING.equals(protocol.getStatus())) {
	// logger.info("协议的状态是{}", protocol.getStatus());
	// throw new RuntimeException("转账成功后的操作失败：协议状态不为processing");
	// }
	//
	// // 如果协议的金额够了，且协议状态位仍然是processing,就变成paid状态
	// if (super.isEnoughMoneyToPaidProtocol(instruction.getProtocolId(),
	// PaymentInstructionType.TRANSFER)
	// && PaymentProtocolStatus.PROCESSING
	// .equals(protocol.getStatus())) {
	// super.setProtocolStatus(instruction.getProtocolId(),
	// PaymentProtocolStatus.SUCCESS);
	// // TODO trade
	// }
	// }

	/**
	 * 创建转账指令
	 */
	@Override
	protected PaymentInstruction createPaymentInstruction(
			CreatePaymentInstructionDTO cpiDTO) {
		// 检验并且 拿到协议
		PaymentProtocol protocol = super
				.validateCreatePaymentInstruction(cpiDTO);

		if (!PaymentInstructionType.TRANSFER.equals(cpiDTO.getType())) {
			throw new RuntimeException("指令类型不为转账不能用转账里的创建指令方法");
		}
		Account payer = accountServiceBO.findAccountByUserIdAndType(
				protocol.getPayerId(), AccountType.CASH);
		Account receiver = accountServiceBO.findAccountByUserIdAndType(
				protocol.getReceiverId(), AccountType.CASH);

		// 调用父类重构方法
		return super.createPaymentInstruction(cpiDTO, payer.getAccountId(),
				receiver.getAccountId());
	}

	// @Override
	// protected void matchRcdSuccess(Integer protocolId) {
	// // 验证并查找指令
	// List<PaymentInstruction> instructions = super
	// .validateProtocolAndReturnInstruction(protocolId,
	// PaymentInstructionType.TRANSFER);
	//
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
	// }

	@Override
	protected void processForConcreteBusiness(PaymentInstruction instruction) {
		PaymentProtocol protocol = super.findProtocolByProtocolId(instruction
				.getProtocolId());
		if (protocol == null) {
			throw new RuntimeException("转账核对流水成功后的操作失败：没有找到协议");
		}
		// 需要修改状态，但是协议状态不为processing
		if (!PaymentProtocolStatus.PROCESSING.equals(protocol.getStatus())) {
			logger.info("协议的状态是{}", protocol.getStatus());
			throw new RuntimeException("转账核对流水成功后的操作失败：协议状态不为processing");
		}
		// 转移金钱
		super.transferMoney(instruction);

		// 如果协议的金额够了，且协议状态位仍然是processing,就变成paid状态
		if (super.isEnoughMoneyToPaidProtocol(instruction.getProtocolId(),
				PaymentInstructionType.TRANSFER)
				&& PaymentProtocolStatus.PROCESSING
						.equals(protocol.getStatus())) {
			super.setProtocolStatus(instruction.getProtocolId(),
					PaymentProtocolStatus.SUCCESS);
			// TODO trade
		}
	}
}
