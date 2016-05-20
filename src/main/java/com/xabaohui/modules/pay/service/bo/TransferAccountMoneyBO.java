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
 * ת����
 * 
 * @author YRee
 * 
 */
public class TransferAccountMoneyBO extends PaymentBOu {
	protected static Logger logger = LoggerFactory
			.getLogger(TransferAccountMoneyBO.class);

	/**
	 * ����ת��Э��
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
			throw new RuntimeException("����֧��Э��ʧ�ܣ����鴴���Ĳ����Ƿ���ȷ");
		}

		CreatePaymentProtocolDTO cppDTO = new CreatePaymentProtocolDTO();
		cppDTO.setPayerId(payerId);
		cppDTO.setReceiverId(receiverId);
		cppDTO.setPayMoney(money);
		cppDTO.setType(PaymentProtocolType.TRANSFER);
		return this.createPaymentProtocol(cppDTO);
	}

	// /**
	// * ת��׼�� ��������е�������ת�ˣ���ô���ȳ�ֵ�������س�ֵָ��id
	// *
	// * @param cpiDTO
	// * @param receiverChannel
	// */
	// public Integer transferPrepare(CreatePaymentInstructionDTO cpiDTO,
	// String receiverChannel) {
	// PaymentProtocol protocol = this.validatePayOrTransfer(cpiDTO);
	//
	// if (!PaymentProtocolStatus.INIT.equals(protocol.getStatus())) {
	// throw new RuntimeException("ת��׼��ʧ��:ָ��ĳ�ʼ״̬��Ϊinit");
	// }
	// // ����Э��״̬Ϊprocessing
	// super.setProtocolStatus(protocol.getPaymentProtocolId(),
	// PaymentProtocolStatus.PROCESSING);
	//
	// // ����Ǵ�����ת�˹����Ļ���Ӧ���ȳ�ֵ����ת��
	// if (!PaymentInstructionChannel.BALANCE.equals(cpiDTO.getChannel())) {
	// // ������ֵЭ��
	// PaymentProtocol rechargeProtocol = rechargeBo
	// .createRechargeProtocol(protocol.getPayerId(),
	// cpiDTO.getPayMoney());
	// // ��ֵ������������һ��ҵ����ˮ
	// rechargeBo.recharge(rechargeProtocol.getPaymentProtocolId(),
	// cpiDTO.getChannel());
	//
	// // ��ѯ�������ɵ�ָ��
	// List<PaymentInstruction> rechargeInstructions = super
	// .findInstructionsByProtocolIdAndType(
	// rechargeProtocol.getPaymentProtocolId(),
	// PaymentInstructionType.RECHARGE);
	// if (rechargeInstructions.isEmpty()) {
	// throw new RuntimeException("ת��׼��ʧ�ܣ����ɵĳ�ֵָ��Ϊ��");
	// }
	// if (rechargeInstructions.size() > 1) {
	// throw new RuntimeException("ת��׼��ʧ�ܣ����ɵĳ�ֵָ������һ��");
	// }
	// PaymentInstruction rechargeInstruction = rechargeInstructions
	// .get(0);
	// return rechargeInstruction.getPaymentInstructionId();
	// }
	// return null;
	// }

	// /**
	// * ����֧��ָ���ת��
	// *
	// * @param cpiDTO
	// */
	// public void transferAccountMoney(List<CreatePaymentInstructionDTO>
	// cpiDTOs,
	// String receiverChannel) {
	// // �ж�ֻ�����֧���ı�־
	// boolean isOnlyBlance = true;
	// PaymentInstruction onlyBlanceInstruction = null;
	// if (cpiDTOs == null || cpiDTOs.isEmpty()) {
	// throw new RuntimeException("ת��ʧ�ܣ�������ָ��Ϊ��");
	// }
	// // �Ƿ���֤��Э��״̬
	// boolean isValidateProtocolStatus = false;
	// for (CreatePaymentInstructionDTO cpiDTO : cpiDTOs) {
	// // ��֤��ȡ��Э��
	// PaymentProtocol protocol = this.validatePayOrTransfer(cpiDTO);
	// if (!isValidateProtocolStatus
	// && !PaymentProtocolStatus.INIT.equals(protocol.getStatus())) {
	// throw new RuntimeException("ת��ʧ��:ָ��ĳ�ʼ״̬��Ϊinit");
	// }
	// // û����֤��Э��״̬�Ļ�������Э��״̬Ϊprocessing��ֻ��һ�����ã�
	// if (!isValidateProtocolStatus) {
	// super.setProtocolStatus(protocol.getPaymentProtocolId(),
	// PaymentProtocolStatus.PROCESSING);
	// isValidateProtocolStatus = true;
	// }
	// if (!PaymentInstructionType.TRANSFER.equals(cpiDTO.getType())) {
	// throw new RuntimeException("ת��ʧ�ܣ�֧��ָ������Ͳ�Ϊת��");
	// }
	//
	// // ����֧��ָ��
	// PaymentInstruction instruction = this
	// .createPaymentInstruction(cpiDTO);
	// // ����ָ��״̬Ϊprocessing
	// instruction.setStatus(PaymentInstructionStatus.PROCESSING);
	// this.updatePaymentInstruction(instruction);
	// // ����ǵ����������� ������ָ��ȴ�
	// // ����ǵ�����֧����������ҵ����ˮ���ҵȴ�
	// if (!PaymentInstructionChannel.BALANCE.equals(instruction
	// .getChannel())) {
	// // ���ǻ��߲�ֻ�����֧����
	// isOnlyBlance = false;
	// // ֱ������һ��ҵ����ˮ
	// super.createOurRcdClearing(instruction);
	// // ��ֵ������������һ��ҵ����ˮ
	// } else {
	// // Ψһ�����֧��ָ��
	// onlyBlanceInstruction = instruction;
	// }
	// // ����ֻ��һ�����֧��ָ��
	// if (cpiDTOs.size() == 1 && isOnlyBlance) {
	// // ���ת��ֱ��ִ�У�����Ҫ�޸�״̬
	// this.transferAccountMoneySuccess(onlyBlanceInstruction, true);
	// }
	// }
	// }

	/**
	 * ת�˳ɹ�
	 * 
	 * @param instruction
	 * @param isChengeStatus
	 * 
	 */
	// private void transferAccountMoneySuccess(PaymentInstruction instruction)
	// {
	// // ת�ƽ�Ǯ
	// super.transferMoney(instruction);
	// PaymentProtocol protocol = super.findProtocolByProtocolId(instruction
	// .getProtocolId());
	// if (protocol == null) {
	// throw new RuntimeException("ת�˳ɹ���Ĳ���ʧ�ܣ�û���ҵ�Э��");
	// }
	// // ��Ҫ�޸�״̬������Э��״̬��Ϊprocessing
	// if (PaymentProtocolStatus.PROCESSING.equals(protocol.getStatus())) {
	// logger.info("Э���״̬��{}", protocol.getStatus());
	// throw new RuntimeException("ת�˳ɹ���Ĳ���ʧ�ܣ�Э��״̬��Ϊprocessing");
	// }
	//
	// // ���Э��Ľ��ˣ���Э��״̬λ��Ȼ��processing,�ͱ��paid״̬
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
	 * ����ת��ָ��
	 */
	@Override
	protected PaymentInstruction createPaymentInstruction(
			CreatePaymentInstructionDTO cpiDTO) {
		// ���鲢�� �õ�Э��
		PaymentProtocol protocol = super
				.validateCreatePaymentInstruction(cpiDTO);

		if (!PaymentInstructionType.TRANSFER.equals(cpiDTO.getType())) {
			throw new RuntimeException("ָ�����Ͳ�Ϊת�˲�����ת����Ĵ���ָ���");
		}
		Account payer = accountServiceBO.findAccountByUserIdAndType(
				protocol.getPayerId(), AccountType.CASH);
		Account receiver = accountServiceBO.findAccountByUserIdAndType(
				protocol.getReceiverId(), AccountType.CASH);

		// ���ø����ع�����
		return super.createPaymentInstruction(cpiDTO, payer.getAccountId(),
				receiver.getAccountId());
	}

	// @Override
	// protected void matchRcdSuccess(Integer protocolId) {
	// // ��֤������ָ��
	// List<PaymentInstruction> instructions = super
	// .validateProtocolAndReturnInstruction(protocolId,
	// PaymentInstructionType.TRANSFER);
	//
	// for (PaymentInstruction instruction : instructions) {
	// // ������֧�������Ѿ�֧���ɹ��ľ�����
	// if (!PaymentInstructionChannel.isInnerChannel(instruction
	// .getChannel())
	// || PaymentInstructionStatus.SUCCESS.equals(instruction
	// .getStatus())) {
	// continue;
	// }
	// // ���ڲ�����ָ��ִ��Ӧ�еĲ���
	// super.processOne(instruction);
	// }
	// }

	@Override
	protected void processForConcreteBusiness(PaymentInstruction instruction) {
		PaymentProtocol protocol = super.findProtocolByProtocolId(instruction
				.getProtocolId());
		if (protocol == null) {
			throw new RuntimeException("ת�˺˶���ˮ�ɹ���Ĳ���ʧ�ܣ�û���ҵ�Э��");
		}
		// ��Ҫ�޸�״̬������Э��״̬��Ϊprocessing
		if (!PaymentProtocolStatus.PROCESSING.equals(protocol.getStatus())) {
			logger.info("Э���״̬��{}", protocol.getStatus());
			throw new RuntimeException("ת�˺˶���ˮ�ɹ���Ĳ���ʧ�ܣ�Э��״̬��Ϊprocessing");
		}
		// ת�ƽ�Ǯ
		super.transferMoney(instruction);

		// ���Э��Ľ��ˣ���Э��״̬λ��Ȼ��processing,�ͱ��paid״̬
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
