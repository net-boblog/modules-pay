/**
 * 
 */
package com.xabaohui.modules.pay.service.bo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xabaohui.modules.pay.bean.Account;
import com.xabaohui.modules.pay.bean.PaymentInstruction;
import com.xabaohui.modules.pay.bean.PaymentProtocol;
import com.xabaohui.modules.pay.bean.SpecialAccount;
import com.xabaohui.modules.pay.bean.status.PaymentProtocolStatus;
import com.xabaohui.modules.pay.bean.type.AccountType;
import com.xabaohui.modules.pay.bean.type.PaymentProtocolType;
import com.xabaohui.modules.pay.dto.CreatePaymentInstructionDTO;
import com.xabaohui.modules.pay.dto.CreatePaymentProtocolDTO;
import com.xabaohui.modules.pay.util.Validation;

/**
 * @author YRee
 * 
 */
public class WithdrawBo extends PaymentBOu {
	protected static Logger logger = LoggerFactory.getLogger(WithdrawBo.class);

	/**
	 * 提现（创建提现协议）
	 * 
	 * @param userId
	 * @param money
	 * @return
	 */
	public PaymentProtocol createWithdrawProtocol(Integer userId, double money) {
		if (userId == null) {
			throw new RuntimeException("请检查userId是否正确");
		}
		if (!Validation.isValidMoney(money)) {
			throw new RuntimeException("请检查传来的充值金额是否正确");
		}
		// 付款人为提现用户，收款人不体现,订单不体现
		CreatePaymentProtocolDTO cppDTO = new CreatePaymentProtocolDTO();
		cppDTO.setPayerId(userId);
		cppDTO.setPayMoney(money);
		cppDTO.setType(PaymentProtocolType.WITHDRAW);
		// 创建提现协议
		PaymentProtocol protocol = this.createPaymentProtocol(cppDTO);
		return protocol;
	}

	// /**
	// * 提现操作
	// *
	// * @param protocolId
	// * @param channel
	// */
	// public void withdraw(Integer protocolId) {
	// if (protocolId == null) {
	// throw new RuntimeException("提现失败：协议id有误");
	// }
	// PaymentProtocol protocol = super.findProtocolByProtocolId(protocolId);
	//
	// if (protocol == null) {
	// throw new RuntimeException("提现失败：协议并没有找到");
	// }
	// // 如果类型不是充值
	// if (!PaymentProtocolType.WITHDRAW.equals(protocol.getType())) {
	// throw new RuntimeException("提现失败：协议类型不是提现");
	// }
	// if (!PaymentProtocolStatus.INIT.equals(protocol.getStatus())) {
	// throw new RuntimeException("提现失败:指令的初始状态不为init");
	// }
	// // 设置协议状态为processing
	// super.setProtocolStatus(protocol.getPaymentProtocolId(),
	// PaymentProtocolStatus.PROCESSING);
	// // 创提现指令
	// CreatePaymentInstructionDTO cpiDTO = new CreatePaymentInstructionDTO();
	// cpiDTO.setPayMoney(protocol.getPayMoney());
	// cpiDTO.setChannel(PaymentInstructionChannel.BALANCE);
	// cpiDTO.setProtocolId(protocolId);
	// cpiDTO.setType(PaymentInstructionType.WITHDRAW);
	//
	// PaymentInstruction withdrawInstruction = this
	// .createPaymentInstruction(cpiDTO);
	// // 充值要先冻结
	// super.freezeAccountMoney(withdrawInstruction);
	// // 设置协议状态为processing
	// withdrawInstruction.setStatus(PaymentInstructionStatus.PROCESSING);
	// super.updatePaymentInstruction(withdrawInstruction);
	// // 生成一方业务流水
	// super.createOurRcdClearing(withdrawInstruction);
	// }

	// /**
	// * 确定提现成功
	// *
	// * @param protocolId
	// */
	// private void confirmWithdraw(Integer protocolId) {
	// if (protocolId == null) {
	// throw new RuntimeException("确定提现失败：协议id不能为空");
	// }
	// List<PaymentInstruction> instructions = super
	// .findInstructionsByProtocolIdAndType(protocolId,
	// PaymentInstructionType.WITHDRAW);
	//
	// if (instructions == null || instructions.isEmpty()) {
	// throw new RuntimeException("确定提现失败：通过protocoId没有找到指令");
	// }
	// if (instructions.size() > 1) {
	// throw new RuntimeException("确定提现失败：通过protocoId找到了多条提现指令");
	// }
	// PaymentProtocol protocol = super.findProtocolByProtocolId(protocolId);
	// // 如果协议状态不为processing
	// if (!PaymentProtocolStatus.PROCESSING.equals(protocol.getStatus())) {
	// throw new RuntimeException("确定提现失败：协议状态不为processing");
	// }
	// super.unfreezeAccountMoney(instructions.get(0));
	// // 协议执行成功
	// this.setProtocolStatus(protocolId, PaymentProtocolStatus.SUCCESS);
	// }

	@Override
	protected PaymentInstruction createPaymentInstruction(
			CreatePaymentInstructionDTO cpiDTO) {
		// 检验并且 拿到协议
		PaymentProtocol protocol = super
				.validateCreatePaymentInstruction(cpiDTO);
		// 提现时支付人是用户
		Account payer = accountServiceBO.findAccountByUserIdAndType(
				protocol.getPayerId(), AccountType.CASH);
		Account receiver = accountServiceBO
				.findByAccountId(SpecialAccount.bankget);
		if (payer == null) {
			throw new RuntimeException("提现失败：您传来的userId并没有找到现金账户");
		}
		// 调用父类重构方法
		return super.createPaymentInstruction(cpiDTO, payer.getAccountId(),
				receiver.getAccountId());
	}

	//
	// /**
	// * 提现一方三方业务流水匹配成功
	// */
	// @Override
	// protected void matchRcdSuccess(Integer protocolId) {
	// // 验证并查找指令
	// List<PaymentInstruction> instructions = super
	// .validateProtocolAndReturnInstruction(protocolId,
	// PaymentInstructionType.WITHDRAW);
	//
	// // 遍历集合，让所有的指令执行成功
	// for (PaymentInstruction instruction : instructions) {
	// this.confirmWithdraw(instruction.getProtocolId());
	// logger.info("执行了{}号指令的{}操作成功",
	// instruction.getPaymentInstructionId(),
	// instruction.getType());
	// }
	// }

	@Override
	protected void processForConcreteBusiness(PaymentInstruction instruction) {
		PaymentProtocol protocol = super.findProtocolByProtocolId(instruction
				.getProtocolId());
		if (protocol == null) {
			throw new RuntimeException("提现核对流水成功后的操作失败：没有找到协议");
		}
		// 如果协议状态不为processing
		if (!PaymentProtocolStatus.PROCESSING.equals(protocol.getStatus())) {
			logger.info("协议【{}】状态为{}", protocol.getPaymentProtocolId(),
					protocol.getStatus());
			throw new RuntimeException("确定提现失败：协议状态不为processing");
		}
		// 解冻
		super.unfreezeAccountMoney(instruction);
		// 协议执行成功
		this.setProtocolStatus(protocol.getPaymentProtocolId(),
				PaymentProtocolStatus.SUCCESS);

	}
}
