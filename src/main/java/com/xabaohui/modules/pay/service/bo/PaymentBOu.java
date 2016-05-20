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
	 * 通过cpiDTOs得到instructions
	 * 
	 * @param cpiDTOs
	 * @return
	 */
	public List<PaymentInstruction> processGetInstructions(
			List<CreatePaymentInstructionDTO> cpiDTOs) {
		if (cpiDTOs == null || cpiDTOs.isEmpty()) {
			throw new RuntimeException("操作失败：传来的指令为空");
		}
		// 是否验证过协议状态
		boolean isValidateProtocolStatus = false;
		List<PaymentInstruction> instructions = new ArrayList<PaymentInstruction>();
		for (CreatePaymentInstructionDTO cpiDTO : cpiDTOs) {
			// 检查对象
			PaymentProtocol protocol = this.validateCpiDTO(cpiDTO);
			// 没有验证过协议状态的话，检查协议状态
			if (!isValidateProtocolStatus
					&& !PaymentProtocolStatus.INIT.equals(protocol.getStatus())) {
				throw new RuntimeException("操作失败：协议的初始状态不为init");
			}
			// 没有验证过协议状态的话，设置协议状态为processing（只第一次设置）
			// 退款不修改协议状态
			if (!PaymentInstructionType.REFUND.equals(cpiDTO.getType())
					&& !isValidateProtocolStatus) {
				logger.info("将协议{}的状态修改为了PROCESSING",
						protocol.getPaymentProtocolId());
				this.setProtocolStatus(protocol.getPaymentProtocolId(),
						PaymentProtocolStatus.PROCESSING);
				isValidateProtocolStatus = true;
			}
			// 生成指令
			PaymentInstruction instruction = this
					.createPaymentInstruction(cpiDTO);

			instructions.add(instruction);

		}

		return instructions;
	}

	/**
	 * 核心执行
	 * 
	 * @param cpiDTO
	 */
	public void process(List<PaymentInstruction> instructions) {
		// 首先执行的指令
		PaymentInstruction outerChannelInstruction = null;
		// 是否验证过协议状态
		for (PaymentInstruction instruction : instructions) {
			// 检查对象
			PaymentProtocol protocol = this
					.findProtocolByProtocolId(instruction.getProtocolId());
			// 非退款的其他操作没有验证过协议状态的话，检查协议状态
			if (!PaymentInstructionType.REFUND.equals(instruction.getType())
					&& !PaymentProtocolStatus.PROCESSING.equals(protocol
							.getStatus())) {

				throw new RuntimeException("操作失败：协议的初始状态不为processing");
			}

			// 退款和提现， 就先冻结金额,退款的话并且是外部渠道
			if (PaymentInstructionType.WITHDRAW.equals(instruction.getType())
					|| PaymentInstructionType.REFUND.equals(instruction
							.getType())
					&& PaymentInstructionChannel.isOuterChannel(instruction
							.getChannel())) {
				this.freezeAccountMoney(instruction);
			}

			// 排序并将外部支付放在最前
			if (PaymentInstructionChannel.isOuterChannel(instruction
					.getChannel())) {
				outerChannelInstruction = instruction;
				logger.info("生成{}指令【{}】,是一条外部指令",
						outerChannelInstruction.getType(),
						outerChannelInstruction.getPaymentInstructionId());
			}
		}
		// 存在外部渠道指令
		if (outerChannelInstruction != null) {
			logger.info("存在外部指令，进行了处理，并开始等待");
			this.processOne(outerChannelInstruction);
		} else {// 只有内部渠道指令
			logger.info("不存在外部指令，直接处理内部治理");
			for (PaymentInstruction instruction : instructions) {
				this.processOne(instruction);
			}
		}

	}

	/**
	 * 校验cpiDTO对象并且返回协议
	 * 
	 * @param cpiDTO
	 * @return
	 */
	protected PaymentProtocol validateCpiDTO(CreatePaymentInstructionDTO cpiDTO) {
		if (cpiDTO == null) {
			throw new RuntimeException("传来的支付指令对象是空");
		}
		if (StringUtils.isBlank(cpiDTO.getChannel())
				|| StringUtils.isBlank(cpiDTO.getType())
				|| cpiDTO.getProtocolId() == null
				|| !Validation.isValidMoney(cpiDTO.getPayMoney())) {
			throw new RuntimeException("请检查你的数据是否正确");
		}
		// 判断协议状态
		PaymentProtocol protocol = this.findProtocolByProtocolId(cpiDTO
				.getProtocolId());
		if (protocol == null
				|| PaymentProtocolStatus.CLOSE.equals(protocol.getStatus())) {
			throw new RuntimeException("没有该协议或者协议已经关闭");
		}

		return protocol;
	}

	/**
	 * 生成支付协议
	 * 
	 * @param cppDTO
	 */
	public PaymentProtocol createPaymentProtocol(CreatePaymentProtocolDTO cppDTO) {
		if (cppDTO == null) {
			throw new RuntimeException("支付协议对象是空");
		}
		// 判断传来的对象中的必要属性有无是空
		if (StringUtils.isBlank(cppDTO.getType())
				|| !Validation.isValidMoney(cppDTO.getPayMoney())) {
			throw new RuntimeException("您传来的数据有误");
		}

		// 验证支付状态订单号是否重复
		if (PaymentProtocolType.PAY.equals(cppDTO.getType())
				&& this.protocolDao.findByOrderId(cppDTO.getOrderId()) != null) {
			throw new RuntimeException("创建协议失败：您的订单已经创建过协议了");
		}

		Date now = Time.getNow();
		PaymentProtocol protocol = new PaymentProtocol();
		// 将属性值从cppDTO拷贝到protocol中
		BeanUtils.copyProperties(cppDTO, protocol);
		protocol.setStatus(PaymentProtocolStatus.INIT);
		protocol.setGmtCreate(now);
		protocol.setGmtModify(now);
		protocol.setVersion(1);
		protocolDao.save(protocol);
		logger.info("生成了协议" + protocol.getPaymentProtocolId());
		return protocol;
	}

	// 检验并且取到协议对象
	protected PaymentProtocol validateCreatePaymentInstruction(
			CreatePaymentInstructionDTO cpiDTO) {
		if (cpiDTO == null) {
			throw new RuntimeException("cpiDTO对象不能为空");
		}

		if (StringUtils.isBlank(cpiDTO.getChannel())
				|| cpiDTO.getProtocolId() == null
				|| !Validation.isValidMoney(cpiDTO.getPayMoney())
				|| StringUtils.isBlank(cpiDTO.getType())) {
			throw new RuntimeException("传来的cpiDTO对象有问题");
		}
		PaymentProtocol protocol = findProtocolByProtocolId(cpiDTO
				.getProtocolId());
		// 检验协议状态
		if (PaymentProtocolStatus.CLOSE.equals(protocol.getStatus())) {
			throw new RuntimeException("协议已经关闭");
		}
		logger.info("拿到了协议对象 " + protocol.getPaymentProtocolId());
		return protocol;
	}

	/**
	 * 生成支付指令
	 * 
	 * @param cpiDTO
	 */
	protected abstract PaymentInstruction createPaymentInstruction(
			CreatePaymentInstructionDTO cpiDTO);

	/**
	 * 协议的金额是否和已经成功的指令金额总和相等
	 * 
	 * @param protocolId
	 * @param type
	 * @return
	 */
	protected boolean isEnoughMoneyToPaidProtocol(int protocolId,
			String instructionType) {
		PaymentProtocol protocol = this.findProtocolByProtocolId(protocolId);
		// 如果订单关闭
		if (PaymentProtocolStatus.CLOSE.equals(protocol.getStatus())) {
			throw new RuntimeException("订单已经关闭");
		}
		logger.info("为调整协议状态比较金额");
		// 如果是确定收款的话，要加上已经退款金额
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
			// 只加支付成功，状态为success的
			if (PaymentInstructionStatus.SUCCESS.equals(paymentInstruction
					.getStatus())) {
				payMoney += paymentInstruction.getPayMoney();
			}
		}
		logger.info("已经退款金额为:  " + hadRefundMoney);
		logger.info(instructionType + "的金额为：" + payMoney);
		logger.info(instructionType + "的总共金额为：" + (payMoney + hadRefundMoney));
		logger.info(instructionType + "总共需要的金额为：" + protocol.getPayMoney());
		if (hadRefundMoney + payMoney == protocol.getPayMoney()) {
			logger.info("钱和协议里的钱对等了");
			return true;
		} else {
			logger.info("钱和协议里的钱不对等");
			return false;
		}
	}

	/**
	 * 生成支付指令
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
		// 复制属性
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
		// 保存支付协议
		instructionDao.save(instruction);
		return instruction;
	}

	public PaymentProtocol findProtocolByProtocolId(Integer id) {
		if (id == null) {
			throw new RuntimeException("传来的Id有误");
		}
		return protocolDao.findById(id);
	}

	/**
	 * 通过协议id查指令
	 * 
	 * @param protocolId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected List<PaymentInstruction> findInstructionsByProtocolId(
			Integer protocolId) {
		if (protocolId == null) {
			throw new RuntimeException("传来的protocolId不能为空");
		}
		DetachedCriteria criteria = DetachedCriteria
				.forClass(PaymentInstruction.class);
		criteria.add(Restrictions.eq("protocolId", protocolId));
		// 不差失败状态的
		criteria.add(Restrictions
				.ne("status", PaymentInstructionStatus.FAILURE));
		return instructionDao.findByCriteria(criteria);
	}

	/**
	 * 将钱通过协议转账 支付时候的转账
	 * 
	 * @param protocol
	 */
	protected void transferMoney(PaymentInstruction instruction) {
		if (instruction == null) {
			throw new RuntimeException("支付指令是空");
		}
		if (instruction.getPaymentInstructionId() == null
				|| instruction.getPayerId() == null
				|| instruction.getReceiverId() == null
				|| !Validation.isValidMoney(instruction.getPayMoney())
				|| StringUtils.isBlank(instruction.getStatus())) {
			throw new RuntimeException("传来的参数有误" + instruction);
		}
		if (!instruction.getStatus()
				.equals(PaymentInstructionStatus.PROCESSING)) {
			throw new RuntimeException("指令不是processing化状态，不能执行转移金额");
		}
		// 指令中的钱已经退完了,就不往下执行了
		if (instruction.getPayMoney() == instruction.getRefundMoney()) {
			return;
		}

		Account payer = accountServiceBO.findByAccountId(instruction
				.getPayerId());
		Account recevier = accountServiceBO.findByAccountId(instruction
				.getReceiverId());
		if (payer == null || recevier == null) {
			throw new RuntimeException("支付人或者收款人未找到 ");
		}
		// 验证账户
		if (payer.getStatus().equals(AccountStatus.ABANDON)
				|| recevier.getStatus().equals(AccountStatus.ABANDON)) {
			throw new RuntimeException("账户已经废弃");
		}
		// 支付用户的可用余额
		double payerAvailableMoney = accountServiceBO
				.findAvailableByAccountId(payer.getAccountId());
		// 账户操作不为充值，且账户可用户余额比要支付的钱少，不够支付
		if (!PaymentInstructionType.RECHARGE.equals(instruction.getType())
				&& payerAvailableMoney < instruction.getPayMoney()) {
			throw new RuntimeException("余额不够支付");
		}

		// 修改账户金额
		accountServiceBO.addAccountAmount(instruction);
		accountServiceBO.reduceAccountAmount(instruction);
		// 指令执行完毕
		instruction.setStatus(PaymentInstructionStatus.SUCCESS);
		// 更新数据
		this.updatePaymentInstruction(instruction);
		logger.info("{}类型的指令【{}】的转移金额执行完毕", instruction.getType(),
				instruction.getPaymentInstructionId());
	}

	/**
	 * 修改支付指令
	 * 
	 * @param instruction
	 */
	protected void updatePaymentInstruction(PaymentInstruction instruction) {
		if (instruction == null) {
			throw new RuntimeException("传来的指令对象是空");
		}
		if (instruction.getPaymentInstructionId() == null) {
			throw new RuntimeException("请检查指令id是否正确");
		}
		instruction.setGmtModify(Time.getNow());
		instruction.setVersion(instruction.getVersion() + 1);
		instructionDao.update(instruction);
	}

	public PaymentInstruction findByInstructionId(Integer id) {
		if (id == null) {
			throw new RuntimeException("传来的指令id值不对");
		}
		return instructionDao.findById(id);
	}

	/**
	 * 通过协议id和类型查找指令
	 * 
	 * @param protocolId
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected List<PaymentInstruction> findInstructionsByProtocolIdAndType(
			Integer protocolId, String type) {
		if (protocolId == null || StringUtils.isBlank(type)) {
			throw new RuntimeException("请检查类型和协议id");
		}

		DetachedCriteria criteria = DetachedCriteria
				.forClass(PaymentInstruction.class);
		criteria.add(Restrictions.eq("protocolId", protocolId));
		criteria.add(Restrictions.eq("type", type));
		// 不查废弃
		criteria.add(Restrictions
				.ne("status", PaymentInstructionStatus.FAILURE));

		List<PaymentInstruction> listInstruction = instructionDao
				.findByCriteria(criteria);
		logger.info("找到了" + listInstruction.size() + "满足条件的指令");
		return listInstruction;
	}

	/**
	 * 协议执行成功
	 * 
	 * @param protocolId
	 */
	protected void setProtocolStatus(int protocolId, String status) {
		PaymentProtocol paymentProtocol = protocolDao.findById(protocolId);
		paymentProtocol.setStatus(status);
		paymentProtocol.setGmtModify(Time.getNow());
		paymentProtocol.setVersion(paymentProtocol.getVersion() + 1);
		protocolDao.update(paymentProtocol);
		logger.info("协议" + protocolId + "的状态修改为" + status);
	}

	/**
	 * 支付协议中所有的现金支付是否够退款
	 * 
	 * @param rppDTO
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean isEnoughRefund(Integer protocolId, Double refundMoney) {
		if (protocolId == null || !Validation.isValidMoney(refundMoney)) {
			throw new RuntimeException("请检查传来的值是否正确");
		}
		// 拿到协议对象
		PaymentProtocol protocol = this.findProtocolByProtocolId(protocolId);
		if (protocol == null) {
			throw new RuntimeException("传来的协议id没有找到协议");
		}
		// 已经退款金额
		double hadRefundMoney = 0.0;
		List<RefundDetail> listRefundDetail = refundDetailDao
				.findByProtocolId(protocolId);
		// 获得所有已退款的金额总和
		for (RefundDetail refundDetail : listRefundDetail) {
			hadRefundMoney += refundDetail.getRefundMoney();
		}
		logger.info("需要退款的金额{}，还能退的金额{}", refundMoney,
				(protocol.getPayMoney() - hadRefundMoney));
		// 协议的总额不足以及退款的金额以及这次要退款的金额
		if (protocol.getPayMoney() < hadRefundMoney + refundMoney) {
			return false;
		}
		return true;
	}

	/**
	 * 解冻冻结金额
	 * 
	 * @param instruction
	 */
	protected void freezeAccountMoney(PaymentInstruction instruction) {

		if (PaymentInstructionStatus.FAILURE.equals(instruction.getStatus())
				|| PaymentInstructionStatus.SUCCESS.equals(instruction
						.getStatus())) {
			throw new RuntimeException("冻结金额失败：指令已经执行完毕或者已经失败");
		}
		// 冻结金额
		accountServiceBO
				.freezeMoneyOperation(instruction, FreezeLogType.FREEZE);
		// // 协议正在处理
		// this.setProtocolStatus(instruction.getProtocolId(),
		// PaymentProtocolStatus.PROCESSING);
	}

	/**
	 * 解冻
	 * 
	 * @param protocolId
	 * @param protocolType
	 */
	protected void unfreezeAccountMoney(PaymentInstruction instruction) {
		if (instruction == null) {
			throw new RuntimeException("解冻失败：传来的指令为空");
		}
		this.validateInstructionForCreateClearing(instruction);
		if (PaymentInstructionStatus.FAILURE.equals(instruction.getStatus())) {
			throw new RuntimeException("解冻失败：已经失败的指令不能再修改状态");
		}
		// 提现或者退款协议在解冻时转账
		this.transferMoney(instruction);// 协议已经执行 转账了，指令状态为success
		// 解冻金额
		accountServiceBO.freezeMoneyOperation(instruction,
				FreezeLogType.UNFREEZE);

		this.updatePaymentInstruction(instruction);
	}

	/**
	 * 生成三方业务明细并比较，然后处理
	 * 
	 * @param exoRcd
	 */
	public void processExoRcdClearing(CreateExoRcdClearingDTO cercDTO) {
		// 生成三方业务流水
		ExoRcdClearing exoRcdClearing = this.createExoRcdClearing(cercDTO);
		// 拿到一方业务流水
		OurRcdClearing ourRcdClearing = ourRcdClearingDao
				.findById(exoRcdClearing.getOurRcdId());
		PaymentInstruction instruction = instructionDao.findById(exoRcdClearing
				.getInstructionId());
		if (!PaymentInstructionStatus.PROCESSING
				.equals(instruction.getStatus())) {
			throw new RuntimeException("修改指令状态失败:指令状态不为processing");
		}
		// 匹配成功
		if (this.matchRcdClearing(ourRcdClearing, exoRcdClearing)) {
			this.matchRcdSuccess(exoRcdClearing.getProtocolId(),
					instruction.getType());
			// 设置指令执行成功,针对充值等先执行了，才匹配一三方业务流水的
			instruction.setStatus(PaymentInstructionStatus.SUCCESS);
			this.updatePaymentInstruction(instruction);
			logger.info("一三方业务流水匹配成功");
		} else {// 匹配失败
			// 拿到所有的支付指令
			List<PaymentInstruction> instructions = this
					.findInstructionsByProtocolIdAndType(
							ourRcdClearing.getProtocolId(),
							PaymentInstructionType.PAY);
			// 设置所有的支付指令为失败
			for (PaymentInstruction instruct : instructions) {
				// 设置instruction 状态为失败
				instruct.setStatus(PaymentInstructionStatus.FAILURE);
				this.updatePaymentInstruction(instruct);
				// 设置提现退款解冻
				// 充值和提现， 就解冻金额
				if (PaymentInstructionType.WITHDRAW.equals(instruction
						.getType())
						|| PaymentInstructionType.REFUND.equals(instruction
								.getType())) {
					accountServiceBO.freezeMoneyOperation(instruction,
							FreezeLogType.UNFREEZE);
				}
			}
			logger.info("一三方业务流水匹配失败");
		}

	}

	/**
	 * 生成三方业务明细并比较，然后处理
	 * 
	 * @param exoRcd
	 */
	public void processExoRcdClearing(String trade_no, Integer instructionId) {
		if (StringUtils.isBlank(trade_no) || instructionId == null) {
			throw new RuntimeException("核对一三方业务流水失败：传来的交易号或者指令号为空");
		}

		CreateExoRcdClearingDTO cercDTO = new CreateExoRcdClearingDTO();
		// 取得协议
		PaymentInstruction instruction = this
				.findByInstructionId(instructionId);
		// 取到一方业务流水
		OurRcdClearing ourRcdClearing = ourRcdClearingDao
				.findByInstructionId(instructionId);
		// 交易号设置
		cercDTO.setExoRef(trade_no);
		cercDTO.setInstructionId(instructionId);
		cercDTO.setProtocolId(instruction.getProtocolId());
		cercDTO.setTxType(instruction.getType());
		// 设置一方业务流水
		cercDTO.setOurRcdId(ourRcdClearing.getOurRcdId());
		cercDTO.setTxChannel(instruction.getChannel());
		cercDTO.setTxMoney(instruction.getPayMoney());
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!" + cercDTO);
		// 生成三方业务流水
		ExoRcdClearing exoRcdClearing = this.createExoRcdClearing(cercDTO);
		// 拿到一方业务流水
		// OurRcdClearing ourRcdClearing = ourRcdClearingDao
		// .findById(exoRcdClearing.getOurRcdId());
		// PaymentInstruction instruction =
		// instructionDao.findById(exoRcdClearing
		// .getInstructionId());

		if (!PaymentInstructionStatus.PROCESSING
				.equals(instruction.getStatus())) {
			throw new RuntimeException("修改指令状态失败:指令状态不为processing");
		}
		// 匹配成功
		if (this.matchRcdClearing(ourRcdClearing, exoRcdClearing)) {
			this.matchRcdSuccess(exoRcdClearing.getProtocolId(),
					instruction.getType());
			// 设置指令执行成功,针对充值等先执行了，才匹配一三方业务流水的
			instruction.setStatus(PaymentInstructionStatus.SUCCESS);
			this.updatePaymentInstruction(instruction);
			logger.info("一三方业务流水匹配成功");
		} else {// 匹配失败
			// 拿到所有的支付指令
			List<PaymentInstruction> instructions = this
					.findInstructionsByProtocolIdAndType(
							ourRcdClearing.getProtocolId(),
							PaymentInstructionType.PAY);
			// 设置所有的支付指令为成功
			for (PaymentInstruction instruct : instructions) {
				// 设置instruction 状态为失败
				instruct.setStatus(PaymentInstructionStatus.FAILURE);
				this.updatePaymentInstruction(instruct);
				// 设置提现退款解冻
				// 充值和提现， 就解冻金额
				if (PaymentInstructionType.WITHDRAW.equals(instruction
						.getType())
						|| PaymentInstructionType.REFUND.equals(instruction
								.getType())) {
					accountServiceBO.freezeMoneyOperation(instruction,
							FreezeLogType.UNFREEZE);
				}
			}
			logger.info("一三方业务流水匹配失败");
		}

	}

	/**
	 * 核对成功后的操作
	 * 
	 * @param protocolId
	 * @param instructionType
	 */
	protected void matchRcdSuccess(Integer protocolId, String instructionType) {
		// 验证并查找指令
		List<PaymentInstruction> instructions = this
				.validateProtocolAndReturnInstruction(protocolId,
						instructionType);

		for (PaymentInstruction instruction : instructions) {
			// 第三方支付的
			if (PaymentInstructionChannel.isOuterChannel(instruction
					.getChannel())) {
				this.processForConcreteBusiness(instruction);
			}// 内部渠道支付
			else {
				// 如果内部渠道的指令状态不为init，就跳过
				if (!PaymentInstructionStatus.INIT.equals(instruction
						.getStatus())) {
					continue;
				}
				// 对内部渠道指令执行应有的操作
				this.processOne(instruction);
			}
		}
	}

	/**
	 * 为流水校验指令
	 * 
	 * @param instruction
	 * @return
	 */
	protected void validateInstructionForCreateClearing(
			PaymentInstruction instruction) {
		if (instruction == null) {
			throw new RuntimeException("指令为空");
		}
		if (instruction.getPaymentInstructionId() == null
				|| instruction.getProtocolId() == null
				|| !Validation.isValidMoney(instruction.getPayMoney())
				|| StringUtils.isBlank(instruction.getType())
				|| StringUtils.isBlank(instruction.getChannel())) {
			throw new RuntimeException("指令中某属性值错误");
		}
	}

	/**
	 * 生成一方业务流水
	 * 
	 * @param instruction
	 */
	protected OurRcdClearing createOurRcdClearing(PaymentInstruction instruction) {
		// 验证指令
		this.validateInstructionForCreateClearing(instruction);
		OurRcdClearing clearing = new OurRcdClearing();
		clearing.setInstructionId(instruction.getPaymentInstructionId());
		clearing.setProtocolId(instruction.getProtocolId());
		clearing.setTxType(instruction.getType());
		// 针对具体 的业务查找具体的渠道
		// 设置渠道
		clearing.setTxChannel(instruction.getChannel());

		clearing.setTxMoney(instruction.getPayMoney());
		Date now = Time.getNow();
		clearing.setGmtCreate(now);
		clearing.setGmtModify(now);
		clearing.setVersion(1);
		ourRcdClearingDao.save(clearing);
		logger.info("生成了一方业务流水【{}】", clearing.getOurRcdId());
		return clearing;
	}

	/**
	 * 生成三方业务流水
	 * 
	 * @param ourRcdId
	 * @param exoRef
	 * @return
	 */
	public ExoRcdClearing createExoRcdClearing(CreateExoRcdClearingDTO cercDTO) {
		if (cercDTO == null) {
			throw new RuntimeException("生成三方业务流水失败：传来的cercDTO为空");
		}

		if (StringUtils.isBlank(cercDTO.getExoRef())
				|| cercDTO.getInstructionId() == null
				|| cercDTO.getProtocolId() == null
				|| cercDTO.getOurRcdId() == null
				|| StringUtils.isBlank(cercDTO.getTxChannel())
				|| StringUtils.isBlank(cercDTO.getTxType())
				|| !Validation.isValidMoney(cercDTO.getTxMoney())) {
			throw new RuntimeException("生成三方业务流水失败：您传来的cercDTO对象中值不正确");
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
	 * 创建一方资金流水
	 * 
	 * @param ourRcdId
	 * @return
	 */
	protected OurFsClearing createOurFsClearing(OurRcdClearing ourRcdClearing) {
		OurFsClearing ourFsClearing = new OurFsClearing();
		if (ourRcdClearing == null) {
			throw new RuntimeException("创建一方资金流水失败：一方业务流水不能为空");
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
	 * 生成三方资金流水
	 * 
	 * @param cefcDTO
	 * @return
	 */
	public ExoFsClearing createExoFsClearing(CreateExoFsClearingDTO cefcDTO) {
		if (cefcDTO == null) {
			throw new RuntimeException("生成三方业务流水失败：传来的cefcDTO为空");
		}
		if (StringUtils.isBlank(cefcDTO.getExoRef())
				|| cefcDTO.getInstructionId() == null
				|| cefcDTO.getProtocolId() == null
				|| cefcDTO.getOurRcdId() == null
				|| StringUtils.isBlank(cefcDTO.getTxChannel())
				|| StringUtils.isBlank(cefcDTO.getTxType())
				|| Validation.isValidMoney(cefcDTO.getTxMoney())
				|| Validation.isValidMoney(cefcDTO.getExoFee())) {
			throw new RuntimeException("生成三方业务流水失败：您传来的cefcDTO对象中值不正确");
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
	 * 比较一三方业务流水是否匹配
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
	 * 比较一方资金流水和三方资金流水是否匹配
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
	 * 检测指令id并返回指令
	 * 
	 * @param instructionId
	 * @return
	 */
	protected PaymentInstruction validateInstructionId(Integer instructionId) {
		if (instructionId == null) {
			throw new RuntimeException("一方和三方业务匹配后操作失败：指令id为空");
		}
		PaymentInstruction instruction = this
				.findByInstructionId(instructionId);
		if (instruction == null) {
			throw new RuntimeException("一方和三方业务匹配后操作失败：传来的指令id查到的指令为空");
		}
		return instruction;
	}

	/**
	 * 验证协议id并且返回指令集合
	 * 
	 * @param protocolId
	 * @param instructionType
	 * @return
	 */
	protected List<PaymentInstruction> validateProtocolAndReturnInstruction(
			Integer protocolId, String instructionType) {
		if (protocolId == null) {
			throw new RuntimeException("一三方业务流水匹配成功后操作的失败：协议id不能为空");
		}
		// 验证并查找指令
		List<PaymentInstruction> instructions = this
				.findInstructionsByProtocolIdAndType(protocolId,
						instructionType);
		if (instructions == null || instructions.isEmpty()) {
			throw new RuntimeException("一三方业务流水匹配成功后操作的失败：指令不能为空");
		}
		return instructions;
	}

	/**
	 * 对指令的逐条执行
	 * 
	 * @param instruction
	 */
	public void processOne(PaymentInstruction instruction) {
		// invoke outer Api 调用外部接口
		if (PaymentInstructionChannel.isOuterChannel(instruction.getChannel())) {
			// TODO invoke
		}

		// 创建具体类型的一方业务流水
		this.createOurRcdClearing(instruction);
		// 设置协议状态为processing
		instruction.setStatus(PaymentInstructionStatus.PROCESSING);
		this.updatePaymentInstruction(instruction);
		logger.info("指令【{}】的状态变成了processing",
				instruction.getPaymentInstructionId());
		// 如果是内部操作，直接成功
		if (PaymentInstructionChannel.isInnerChannel(instruction.getChannel())) {
			this.processForConcreteBusiness(instruction);
		}
	}

	/**
	 * 针对具体的业务执行具体的工序
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