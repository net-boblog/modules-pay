/**
 * 
 */
package com.xabaohui.modules.pay.service.bo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.xabaohui.modules.pay.bean.Account;
import com.xabaohui.modules.pay.bean.PaymentInstruction;
import com.xabaohui.modules.pay.bean.PaymentProtocol;
import com.xabaohui.modules.pay.bean.RefundDetail;
import com.xabaohui.modules.pay.bean.SpecialAccount;
import com.xabaohui.modules.pay.bean.channel.PaymentInstructionChannel;
import com.xabaohui.modules.pay.bean.status.PaymentInstructionStatus;
import com.xabaohui.modules.pay.bean.status.PaymentProtocolStatus;
import com.xabaohui.modules.pay.bean.status.RefundDetailStatus;
import com.xabaohui.modules.pay.bean.type.AccountType;
import com.xabaohui.modules.pay.bean.type.PaymentInstructionType;
import com.xabaohui.modules.pay.bean.type.PaymentProtocolType;
import com.xabaohui.modules.pay.dto.CreatePaymentInstructionDTO;
import com.xabaohui.modules.pay.util.Time;
import com.xabaohui.modules.pay.util.Validation;

/**
 * @author YRee
 * 
 */
public class RefundDetailBO extends PaymentBOu {

	public List<PaymentInstruction> getRefundInstructions(int protocolId,
			double refundMoney) {
		// ��֤
		this.validateRefundMoney(protocolId, refundMoney);
		// �����˿���ϸ
		RefundDetail refundDetail = this.createRefundDetail(protocolId,
				refundMoney);
		// �����˿�ָ��
		List<PaymentInstruction> listRefundInstruction = this
				.createRefundInstruction(refundDetail.getRefundDetailId());
		return listRefundInstruction;
	}

	// /**
	// * �˿�
	// *
	// * @param rppDTO
	// */
	// public void refundMoney(int protocolId, double refundMoney) {
	// // ��֤
	// PaymentProtocol protocol = this.validateRefundMoney(protocolId,
	// refundMoney);
	// // �����˿���ϸ
	// RefundDetail refundDetail = this.createRefundDetail(protocolId,
	// refundMoney);
	// // �����˿�ָ��
	// List<PaymentInstruction> listRefundInstruction = this
	// .createRefundInstruction(refundDetail.getRefundDetailId());
	// // �Ƿ�ȫ�����˿���
	// boolean isAllBlanceRefund = true;
	// // �Ѿ��˿���
	// double hadRefundMoney = 0.0;
	// // ת�ƽ��
	// for (PaymentInstruction refundInstruction : listRefundInstruction) {
	// // ����Э��״̬Ϊprocessing
	// refundInstruction.setStatus(PaymentInstructionStatus.PROCESSING);
	// super.updatePaymentInstruction(refundInstruction);
	// // �Ѿ��˿�Ľ��
	// hadRefundMoney += refundInstruction.getPayMoney();
	//
	// // ������˿���
	// if (PaymentInstructionChannel.BALANCE.equals(refundInstruction
	// .getChannel())) {
	// // ���˿�ָ��״̬λΪprocessing��ʱ���ת�ƽ���ʼ���������Ѿ��رյĻ��Ͳ�����
	// if (PaymentInstructionStatus.PROCESSING
	// .equals(refundInstruction.getStatus())) {
	// logger.info("ת�ƽ��");
	// this.transferMoney(refundInstruction);
	// }
	// // �����ô�ѭ�����������±ߵ�
	// continue;
	// }
	//
	// // �����������˿�
	// isAllBlanceRefund = false;
	// // ������
	// super.freezeAccountMoney(refundInstruction);
	//
	// // ����һ��ҵ����ˮ,�˿����������⴦�������ŵ����տ������
	// this.createOurRcdClearing(refundInstruction);
	// }
	//
	// // ȫ��ָ�Ϊ�����������Ҫ���˿�ɹ���
	// if (isAllBlanceRefund) {
	// // �˿�ɹ�
	// this.successRefundDetail(refundDetail.getRefundDetailId());
	// //
	// }
	//
	// // ��������Լ������˽��׽��ر�Э��
	// logger.info("���˿��" + hadRefundMoney);
	// logger.info("���׽��" + protocol.getPayMoney());
	// if (hadRefundMoney == protocol.getPayMoney()) {
	// logger.info("�˿�Ľ���Ѿ�������Э����ر�Э��");
	// this.setProtocolStatus(protocolId, PaymentProtocolStatus.CLOSE);
	//
	// }
	//
	// }

	/**
	 * �ж��Ƿ���Ϊ�˿����������ر�Э��
	 * 
	 * @param protocolId
	 * @return
	 */
	protected boolean isCloseProtocol(Integer protocolId) {
		if (protocolId == null) {
			throw new RuntimeException("protocolId����Ϊ��");
		}
		PaymentProtocol protocol = super.findProtocolByProtocolId(protocolId);
		if (protocol == null) {
			throw new RuntimeException("protocolΪ��");
		}
		List<RefundDetail> refundDetails = this
				.findRefundDetailByProcolIdAndStatus(protocolId,
						RefundDetailStatus.SUCCESS);
		if (refundDetails == null || refundDetails.isEmpty()) {
			throw new RuntimeException("�˿���ϸΪ��");
		}
		double hadRefundMoney = 0.0;
		for (RefundDetail refundDetail : refundDetails) {
			hadRefundMoney += refundDetail.getRefundMoney();
		}
		if (hadRefundMoney == protocol.getPayMoney()) {
			logger.info("�˿�Ľ���Ѿ�������Э����ر�Э��");
			// this.setProtocolStatus(protocolId, PaymentProtocolStatus.CLOSE);
			return true;
		}
		return false;
	}

	/**
	 * �����˿�ɹ�
	 * 
	 * @param refundDetail
	 */
	private void successRefundDetail(int refundDetailId) {
		RefundDetail refundDetail = refundDetailDao.findById(refundDetailId);
		if (!RefundDetailStatus.PROCESSING.equals(refundDetail.getStatus())) {
			throw new RuntimeException("���������˿���˿���ϸ�����˿�ɹ�");
		}
		refundDetail.setStatus(RefundDetailStatus.SUCCESS);
		Date now = Time.getNow();
		refundDetail.setGmtModify(now);
		refundDetail.setVersion(refundDetail.getVersion() + 1);
		refundDetailDao.update(refundDetail);

	}

	/**
	 * �����˿�ָ��
	 * 
	 * @param protocolId
	 */
	private List<PaymentInstruction> createRefundInstruction(int refundDetailId) {
		// List<RefundDetail> detailList = this
		// .findRefundDetailByProcolIdAndStatus(protocolId,
		// RefundDetailStatus.PROCESSING);
		RefundDetail refundDetail = refundDetailDao.findById(refundDetailId);
		if (refundDetail == null) {
			throw new RuntimeException("�����˿�ָ��ʧ�ܣ��˿���ϸid" + refundDetailId
					+ "û�ж�Ӧ���˿���ϸ");
		}
		if (RefundDetailStatus.SUCCESS.equals(refundDetail.getStatus())) {
			throw new RuntimeException("�����˿�ָ��ʧ�ܣ��Ѿ��˿���ɵ�Э�鲻���ٴ�ִ��");
		}
		// �˿���ϸ���ŵ�ָ��id
		StringBuffer refundInstructionIds = new StringBuffer("");
		// �˿�ָ��list
		List<PaymentInstruction> listRefundInstruction = new ArrayList<PaymentInstruction>();
		// �õ����ɵ��˿���ϸȻ������ָ��
		// for (RefundDetail refundDetail : detailList) {
		// ��ѯЭ���µ�����֧��ָ����������򣨰������֧�����ȵ�ԭ��
		List<PaymentInstruction> listInstructions = getPaymentInstructionForRefund(refundDetail
				.getProtocolId());
		logger.info("������ɣ���ʼ�����˿�ָ��");
		// ʣ����˿���
		double restToBeRefund = refundDetail.getRefundMoney();
		// ��������֧��ָ���������˿�ָ��
		for (PaymentInstruction paymentInstruction : listInstructions) {
			// ����֧��ָ����˿���
			double currentRefundAvailable = paymentInstruction.getPayMoney()
					- paymentInstruction.getRefundMoney();
			// ���˿���С��0������
			if (currentRefundAvailable < 0) {
				throw new RuntimeException("���ݴ���֧��ָ��Ŀ��˿���С��0��instructId="
						+ paymentInstruction.getPaymentInstructionId());
			}
			// ���˿������0������
			if (currentRefundAvailable == 0) {
				continue;
			}
			// �����˿���
			double thisRefundMoney = (restToBeRefund <= currentRefundAvailable) ? restToBeRefund
					: currentRefundAvailable;
			// �����˿�ָ��
			CreatePaymentInstructionDTO cpiDTO = new CreatePaymentInstructionDTO();
			// TODO �˿��;����Ӧ�ö������������������˸�����������˿�����������������������տ��˵�����
			cpiDTO.setChannel(paymentInstruction.getChannel());
			cpiDTO.setProtocolId(refundDetail.getProtocolId());
			cpiDTO.setType(PaymentInstructionType.REFUND);
			cpiDTO.setPayMoney(thisRefundMoney);
			PaymentInstruction refundInstruction = this
					.createPaymentInstruction(cpiDTO);
			listRefundInstruction.add(refundInstruction);
			// ��ָ���idƴ������
			refundInstructionIds.append(","
					+ refundInstruction.getPaymentInstructionId() + ",");
			// ����֧��ָ������˿���
			paymentInstruction.setRefundMoney(paymentInstruction
					.getRefundMoney() + thisRefundMoney);
			instructionDao.update(paymentInstruction);
			// ���˿�ָ�����list��
			// ���ʣ����˿���С�ڵ���0���˳�
			restToBeRefund = restToBeRefund - thisRefundMoney;
			if (restToBeRefund <= 0) {
				break;
			}
		}
		// }
		// �����˿���ϸ��Ϣ
		refundInstructionIds.insert(0, ",");
		refundInstructionIds.append(",");
		refundDetail.setRefundInstructions(refundInstructionIds.toString());
		refundDetail.setVersion(refundDetail.getVersion() + 1);
		refundDetail.setGmtModify(Time.getNow());
		refundDetailDao.update(refundDetail);
		return listRefundInstruction;
	}

	/**
	 * Ϊ���˿ĳ��Э���֧����ָ��ȡ����������
	 * 
	 * @param protocolId
	 * @return
	 */
	private List<PaymentInstruction> getPaymentInstructionForRefund(
			Integer protocolId) {
		List<PaymentInstruction> listInstructions = this
				.findInstructionsByProtocolIdAndType(protocolId,
						PaymentInstructionType.PAY);
		// ����һ�£����ɵ��˿����֧���ķ�����ǰ��
		logger.info("����֧��ָ�blance�����ķ�����ǰ");
		for (PaymentInstruction paymentInstruction : listInstructions) {
			if (PaymentInstructionChannel.BALANCE.equals(paymentInstruction
					.getChannel())) {
				// �õ����֧�����±�
				int blanceIndex = listInstructions.indexOf(paymentInstruction);
				PaymentInstruction instructionFlog = null;
				instructionFlog = listInstructions.get(0);
				listInstructions.set(0, paymentInstruction);
				listInstructions.set(blanceIndex, instructionFlog);
			}
		}
		return listInstructions;
	}

	/**
	 * �����˿���ϸͨ��Э��id��״̬
	 * 
	 * @param protocolId
	 * @param status
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<RefundDetail> findRefundDetailByProcolIdAndStatus(
			int protocolId, String status) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(RefundDetail.class);
		criteria.add(Restrictions.eq("protocolId", protocolId));
		criteria.add(Restrictions.eq("status", status));

		List<RefundDetail> list = refundDetailDao.findByCriteria(criteria);
		return list;
	}

	/**
	 * Ϊ�˿�����֤
	 * 
	 * @param protocolId
	 * @param refundMoney
	 * @return
	 */
	private PaymentProtocol validateRefundMoney(Integer protocolId,
			double refundMoney) {
		if (protocolId == null) {
			throw new RuntimeException("�˿�У��ʧ�ܣ�Э��ID����Ϊ��");
		}
		if (!Validation.isValidMoney(refundMoney)) {
			throw new RuntimeException("�˿�У��ʧ�ܣ������������");
		}
		// �õ�Э�����
		PaymentProtocol protocol = this.findProtocolByProtocolId(protocolId);
		if (protocol == null) {
			throw new RuntimeException("������Э��idû���ҵ�Э��");
		}
		// �ж�Э������
		if (!PaymentProtocolType.PAY.equals(protocol.getType())) {
			throw new RuntimeException("��֧�����Ͳ����˿�");
		}
		// �ж�Э��״̬
		if (PaymentProtocolStatus.INIT.equals(protocol.getStatus())
				|| PaymentProtocolStatus.CLOSE.equals(protocol.getStatus())) {
			throw new RuntimeException("֧��Э��δ֧�������Ѿ��ر�");
		}
		// �ж�ʣ���Ǯ�Ƿ񹻸ô��˿�
		if (!this.isEnoughRefund(protocolId, refundMoney)) {
			throw new RuntimeException("ʣ���Ǯ�����ô��˿�");
		}
		return protocol;
	}

	/**
	 * �ڲ����� �����˿���ϸ
	 * 
	 * @param payCountId
	 * @param refundCountId
	 * @return
	 */
	private RefundDetail createRefundDetail(int protocolId, double refundMoney) {
		logger.info("��ʼ�����˿�ָ��");
		PaymentProtocol protocol = this.findProtocolByProtocolId(protocolId);
		RefundDetail refundDetail = new RefundDetail();
		refundDetail.setProtocolId(protocol.getPaymentProtocolId());
		refundDetail.setRefundMoney(refundMoney);
		refundDetail.setBuyerId(protocol.getPayerId());
		refundDetail.setSellerId(protocol.getReceiverId());
		refundDetail.setStatus(RefundDetailStatus.PROCESSING);
		refundDetail.setVersion(1);
		Date now = Time.getNow();
		refundDetail.setGmtCreate(now);
		refundDetail.setGmtModify(now);
		refundDetail.setRefundInstructions("");
		refundDetailDao.save(refundDetail);
		logger.info("�������˿���ϸ{}", refundDetail.getRefundDetailId());
		return refundDetail;
	}

	/**
	 * ͨ��Э��idȥ�����е��˿���ϸ
	 * 
	 * @param protocolId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<RefundDetail> findRefundDetailsByProtocolId(Integer protocolId) {
		if (protocolId == null) {
			throw new RuntimeException("Э��id����Ϊ��");
		}
		DetachedCriteria criteria = DetachedCriteria
				.forClass(RefundDetail.class);
		criteria.add(Restrictions.eq("protocolId", protocolId));
		// �������
		criteria.add(Restrictions.ne("status", RefundDetailStatus.ABANDON));
		return refundDetailDao.findByCriteria(criteria);
		// return refundDetailDao.findByProtocolId(protocolId);
	}

	@Override
	protected PaymentInstruction createPaymentInstruction(
			CreatePaymentInstructionDTO cpiDTO) {
		// ��֤���õ�Э��
		PaymentProtocol protocol = super
				.validateCreatePaymentInstruction(cpiDTO);

		if (!PaymentInstructionType.REFUND.equals(cpiDTO.getType())) {
			throw new RuntimeException("ָ�����Ͳ�Ϊ�˿�����˿���Ĵ���ָ���");
		}

		Account payer = null;
		Account receiver = null;
		// Ǯ������
		if (PaymentProtocolStatus.SUCCESS.equals(protocol.getStatus())) {
			// ֧����������
			payer = accountServiceBO.findAccountByUserIdAndType(
					protocol.getReceiverId(), AccountType.CASH);

		}// Ǯ�ڵ�����
		else {
			// ֧�����ǵ�����
			payer = accountServiceBO
					.findByAccountId(SpecialAccount.guaranteeId);
		}
		// �տ������
		receiver = accountServiceBO.findAccountByUserIdAndType(
				protocol.getPayerId(), AccountType.CASH);
		// ���ø����ع�����
		return super.createPaymentInstruction(cpiDTO, payer.getAccountId(),
				receiver.getAccountId());
	}

	/**
	 * ͨ��ָ��id�����˿���ϸ
	 * 
	 * @param instructionId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public RefundDetail findRefundDetailByInstructionId(Integer instructionId) {
		if (instructionId == null) {
			throw new RuntimeException("�����˿���ϸʧ��:ָ��id����Ϊ��");
		}
		DetachedCriteria criteria = DetachedCriteria
				.forClass(RefundDetail.class);
		criteria.add(Restrictions.like("refundInstructions", ",,"
				+ instructionId + ",,"));
		// ���������
		criteria.add(Restrictions.ne("status", RefundDetailStatus.ABANDON));
		List<RefundDetail> listRefund = refundDetailDao
				.findByCriteria(criteria);
		if (listRefund == null || listRefund.isEmpty()) {
			return null;
		}
		// һ��ָ��ֻ����һ���˿���ϸ����ڣ��������ڶ���˿���ϸ�д���
		if (listRefund.size() > 1) {
			throw new RuntimeException("�����˿���ϸʧ�ܣ�һ��ָ��id���ҵ��˶���˿���ϸ");
		}
		logger.info("�ҵ����˿���ϸ{}", listRefund.get(0).getRefundDetailId());
		return listRefund.get(0);
	}

	// /**
	// * һ����ҵ����ˮƥ��ɹ�
	// */
	// // @Override
	// protected void matchRcdSuccess(Integer protocolId) {
	// List<PaymentInstruction> instructions = super
	// .validateProtocolAndReturnInstruction(protocolId,
	// PaymentInstructionType.REFUND);
	//
	// // �������ϣ������е�ָ��ִ�гɹ�
	// for (PaymentInstruction thisInstruction : instructions) {
	//
	// // �õ��˿���ϸ
	// RefundDetail refundDetail = this
	// .findRefundDetailByInstructionId(thisInstruction
	// .getPaymentInstructionId());
	// if (refundDetail == null) {
	// throw new RuntimeException(
	// "�˿��ƥ��һ����ҵ��ɹ���Ĳ���ʧ�ܣ�instructionIdû�в��ҳ��˿���ϸ");
	// }
	// // ����˿���ϸ�Ѿ�ִ�����,����
	// if (RefundDetailStatus.SUCCESS.equals(refundDetail.getStatus())) {
	// continue;
	// }
	// // ��ô��ָ��Ϳ��Խⶳ
	// super.unfreezeAccountMoney(thisInstruction);
	//
	// logger.info("ִ����{}��ָ���{}�����ɹ�",
	// thisInstruction.getPaymentInstructionId(),
	// thisInstruction.getType());
	//
	// // �����˿�ɹ�
	// this.successRefundDetail(refundDetail.getRefundDetailId());
	//
	// // // �ж��Ƿ��˿�Э��
	// // // ȡ�����е��˿�ָ��
	// // List<PaymentInstruction> refundInstructions = new
	// // ArrayList<PaymentInstruction>();
	// // // �õ�String���͵�ָ��id
	// // String[] instructionIdsString = refundDetail
	// // .getRefundInstructions().split(",,");
	// // List<Integer> ids = new ArrayList<Integer>();
	// // // ת�����������͵�id
	// // for (String idString : instructionIdsString) {
	// // Integer id = Integer.parseInt(idString);
	// // ids.add(id);
	// // }
	// // // ���������������͵�id�����ҳ���Ӧ��ָ��ӵ�ָ�����
	// // for (Integer id : ids) {
	// // PaymentInstruction instruction = super.findByInstructionId(id);
	// // if (instruction == null) {
	// // throw new RuntimeException(
	// // "�˿��ƥ��һ����ҵ��ɹ���Ĳ���ʧ�ܣ�ͨ���˿���ϸ��ָ��id�ҵ���ָ��Ϊ��");
	// // }
	// // refundInstructions.add(instruction);
	// // }
	// //
	// // if (refundInstructions.isEmpty()) {
	// // throw new RuntimeException("�˿��ƥ��һ����ҵ��ɹ���Ĳ���ʧ�ܣ�û���ҵ��˿�ָ�� ");
	// // }
	// // boolean isRefundOk = true;
	// // // �������е��˿�ָ�����Ƿ��˿����
	// // for (PaymentInstruction refundInstruction : refundInstructions) {
	// // // �������Ϊ�ɹ���ָ��
	// // if (!PaymentInstructionStatus.SUCCESS.equals(refundInstruction
	// // .getStatus())) {
	// // isRefundOk = false;
	// // }
	// // }
	// // // �����˿�ɹ�
	// // if (isRefundOk) {
	// // this.successRefundDetail(refundDetail.getRefundDetailId());
	// // }
	//
	// }
	//
	// }

	/**
	 * �Ƿ��˿����
	 * 
	 * @param refundDetail
	 * @return
	 */
	protected boolean isEnoughRefundDetail(RefundDetail refundDetail) {
		if (refundDetail == null) {
			throw new RuntimeException("û���ҵ��˿���ϸ");
		}

		logger.info("�˿�ָ���С�{}��", refundDetail.getRefundInstructions());
		String instructionIdsString = refundDetail.getRefundInstructions();
		// �õ�String���͵�ָ��id
		String[] idsString = instructionIdsString.split(",,");
		List<Integer> ids = new ArrayList<Integer>();

		// ת�����������͵�id���ӵڶ�����ʼ����һ���ǿ��ַ���
		for (int i = 1; i < idsString.length; i++) {
			logger.info("�˿���ϸ����id{}", idsString[i]);
			Integer id = Integer.parseInt(idsString[i]);
			ids.add(id);
		}
		double hadRefundMoney = 0.0;
		// ���������������͵�id�����ҳ���Ӧ��ָ��ӵ�ָ�����
		for (Integer id : ids) {
			PaymentInstruction instruction = super.findByInstructionId(id);
			if (instruction == null) {
				throw new RuntimeException(
						"�˿��ƥ��һ����ҵ��ɹ���Ĳ���ʧ�ܣ�ͨ���˿���ϸ��ָ��id�ҵ���ָ��Ϊ��");
			}
			// �ɹ�ִ�е�ָ��
			if (PaymentInstructionStatus.SUCCESS
					.equals(instruction.getStatus())) {
				hadRefundMoney += instruction.getPayMoney();
			}
		}
		// �˿������Ӧ�˿���
		if (hadRefundMoney > refundDetail.getRefundMoney()) {
			throw new RuntimeException("�˿��ƥ��һ����ҵ��ɹ���Ĳ���ʧ�ܣ�ͨ���˿���ϸ��ָ��id�ҵ���ָ��Ϊ��");
		}
		// �˿���������˿���
		if (hadRefundMoney == refundDetail.getRefundMoney()) {
			return true;
		}
		// �˿���С�����˿���
		return false;
	}

	@Override
	protected void processForConcreteBusiness(PaymentInstruction instruction) {
		PaymentProtocol protocol = super.findProtocolByProtocolId(instruction
				.getProtocolId());
		if (protocol == null) {
			throw new RuntimeException("�˿�˶Գɹ���Ĳ���ʧ�ܣ�û���ҵ�Э��");
		}
		logger.info("�˿����ָ��{}", instruction.getPaymentInstructionId());
		RefundDetail refundDetail = this
				.findRefundDetailByInstructionId(instruction
						.getPaymentInstructionId());
		if (refundDetail == null) {
			throw new RuntimeException("�˿�˶Գɹ���Ĳ���ʧ�ܣ�û���ҵ��˿���ϸ");
		}

		// �ڲ�������ת�ƽ�Ǯ
		if (PaymentInstructionChannel.isInnerChannel(instruction.getChannel())) {
			super.transferMoney(instruction);
		} else {
			super.unfreezeAccountMoney(instruction);
		}
		// �˴��˿����
		if (this.isEnoughRefundDetail(refundDetail)) {
			this.successRefundDetail(refundDetail.getRefundDetailId());
			// TODO notify trade �˿�ɹ�
		}
		// Э���Ǯ������
		if (this.isCloseProtocol(protocol.getPaymentProtocolId())) {
			super.setProtocolStatus(protocol.getPaymentProtocolId(),
					PaymentProtocolStatus.CLOSE);
			// TODO����notify trade Э��ر�
		}
	}
}
