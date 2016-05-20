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
	 * ����֧��Э��
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
			throw new RuntimeException("����֧��Э��ʧ�ܣ����鴴���Ĳ����Ƿ���ȷ");
		}
		// �����֧�����͵�ҵ�����orderId����
		if (protocolDao.findByOrderId(orderId) != null) {
			throw new RuntimeException("����" + orderId + "�Ѿ�������Э�飬��ȷ��");
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
	// * ���֧��
	// *
	// * @param cpiDTO
	// */
	// public void pay(List<CreatePaymentInstructionDTO> cpiDTOs) {
	// if (cpiDTOs == null || cpiDTOs.isEmpty()) {
	// throw new RuntimeException("֧��ʧ�ܣ�������ָ��Ϊ��");
	// }
	// // ����ִ�е�ָ��
	// PaymentInstruction outerChannelInstruction = null;
	// // �Ƿ���֤��Э��״̬
	// boolean isValidateProtocolStatus = false;
	// List<PaymentInstruction> instructions = new
	// ArrayList<PaymentInstruction>();
	// for (CreatePaymentInstructionDTO cpiDTO : cpiDTOs) {
	// // ������
	// PaymentProtocol protocol = this.validatePayOrTransfer(cpiDTO);
	// if (!PaymentInstructionType.PAY.equals(cpiDTO.getType())) {
	// throw new RuntimeException("֧��ʧ�ܣ�֧��ָ������Ͳ�Ϊ֧��");
	// }
	// // û����֤��Э��״̬�Ļ������Э��״̬
	// if (!isValidateProtocolStatus
	// && !PaymentProtocolStatus.INIT.equals(protocol.getStatus())) {
	// throw new RuntimeException("֧��ʧ��:ָ��ĳ�ʼ״̬��Ϊinit");
	// }
	// // û����֤��Э��״̬�Ļ�������Э��״̬Ϊprocessing��ֻ��һ�����ã�
	// if (!isValidateProtocolStatus) {
	// super.setProtocolStatus(protocol.getPaymentProtocolId(),
	// PaymentProtocolStatus.PROCESSING);
	// isValidateProtocolStatus = true;
	// }
	// // ����֧��ָ��
	// PaymentInstruction instruction = this
	// .createPaymentInstruction(cpiDTO);
	// instructions.add(instruction);
	// // ���򲢽��ⲿ֧��������ǰ
	// if (!PaymentInstructionChannel.BALANCE.equals(instruction
	// .getChannel())) {
	// outerChannelInstruction = instruction;
	// }
	// }
	// // �����ⲿ����ָ��
	// if (outerChannelInstruction != null) {
	// super.processOne(outerChannelInstruction);
	// } else {// ֻ���ڲ�����ָ��
	// for (PaymentInstruction instruction : instructions) {
	// super.processOne(instruction);
	// }
	// }
	//
	// // ÿһ��ָ������ִ��
	// logger.info("����֧��ָ�{}��",
	// outerChannelInstruction.getPaymentInstructionId());
	// }

	// /**
	// * ���֧��
	// *
	// * @param cpiDTO
	// */
	// public void pay2(List<CreatePaymentInstructionDTO> cpiDTOs) {
	// // �ж�ֻ�����֧���ı�־
	// boolean isOnlyBlance = true;
	// PaymentInstruction onlyBlanceInstruction = null;
	// if (cpiDTOs == null || cpiDTOs.isEmpty()) {
	// throw new RuntimeException("֧��ʧ�ܣ�������ָ��Ϊ��");
	// }
	// // �Ƿ���֤��Э��״̬
	// boolean isValidateProtocolStatus = false;
	// for (CreatePaymentInstructionDTO cpiDTO : cpiDTOs) {
	// // ������
	// PaymentProtocol protocol = this.validatePayOrTransfer(cpiDTO);
	// if (!PaymentInstructionType.PAY.equals(cpiDTO.getType())) {
	// logger.error("֧��ָ������Ͳ�Ϊ֧��");
	// throw new RuntimeException("֧��ʧ�ܣ�֧��ָ������Ͳ�Ϊ֧��");
	// }
	// // û����֤��Э��״̬�Ļ������Э��״̬
	// if (!isValidateProtocolStatus
	// && !PaymentProtocolStatus.INIT.equals(protocol.getStatus())) {
	// throw new RuntimeException("֧��ʧ��:ָ��ĳ�ʼ״̬��Ϊinit");
	// }
	// // û����֤��Э��״̬�Ļ�������Э��״̬Ϊprocessing��ֻ��һ�����ã�
	// if (!isValidateProtocolStatus) {
	// super.setProtocolStatus(protocol.getPaymentProtocolId(),
	// PaymentProtocolStatus.PROCESSING);
	// isValidateProtocolStatus = true;
	// }
	// // ����֧��ָ��
	// PaymentInstruction instruction = this
	// .createPaymentInstruction(cpiDTO);
	// logger.info("����֧��ָ�{}��", instruction.getPaymentInstructionId());
	// // ����Э��״̬Ϊprocessing
	// instruction.setStatus(PaymentInstructionStatus.PROCESSING);
	// super.updatePaymentInstruction(instruction);
	//
	// // ����ǵ�����֧����������ҵ����ˮ���ҵȴ�
	// if (!PaymentInstructionChannel.BALANCE.equals(instruction
	// .getChannel())) {
	// // ���ǻ��߲�ֻ�����֧����
	// isOnlyBlance = false;
	// // ������ֵ����ֵ�Ļ������صĻ��ǳ�ֵЭ���id������֧��Э���id
	// // TODO invoke outer API
	// // ֱ������һ��ҵ����ˮ
	// super.createOurRcdClearing(instruction,
	// PaymentInstructionChannel.BALANCE);
	// // ��ֵ������������һ��ҵ����ˮ
	//
	// } else {
	// // Ψһ�����֧��ָ��
	// onlyBlanceInstruction = instruction;
	// }
	// }
	// // ����ֻ��һ�����֧��ָ��
	// if (cpiDTOs.size() == 1 && isOnlyBlance) {
	// // ���֧��ֱ��ִ��
	// this.processForPay(onlyBlanceInstruction);
	//
	// }
	// }

	// /**
	// * ֧���ɹ�
	// *
	// * @param instruction
	// */
	// private void processForPay(PaymentInstruction instruction) {
	// logger.info("ת�ƽ�Ǯ");
	// // ת�ƽ�Ǯ
	// super.transferMoney(instruction);
	// PaymentProtocol protocol = super.findProtocolByProtocolId(instruction
	// .getProtocolId());
	// if (protocol == null) {
	// throw new RuntimeException("֧���ɹ���Ĳ���ʧ�ܣ�û���ҵ�Э��");
	// }
	// // ����Э��״̬��Ϊprocessing
	// if (!PaymentProtocolStatus.PROCESSING.equals(protocol.getStatus())) {
	// logger.info("Э���״̬��{}", protocol.getStatus());
	// throw new RuntimeException("֧���ɹ���Ĳ���ʧ�ܣ�Э��״̬��Ϊprocessing");
	// }
	// // ���Э��Ľ��ˣ���״̬λ��Ȼ��processing,�ͱ��paid״̬
	// if (super.isEnoughMoneyToPaidProtocol(instruction.getProtocolId(),
	// PaymentInstructionType.PAY)
	// && PaymentProtocolStatus.PROCESSING
	// .equals(protocol.getStatus())) {
	// super.setProtocolStatus(instruction.getProtocolId(),
	// PaymentProtocolStatus.PAID);
	// // ���� trade
	// }
	// }

	/**
	 * ȷ���ջ�
	 * 
	 * @param protocolId
	 */
	public void confirmGetMoney(Integer protocolId) {
		if (protocolId == null) {
			throw new RuntimeException("������Э��id����Ϊ��");
		}
		// �õ�Э��
		PaymentProtocol protocol = findProtocolByProtocolId(protocolId);
		if (protocol == null) {
			throw new RuntimeException("û���ҵ����Э�飬����");
		}
		if (PaymentProtocolStatus.SUCCESS.equals(protocol.getStatus())
				|| PaymentProtocolStatus.INIT.equals(protocol.getStatus())
				|| PaymentProtocolStatus.CLOSE.equals(protocol.getStatus())) {
			logger.info("Э���ʱ״̬����ȷ��״̬Ϊ:" + protocol.getStatus());
			throw new RuntimeException("����Э��״̬");
		}
		// ֧�����ܶ�
		double payMoney = protocol.getPayMoney();
		// �Ѿ��˹������ˮ
		List<RefundDetail> listRefundDetails = refundDetailBO
				.findRefundDetailsByProtocolId(protocolId);
		// ����ҵ����˿�Ԫ��
		if (listRefundDetails != null && !listRefundDetails.isEmpty()) {
			for (RefundDetail refundDetail : listRefundDetails) {
				payMoney = payMoney - refundDetail.getRefundMoney(); // �˿���ϼ���
			}
		}
		// ����ȷ���տ�ָ�ת�ƽ�Ǯ
		CreatePaymentInstructionDTO cpiDTO = new CreatePaymentInstructionDTO();
		cpiDTO.setChannel(PaymentInstructionChannel.BALANCE);
		cpiDTO.setProtocolId(protocolId);
		cpiDTO.setPayMoney(payMoney);
		cpiDTO.setType(PaymentInstructionType.CONFIRM);
		// ȷ���տ��ָ��
		PaymentInstruction instruction = this.createPaymentInstruction(cpiDTO);
		// �޸�ָ��״̬
		instruction.setStatus(PaymentInstructionStatus.PROCESSING);
		super.updatePaymentInstruction(instruction);
		super.transferMoney(instruction);
		// ���ȷ���տ��Ǯ����
		if (isEnoughMoneyToPaidProtocol(protocol.getPaymentProtocolId(),
				PaymentInstructionType.CONFIRM)) {
			this.setProtocolStatus(protocol.getPaymentProtocolId(),
					PaymentProtocolStatus.SUCCESS);
		}
	}

	/**
	 * ͨ��orderId ��ѯ֧��Э��id
	 * 
	 * @param orderId
	 * @return
	 */
	public PaymentProtocol findProtocolByOrderId(Integer orderId) {
		if (orderId == null) {
			throw new RuntimeException("������orderId����");
		}

		return protocolDao.findByOrderId(orderId);
	}

	/**
	 * �޸�֧��Э����
	 * 
	 * @param mppDTO
	 */
	public void modifyProtocol(Integer protocolId, double money) {
		if (protocolId == null || !Validation.isValidMoney(money)) {
			throw new RuntimeException("���鴫���������Ƿ���Ч");
		}

		// �޸�Э��
		PaymentProtocol protocol = this.findProtocolByProtocolId(protocolId);
		if (protocol == null) {
			throw new RuntimeException("������Э��id��û���ҵ�����");
		}
		// ���Э��״̬
		if (!PaymentProtocolStatus.INIT.equals(protocol.getStatus())) {
			throw new RuntimeException("Э���Ѿ������������޸�");
		}
		// �޸�Э����
		protocol.setPayMoney(money);

		protocol.setGmtModify(Time.getNow());
		protocol.setVersion(protocol.getVersion() + 1);
		protocolDao.update(protocol);
	}

	@Override
	protected PaymentInstruction createPaymentInstruction(
			CreatePaymentInstructionDTO cpiDTO) {
		// ���鲢�� �õ�Э��
		PaymentProtocol protocol = super
				.validateCreatePaymentInstruction(cpiDTO);
		logger.info("ָ�������Ϊ" + cpiDTO.getType());
		if (!PaymentInstructionType.PAY.equals(cpiDTO.getType())
				&& !PaymentInstructionType.CONFIRM.equals(cpiDTO.getType())) {
			throw new RuntimeException("ָ�����Ͳ�Ϊ֧������ȷ�ϸ������֧����Ĵ���ָ���");
		}
		Account payer = null;
		Account receiver = null;
		// ֧������
		if (PaymentInstructionType.PAY.equals(cpiDTO.getType())) {
			// ֧����������
			payer = accountServiceBO.findAccountByUserIdAndType(
					protocol.getPayerId(), AccountType.CASH);
			// �տ����ǵ�����
			receiver = accountServiceBO
					.findByAccountId(SpecialAccount.guaranteeId);
		}
		if (PaymentInstructionType.CONFIRM.equals(cpiDTO.getType())) {

			// ֧�����ǵ�����
			payer = accountServiceBO
					.findByAccountId(SpecialAccount.guaranteeId);
			// �տ���������
			receiver = accountServiceBO.findAccountByUserIdAndType(
					protocol.getReceiverId(), AccountType.CASH);
		}
		logger.info("payerId��{}��,receiverId��{}��,ָ��׽��{},��������Ϊ��{}��",
				payer.getAccountId(), receiver.getAccountId(),
				cpiDTO.getPayMoney(), cpiDTO.getChannel());

		// ���ø����ع�����
		return super.createPaymentInstruction(cpiDTO, payer.getAccountId(),
				receiver.getAccountId());
	}

	// @Override
	// protected void matchRcdSuccess(Integer protocolId) {
	// // ��֤������ָ��
	// List<PaymentInstruction> instructions = super
	// .validateProtocolAndReturnInstruction(protocolId,
	// PaymentInstructionType.PAY);
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
	//
	// }

	@Override
	protected void processForConcreteBusiness(PaymentInstruction instruction) {
		logger.info("ת�ƽ�Ǯ");
		PaymentProtocol protocol = super.findProtocolByProtocolId(instruction
				.getProtocolId());
		if (protocol == null) {
			throw new RuntimeException("֧���ɹ���Ĳ���ʧ�ܣ�û���ҵ�Э��");
		}
		// ����Э��״̬��Ϊprocessing
		if (!PaymentProtocolStatus.PROCESSING.equals(protocol.getStatus())) {
			logger.info("Э���״̬��{}", protocol.getStatus());
			throw new RuntimeException("֧���ɹ���Ĳ���ʧ�ܣ�Э��״̬��Ϊprocessing");
		}
		// ת�ƽ�Ǯ
		super.transferMoney(instruction);
		// ���Э��Ľ��ˣ���״̬λ��Ȼ��processing,�ͱ��paid״̬
		if (super.isEnoughMoneyToPaidProtocol(instruction.getProtocolId(),
				PaymentInstructionType.PAY)
				&& PaymentProtocolStatus.PROCESSING
						.equals(protocol.getStatus())) {
			super.setProtocolStatus(instruction.getProtocolId(),
					PaymentProtocolStatus.PAID);
			// ���� trade
		}
	}

	public RefundDetailBO getRefundDetailBO() {
		return refundDetailBO;
	}

	public void setRefundDetailBO(RefundDetailBO refundDetailBO) {
		this.refundDetailBO = refundDetailBO;
	}

}
