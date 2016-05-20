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
public class RechargeBo extends PaymentBOu {
	protected static Logger logger = LoggerFactory.getLogger(RechargeBo.class);

	/**
	 * 生成充值协议
	 * 
	 * @param userId
	 * @param money
	 */
	public PaymentProtocol createRechargeProtocol(Integer userId, double money) {
		if (userId == null) {
			throw new RuntimeException("请检查userId是否正确");
		}
		if (!Validation.isValidMoney(money)) {
			throw new RuntimeException("请检查传来的充值金额是否正确");
		}
		// 收款人为充值用户，付款人不体现,订单不体现
		CreatePaymentProtocolDTO cppDTO = new CreatePaymentProtocolDTO();
		cppDTO.setReceiverId(userId);
		cppDTO.setPayMoney(money);
		cppDTO.setType(PaymentProtocolType.RECHARGE);
		// 创建充值协议
		PaymentProtocol protocol = this.createPaymentProtocol(cppDTO);
		return protocol;
	}

	// /**
	// * 充值
	// *
	// * @param protocolId
	// * @param channel
	// *
	// */
	// public void recharge(Integer protocolId, String channel) {
	// if (protocolId == null) {
	// throw new RuntimeException("充值失败：协议id有误");
	// }
	// PaymentProtocol protocol = super.findProtocolByProtocolId(protocolId);
	//
	// if (protocol == null) {
	// throw new RuntimeException("充值失败：协议并没有找到");
	// }
	// // 如果类型不是充值
	// if (!PaymentProtocolType.RECHARGE.equals(protocol.getType())) {
	// throw new RuntimeException("充值失败：协议类型不是充值");
	// }
	// // 判断支付渠道
	// if (PaymentInstructionChannel.BALANCE.equals(channel)) {
	// throw new RuntimeException("充值失败：支付渠道不能是余额");
	// }
	// if (!PaymentProtocolStatus.INIT.equals(protocol.getStatus())) {
	// throw new RuntimeException("充值失败:指令的初始状态不为init");
	// }
	// // 设置协议状态为processing
	// super.setProtocolStatus(protocol.getPaymentProtocolId(),
	// PaymentProtocolStatus.PROCESSING);
	// // 创建充值指令
	// CreatePaymentInstructionDTO cpiDTO = new CreatePaymentInstructionDTO();
	// cpiDTO.setPayMoney(protocol.getPayMoney());
	// cpiDTO.setChannel(channel);
	// cpiDTO.setProtocolId(protocolId);
	// cpiDTO.setType(PaymentInstructionType.RECHARGE);
	// PaymentInstruction rechargeInstruction = this
	// .createPaymentInstruction(cpiDTO);
	//
	// // 设置协议状态为processing
	// rechargeInstruction.setStatus(PaymentInstructionStatus.PROCESSING);
	// super.updatePaymentInstruction(rechargeInstruction);
	// // 生成一方业务流水
	// super.createOurRcdClearing(rechargeInstruction);
	// }
	//
	// /**
	// * 确定充值成功
	// *
	// * @param protocolId
	// */
	// private void confirmRecharge(Integer protocolId) {
	// if (protocolId == null) {
	// throw new RuntimeException("确定充值失败：协议id不能为空");
	// }
	// List<PaymentInstruction> instructions = super
	// .findInstructionsByProtocolIdAndType(protocolId,
	// PaymentInstructionType.RECHARGE);
	//
	// if (instructions == null || instructions.isEmpty()) {
	// throw new RuntimeException("确定充值失败：通过protocoId没有找到指令");
	// }
	// if (instructions.size() > 1) {
	// throw new RuntimeException("确定充值失败：通过protocoId找到了多条充值指令");
	// }
	// PaymentProtocol protocol = super.findProtocolByProtocolId(protocolId);
	// // 如果协议状态不为processing
	// if (!PaymentProtocolStatus.PROCESSING.equals(protocol.getStatus())) {
	// logger.info("确定充值失败：协议状态为：", protocol.getStatus());
	// throw new RuntimeException("确定充值失败：协议状态不为processing");
	// }
	// super.unfreezeChargeOrWithdraw(instructions.get(0));
	// // 协议执行成功
	// this.setProtocolStatus(protocolId, PaymentProtocolStatus.SUCCESS);
	// }

	@Override
	protected PaymentInstruction createPaymentInstruction(
			CreatePaymentInstructionDTO cpiDTO) {
		// 检验并且 拿到协议
		PaymentProtocol protocol = super
				.validateCreatePaymentInstruction(cpiDTO);
		// 充值时收款人是用户
		Account payer = accountServiceBO
				.findByAccountId(SpecialAccount.bankpay);
		Account receiver = accountServiceBO.findAccountByUserIdAndType(
				protocol.getReceiverId(), AccountType.CASH);
		if (receiver == null) {
			throw new RuntimeException("充值失败：您传来的userID并没有找到现金用户");
		}
		// 调用父类重构方法
		return super.createPaymentInstruction(cpiDTO, payer.getAccountId(),
				receiver.getAccountId());
	}

	// /**
	// * 充值时一方和三方业务流水核对成功
	// */
	// @Override
	// protected void matchRcdSuccess(Integer protocolId) {
	// // 验证并查找指令
	// List<PaymentInstruction> instructions = super
	// .validateProtocolAndReturnInstruction(protocolId,
	// PaymentInstructionType.RECHARGE);
	//
	// // 遍历集合，让所有的指令执行成功
	// for (PaymentInstruction instruction : instructions) {
	// // TODO
	// // 冻结金额
	// this.freezeAccountMoney(instruction);
	// logger.info("执行了{}号指令的{}操作成功",
	// instruction.getPaymentInstructionId(),
	// instruction.getType());
	// // 确定充值成功并且解冻
	// this.confirmRecharge(instruction.getProtocolId());
	// }
	// }

	@Override
	protected void processForConcreteBusiness(PaymentInstruction instruction) {
		// 转移金钱
		PaymentProtocol protocol = super.findProtocolByProtocolId(instruction
				.getProtocolId());
		if (protocol == null) {
			throw new RuntimeException("充值核对流水成功后的操作失败：没有找到协议");
		}
		// 如果协议状态不为processing
		if (!PaymentProtocolStatus.PROCESSING.equals(protocol.getStatus())) {
			logger.info("确定充值失败：协议状态为：", protocol.getStatus());
			throw new RuntimeException("确定充值失败：协议状态不为processing");
		}
		super.transferMoney(instruction);
		// 协议执行成功
		this.setProtocolStatus(protocol.getPaymentProtocolId(),
				PaymentProtocolStatus.SUCCESS);
		logger.info("充值协议【{}】执行完毕", protocol.getPaymentProtocolId());

	}

}
