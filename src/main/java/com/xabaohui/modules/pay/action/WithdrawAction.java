/**
 * 
 */
package com.xabaohui.modules.pay.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.xabaohui.modules.pay.bean.PaymentProtocol;
import com.xabaohui.modules.pay.service.PayService;

/**
 * @author YRee
 * 
 */
@Controller
@RequestMapping(value = "withdraw")
public class WithdrawAction {
	@Autowired
	private PayService payService;

	/**
	 * 创建提现协议
	 * 
	 * @param userId
	 * @param withdrawMoney
	 * @return
	 */
	@RequestMapping(value = "/createWithdrawProtocol")
	public ModelAndView createWithdrawProtocol(String userId,
			String withdrawMoney) {
		// 都为空
		if (userId == null || withdrawMoney == null) {
		}
		int id = Integer.parseInt(userId);
		Double money = Double.parseDouble(withdrawMoney);
		// 创建协议
		PaymentProtocol withdrawProtocol = payService.createWithdrawProtocol(
				id, money);
		return new ModelAndView("withdraw/withdraw", "withdrawProtocol",
				withdrawProtocol);
	}

	@RequestMapping(value = "withdraw")
	public String withdraw(String protocolId) {
		Integer id = Integer.parseInt(protocolId);
		// payService.withdraw(id, PaymentInstructionChannel.ALIPAY);
		return "TestOk";
	}

	/**
	 * 确定提现成功
	 * 
	 * @param protocolId
	 * @return
	 */
	@RequestMapping(value = "confirmWithdraw")
	public String confirmWithdraw(String protocolId) {
		int id = Integer.parseInt(protocolId);
		// TODO 确定核对成功
		// payService.confirmWithdraw(id);
		return "TestOk";
	}
}
