/**
 * 
 */
package com.xabaohui.modules.pay.bo;

/**
 * @author YRee
 * 
 */
public class AbstractPaymentBO {

	/**
	 * 创建协议
	 */
	public void createProtocol() {

	}

	/**
	 * 创建指令
	 */
	public void createInstruct() {

	}

	/**
	 * 创建指令（批量）
	 */
	public void createInstructs() {
		for (int i = 0; i < 10; i++) {
			createInstruct();
		}
	}

	/**
	 * 执行指令
	 */
	public void process(Integer instructId) {
		invokeOuterApi();
		updateInstructToProcessing();
		createOurRcd();
	}

	protected void invokeOuterApi() {

	}

	protected void updateInstructToProcessing() {

	}

	protected void createOurRcd() {

	}

	/**
	 * 生成三方业务流水
	 */
	public void createExportRcd() {
		checkForRcd();
	}

	/**
	 * 业务核对-比较一方业务流水和三方业务流水
	 */
	protected void checkForRcd() {

	}

	/**
	 * 触发其他相关指令
	 */
	protected void invokeRelatedInstructs() {
		Integer relateInstructId = null;
		process(relateInstructId);
	}

}
