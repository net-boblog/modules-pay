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
	 * ���֣���������Э�飩
	 * 
	 * @param userId
	 * @param money
	 * @return
	 */
	public PaymentProtocol createWithdrawProtocol(Integer userId, double money) {
		if (userId == null) {
			throw new RuntimeException("����userId�Ƿ���ȷ");
		}
		if (!Validation.isValidMoney(money)) {
			throw new RuntimeException("���鴫���ĳ�ֵ����Ƿ���ȷ");
		}
		// ������Ϊ�����û����տ��˲�����,����������
		CreatePaymentProtocolDTO cppDTO = new CreatePaymentProtocolDTO();
		cppDTO.setPayerId(userId);
		cppDTO.setPayMoney(money);
		cppDTO.setType(PaymentProtocolType.WITHDRAW);
		// ��������Э��
		PaymentProtocol protocol = this.createPaymentProtocol(cppDTO);
		return protocol;
	}

	// /**
	// * ���ֲ���
	// *
	// * @param protocolId
	// * @param channel
	// */
	// public void withdraw(Integer protocolId) {
	// if (protocolId == null) {
	// throw new RuntimeException("����ʧ�ܣ�Э��id����");
	// }
	// PaymentProtocol protocol = super.findProtocolByProtocolId(protocolId);
	//
	// if (protocol == null) {
	// throw new RuntimeException("����ʧ�ܣ�Э�鲢û���ҵ�");
	// }
	// // ������Ͳ��ǳ�ֵ
	// if (!PaymentProtocolType.WITHDRAW.equals(protocol.getType())) {
	// throw new RuntimeException("����ʧ�ܣ�Э�����Ͳ�������");
	// }
	// if (!PaymentProtocolStatus.INIT.equals(protocol.getStatus())) {
	// throw new RuntimeException("����ʧ��:ָ��ĳ�ʼ״̬��Ϊinit");
	// }
	// // ����Э��״̬Ϊprocessing
	// super.setProtocolStatus(protocol.getPaymentProtocolId(),
	// PaymentProtocolStatus.PROCESSING);
	// // ������ָ��
	// CreatePaymentInstructionDTO cpiDTO = new CreatePaymentInstructionDTO();
	// cpiDTO.setPayMoney(protocol.getPayMoney());
	// cpiDTO.setChannel(PaymentInstructionChannel.BALANCE);
	// cpiDTO.setProtocolId(protocolId);
	// cpiDTO.setType(PaymentInstructionType.WITHDRAW);
	//
	// PaymentInstruction withdrawInstruction = this
	// .createPaymentInstruction(cpiDTO);
	// // ��ֵҪ�ȶ���
	// super.freezeAccountMoney(withdrawInstruction);
	// // ����Э��״̬Ϊprocessing
	// withdrawInstruction.setStatus(PaymentInstructionStatus.PROCESSING);
	// super.updatePaymentInstruction(withdrawInstruction);
	// // ����һ��ҵ����ˮ
	// super.createOurRcdClearing(withdrawInstruction);
	// }

	// /**
	// * ȷ�����ֳɹ�
	// *
	// * @param protocolId
	// */
	// private void confirmWithdraw(Integer protocolId) {
	// if (protocolId == null) {
	// throw new RuntimeException("ȷ������ʧ�ܣ�Э��id����Ϊ��");
	// }
	// List<PaymentInstruction> instructions = super
	// .findInstructionsByProtocolIdAndType(protocolId,
	// PaymentInstructionType.WITHDRAW);
	//
	// if (instructions == null || instructions.isEmpty()) {
	// throw new RuntimeException("ȷ������ʧ�ܣ�ͨ��protocoIdû���ҵ�ָ��");
	// }
	// if (instructions.size() > 1) {
	// throw new RuntimeException("ȷ������ʧ�ܣ�ͨ��protocoId�ҵ��˶�������ָ��");
	// }
	// PaymentProtocol protocol = super.findProtocolByProtocolId(protocolId);
	// // ���Э��״̬��Ϊprocessing
	// if (!PaymentProtocolStatus.PROCESSING.equals(protocol.getStatus())) {
	// throw new RuntimeException("ȷ������ʧ�ܣ�Э��״̬��Ϊprocessing");
	// }
	// super.unfreezeAccountMoney(instructions.get(0));
	// // Э��ִ�гɹ�
	// this.setProtocolStatus(protocolId, PaymentProtocolStatus.SUCCESS);
	// }

	@Override
	protected PaymentInstruction createPaymentInstruction(
			CreatePaymentInstructionDTO cpiDTO) {
		// ���鲢�� �õ�Э��
		PaymentProtocol protocol = super
				.validateCreatePaymentInstruction(cpiDTO);
		// ����ʱ֧�������û�
		Account payer = accountServiceBO.findAccountByUserIdAndType(
				protocol.getPayerId(), AccountType.CASH);
		Account receiver = accountServiceBO
				.findByAccountId(SpecialAccount.bankget);
		if (payer == null) {
			throw new RuntimeException("����ʧ�ܣ���������userId��û���ҵ��ֽ��˻�");
		}
		// ���ø����ع�����
		return super.createPaymentInstruction(cpiDTO, payer.getAccountId(),
				receiver.getAccountId());
	}

	//
	// /**
	// * ����һ������ҵ����ˮƥ��ɹ�
	// */
	// @Override
	// protected void matchRcdSuccess(Integer protocolId) {
	// // ��֤������ָ��
	// List<PaymentInstruction> instructions = super
	// .validateProtocolAndReturnInstruction(protocolId,
	// PaymentInstructionType.WITHDRAW);
	//
	// // �������ϣ������е�ָ��ִ�гɹ�
	// for (PaymentInstruction instruction : instructions) {
	// this.confirmWithdraw(instruction.getProtocolId());
	// logger.info("ִ����{}��ָ���{}�����ɹ�",
	// instruction.getPaymentInstructionId(),
	// instruction.getType());
	// }
	// }

	@Override
	protected void processForConcreteBusiness(PaymentInstruction instruction) {
		PaymentProtocol protocol = super.findProtocolByProtocolId(instruction
				.getProtocolId());
		if (protocol == null) {
			throw new RuntimeException("���ֺ˶���ˮ�ɹ���Ĳ���ʧ�ܣ�û���ҵ�Э��");
		}
		// ���Э��״̬��Ϊprocessing
		if (!PaymentProtocolStatus.PROCESSING.equals(protocol.getStatus())) {
			logger.info("Э�顾{}��״̬Ϊ{}", protocol.getPaymentProtocolId(),
					protocol.getStatus());
			throw new RuntimeException("ȷ������ʧ�ܣ�Э��״̬��Ϊprocessing");
		}
		// �ⶳ
		super.unfreezeAccountMoney(instruction);
		// Э��ִ�гɹ�
		this.setProtocolStatus(protocol.getPaymentProtocolId(),
				PaymentProtocolStatus.SUCCESS);

	}
}
