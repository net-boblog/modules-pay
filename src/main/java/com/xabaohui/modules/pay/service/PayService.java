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
	 * ����֧��Э��
	 * 
	 * @param cppDTO
	 */
	public PaymentProtocol createPaymentProtocol(CreatePaymentProtocolDTO cppDTO) {
		return payServiceBO.createPaymentProtocol(cppDTO);
	}

	/**
	 * ֧��
	 * 
	 * @param cpiDTO
	 */
	public void pay(List<CreatePaymentInstructionDTO> cpiDTOs) {
		List<PaymentInstruction> instructions = payServiceBO
				.processGetInstructions(cpiDTOs);
		payServiceBO.process(instructions);
	}

	/**
	 * �޸�Э����
	 * 
	 * @param mppDTO
	 */
	public void modifyProtocol(Integer protocolId, Double money) {
		payServiceBO.modifyProtocol(protocolId, money);
	}

	/**
	 * ȷ���տ�
	 * 
	 * @param protocolId
	 */
	public void confirmGetMoney(int protocolId) {
		payServiceBO.confirmGetMoney(protocolId);
	}

	/**
	 * ����ת��Э��
	 * 
	 * @param cppDTO
	 * @return
	 */
	public PaymentProtocol createTransferAccountMoneyProtocol(
			CreatePaymentProtocolDTO cppDTO) {
		return transferAccountMoneyBO.createPaymentProtocol(cppDTO);
	}

	/**
	 * ����һ��Э�飬����֧�ֶ�������ת�ˣ��������������ʹ����� ת��
	 * 
	 * @param cpiDTO
	 */
	public void transferAccountMoney(List<CreatePaymentInstructionDTO> cpiDTOs) {
		List<PaymentInstruction> instructions = transferAccountMoneyBO
				.processGetInstructions(cpiDTOs);
		transferAccountMoneyBO.process(instructions);
	}

	/**
	 * �ж�ĳ��Э���Ƿ���Ǯ����
	 * 
	 * @param protocolId
	 * @param refundMoney
	 * @return
	 */
	public boolean isEnoughRefund(int protocolId, Double refundMoney) {
		return payServiceBO.isEnoughRefund(protocolId, refundMoney);
	}

	/**
	 * ĳ��Э����˿�
	 * 
	 * @param protocolId
	 * @param refundMoney
	 */
	public void refundMoney(int protocolId, double refundMoney) {
		// �������ķ����Ļ��Э��Ĳ�����һ��
		List<PaymentInstruction> instructions = refundDetailBO
				.getRefundInstructions(protocolId, refundMoney);
		refundDetailBO.process(instructions);
	}

	/**
	 * ���ɳ�ֵЭ��
	 * 
	 * @param userId
	 * @param money
	 * @return ��ֵЭ��
	 */
	public PaymentProtocol createRechargeProtocol(Integer userId, double money) {
		return rechargeBo.createRechargeProtocol(userId, money);
	}

	/**
	 * ��ֵ
	 * 
	 * @param cpiDTOs
	 */
	public PaymentInstruction recharge(List<CreatePaymentInstructionDTO> cpiDTOs) {
		List<PaymentInstruction> instructions = rechargeBo
				.processGetInstructions(cpiDTOs);
		rechargeBo.process(instructions);
		// ��ֵָ��϶�ֻ��һ��
		return instructions.get(0);

	}

	/**
	 * ��������Э��
	 * 
	 * @param userId
	 * @param money
	 * @return ����Э��
	 */
	public PaymentProtocol createWithdrawProtocol(Integer userId, double money) {
		return withdrawBo.createWithdrawProtocol(userId, money);
	}

	/**
	 * ����
	 * 
	 * @param cpiDTOs
	 */
	public void withdraw(List<CreatePaymentInstructionDTO> cpiDTOs) {
		List<PaymentInstruction> instructions = withdrawBo
				.processGetInstructions(cpiDTOs);
		withdrawBo.process(instructions);
	}

	/**
	 * ֧����������ҵ����ϸ���Ƚϣ�Ȼ����
	 * 
	 * @param exoRcd
	 */
	public void processExoRcdClearingForPay(CreateExoRcdClearingDTO cercDTO) {
		payServiceBO.processExoRcdClearing(cercDTO);
	}

	/**
	 * ֧����ҵ�����������ҵ����ϸ���Ƚϣ�Ȼ����
	 * 
	 * @param exoRcd
	 */
	public void processExoRcdClearingForPay(String trade_no,
			Integer instructionId) {
		payServiceBO.processExoRcdClearing(trade_no, instructionId);
	}

	/**
	 * ��ֵ��������ҵ����ϸ���Ƚϣ�Ȼ����
	 * 
	 * @param exoRcd
	 */
	public void processExoRcdClearingForRecharge(CreateExoRcdClearingDTO cercDTO) {
		rechargeBo.processExoRcdClearing(cercDTO);
	}

	/**
	 * ��ֵ��ҵ�����������ҵ����ϸ���Ƚϣ�Ȼ����
	 * 
	 * @param exoRcd
	 */
	public void processExoRcdClearingForRecharge(String trade_no,
			Integer instructionId) {
		rechargeBo.processExoRcdClearing(trade_no, instructionId);
	}

	/**
	 * ������������ҵ����ϸ���Ƚϣ�Ȼ����
	 * 
	 * @param exoRcd
	 */
	public void processExoRcdClearingForWithDraw(CreateExoRcdClearingDTO cercDTO) {
		withdrawBo.processExoRcdClearing(cercDTO);
	}

	/**
	 * ���ֵ�ҵ�����������ҵ����ϸ���Ƚϣ�Ȼ����
	 * 
	 * @param exoRcd
	 */
	public void processExoRcdClearingForWithDraw(String trade_no,
			Integer instructionId) {
		withdrawBo.processExoRcdClearing(trade_no, instructionId);
	}

	/**
	 * ת����������ҵ����ϸ���Ƚϣ�Ȼ����
	 * 
	 * @param exoRcd
	 */
	public void processExoRcdClearingForTransferAccount(
			CreateExoRcdClearingDTO cercDTO) {
		transferAccountMoneyBO.processExoRcdClearing(cercDTO);
	}

	/**
	 * ת�˵�ҵ�����������ҵ����ϸ���Ƚϣ�Ȼ����
	 * 
	 * @param exoRcd
	 */
	public void processExoRcdClearingForTransferAccount(String trade_no,
			Integer instructionId) {
		transferAccountMoneyBO.processExoRcdClearing(trade_no, instructionId);
	}

	/**
	 * �˿���������ҵ����ϸ���Ƚϣ�Ȼ����
	 * 
	 * @param exoRcd
	 */
	public void processExoRcdClearingForRefund(CreateExoRcdClearingDTO cercDTO) {
		refundDetailBO.processExoRcdClearing(cercDTO);
	}

	/**
	 * �˿��ҵ�����������ҵ����ϸ���Ƚϣ�Ȼ����
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
