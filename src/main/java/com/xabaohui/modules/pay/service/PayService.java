package com.xabaohui.modules.pay.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xabaohui.modules.pay.bean.PaymentInstruction;
import com.xabaohui.modules.pay.bean.PaymentProtocol;
import com.xabaohui.modules.pay.dto.CreateExoRcdClearingDTO;
import com.xabaohui.modules.pay.dto.CreatePaymentInstructionDTO;
import com.xabaohui.modules.pay.dto.CreatePaymentProtocolDTO;
import com.xabaohui.modules.pay.service.bo.PayServiceBO;
import com.xabaohui.modules.pay.service.bo.RechargeBo;
import com.xabaohui.modules.pay.service.bo.RefundDetailBO;
import com.xabaohui.modules.pay.service.bo.TransferAccountMoneyBO;
import com.xabaohui.modules.pay.service.bo.WithdrawBo;

@Service
public class PayService {
	private PayServiceBO payServiceBO;
	private RefundDetailBO refundDetailBO;
	private TransferAccountMoneyBO transferAccountMoneyBO;
	private RechargeBo rechargeBo;
	@Autowired
	private WithdrawBo withdrawBo;

	/**
	 * 生成支付协议
	 * 
	 * @param cppDTO
	 */
	public PaymentProtocol createPaymentProtocol(CreatePaymentProtocolDTO cppDTO) {
		return payServiceBO.createPaymentProtocol(cppDTO);
	}

	/**
	 * 支付
	 * 
	 * @param cpiDTO
	 */
	public void pay(List<CreatePaymentInstructionDTO> cpiDTOs) {
		List<PaymentInstruction> instructions = payServiceBO
				.processGetInstructions(cpiDTOs);
		payServiceBO.process(instructions);
	}

	/**
	 * 修改协议金额
	 * 
	 * @param mppDTO
	 */
	public void modifyProtocol(Integer protocolId, Double money) {
		payServiceBO.modifyProtocol(protocolId, money);
	}

	/**
	 * 确定收款
	 * 
	 * @param protocolId
	 */
	public void confirmGetMoney(int protocolId) {
		payServiceBO.confirmGetMoney(protocolId);
	}

	/**
	 * 生成转账协议
	 * 
	 * @param cppDTO
	 * @return
	 */
	public PaymentProtocol createTransferAccountMoneyProtocol(
			CreatePaymentProtocolDTO cppDTO) {
		return transferAccountMoneyBO.createPaymentProtocol(cppDTO);
	}

	/**
	 * 生成一条协议，可以支持多种渠道转账，用那种渠道，就传进来 转账
	 * 
	 * @param cpiDTO
	 */
	public void transferAccountMoney(List<CreatePaymentInstructionDTO> cpiDTOs) {
		List<PaymentInstruction> instructions = transferAccountMoneyBO
				.processGetInstructions(cpiDTOs);
		transferAccountMoneyBO.process(instructions);
	}

	/**
	 * 判断某个协议是否还有钱可退
	 * 
	 * @param protocolId
	 * @param refundMoney
	 * @return
	 */
	public boolean isEnoughRefund(int protocolId, Double refundMoney) {
		return payServiceBO.isEnoughRefund(protocolId, refundMoney);
	}

	/**
	 * 某个协议的退款
	 * 
	 * @param protocolId
	 * @param refundMoney
	 */
	public void refundMoney(int protocolId, double refundMoney) {
		// 和其他的方法的获得协议的操作不一样
		List<PaymentInstruction> instructions = refundDetailBO
				.getRefundInstructions(protocolId, refundMoney);
		refundDetailBO.process(instructions);
	}

	/**
	 * 生成充值协议
	 * 
	 * @param userId
	 * @param money
	 * @return 充值协议
	 */
	public PaymentProtocol createRechargeProtocol(Integer userId, double money) {
		return rechargeBo.createRechargeProtocol(userId, money);
	}

	/**
	 * 充值
	 * 
	 * @param cpiDTOs
	 */
	public PaymentInstruction recharge(List<CreatePaymentInstructionDTO> cpiDTOs) {
		List<PaymentInstruction> instructions = rechargeBo
				.processGetInstructions(cpiDTOs);
		rechargeBo.process(instructions);
		// 充值指令肯定只有一个
		return instructions.get(0);

	}

	/**
	 * 生成提现协议
	 * 
	 * @param userId
	 * @param money
	 * @return 提现协议
	 */
	public PaymentProtocol createWithdrawProtocol(Integer userId, double money) {
		return withdrawBo.createWithdrawProtocol(userId, money);
	}

	/**
	 * 提现
	 * 
	 * @param cpiDTOs
	 */
	public void withdraw(List<CreatePaymentInstructionDTO> cpiDTOs) {
		List<PaymentInstruction> instructions = withdrawBo
				.processGetInstructions(cpiDTOs);
		withdrawBo.process(instructions);
	}

	/**
	 * 支付生成三方业务明细并比较，然后处理
	 * 
	 * @param exoRcd
	 */
	public void processExoRcdClearingForPay(CreateExoRcdClearingDTO cercDTO) {
		payServiceBO.processExoRcdClearing(cercDTO);
	}

	/**
	 * 支付的业务的生成三方业务明细并比较，然后处理
	 * 
	 * @param exoRcd
	 */
	public void processExoRcdClearingForPay(String trade_no,
			Integer instructionId) {
		payServiceBO.processExoRcdClearing(trade_no, instructionId);
	}

	/**
	 * 充值生成三方业务明细并比较，然后处理
	 * 
	 * @param exoRcd
	 */
	public void processExoRcdClearingForRecharge(CreateExoRcdClearingDTO cercDTO) {
		rechargeBo.processExoRcdClearing(cercDTO);
	}

	/**
	 * 充值的业务的生成三方业务明细并比较，然后处理
	 * 
	 * @param exoRcd
	 */
	public void processExoRcdClearingForRecharge(String trade_no,
			Integer instructionId) {
		rechargeBo.processExoRcdClearing(trade_no, instructionId);
	}

	/**
	 * 提现生成三方业务明细并比较，然后处理
	 * 
	 * @param exoRcd
	 */
	public void processExoRcdClearingForWithDraw(CreateExoRcdClearingDTO cercDTO) {
		withdrawBo.processExoRcdClearing(cercDTO);
	}

	/**
	 * 提现的业务的生成三方业务明细并比较，然后处理
	 * 
	 * @param exoRcd
	 */
	public void processExoRcdClearingForWithDraw(String trade_no,
			Integer instructionId) {
		withdrawBo.processExoRcdClearing(trade_no, instructionId);
	}

	/**
	 * 转账生成三方业务明细并比较，然后处理
	 * 
	 * @param exoRcd
	 */
	public void processExoRcdClearingForTransferAccount(
			CreateExoRcdClearingDTO cercDTO) {
		transferAccountMoneyBO.processExoRcdClearing(cercDTO);
	}

	/**
	 * 转账的业务的生成三方业务明细并比较，然后处理
	 * 
	 * @param exoRcd
	 */
	public void processExoRcdClearingForTransferAccount(String trade_no,
			Integer instructionId) {
		transferAccountMoneyBO.processExoRcdClearing(trade_no, instructionId);
	}

	/**
	 * 退款生成三方业务明细并比较，然后处理
	 * 
	 * @param exoRcd
	 */
	public void processExoRcdClearingForRefund(CreateExoRcdClearingDTO cercDTO) {
		refundDetailBO.processExoRcdClearing(cercDTO);
	}

	/**
	 * 退款的业务的生成三方业务明细并比较，然后处理
	 * 
	 * @param exoRcd
	 */
	public void processExoRcdClearingForRefund(String trade_no,
			Integer instructionId) {
		refundDetailBO.processExoRcdClearing(trade_no, instructionId);
	}

	public PayServiceBO getPayServiceBO() {
		return payServiceBO;
	}

	public void setPayServiceBO(PayServiceBO payServiceBO) {
		this.payServiceBO = payServiceBO;
	}

	public RefundDetailBO getRefundDetailBO() {
		return refundDetailBO;
	}

	public void setRefundDetailBO(RefundDetailBO refundDetailBO) {
		this.refundDetailBO = refundDetailBO;
	}

	public TransferAccountMoneyBO getTransferAccountMoneyBO() {
		return transferAccountMoneyBO;
	}

	public void setTransferAccountMoneyBO(
			TransferAccountMoneyBO transferAccountMoneyBO) {
		this.transferAccountMoneyBO = transferAccountMoneyBO;
	}

	public RechargeBo getRechargeBo() {
		return rechargeBo;
	}

	public void setRechargeBo(RechargeBo rechargeBo) {
		this.rechargeBo = rechargeBo;
	}

}
