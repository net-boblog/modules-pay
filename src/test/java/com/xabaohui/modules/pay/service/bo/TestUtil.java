/**
 * 
 */
package com.xabaohui.modules.pay.service.bo;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.xabaohui.modules.pay.bean.Account;
import com.xabaohui.modules.pay.bean.PaymentInstruction;
import com.xabaohui.modules.pay.bean.PaymentProtocol;
import com.xabaohui.modules.pay.bean.SpecialAccount;
import com.xabaohui.modules.pay.bean.channel.PaymentInstructionChannel;
import com.xabaohui.modules.pay.bean.type.AccountType;
import com.xabaohui.modules.pay.bean.type.PaymentInstructionType;
import com.xabaohui.modules.pay.bean.type.PaymentProtocolType;
import com.xabaohui.modules.pay.dto.CreatePaymentInstructionDTO;
import com.xabaohui.modules.pay.dto.CreatePaymentProtocolDTO;

/**
 * @author YRee
 * 
 */
@Component
public class TestUtil {
	protected static Logger logger = LoggerFactory
			.getLogger(TransferAccountMoneyBOTest.class);
	@Resource
	private PayServiceBO payServiceBO;
	@Resource
	private AccountServiceBO accountServiceBO;
	@Resource
	private TransferAccountMoneyBO transferAccountMoneyBO;

	// ----------------以下是工具类---------------------------

	/**
	 * 生成协议DTO
	 * 
	 * @return
	 */
	public CreatePaymentProtocolDTO createProtocolDTO(int userId) {
		// 随机一个订单id
		Integer orderId = this.createRandomOrderId();
		CreatePaymentProtocolDTO cppDTO = new CreatePaymentProtocolDTO();
		cppDTO.setOrderId(orderId);
		// 随机生成一个有钱的100元账户
		cppDTO.setPayerId(this.createOwnMoneyAccount());
		cppDTO.setReceiverId(userId);
		cppDTO.setPayMoney(20.0);
		cppDTO.setType(PaymentProtocolType.PAY);
		return cppDTO;
	}

	/**
	 * 创建一个有钱的账户，并返回其用户id 100元
	 */
	public int createOwnMoneyAccount() {
		Integer orderId = this.createRandomOrderId();
		// 随机一个用户id
		int userId = this.createRandomUserId();
		// 创建账户
		String accountType = AccountType.CASH;
		Account account = accountServiceBO.createAccount(userId, accountType);
		// 创建协议
		CreatePaymentProtocolDTO cppDTO = new CreatePaymentProtocolDTO();
		cppDTO.setOrderId(orderId);
		cppDTO.setPayerId(null);
		cppDTO.setReceiverId(userId);
		cppDTO.setPayMoney(100.0);
		cppDTO.setType(PaymentProtocolType.RECHARGE);

		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		// 创建协议
		CreatePaymentInstructionDTO cpiDTO = new CreatePaymentInstructionDTO();
		cpiDTO.setChannel(PaymentInstructionChannel.BALANCE);
		cpiDTO.setPayMoney(100.0);
		cpiDTO.setProtocolId(protocol.getPaymentProtocolId());
		cpiDTO.setType(PaymentInstructionType.RECHARGE);
		PaymentInstruction instruction = payServiceBO.createPaymentInstruction(
				cpiDTO, SpecialAccount.bankpay, account.getAccountId());

		accountServiceBO.addAccountAmount(instruction);
		return userId;
	}

	/**
	 * 随机创建一个没有创建过账户的userId
	 * 
	 * @return
	 */
	public Integer createRandomUserId() {
		List<Account> listAccount = new ArrayList<Account>();
		Integer randomId = null;
		do {
			randomId = ((Double) (Math.random() * 10000)).intValue();
			listAccount = this.accountServiceBO.findAccountsByUserId(randomId);
		} while (!listAccount.isEmpty());
		return randomId;
	}

	/**
	 * 生成余额支付指令DTO
	 * 
	 * @param protocolId
	 * @return
	 */
	public CreatePaymentInstructionDTO createInstructionDTO(int protocolId) {
		CreatePaymentInstructionDTO cpiDTO = new CreatePaymentInstructionDTO();
		cpiDTO.setChannel(PaymentInstructionChannel.BALANCE);
		cpiDTO.setPayMoney(20.0);
		cpiDTO.setProtocolId(protocolId);
		cpiDTO.setType(PaymentProtocolType.PAY);
		return cpiDTO;
	}

	/**
	 * 生成随机的orderId
	 * 
	 * @return
	 */
	public Integer createRandomOrderId() {
		PaymentProtocol pp = null;
		Integer randomId = null;
		do {
			randomId = ((Double) (Math.random() * 10000)).intValue();
			pp = this.payServiceBO.findProtocolByOrderId(randomId);
		} while (pp != null);
		return randomId;
	}
}
