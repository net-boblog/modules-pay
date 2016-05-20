/**
 * 
 */
package com.xabaohui.modules.pay.service.bo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import com.xabaohui.modules.pay.bean.Account;
import com.xabaohui.modules.pay.bean.ExoFsClearing;
import com.xabaohui.modules.pay.bean.ExoRcdClearing;
import com.xabaohui.modules.pay.bean.OurFsClearing;
import com.xabaohui.modules.pay.bean.OurRcdClearing;
import com.xabaohui.modules.pay.bean.PaymentInstruction;
import com.xabaohui.modules.pay.bean.PaymentProtocol;
import com.xabaohui.modules.pay.bean.RefundDetail;
import com.xabaohui.modules.pay.bean.channel.PaymentInstructionChannel;
import com.xabaohui.modules.pay.bean.status.AccountStatus;
import com.xabaohui.modules.pay.bean.status.PaymentInstructionStatus;
import com.xabaohui.modules.pay.bean.status.PaymentProtocolStatus;
import com.xabaohui.modules.pay.bean.type.FreezeLogType;
import com.xabaohui.modules.pay.bean.type.PaymentInstructionType;
import com.xabaohui.modules.pay.bean.type.PaymentProtocolType;
import com.xabaohui.modules.pay.dao.ExoFsClearingDao;
import com.xabaohui.modules.pay.dao.ExoRcdClearingDao;
import com.xabaohui.modules.pay.dao.OurFsClearingDao;
import com.xabaohui.modules.pay.dao.OurRcdClearingDao;
import com.xabaohui.modules.pay.dao.PaymentInstructionDao;
import com.xabaohui.modules.pay.dao.PaymentProtocolDao;
import com.xabaohui.modules.pay.dao.RefundDetailDao;
import com.xabaohui.modules.pay.dto.CreateExoFsClearingDTO;
import com.xabaohui.modules.pay.dto.CreateExoRcdClearingDTO;
import com.xabaohui.modules.pay.dto.CreatePaymentInstructionDTO;
import com.xabaohui.modules.pay.dto.CreatePaymentProtocolDTO;
import com.xabaohui.modules.pay.util.Time;
import com.xabaohui.modules.pay.util.Validation;

/**
 * @author YRee
 * 
 */
public abstract class PaymentBOu {

	protected static Logger logger = LoggerFactory.getLogger(PaymentBOu.class);
	protected PaymentProtocolDao protocolDao;
	protected PaymentInstructionDao instructionDao;
	protected RefundDetailDao refundDetailDao;
	protected AccountServiceBO accountServiceBO;
	@Resource
	protected OurRcdClearingDao ourRcdClearingDao;
	@Resource
	protected ExoRcdClearingDao exoRcdClearingDao;
	@Resource
	protected ExoFsClearingDao exoFsClearingDao;
	@Resource
	protected OurFsClearingDao ourFsClearingDao;

	/**
	 * 
	 */
	public PaymentBOu() {
		super();
	}

	/**
	 * ͨ��cpiDTOs�õ�instructions
	 * 
	 * @param cpiDTOs
	 * @return
	 */
	public List<PaymentInstruction> processGetInstructions(
			List<CreatePaymentInstructionDTO> cpiDTOs) {
		if (cpiDTOs == null || cpiDTOs.isEmpty()) {
			throw new RuntimeException("����ʧ�ܣ�������ָ��Ϊ��");
		}
		// �Ƿ���֤��Э��״̬
		boolean isValidateProtocolStatus = false;
		List<PaymentInstruction> instructions = new ArrayList<PaymentInstruction>();
		for (CreatePaymentInstructionDTO cpiDTO : cpiDTOs) {
			// ������
			PaymentProtocol protocol = this.validateCpiDTO(cpiDTO);
			// û����֤��Э��״̬�Ļ������Э��״̬
			if (!isValidateProtocolStatus
					&& !PaymentProtocolStatus.INIT.equals(protocol.getStatus())) {
				throw new RuntimeException("����ʧ�ܣ�Э��ĳ�ʼ״̬��Ϊinit");
			}
			// û����֤��Э��״̬�Ļ�������Э��״̬Ϊprocessing��ֻ��һ�����ã�
			// �˿�޸�Э��״̬
			if (!PaymentInstructionType.REFUND.equals(cpiDTO.getType())
					&& !isValidateProtocolStatus) {
				logger.info("��Э��{}��״̬�޸�Ϊ��PROCESSING",
						protocol.getPaymentProtocolId());
				this.setProtocolStatus(protocol.getPaymentProtocolId(),
						PaymentProtocolStatus.PROCESSING);
				isValidateProtocolStatus = true;
			}
			// ����ָ��
			PaymentInstruction instruction = this
					.createPaymentInstruction(cpiDTO);

			instructions.add(instruction);

		}

		return instructions;
	}

	/**
	 * ����ִ��
	 * 
	 * @param cpiDTO
	 */
	public void process(List<PaymentInstruction> instructions) {
		// ����ִ�е�ָ��
		PaymentInstruction outerChannelInstruction = null;
		// �Ƿ���֤��Э��״̬
		for (PaymentInstruction instruction : instructions) {
			// ������
			PaymentProtocol protocol = this
					.findProtocolByProtocolId(instruction.getProtocolId());
			// ���˿����������û����֤��Э��״̬�Ļ������Э��״̬
			if (!PaymentInstructionType.REFUND.equals(instruction.getType())
					&& !PaymentProtocolStatus.PROCESSING.equals(protocol
							.getStatus())) {

				throw new RuntimeException("����ʧ�ܣ�Э��ĳ�ʼ״̬��Ϊprocessing");
			}

			// �˿�����֣� ���ȶ�����,�˿�Ļ��������ⲿ����
			if (PaymentInstructionType.WITHDRAW.equals(instruction.getType())
					|| PaymentInstructionType.REFUND.equals(instruction
							.getType())
					&& PaymentInstructionChannel.isOuterChannel(instruction
							.getChannel())) {
				this.freezeAccountMoney(instruction);
			}

			// ���򲢽��ⲿ֧��������ǰ
			if (PaymentInstructionChannel.isOuterChannel(instruction
					.getChannel())) {
				outerChannelInstruction = instruction;
				logger.info("����{}ָ�{}��,��һ���ⲿָ��",
						outerChannelInstruction.getType(),
						outerChannelInstruction.getPaymentInstructionId());
			}
		}
		// �����ⲿ����ָ��
		if (outerChannelInstruction != null) {
			logger.info("�����ⲿָ������˴�������ʼ�ȴ�");
			this.processOne(outerChannelInstruction);
		} else {// ֻ���ڲ�����ָ��
			logger.info("�������ⲿָ�ֱ�Ӵ����ڲ�����");
			for (PaymentInstruction instruction : instructions) {
				this.processOne(instruction);
			}
		}

	}

	/**
	 * У��cpiDTO�����ҷ���Э��
	 * 
	 * @param cpiDTO
	 * @return
	 */
	protected PaymentProtocol validateCpiDTO(CreatePaymentInstructionDTO cpiDTO) {
		if (cpiDTO == null) {
			throw new RuntimeException("������֧��ָ������ǿ�");
		}
		if (StringUtils.isBlank(cpiDTO.getChannel())
				|| StringUtils.isBlank(cpiDTO.getType())
				|| cpiDTO.getProtocolId() == null
				|| !Validation.isValidMoney(cpiDTO.getPayMoney())) {
			throw new RuntimeException("������������Ƿ���ȷ");
		}
		// �ж�Э��״̬
		PaymentProtocol protocol = this.findProtocolByProtocolId(cpiDTO
				.getProtocolId());
		if (protocol == null
				|| PaymentProtocolStatus.CLOSE.equals(protocol.getStatus())) {
			throw new RuntimeException("û�и�Э�����Э���Ѿ��ر�");
		}

		return protocol;
	}

	/**
	 * ����֧��Э��
	 * 
	 * @param cppDTO
	 */
	public PaymentProtocol createPaymentProtocol(CreatePaymentProtocolDTO cppDTO) {
		if (cppDTO == null) {
			throw new RuntimeException("֧��Э������ǿ�");
		}
		// �жϴ����Ķ����еı�Ҫ���������ǿ�
		if (StringUtils.isBlank(cppDTO.getType())
				|| !Validation.isValidMoney(cppDTO.getPayMoney())) {
			throw new RuntimeException("����������������");
		}

		// ��֤֧��״̬�������Ƿ��ظ�
		if (PaymentProtocolType.PAY.equals(cppDTO.getType())
				&& this.protocolDao.findByOrderId(cppDTO.getOrderId()) != null) {
			throw new RuntimeException("����Э��ʧ�ܣ����Ķ����Ѿ�������Э����");
		}

		Date now = Time.getNow();
		PaymentProtocol protocol = new PaymentProtocol();
		// ������ֵ��cppDTO������protocol��
		BeanUtils.copyProperties(cppDTO, protocol);
		protocol.setStatus(PaymentProtocolStatus.INIT);
		protocol.setGmtCreate(now);
		protocol.setGmtModify(now);
		protocol.setVersion(1);
		protocolDao.save(protocol);
		logger.info("������Э��" + protocol.getPaymentProtocolId());
		return protocol;
	}

	// ���鲢��ȡ��Э�����
	protected PaymentProtocol validateCreatePaymentInstruction(
			CreatePaymentInstructionDTO cpiDTO) {
		if (cpiDTO == null) {
			throw new RuntimeException("cpiDTO������Ϊ��");
		}

		if (StringUtils.isBlank(cpiDTO.getChannel())
				|| cpiDTO.getProtocolId() == null
				|| !Validation.isValidMoney(cpiDTO.getPayMoney())
				|| StringUtils.isBlank(cpiDTO.getType())) {
			throw new RuntimeException("������cpiDTO����������");
		}
		PaymentProtocol protocol = findProtocolByProtocolId(cpiDTO
				.getProtocolId());
		// ����Э��״̬
		if (PaymentProtocolStatus.CLOSE.equals(protocol.getStatus())) {
			throw new RuntimeException("Э���Ѿ��ر�");
		}
		logger.info("�õ���Э����� " + protocol.getPaymentProtocolId());
		return protocol;
	}

	/**
	 * ����֧��ָ��
	 * 
	 * @param cpiDTO
	 */
	protected abstract PaymentInstruction createPaymentInstruction(
			CreatePaymentInstructionDTO cpiDTO);

	/**
	 * Э��Ľ���Ƿ���Ѿ��ɹ���ָ�����ܺ����
	 * 
	 * @param protocolId
	 * @param type
	 * @return
	 */
	protected boolean isEnoughMoneyToPaidProtocol(int protocolId,
			String instructionType) {
		PaymentProtocol protocol = this.findProtocolByProtocolId(protocolId);
		// ��������ر�
		if (PaymentProtocolStatus.CLOSE.equals(protocol.getStatus())) {
			throw new RuntimeException("�����Ѿ��ر�");
		}
		logger.info("Ϊ����Э��״̬�ȽϽ��");
		// �����ȷ���տ�Ļ���Ҫ�����Ѿ��˿���
		double hadRefundMoney = 0.0;
		if (PaymentInstructionType.CONFIRM.equals(instructionType)) {
			List<PaymentInstruction> listRefund = this
					.findInstructionsByProtocolIdAndType(protocolId,
							PaymentInstructionType.REFUND);
			for (PaymentInstruction refundInstruction : listRefund) {
				hadRefundMoney += refundInstruction.getPayMoney();
			}
		}

		double payMoney = 0.0;
		List<PaymentInstruction> listInstructions = this
				.findInstructionsByProtocolIdAndType(protocolId,
						instructionType);
		for (PaymentInstruction paymentInstruction : listInstructions) {
			// ֻ��֧���ɹ���״̬Ϊsuccess��
			if (PaymentInstructionStatus.SUCCESS.equals(paymentInstruction
					.getStatus())) {
				payMoney += paymentInstruction.getPayMoney();
			}
		}
		logger.info("�Ѿ��˿���Ϊ:  " + hadRefundMoney);
		logger.info(instructionType + "�Ľ��Ϊ��" + payMoney);
		logger.info(instructionType + "���ܹ����Ϊ��" + (payMoney + hadRefundMoney));
		logger.info(instructionType + "�ܹ���Ҫ�Ľ��Ϊ��" + protocol.getPayMoney());
		if (hadRefundMoney + payMoney == protocol.getPayMoney()) {
			logger.info("Ǯ��Э�����Ǯ�Ե���");
			return true;
		} else {
			logger.info("Ǯ��Э�����Ǯ���Ե�");
			return false;
		}
	}

	/**
	 * ����֧��ָ��
	 * 
	 * @param cpiDTO
	 * @param payerId
	 * @param recevierId
	 * @return
	 */
	protected PaymentInstruction createPaymentInstruction(
			CreatePaymentInstructionDTO cpiDTO, Integer payerId,
			Integer receiverId) {
		PaymentInstruction instruction = new PaymentInstruction();
		// ��������
		BeanUtils.copyProperties(cpiDTO, instruction);
		Date now = Time.getNow();
		instruction.setPayerId(payerId);
		instruction.setReceiverId(receiverId);
		instruction.setStatus(PaymentInstructionStatus.INIT);
		instruction.setPayTime(now);
		instruction.setRefundMoney(0.0);
		instruction.setGmtCreate(now);
		instruction.setGmtModify(now);
		instruction.setVersion(1);
		// ����֧��Э��
		instructionDao.save(instruction);
		return instruction;
	}

	public PaymentProtocol findProtocolByProtocolId(Integer id) {
		if (id == null) {
			throw new RuntimeException("������Id����");
		}
		return protocolDao.findById(id);
	}

	/**
	 * ͨ��Э��id��ָ��
	 * 
	 * @param protocolId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected List<PaymentInstruction> findInstructionsByProtocolId(
			Integer protocolId) {
		if (protocolId == null) {
			throw new RuntimeException("������protocolId����Ϊ��");
		}
		DetachedCriteria criteria = DetachedCriteria
				.forClass(PaymentInstruction.class);
		criteria.add(Restrictions.eq("protocolId", protocolId));
		// ����ʧ��״̬��
		criteria.add(Restrictions
				.ne("status", PaymentInstructionStatus.FAILURE));
		return instructionDao.findByCriteria(criteria);
	}

	/**
	 * ��Ǯͨ��Э��ת�� ֧��ʱ���ת��
	 * 
	 * @param protocol
	 */
	protected void transferMoney(PaymentInstruction instruction) {
		if (instruction == null) {
			throw new RuntimeException("֧��ָ���ǿ�");
		}
		if (instruction.getPaymentInstructionId() == null
				|| instruction.getPayerId() == null
				|| instruction.getReceiverId() == null
				|| !Validation.isValidMoney(instruction.getPayMoney())
				|| StringUtils.isBlank(instruction.getStatus())) {
			throw new RuntimeException("�����Ĳ�������" + instruction);
		}
		if (!instruction.getStatus()
				.equals(PaymentInstructionStatus.PROCESSING)) {
			throw new RuntimeException("ָ���processing��״̬������ִ��ת�ƽ��");
		}
		// ָ���е�Ǯ�Ѿ�������,�Ͳ�����ִ����
		if (instruction.getPayMoney() == instruction.getRefundMoney()) {
			return;
		}

		Account payer = accountServiceBO.findByAccountId(instruction
				.getPayerId());
		Account recevier = accountServiceBO.findByAccountId(instruction
				.getReceiverId());
		if (payer == null || recevier == null) {
			throw new RuntimeException("֧���˻����տ���δ�ҵ� ");
		}
		// ��֤�˻�
		if (payer.getStatus().equals(AccountStatus.ABANDON)
				|| recevier.getStatus().equals(AccountStatus.ABANDON)) {
			throw new RuntimeException("�˻��Ѿ�����");
		}
		// ֧���û��Ŀ������
		double payerAvailableMoney = accountServiceBO
				.findAvailableByAccountId(payer.getAccountId());
		// �˻�������Ϊ��ֵ�����˻����û�����Ҫ֧����Ǯ�٣�����֧��
		if (!PaymentInstructionType.RECHARGE.equals(instruction.getType())
				&& payerAvailableMoney < instruction.getPayMoney()) {
			throw new RuntimeException("����֧��");
		}

		// �޸��˻����
		accountServiceBO.addAccountAmount(instruction);
		accountServiceBO.reduceAccountAmount(instruction);
		// ָ��ִ�����
		instruction.setStatus(PaymentInstructionStatus.SUCCESS);
		// ��������
		this.updatePaymentInstruction(instruction);
		logger.info("{}���͵�ָ�{}����ת�ƽ��ִ�����", instruction.getType(),
				instruction.getPaymentInstructionId());
	}

	/**
	 * �޸�֧��ָ��
	 * 
	 * @param instruction
	 */
	protected void updatePaymentInstruction(PaymentInstruction instruction) {
		if (instruction == null) {
			throw new RuntimeException("������ָ������ǿ�");
		}
		if (instruction.getPaymentInstructionId() == null) {
			throw new RuntimeException("����ָ��id�Ƿ���ȷ");
		}
		instruction.setGmtModify(Time.getNow());
		instruction.setVersion(instruction.getVersion() + 1);
		instructionDao.update(instruction);
	}

	public PaymentInstruction findByInstructionId(Integer id) {
		if (id == null) {
			throw new RuntimeException("������ָ��idֵ����");
		}
		return instructionDao.findById(id);
	}

	/**
	 * ͨ��Э��id�����Ͳ���ָ��
	 * 
	 * @param protocolId
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected List<PaymentInstruction> findInstructionsByProtocolIdAndType(
			Integer protocolId, String type) {
		if (protocolId == null || StringUtils.isBlank(type)) {
			throw new RuntimeException("�������ͺ�Э��id");
		}

		DetachedCriteria criteria = DetachedCriteria
				.forClass(PaymentInstruction.class);
		criteria.add(Restrictions.eq("protocolId", protocolId));
		criteria.add(Restrictions.eq("type", type));
		// �������
		criteria.add(Restrictions
				.ne("status", PaymentInstructionStatus.FAILURE));

		List<PaymentInstruction> listInstruction = instructionDao
				.findByCriteria(criteria);
		logger.info("�ҵ���" + listInstruction.size() + "����������ָ��");
		return listInstruction;
	}

	/**
	 * Э��ִ�гɹ�
	 * 
	 * @param protocolId
	 */
	protected void setProtocolStatus(int protocolId, String status) {
		PaymentProtocol paymentProtocol = protocolDao.findById(protocolId);
		paymentProtocol.setStatus(status);
		paymentProtocol.setGmtModify(Time.getNow());
		paymentProtocol.setVersion(paymentProtocol.getVersion() + 1);
		protocolDao.update(paymentProtocol);
		logger.info("Э��" + protocolId + "��״̬�޸�Ϊ" + status);
	}

	/**
	 * ֧��Э�������е��ֽ�֧���Ƿ��˿�
	 * 
	 * @param rppDTO
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean isEnoughRefund(Integer protocolId, Double refundMoney) {
		if (protocolId == null || !Validation.isValidMoney(refundMoney)) {
			throw new RuntimeException("���鴫����ֵ�Ƿ���ȷ");
		}
		// �õ�Э�����
		PaymentProtocol protocol = this.findProtocolByProtocolId(protocolId);
		if (protocol == null) {
			throw new RuntimeException("������Э��idû���ҵ�Э��");
		}
		// �Ѿ��˿���
		double hadRefundMoney = 0.0;
		List<RefundDetail> listRefundDetail = refundDetailDao
				.findByProtocolId(protocolId);
		// ����������˿�Ľ���ܺ�
		for (RefundDetail refundDetail : listRefundDetail) {
			hadRefundMoney += refundDetail.getRefundMoney();
		}
		logger.info("��Ҫ�˿�Ľ��{}�������˵Ľ��{}", refundMoney,
				(protocol.getPayMoney() - hadRefundMoney));
		// Э����ܶ���Լ��˿�Ľ���Լ����Ҫ�˿�Ľ��
		if (protocol.getPayMoney() < hadRefundMoney + refundMoney) {
			return false;
		}
		return true;
	}

	/**
	 * �ⶳ������
	 * 
	 * @param instruction
	 */
	protected void freezeAccountMoney(PaymentInstruction instruction) {

		if (PaymentInstructionStatus.FAILURE.equals(instruction.getStatus())
				|| PaymentInstructionStatus.SUCCESS.equals(instruction
						.getStatus())) {
			throw new RuntimeException("������ʧ�ܣ�ָ���Ѿ�ִ����ϻ����Ѿ�ʧ��");
		}
		// ������
		accountServiceBO
				.freezeMoneyOperation(instruction, FreezeLogType.FREEZE);
		// // Э�����ڴ���
		// this.setProtocolStatus(instruction.getProtocolId(),
		// PaymentProtocolStatus.PROCESSING);
	}

	/**
	 * �ⶳ
	 * 
	 * @param protocolId
	 * @param protocolType
	 */
	protected void unfreezeAccountMoney(PaymentInstruction instruction) {
		if (instruction == null) {
			throw new RuntimeException("�ⶳʧ�ܣ�������ָ��Ϊ��");
		}
		this.validateInstructionForCreateClearing(instruction);
		if (PaymentInstructionStatus.FAILURE.equals(instruction.getStatus())) {
			throw new RuntimeException("�ⶳʧ�ܣ��Ѿ�ʧ�ܵ�ָ������޸�״̬");
		}
		// ���ֻ����˿�Э���ڽⶳʱת��
		this.transferMoney(instruction);// Э���Ѿ�ִ�� ת���ˣ�ָ��״̬Ϊsuccess
		// �ⶳ���
		accountServiceBO.freezeMoneyOperation(instruction,
				FreezeLogType.UNFREEZE);

		this.updatePaymentInstruction(instruction);
	}

	/**
	 * ��������ҵ����ϸ���Ƚϣ�Ȼ����
	 * 
	 * @param exoRcd
	 */
	public void processExoRcdClearing(CreateExoRcdClearingDTO cercDTO) {
		// ��������ҵ����ˮ
		ExoRcdClearing exoRcdClearing = this.createExoRcdClearing(cercDTO);
		// �õ�һ��ҵ����ˮ
		OurRcdClearing ourRcdClearing = ourRcdClearingDao
				.findById(exoRcdClearing.getOurRcdId());
		PaymentInstruction instruction = instructionDao.findById(exoRcdClearing
				.getInstructionId());
		if (!PaymentInstructionStatus.PROCESSING
				.equals(instruction.getStatus())) {
			throw new RuntimeException("�޸�ָ��״̬ʧ��:ָ��״̬��Ϊprocessing");
		}
		// ƥ��ɹ�
		if (this.matchRcdClearing(ourRcdClearing, exoRcdClearing)) {
			this.matchRcdSuccess(exoRcdClearing.getProtocolId(),
					instruction.getType());
			// ����ָ��ִ�гɹ�,��Գ�ֵ����ִ���ˣ���ƥ��һ����ҵ����ˮ��
			instruction.setStatus(PaymentInstructionStatus.SUCCESS);
			this.updatePaymentInstruction(instruction);
			logger.info("һ����ҵ����ˮƥ��ɹ�");
		} else {// ƥ��ʧ��
			// �õ����е�֧��ָ��
			List<PaymentInstruction> instructions = this
					.findInstructionsByProtocolIdAndType(
							ourRcdClearing.getProtocolId(),
							PaymentInstructionType.PAY);
			// �������е�֧��ָ��Ϊʧ��
			for (PaymentInstruction instruct : instructions) {
				// ����instruction ״̬Ϊʧ��
				instruct.setStatus(PaymentInstructionStatus.FAILURE);
				this.updatePaymentInstruction(instruct);
				// ���������˿�ⶳ
				// ��ֵ�����֣� �ͽⶳ���
				if (PaymentInstructionType.WITHDRAW.equals(instruction
						.getType())
						|| PaymentInstructionType.REFUND.equals(instruction
								.getType())) {
					accountServiceBO.freezeMoneyOperation(instruction,
							FreezeLogType.UNFREEZE);
				}
			}
			logger.info("һ����ҵ����ˮƥ��ʧ��");
		}

	}

	/**
	 * ��������ҵ����ϸ���Ƚϣ�Ȼ����
	 * 
	 * @param exoRcd
	 */
	public void processExoRcdClearing(String trade_no, Integer instructionId) {
		if (StringUtils.isBlank(trade_no) || instructionId == null) {
			throw new RuntimeException("�˶�һ����ҵ����ˮʧ�ܣ������Ľ��׺Ż���ָ���Ϊ��");
		}

		CreateExoRcdClearingDTO cercDTO = new CreateExoRcdClearingDTO();
		// ȡ��Э��
		PaymentInstruction instruction = this
				.findByInstructionId(instructionId);
		// ȡ��һ��ҵ����ˮ
		OurRcdClearing ourRcdClearing = ourRcdClearingDao
				.findByInstructionId(instructionId);
		// ���׺�����
		cercDTO.setExoRef(trade_no);
		cercDTO.setInstructionId(instructionId);
		cercDTO.setProtocolId(instruction.getProtocolId());
		cercDTO.setTxType(instruction.getType());
		// ����һ��ҵ����ˮ
		cercDTO.setOurRcdId(ourRcdClearing.getOurRcdId());
		cercDTO.setTxChannel(instruction.getChannel());
		cercDTO.setTxMoney(instruction.getPayMoney());
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!" + cercDTO);
		// ��������ҵ����ˮ
		ExoRcdClearing exoRcdClearing = this.createExoRcdClearing(cercDTO);
		// �õ�һ��ҵ����ˮ
		// OurRcdClearing ourRcdClearing = ourRcdClearingDao
		// .findById(exoRcdClearing.getOurRcdId());
		// PaymentInstruction instruction =
		// instructionDao.findById(exoRcdClearing
		// .getInstructionId());

		if (!PaymentInstructionStatus.PROCESSING
				.equals(instruction.getStatus())) {
			throw new RuntimeException("�޸�ָ��״̬ʧ��:ָ��״̬��Ϊprocessing");
		}
		// ƥ��ɹ�
		if (this.matchRcdClearing(ourRcdClearing, exoRcdClearing)) {
			this.matchRcdSuccess(exoRcdClearing.getProtocolId(),
					instruction.getType());
			// ����ָ��ִ�гɹ�,��Գ�ֵ����ִ���ˣ���ƥ��һ����ҵ����ˮ��
			instruction.setStatus(PaymentInstructionStatus.SUCCESS);
			this.updatePaymentInstruction(instruction);
			logger.info("һ����ҵ����ˮƥ��ɹ�");
		} else {// ƥ��ʧ��
			// �õ����е�֧��ָ��
			List<PaymentInstruction> instructions = this
					.findInstructionsByProtocolIdAndType(
							ourRcdClearing.getProtocolId(),
							PaymentInstructionType.PAY);
			// �������е�֧��ָ��Ϊ�ɹ�
			for (PaymentInstruction instruct : instructions) {
				// ����instruction ״̬Ϊʧ��
				instruct.setStatus(PaymentInstructionStatus.FAILURE);
				this.updatePaymentInstruction(instruct);
				// ���������˿�ⶳ
				// ��ֵ�����֣� �ͽⶳ���
				if (PaymentInstructionType.WITHDRAW.equals(instruction
						.getType())
						|| PaymentInstructionType.REFUND.equals(instruction
								.getType())) {
					accountServiceBO.freezeMoneyOperation(instruction,
							FreezeLogType.UNFREEZE);
				}
			}
			logger.info("һ����ҵ����ˮƥ��ʧ��");
		}

	}

	/**
	 * �˶Գɹ���Ĳ���
	 * 
	 * @param protocolId
	 * @param instructionType
	 */
	protected void matchRcdSuccess(Integer protocolId, String instructionType) {
		// ��֤������ָ��
		List<PaymentInstruction> instructions = this
				.validateProtocolAndReturnInstruction(protocolId,
						instructionType);

		for (PaymentInstruction instruction : instructions) {
			// ������֧����
			if (PaymentInstructionChannel.isOuterChannel(instruction
					.getChannel())) {
				this.processForConcreteBusiness(instruction);
			}// �ڲ�����֧��
			else {
				// ����ڲ�������ָ��״̬��Ϊinit��������
				if (!PaymentInstructionStatus.INIT.equals(instruction
						.getStatus())) {
					continue;
				}
				// ���ڲ�����ָ��ִ��Ӧ�еĲ���
				this.processOne(instruction);
			}
		}
	}

	/**
	 * Ϊ��ˮУ��ָ��
	 * 
	 * @param instruction
	 * @return
	 */
	protected void validateInstructionForCreateClearing(
			PaymentInstruction instruction) {
		if (instruction == null) {
			throw new RuntimeException("ָ��Ϊ��");
		}
		if (instruction.getPaymentInstructionId() == null
				|| instruction.getProtocolId() == null
				|| !Validation.isValidMoney(instruction.getPayMoney())
				|| StringUtils.isBlank(instruction.getType())
				|| StringUtils.isBlank(instruction.getChannel())) {
			throw new RuntimeException("ָ����ĳ����ֵ����");
		}
	}

	/**
	 * ����һ��ҵ����ˮ
	 * 
	 * @param instruction
	 */
	protected OurRcdClearing createOurRcdClearing(PaymentInstruction instruction) {
		// ��ָ֤��
		this.validateInstructionForCreateClearing(instruction);
		OurRcdClearing clearing = new OurRcdClearing();
		clearing.setInstructionId(instruction.getPaymentInstructionId());
		clearing.setProtocolId(instruction.getProtocolId());
		clearing.setTxType(instruction.getType());
		// ��Ծ��� ��ҵ����Ҿ��������
		// ��������
		clearing.setTxChannel(instruction.getChannel());

		clearing.setTxMoney(instruction.getPayMoney());
		Date now = Time.getNow();
		clearing.setGmtCreate(now);
		clearing.setGmtModify(now);
		clearing.setVersion(1);
		ourRcdClearingDao.save(clearing);
		logger.info("������һ��ҵ����ˮ��{}��", clearing.getOurRcdId());
		return clearing;
	}

	/**
	 * ��������ҵ����ˮ
	 * 
	 * @param ourRcdId
	 * @param exoRef
	 * @return
	 */
	public ExoRcdClearing createExoRcdClearing(CreateExoRcdClearingDTO cercDTO) {
		if (cercDTO == null) {
			throw new RuntimeException("��������ҵ����ˮʧ�ܣ�������cercDTOΪ��");
		}

		if (StringUtils.isBlank(cercDTO.getExoRef())
				|| cercDTO.getInstructionId() == null
				|| cercDTO.getProtocolId() == null
				|| cercDTO.getOurRcdId() == null
				|| StringUtils.isBlank(cercDTO.getTxChannel())
				|| StringUtils.isBlank(cercDTO.getTxType())
				|| !Validation.isValidMoney(cercDTO.getTxMoney())) {
			throw new RuntimeException("��������ҵ����ˮʧ�ܣ���������cercDTO������ֵ����ȷ");
		}
		ExoRcdClearing exoRcdClearing = new ExoRcdClearing();
		BeanUtils.copyProperties(cercDTO, exoRcdClearing);

		Date now = Time.getNow();
		exoRcdClearing.setGmtCreate(now);
		exoRcdClearing.setGmtModify(now);
		exoRcdClearing.setVersion(1);
		exoRcdClearingDao.save(exoRcdClearing);
		return exoRcdClearing;
	}

	/**
	 * ����һ���ʽ���ˮ
	 * 
	 * @param ourRcdId
	 * @return
	 */
	protected OurFsClearing createOurFsClearing(OurRcdClearing ourRcdClearing) {
		OurFsClearing ourFsClearing = new OurFsClearing();
		if (ourRcdClearing == null) {
			throw new RuntimeException("����һ���ʽ���ˮʧ�ܣ�һ��ҵ����ˮ����Ϊ��");
		}
		BeanUtils.copyProperties(ourRcdClearing, ourFsClearing);
		Date now = Time.getNow();
		ourRcdClearing.setGmtCreate(now);
		ourRcdClearing.setGmtModify(now);
		ourRcdClearing.setVersion(1);
		ourRcdClearingDao.save(ourRcdClearing);
		return ourFsClearing;
	}

	/**
	 * ���������ʽ���ˮ
	 * 
	 * @param cefcDTO
	 * @return
	 */
	public ExoFsClearing createExoFsClearing(CreateExoFsClearingDTO cefcDTO) {
		if (cefcDTO == null) {
			throw new RuntimeException("��������ҵ����ˮʧ�ܣ�������cefcDTOΪ��");
		}
		if (StringUtils.isBlank(cefcDTO.getExoRef())
				|| cefcDTO.getInstructionId() == null
				|| cefcDTO.getProtocolId() == null
				|| cefcDTO.getOurRcdId() == null
				|| StringUtils.isBlank(cefcDTO.getTxChannel())
				|| StringUtils.isBlank(cefcDTO.getTxType())
				|| Validation.isValidMoney(cefcDTO.getTxMoney())
				|| Validation.isValidMoney(cefcDTO.getExoFee())) {
			throw new RuntimeException("��������ҵ����ˮʧ�ܣ���������cefcDTO������ֵ����ȷ");
		}
		ExoFsClearing exoFsClearing = new ExoFsClearing();
		BeanUtils.copyProperties(cefcDTO, exoFsClearing);
		Date now = Time.getNow();
		exoFsClearing.setGmtCreate(now);
		exoFsClearing.setGmtModify(now);
		exoFsClearing.setVersion(1);
		exoFsClearingDao.save(exoFsClearing);
		return null;
	}

	/**
	 * �Ƚ�һ����ҵ����ˮ�Ƿ�ƥ��
	 * 
	 * @param ourRcdClearing
	 * @param exoRcdClearing
	 * @return
	 */
	protected boolean matchRcdClearing(OurRcdClearing ourRcdClearing,
			ExoRcdClearing exoRcdClearing) {
		if (ourRcdClearing.getOurRcdId().equals(exoRcdClearing.getOurRcdId())
				&& ourRcdClearing.getProtocolId().equals(
						exoRcdClearing.getProtocolId())
				&& ourRcdClearing.getInstructionId().equals(
						exoRcdClearing.getInstructionId())
				&& ourRcdClearing.getTxChannel().equals(
						exoRcdClearing.getTxChannel())
				&& ourRcdClearing.getTxMoney().equals(
						exoRcdClearing.getTxMoney())
				&& ourRcdClearing.getTxType()
						.equals(exoRcdClearing.getTxType())) {
			return true;
		}
		return false;
	}

	/**
	 * �Ƚ�һ���ʽ���ˮ�������ʽ���ˮ�Ƿ�ƥ��
	 * 
	 * @param ourFsClearing
	 * @param exoFsClearing
	 * @return
	 */
	protected boolean matchFsClearing(OurFsClearing ourFsClearing,
			ExoFsClearing exoFsClearing) {
		if (ourFsClearing.getOurFsId().equals(exoFsClearing.getOurFsId())
				&& ourFsClearing.getProtocolId().equals(
						exoFsClearing.getProtocolId())
				&& ourFsClearing.getInstructionId().equals(
						exoFsClearing.getInstructionId())
				&& ourFsClearing.getTxChannel().equals(
						exoFsClearing.getTxChannel())
				&& ourFsClearing.getTxMoney()
						.equals(exoFsClearing.getTxMoney())
				&& ourFsClearing.getTxType().equals(exoFsClearing.getTxType())) {
			return true;
		}
		return false;
	}

	/**
	 * ���ָ��id������ָ��
	 * 
	 * @param instructionId
	 * @return
	 */
	protected PaymentInstruction validateInstructionId(Integer instructionId) {
		if (instructionId == null) {
			throw new RuntimeException("һ��������ҵ��ƥ������ʧ�ܣ�ָ��idΪ��");
		}
		PaymentInstruction instruction = this
				.findByInstructionId(instructionId);
		if (instruction == null) {
			throw new RuntimeException("һ��������ҵ��ƥ������ʧ�ܣ�������ָ��id�鵽��ָ��Ϊ��");
		}
		return instruction;
	}

	/**
	 * ��֤Э��id���ҷ���ָ���
	 * 
	 * @param protocolId
	 * @param instructionType
	 * @return
	 */
	protected List<PaymentInstruction> validateProtocolAndReturnInstruction(
			Integer protocolId, String instructionType) {
		if (protocolId == null) {
			throw new RuntimeException("һ����ҵ����ˮƥ��ɹ��������ʧ�ܣ�Э��id����Ϊ��");
		}
		// ��֤������ָ��
		List<PaymentInstruction> instructions = this
				.findInstructionsByProtocolIdAndType(protocolId,
						instructionType);
		if (instructions == null || instructions.isEmpty()) {
			throw new RuntimeException("һ����ҵ����ˮƥ��ɹ��������ʧ�ܣ�ָ���Ϊ��");
		}
		return instructions;
	}

	/**
	 * ��ָ�������ִ��
	 * 
	 * @param instruction
	 */
	public void processOne(PaymentInstruction instruction) {
		// invoke outer Api �����ⲿ�ӿ�
		if (PaymentInstructionChannel.isOuterChannel(instruction.getChannel())) {
			// TODO invoke
		}

		// �����������͵�һ��ҵ����ˮ
		this.createOurRcdClearing(instruction);
		// ����Э��״̬Ϊprocessing
		instruction.setStatus(PaymentInstructionStatus.PROCESSING);
		this.updatePaymentInstruction(instruction);
		logger.info("ָ�{}����״̬�����processing",
				instruction.getPaymentInstructionId());
		// ������ڲ�������ֱ�ӳɹ�
		if (PaymentInstructionChannel.isInnerChannel(instruction.getChannel())) {
			this.processForConcreteBusiness(instruction);
		}
	}

	/**
	 * ��Ծ����ҵ��ִ�о���Ĺ���
	 * 
	 * @param instruction
	 */
	protected abstract void processForConcreteBusiness(
			PaymentInstruction instruction);

	public PaymentProtocolDao getProtocolDao() {
		return protocolDao;
	}

	public void setProtocolDao(PaymentProtocolDao protocolDao) {
		this.protocolDao = protocolDao;
	}

	public PaymentInstructionDao getInstructionDao() {
		return instructionDao;
	}

	public void setInstructionDao(PaymentInstructionDao instructionDao) {
		this.instructionDao = instructionDao;
	}

	public AccountServiceBO getAccountServiceBO() {
		return accountServiceBO;
	}

	public void setAccountServiceBO(AccountServiceBO accountServiceBO) {
		this.accountServiceBO = accountServiceBO;
	}

	public RefundDetailDao getRefundDetailDao() {
		return refundDetailDao;
	}

	public void setRefundDetailDao(RefundDetailDao refundDetailDao) {
		this.refundDetailDao = refundDetailDao;
	}
}