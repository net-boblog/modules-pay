/**
 * 
 */
package com.xabaohui.modules.pay.dto;

/**
 * @author YRee
 * 
 */
public class CreateFreezeLogDTO {
	/**
	 * �����˻�
	 */
	private Integer accountId;
	/**
	 * ������
	 */
	private double frozenMoney;
	/**
	 * ��ˮ
	 */
	private int bizRef;
	/**
	 * ������ˮ
	 */
	private String operateType;

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	public double getFrozenMoney() {
		return frozenMoney;
	}

	public void setFrozenMoney(double frozenMoney) {
		this.frozenMoney = frozenMoney;
	}

	public int getBizRef() {
		return bizRef;
	}

	public void setBizRef(int bizRef) {
		this.bizRef = bizRef;
	}

	public String getOperateType() {
		return operateType;
	}

	public void setOperateType(String operateType) {
		this.operateType = operateType;
	}

}
