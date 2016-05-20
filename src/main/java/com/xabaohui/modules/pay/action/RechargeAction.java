/**
 * 
 */
package com.xabaohui.modules.pay.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.xabaohui.modules.pay.bean.PaymentInstruction;
import com.xabaohui.modules.pay.bean.PaymentProtocol;
import com.xabaohui.modules.pay.bean.channel.PaymentInstructionChannel;
import com.xabaohui.modules.pay.bean.type.PaymentInstructionType;
import com.xabaohui.modules.pay.dto.CreatePaymentInstructionDTO;
import com.xabaohui.modules.pay.service.PayService;

/**
 * @author YRee
 * 
 */
@Controller
@RequestMapping(value = "/recharge")
public class RechargeAction {
	@Autowired
	private PayService payService;

	/**
	 * 创建充值协议
	 * 
	 * @param userId
	 * @param rechargeMoney
	 * @return
	 */
	@RequestMapping(value = "/createRechargeProtocol")
	public String createRechargeProtocol(String userId, String rechargeMoney,
			HttpServletRequest request) {
		// 都为空
		if (userId == null || rechargeMoney == null) {
		}
		int id = Integer.parseInt(userId);

		Double money = Double.parseDouble(rechargeMoney);

		// 创建协议
		PaymentProtocol rechargeProtocol = payService.createRechargeProtocol(
				id, money);
		CreatePaymentInstructionDTO cpiDTO = new CreatePaymentInstructionDTO();
		// 进行充值动作
		cpiDTO.setProtocolId(rechargeProtocol.getPaymentProtocolId());
		cpiDTO.setPayMoney(money);
		cpiDTO.setChannel(PaymentInstructionChannel.ALIPAY);
		cpiDTO.setType(PaymentInstructionType.RECHARGE);
		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		cpiDTOs.add(cpiDTO);
		// 生成指令
		PaymentInstruction rechargeInstruction = payService.recharge(cpiDTOs);
		ModelAndView mv = new ModelAndView();
		// 订单编号设置
		request.setAttribute("WIDout_trade_no",
				rechargeInstruction.getPaymentInstructionId() + "");
		// 订单名称设置
		request.setAttribute("WIDsubject", rechargeInstruction.getType()
				+ rechargeInstruction.getPayMoney());
		// 订单金额设置
		request.setAttribute("WIDtotal_fee", rechargeInstruction.getPayMoney()
				+ "");
		// 订单描述设置
		request.setAttribute(
				"WIDbody",
				"userId" + rechargeProtocol.getReceiverId()
						+ rechargeInstruction.getType()
						+ rechargeInstruction.getPayMoney());
		// mv.setViewName("recharge/alipayapi");
		// new ModelAndView("recharge/recharge", "rechargeProtocol",
		// rechargeProtocol);
		return "alipay/alipayapi";
	}

	/**
	 * 充值
	 * 
	 * @param protocolId
	 * @param channel
	 * @return
	 */
	@RequestMapping(value = "recharge")
	public String recharge(String protocolId, String channel) {
		int id = Integer.parseInt(protocolId);
		// payService.
		// payService.recharge(id, channel);
		return "TestOk";
	}

	/**
	 * 确定充值成功
	 * 
	 * @param protocolId
	 * @return
	 */
	@RequestMapping(value = "confirmRecharge")
	public String confirmRecharge(String trade_no, String out_trade_no) {
		if (StringUtils.isBlank(trade_no) || StringUtils.isBlank(out_trade_no)) {
			throw new RuntimeException("确定支付失败：传来的交易号或者指令号为空");
		}
		int instructionId = Integer.parseInt(out_trade_no);
		payService.processExoRcdClearingForRecharge(trade_no, instructionId);
		return "TestOk";
	}
}
