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
	 * ���ɳ�ֵЭ��
	 * 
	 * @param userId
	 * @param money
	 */
	public PaymentProtocol createRechargeProtocol(Integer userId, double money) {
		if (userId == null) {
			throw new RuntimeException("����userId�Ƿ���ȷ");
		}
		if (!Validation.isValidMoney(money)) {
			throw new RuntimeException("���鴫���ĳ�ֵ����Ƿ���ȷ");
		}
		// �տ���Ϊ��ֵ�û��������˲�����,����������
		CreatePaymentProtocolDTO cppDTO = new CreatePaymentProtocolDTO();
		cppDTO.setReceiverId(userId);
		cppDTO.setPayMoney(money);
		cppDTO.setType(PaymentProtocolType.RECHARGE);
		// ������ֵЭ��
		PaymentProtocol protocol = this.createPaymentProtocol(cppDTO);
		return protocol;
	}

	// /**
	// * ��ֵ
	// *
	// * @param protocolId
	// * @param channel
	// *
	// */
	// public void recharge(Integer protocolId, String channel) {
	// if (protocolId == null) {
	// throw new RuntimeException("��ֵʧ�ܣ�Э��id����");
	// }
	// PaymentProtocol protocol = super.findProtocolByProtocolId(protocolId);
	//
	// if (protocol == null) {
	// throw new RuntimeException("��ֵʧ�ܣ�Э�鲢û���ҵ�");
	// }
	// // ������Ͳ��ǳ�ֵ
	// if (!PaymentProtocolType.RECHARGE.equals(protocol.getType())) {
	// throw new RuntimeException("��ֵʧ�ܣ�Э�����Ͳ��ǳ�ֵ");
	// }
	// // �ж�֧������
	// if (PaymentInstructionChannel.BALANCE.equals(channel)) {
	// throw new RuntimeException("��ֵʧ�ܣ�֧���������������");
	// }
	// if (!PaymentProtocolStatus.INIT.equals(protocol.getStatus())) {
	// throw new RuntimeException("��ֵʧ��:ָ��ĳ�ʼ״̬��Ϊinit");
	// }
	// // ����Э��״̬Ϊprocessing
	// super.setProtocolStatus(protocol.getPaymentProtocolId(),
	// PaymentProtocolStatus.PROCESSING);
	// // ������ֵָ��
	// CreatePaymentInstructionDTO cpiDTO = new CreatePaymentInstructionDTO();
	// cpiDTO.setPayMoney(protocol.getPayMoney());
	// cpiDTO.setChannel(channel);
	// cpiDTO.setProtocolId(protocolId);
	// cpiDTO.setType(PaymentInstructionType.RECHARGE);
	// PaymentInstruction rechargeInstruction = this
	// .createPaymentInstruction(cpiDTO);
	//
	// // ����Э��״̬Ϊprocessing
	// rechargeInstruction.setStatus(PaymentInstructionStatus.PROCESSING);
	// super.updatePaymentInstruction(rechargeInstruction);
	// // ����һ��ҵ����ˮ
	// super.createOurRcdClearing(rechargeInstruction);
	// }
	//
	// /**
	// * ȷ����ֵ�ɹ�
	// *
	// * @param protocolId
	// */
	// private void confirmRecharge(Integer protocolId) {
	// if (protocolId == null) {
	// throw new RuntimeException("ȷ����ֵʧ�ܣ�Э��id����Ϊ��");
	// }
	// List<PaymentInstruction> instructions = super
	// .findInstructionsByProtocolIdAndType(protocolId,
	// PaymentInstructionType.RECHARGE);
	//
	// if (instructions == null || instructions.isEmpty()) {
	// throw new RuntimeException("ȷ����ֵʧ�ܣ�ͨ��protocoIdû���ҵ�ָ��");
	// }
	// if (instructions.size() > 1) {
	// throw new RuntimeException("ȷ����ֵʧ�ܣ�ͨ��protocoId�ҵ��˶�����ֵָ��");
	// }
	// PaymentProtocol protocol = super.findProtocolByProtocolId(protocolId);
	// // ���Э��״̬��Ϊprocessing
	// if (!PaymentProtocolStatus.PROCESSING.equals(protocol.getStatus())) {
	// logger.info("ȷ����ֵʧ�ܣ�Э��״̬Ϊ��", protocol.getStatus());
	// throw new RuntimeException("ȷ����ֵʧ�ܣ�Э��״̬��Ϊprocessing");
	// }
	// super.unfreezeChargeOrWithdraw(instructions.get(0));
	// // Э��ִ�гɹ�
	// this.setProtocolStatus(protocolId, PaymentProtocolStatus.SUCCESS);
	// }

	@Override
	protected PaymentInstruction createPaymentInstruction(
			CreatePaymentInstructionDTO cpiDTO) {
		// ���鲢�� �õ�Э��
		PaymentProtocol protocol = super
				.validateCreatePaymentInstruction(cpiDTO);
		// ��ֵʱ�տ������û�
		Account payer = accountServiceBO
				.findByAccountId(SpecialAccount.bankpay);
		Account receiver = accountServiceBO.findAccountByUserIdAndType(
				protocol.getReceiverId(), AccountType.CASH);
		if (receiver == null) {
			throw new RuntimeException("��ֵʧ�ܣ���������userID��û���ҵ��ֽ��û�");
		}
		// ���ø����ع�����
		return super.createPaymentInstruction(cpiDTO, payer.getAccountId(),
				receiver.getAccountId());
	}

	// /**
	// * ��ֵʱһ��������ҵ����ˮ�˶Գɹ�
	// */
	// @Override
	// protected void matchRcdSuccess(Integer protocolId) {
	// // ��֤������ָ��
	// List<PaymentInstruction> instructions = super
	// .validateProtocolAndReturnInstruction(protocolId,
	// PaymentInstructionType.RECHARGE);
	//
	// // �������ϣ������е�ָ��ִ�гɹ�
	// for (PaymentInstruction instruction : instructions) {
	// // TODO
	// // ������
	// this.freezeAccountMoney(instruction);
	// logger.info("ִ����{}��ָ���{}�����ɹ�",
	// instruction.getPaymentInstructionId(),
	// instruction.getType());
	// // ȷ����ֵ�ɹ����ҽⶳ
	// this.confirmRecharge(instruction.getProtocolId());
	// }
	// }

	@Override
	protected void processForConcreteBusiness(PaymentInstruction instruction) {
		// ת�ƽ�Ǯ
		PaymentProtocol protocol = super.findProtocolByProtocolId(instruction
				.getProtocolId());
		if (protocol == null) {
			throw new RuntimeException("��ֵ�˶���ˮ�ɹ���Ĳ���ʧ�ܣ�û���ҵ�Э��");
		}
		// ���Э��״̬��Ϊprocessing
		if (!PaymentProtocolStatus.PROCESSING.equals(protocol.getStatus())) {
			logger.info("ȷ����ֵʧ�ܣ�Э��״̬Ϊ��", protocol.getStatus());
			throw new RuntimeException("ȷ����ֵʧ�ܣ�Э��״̬��Ϊprocessing");
		}
		super.transferMoney(instruction);
		// Э��ִ�гɹ�
		this.setProtocolStatus(protocol.getPaymentProtocolId(),
				PaymentProtocolStatus.SUCCESS);
		logger.info("��ֵЭ�顾{}��ִ�����", protocol.getPaymentProtocolId());

	}

}
