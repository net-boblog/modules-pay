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
	 * 冻结账户
	 */
	private Integer accountId;
	/**
	 * 冻结金额
	 */
	private double frozenMoney;
	/**
	 * 流水
	 */
	private int bizRef;
	/**
	 * 操作流水
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
