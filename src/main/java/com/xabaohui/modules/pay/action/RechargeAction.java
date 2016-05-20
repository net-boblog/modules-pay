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
	 * ������ֵЭ��
	 * 
	 * @param userId
	 * @param rechargeMoney
	 * @return
	 */
	@RequestMapping(value = "/createRechargeProtocol")
	public String createRechargeProtocol(String userId, String rechargeMoney,
			HttpServletRequest request) {
		// ��Ϊ��
		if (userId == null || rechargeMoney == null) {
		}
		int id = Integer.parseInt(userId);

		Double money = Double.parseDouble(rechargeMoney);

		// ����Э��
		PaymentProtocol rechargeProtocol = payService.createRechargeProtocol(
				id, money);
		CreatePaymentInstructionDTO cpiDTO = new CreatePaymentInstructionDTO();
		// ���г�ֵ����
		cpiDTO.setProtocolId(rechargeProtocol.getPaymentProtocolId());
		cpiDTO.setPayMoney(money);
		cpiDTO.setChannel(PaymentInstructionChannel.ALIPAY);
		cpiDTO.setType(PaymentInstructionType.RECHARGE);
		List<CreatePaymentInstructionDTO> cpiDTOs = new ArrayList<CreatePaymentInstructionDTO>();
		cpiDTOs.add(cpiDTO);
		// ����ָ��
		PaymentInstruction rechargeInstruction = payService.recharge(cpiDTOs);
		ModelAndView mv = new ModelAndView();
		// �����������
		request.setAttribute("WIDout_trade_no",
				rechargeInstruction.getPaymentInstructionId() + "");
		// ������������
		request.setAttribute("WIDsubject", rechargeInstruction.getType()
				+ rechargeInstruction.getPayMoney());
		// �����������
		request.setAttribute("WIDtotal_fee", rechargeInstruction.getPayMoney()
				+ "");
		// ������������
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
	 * ��ֵ
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
	 * ȷ����ֵ�ɹ�
	 * 
	 * @param protocolId
	 * @return
	 */
	@RequestMapping(value = "confirmRecharge")
	public String confirmRecharge(String trade_no, String out_trade_no) {
		if (StringUtils.isBlank(trade_no) || StringUtils.isBlank(out_trade_no)) {
			throw new RuntimeException("ȷ��֧��ʧ�ܣ������Ľ��׺Ż���ָ���Ϊ��");
		}
		int instructionId = Integer.parseInt(out_trade_no);
		payService.processExoRcdClearingForRecharge(trade_no, instructionId);
		return "TestOk";
	}
}
