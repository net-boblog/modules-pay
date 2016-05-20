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
		// 验证
		this.validateRefundMoney(protocolId, refundMoney);
		// 生成退款明细
		RefundDetail refundDetail = this.createRefundDetail(protocolId,
				refundMoney);
		// 生成退款指令
		List<PaymentInstruction> listRefundInstruction = this
				.createRefundInstruction(refundDetail.getRefundDetailId());
		return listRefundInstruction;
	}

	// /**
	// * 退款
	// *
	// * @param rppDTO
	// */
	// public void refundMoney(int protocolId, double refundMoney) {
	// // 验证
	// PaymentProtocol protocol = this.validateRefundMoney(protocolId,
	// refundMoney);
	// // 生成退款明细
	// RefundDetail refundDetail = this.createRefundDetail(protocolId,
	// refundMoney);
	// // 生成退款指令
	// List<PaymentInstruction> listRefundInstruction = this
	// .createRefundInstruction(refundDetail.getRefundDetailId());
	// // 是否全部是退款到余额
	// boolean isAllBlanceRefund = true;
	// // 已经退款金额
	// double hadRefundMoney = 0.0;
	// // 转移金额
	// for (PaymentInstruction refundInstruction : listRefundInstruction) {
	// // 设置协议状态为processing
	// refundInstruction.setStatus(PaymentInstructionStatus.PROCESSING);
	// super.updatePaymentInstruction(refundInstruction);
	// // 已经退款的金额
	// hadRefundMoney += refundInstruction.getPayMoney();
	//
	// // 如果是退款到余额
	// if (PaymentInstructionChannel.BALANCE.equals(refundInstruction
	// .getChannel())) {
	// // 当退款指令状态位为processing的时候才转移金额，初始化，或者已经关闭的话就不进行
	// if (PaymentInstructionStatus.PROCESSING
	// .equals(refundInstruction.getStatus())) {
	// logger.info("转移金额");
	// this.transferMoney(refundInstruction);
	// }
	// // 跳出该次循环，不进行下边的
	// continue;
	// }
	//
	// // 第三方渠道退款
	// isAllBlanceRefund = false;
	// // 冻结金额
	// super.freezeAccountMoney(refundInstruction);
	//
	// // 生成一方业务流水,退款这里做特殊处理，渠道放的是收款方的渠道
	// this.createOurRcdClearing(refundInstruction);
	// }
	//
	// // 全部指令都为余额渠道。就要让退款成功了
	// if (isAllBlanceRefund) {
	// // 退款成功
	// this.successRefundDetail(refundDetail.getRefundDetailId());
	// //
	// }
	//
	// // 如果款金额以及等于了交易金额，关闭协议
	// logger.info("总退款金额：" + hadRefundMoney);
	// logger.info("交易金额" + protocol.getPayMoney());
	// if (hadRefundMoney == protocol.getPayMoney()) {
	// logger.info("退款的金额已经等于了协议金额，关闭协议");
	// this.setProtocolStatus(protocolId, PaymentProtocolStatus.CLOSE);
	//
	// }
	//
	// }

	/**
	 * 判断是否因为退款满足余额而关闭协议
	 * 
	 * @param protocolId
	 * @return
	 */
	protected boolean isCloseProtocol(Integer protocolId) {
		if (protocolId == null) {
			throw new RuntimeException("protocolId不能为空");
		}
		PaymentProtocol protocol = super.findProtocolByProtocolId(protocolId);
		if (protocol == null) {
			throw new RuntimeException("protocol为空");
		}
		List<RefundDetail> refundDetails = this
				.findRefundDetailByProcolIdAndStatus(protocolId,
						RefundDetailStatus.SUCCESS);
		if (refundDetails == null || refundDetails.isEmpty()) {
			throw new RuntimeException("退款明细为空");
		}
		double hadRefundMoney = 0.0;
		for (RefundDetail refundDetail : refundDetails) {
			hadRefundMoney += refundDetail.getRefundMoney();
		}
		if (hadRefundMoney == protocol.getPayMoney()) {
			logger.info("退款的金额已经等于了协议金额，关闭协议");
			// this.setProtocolStatus(protocolId, PaymentProtocolStatus.CLOSE);
			return true;
		}
		return false;
	}

	/**
	 * 设置退款成功
	 * 
	 * @param refundDetail
	 */
	private void successRefundDetail(int refundDetailId) {
		RefundDetail refundDetail = refundDetailDao.findById(refundDetailId);
		if (!RefundDetailStatus.PROCESSING.equals(refundDetail.getStatus())) {
			throw new RuntimeException("不是正在退款的退款明细不能退款成功");
		}
		refundDetail.setStatus(RefundDetailStatus.SUCCESS);
		Date now = Time.getNow();
		refundDetail.setGmtModify(now);
		refundDetail.setVersion(refundDetail.getVersion() + 1);
		refundDetailDao.update(refundDetail);

	}

	/**
	 * 创建退款指令
	 * 
	 * @param protocolId
	 */
	private List<PaymentInstruction> createRefundInstruction(int refundDetailId) {
		// List<RefundDetail> detailList = this
		// .findRefundDetailByProcolIdAndStatus(protocolId,
		// RefundDetailStatus.PROCESSING);
		RefundDetail refundDetail = refundDetailDao.findById(refundDetailId);
		if (refundDetail == null) {
			throw new RuntimeException("创建退款指令失败：退款明细id" + refundDetailId
					+ "没有对应的退款明细");
		}
		if (RefundDetailStatus.SUCCESS.equals(refundDetail.getStatus())) {
			throw new RuntimeException("创建退款指令失败：已经退款完成的协议不能再次执行");
		}
		// 退款明细里存放的指令id
		StringBuffer refundInstructionIds = new StringBuffer("");
		// 退款指令list
		List<PaymentInstruction> listRefundInstruction = new ArrayList<PaymentInstruction>();
		// 拿到生成的退款明细然后生成指令
		// for (RefundDetail refundDetail : detailList) {
		// 查询协议下的所有支付指令，并进行排序（按照余额支付优先的原则）
		List<PaymentInstruction> listInstructions = getPaymentInstructionForRefund(refundDetail
				.getProtocolId());
		logger.info("排序完成，开始生成退款指令");
		// 剩余待退款金额
		double restToBeRefund = refundDetail.getRefundMoney();
		// 遍历所有支付指令，逐个生成退款指令
		for (PaymentInstruction paymentInstruction : listInstructions) {
			// 计算支付指令可退款金额
			double currentRefundAvailable = paymentInstruction.getPayMoney()
					- paymentInstruction.getRefundMoney();
			// 可退款金额小于0，出错
			if (currentRefundAvailable < 0) {
				throw new RuntimeException("数据错误：支付指令的可退款金额小于0，instructId="
						+ paymentInstruction.getPaymentInstructionId());
			}
			// 可退款金额等于0，跳过
			if (currentRefundAvailable == 0) {
				continue;
			}
			// 本次退款金额
			double thisRefundMoney = (restToBeRefund <= currentRefundAvailable) ? restToBeRefund
					: currentRefundAvailable;
			// 生成退款指令
			CreatePaymentInstructionDTO cpiDTO = new CreatePaymentInstructionDTO();
			// TODO 退款的途径本应该都是余额，但是由于区分退给了哪里，所以退款这里特殊区里，渠道都放着收款人的渠道
			cpiDTO.setChannel(paymentInstruction.getChannel());
			cpiDTO.setProtocolId(refundDetail.getProtocolId());
			cpiDTO.setType(PaymentInstructionType.REFUND);
			cpiDTO.setPayMoney(thisRefundMoney);
			PaymentInstruction refundInstruction = this
					.createPaymentInstruction(cpiDTO);
			listRefundInstruction.add(refundInstruction);
			// 将指令的id拼接起来
			refundInstructionIds.append(","
					+ refundInstruction.getPaymentInstructionId() + ",");
			// 更新支付指令的已退款金额
			paymentInstruction.setRefundMoney(paymentInstruction
					.getRefundMoney() + thisRefundMoney);
			instructionDao.update(paymentInstruction);
			// 将退款指令加入list中
			// 如果剩余待退款金额小于等于0，退出
			restToBeRefund = restToBeRefund - thisRefundMoney;
			if (restToBeRefund <= 0) {
				break;
			}
		}
		// }
		// 更新退款明细信息
		refundInstructionIds.insert(0, ",");
		refundInstructionIds.append(",");
		refundDetail.setRefundInstructions(refundInstructionIds.toString());
		refundDetail.setVersion(refundDetail.getVersion() + 1);
		refundDetail.setGmtModify(Time.getNow());
		refundDetailDao.update(refundDetail);
		return listRefundInstruction;
	}

	/**
	 * 为了退款将某条协议的支付的指令取出并且排序
	 * 
	 * @param protocolId
	 * @return
	 */
	private List<PaymentInstruction> getPaymentInstructionForRefund(
			Integer protocolId) {
		List<PaymentInstruction> listInstructions = this
				.findInstructionsByProtocolIdAndType(protocolId,
						PaymentInstructionType.PAY);
		// 排序一下，生成的退款余额支付的放在最前边
		logger.info("排序支付指令，blance渠道的放在最前");
		for (PaymentInstruction paymentInstruction : listInstructions) {
			if (PaymentInstructionChannel.BALANCE.equals(paymentInstruction
					.getChannel())) {
				// 拿到余额支付的下标
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
	 * 查找退款明细通过协议id和状态
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
	 * 为退款做验证
	 * 
	 * @param protocolId
	 * @param refundMoney
	 * @return
	 */
	private PaymentProtocol validateRefundMoney(Integer protocolId,
			double refundMoney) {
		if (protocolId == null) {
			throw new RuntimeException("退款校验失败：协议ID不能为空");
		}
		if (!Validation.isValidMoney(refundMoney)) {
			throw new RuntimeException("退款校验失败：金额必须大于零");
		}
		// 拿到协议对象
		PaymentProtocol protocol = this.findProtocolByProtocolId(protocolId);
		if (protocol == null) {
			throw new RuntimeException("传来的协议id没有找到协议");
		}
		// 判断协议类型
		if (!PaymentProtocolType.PAY.equals(protocol.getType())) {
			throw new RuntimeException("非支付类型不能退款");
		}
		// 判断协议状态
		if (PaymentProtocolStatus.INIT.equals(protocol.getStatus())
				|| PaymentProtocolStatus.CLOSE.equals(protocol.getStatus())) {
			throw new RuntimeException("支付协议未支付或者已经关闭");
		}
		// 判断剩余的钱是否够该次退款
		if (!this.isEnoughRefund(protocolId, refundMoney)) {
			throw new RuntimeException("剩余的钱不够该次退款");
		}
		return protocol;
	}

	/**
	 * 内部调用 生成退款明细
	 * 
	 * @param payCountId
	 * @param refundCountId
	 * @return
	 */
	private RefundDetail createRefundDetail(int protocolId, double refundMoney) {
		logger.info("开始创建退款指令");
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
		logger.info("生成了退款明细{}", refundDetail.getRefundDetailId());
		return refundDetail;
	}

	/**
	 * 通过协议id去查所有的退款明细
	 * 
	 * @param protocolId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<RefundDetail> findRefundDetailsByProtocolId(Integer protocolId) {
		if (protocolId == null) {
			throw new RuntimeException("协议id不能为空");
		}
		DetachedCriteria criteria = DetachedCriteria
				.forClass(RefundDetail.class);
		criteria.add(Restrictions.eq("protocolId", protocolId));
		// 不查废弃
		criteria.add(Restrictions.ne("status", RefundDetailStatus.ABANDON));
		return refundDetailDao.findByCriteria(criteria);
		// return refundDetailDao.findByProtocolId(protocolId);
	}

	@Override
	protected PaymentInstruction createPaymentInstruction(
			CreatePaymentInstructionDTO cpiDTO) {
		// 验证并拿到协议
		PaymentProtocol protocol = super
				.validateCreatePaymentInstruction(cpiDTO);

		if (!PaymentInstructionType.REFUND.equals(cpiDTO.getType())) {
			throw new RuntimeException("指令类型不为退款不能用退款里的创建指令方法");
		}

		Account payer = null;
		Account receiver = null;
		// 钱在卖家
		if (PaymentProtocolStatus.SUCCESS.equals(protocol.getStatus())) {
			// 支付人是卖家
			payer = accountServiceBO.findAccountByUserIdAndType(
					protocol.getReceiverId(), AccountType.CASH);

		}// 钱在担保户
		else {
			// 支付人是担保户
			payer = accountServiceBO
					.findByAccountId(SpecialAccount.guaranteeId);
		}
		// 收款人买家
		receiver = accountServiceBO.findAccountByUserIdAndType(
				protocol.getPayerId(), AccountType.CASH);
		// 调用父类重构方法
		return super.createPaymentInstruction(cpiDTO, payer.getAccountId(),
				receiver.getAccountId());
	}

	/**
	 * 通过指令id查找退款明细
	 * 
	 * @param instructionId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public RefundDetail findRefundDetailByInstructionId(Integer instructionId) {
		if (instructionId == null) {
			throw new RuntimeException("查找退款明细失败:指令id不能为空");
		}
		DetachedCriteria criteria = DetachedCriteria
				.forClass(RefundDetail.class);
		criteria.add(Restrictions.like("refundInstructions", ",,"
				+ instructionId + ",,"));
		// 不差废弃的
		criteria.add(Restrictions.ne("status", RefundDetailStatus.ABANDON));
		List<RefundDetail> listRefund = refundDetailDao
				.findByCriteria(criteria);
		if (listRefund == null || listRefund.isEmpty()) {
			return null;
		}
		// 一条指令只会在一个退款明细红存在，不可能在多个退款明细中存在
		if (listRefund.size() > 1) {
			throw new RuntimeException("查找退款明细失败：一条指令id查找到了多个退款明细");
		}
		logger.info("找到了退款明细{}", listRefund.get(0).getRefundDetailId());
		return listRefund.get(0);
	}

	// /**
	// * 一三方业务流水匹配成功
	// */
	// // @Override
	// protected void matchRcdSuccess(Integer protocolId) {
	// List<PaymentInstruction> instructions = super
	// .validateProtocolAndReturnInstruction(protocolId,
	// PaymentInstructionType.REFUND);
	//
	// // 遍历集合，让所有的指令执行成功
	// for (PaymentInstruction thisInstruction : instructions) {
	//
	// // 拿到退款明细
	// RefundDetail refundDetail = this
	// .findRefundDetailByInstructionId(thisInstruction
	// .getPaymentInstructionId());
	// if (refundDetail == null) {
	// throw new RuntimeException(
	// "退款的匹配一三方业务成功后的操作失败：instructionId没有查找出退款明细");
	// }
	// // 如果退款明细已经执行完毕,跳过
	// if (RefundDetailStatus.SUCCESS.equals(refundDetail.getStatus())) {
	// continue;
	// }
	// // 那么该指令就可以解冻
	// super.unfreezeAccountMoney(thisInstruction);
	//
	// logger.info("执行了{}号指令的{}操作成功",
	// thisInstruction.getPaymentInstructionId(),
	// thisInstruction.getType());
	//
	// // 设置退款成功
	// this.successRefundDetail(refundDetail.getRefundDetailId());
	//
	// // // 判断是否够退款协议
	// // // 取出所有的退款指令
	// // List<PaymentInstruction> refundInstructions = new
	// // ArrayList<PaymentInstruction>();
	// // // 拿到String类型的指令id
	// // String[] instructionIdsString = refundDetail
	// // .getRefundInstructions().split(",,");
	// // List<Integer> ids = new ArrayList<Integer>();
	// // // 转换成整数类型的id
	// // for (String idString : instructionIdsString) {
	// // Integer id = Integer.parseInt(idString);
	// // ids.add(id);
	// // }
	// // // 遍历所有整数类型的id并查找出对应的指令加到指令集合中
	// // for (Integer id : ids) {
	// // PaymentInstruction instruction = super.findByInstructionId(id);
	// // if (instruction == null) {
	// // throw new RuntimeException(
	// // "退款的匹配一三方业务成功后的操作失败：通过退款明细中指令id找到的指令为空");
	// // }
	// // refundInstructions.add(instruction);
	// // }
	// //
	// // if (refundInstructions.isEmpty()) {
	// // throw new RuntimeException("退款的匹配一三方业务成功后的操作失败：没有找到退款指令 ");
	// // }
	// // boolean isRefundOk = true;
	// // // 遍历所有的退款指令，检查是否都退款完成
	// // for (PaymentInstruction refundInstruction : refundInstructions) {
	// // // 如果还有为成功的指令
	// // if (!PaymentInstructionStatus.SUCCESS.equals(refundInstruction
	// // .getStatus())) {
	// // isRefundOk = false;
	// // }
	// // }
	// // // 所有退款成功
	// // if (isRefundOk) {
	// // this.successRefundDetail(refundDetail.getRefundDetailId());
	// // }
	//
	// }
	//
	// }

	/**
	 * 是否退款完毕
	 * 
	 * @param refundDetail
	 * @return
	 */
	protected boolean isEnoughRefundDetail(RefundDetail refundDetail) {
		if (refundDetail == null) {
			throw new RuntimeException("没有找到退款明细");
		}

		logger.info("退款指令有【{}】", refundDetail.getRefundInstructions());
		String instructionIdsString = refundDetail.getRefundInstructions();
		// 拿到String类型的指令id
		String[] idsString = instructionIdsString.split(",,");
		List<Integer> ids = new ArrayList<Integer>();

		// 转换成整数类型的id，从第二个开始，第一个是空字符串
		for (int i = 1; i < idsString.length; i++) {
			logger.info("退款明细里有id{}", idsString[i]);
			Integer id = Integer.parseInt(idsString[i]);
			ids.add(id);
		}
		double hadRefundMoney = 0.0;
		// 遍历所有整数类型的id并查找出对应的指令加到指令集合中
		for (Integer id : ids) {
			PaymentInstruction instruction = super.findByInstructionId(id);
			if (instruction == null) {
				throw new RuntimeException(
						"退款的匹配一三方业务成功后的操作失败：通过退款明细中指令id找到的指令为空");
			}
			// 成功执行的指令
			if (PaymentInstructionStatus.SUCCESS
					.equals(instruction.getStatus())) {
				hadRefundMoney += instruction.getPayMoney();
			}
		}
		// 退款金额大于应退款金额
		if (hadRefundMoney > refundDetail.getRefundMoney()) {
			throw new RuntimeException("退款的匹配一三方业务成功后的操作失败：通过退款明细中指令id找到的指令为空");
		}
		// 退款金额等于了退款金额
		if (hadRefundMoney == refundDetail.getRefundMoney()) {
			return true;
		}
		// 退款金额小于了退款金额
		return false;
	}

	@Override
	protected void processForConcreteBusiness(PaymentInstruction instruction) {
		PaymentProtocol protocol = super.findProtocolByProtocolId(instruction
				.getProtocolId());
		if (protocol == null) {
			throw new RuntimeException("退款核对成功后的操作失败：没有找到协议");
		}
		logger.info("退款操作指令{}", instruction.getPaymentInstructionId());
		RefundDetail refundDetail = this
				.findRefundDetailByInstructionId(instruction
						.getPaymentInstructionId());
		if (refundDetail == null) {
			throw new RuntimeException("退款核对成功后的操作失败：没有找到退款明细");
		}

		// 内部的渠道转移金钱
		if (PaymentInstructionChannel.isInnerChannel(instruction.getChannel())) {
			super.transferMoney(instruction);
		} else {
			super.unfreezeAccountMoney(instruction);
		}
		// 此次退款完毕
		if (this.isEnoughRefundDetail(refundDetail)) {
			this.successRefundDetail(refundDetail.getRefundDetailId());
			// TODO notify trade 退款成功
		}
		// 协议的钱退完了
		if (this.isCloseProtocol(protocol.getPaymentProtocolId())) {
			super.setProtocolStatus(protocol.getPaymentProtocolId(),
					PaymentProtocolStatus.CLOSE);
			// TODO　　notify trade 协议关闭
		}
	}
}
