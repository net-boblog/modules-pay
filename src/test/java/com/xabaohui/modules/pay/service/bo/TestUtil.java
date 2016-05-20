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

	// ----------------�����ǹ�����---------------------------

	/**
	 * ����Э��DTO
	 * 
	 * @return
	 */
	public CreatePaymentProtocolDTO createProtocolDTO(int userId) {
		// ���һ������id
		Integer orderId = this.createRandomOrderId();
		CreatePaymentProtocolDTO cppDTO = new CreatePaymentProtocolDTO();
		cppDTO.setOrderId(orderId);
		// �������һ����Ǯ��100Ԫ�˻�
		cppDTO.setPayerId(this.createOwnMoneyAccount());
		cppDTO.setReceiverId(userId);
		cppDTO.setPayMoney(20.0);
		cppDTO.setType(PaymentProtocolType.PAY);
		return cppDTO;
	}

	/**
	 * ����һ����Ǯ���˻������������û�id 100Ԫ
	 */
	public int createOwnMoneyAccount() {
		Integer orderId = this.createRandomOrderId();
		// ���һ���û�id
		int userId = this.createRandomUserId();
		// �����˻�
		String accountType = AccountType.CASH;
		Account account = accountServiceBO.createAccount(userId, accountType);
		// ����Э��
		CreatePaymentProtocolDTO cppDTO = new CreatePaymentProtocolDTO();
		cppDTO.setOrderId(orderId);
		cppDTO.setPayerId(null);
		cppDTO.setReceiverId(userId);
		cppDTO.setPayMoney(100.0);
		cppDTO.setType(PaymentProtocolType.RECHARGE);

		PaymentProtocol protocol = payServiceBO.createPaymentProtocol(cppDTO);
		// ����Э��
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
	 * �������һ��û�д������˻���userId
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
	 * �������֧��ָ��DTO
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
	 * ���������orderId
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
