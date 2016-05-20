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
	 * ����Э��
	 */
	public void createProtocol() {

	}

	/**
	 * ����ָ��
	 */
	public void createInstruct() {

	}

	/**
	 * ����ָ�������
	 */
	public void createInstructs() {
		for (int i = 0; i < 10; i++) {
			createInstruct();
		}
	}

	/**
	 * ִ��ָ��
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
	 * ��������ҵ����ˮ
	 */
	public void createExportRcd() {
		checkForRcd();
	}

	/**
	 * ҵ��˶�-�Ƚ�һ��ҵ����ˮ������ҵ����ˮ
	 */
	protected void checkForRcd() {

	}

	/**
	 * �����������ָ��
	 */
	protected void invokeRelatedInstructs() {
		Integer relateInstructId = null;
		process(relateInstructId);
	}

}
