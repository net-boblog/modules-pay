package com.alipay.config;

/* *
 *������AlipayConfig
 *���ܣ�����������
 *��ϸ�������ʻ��й���Ϣ������·��
 *�汾��3.4
 *�޸����ڣ�2016-03-08
 *˵����
 *���´���ֻ��Ϊ�˷����̻����Զ��ṩ���������룬�̻����Ը����Լ���վ����Ҫ�����ռ����ĵ���д,����һ��Ҫʹ�øô��롣
 *�ô������ѧϰ���о�֧�����ӿ�ʹ�ã�ֻ���ṩһ���ο���
 */

public class AlipayConfig {

	// �����������������������������������Ļ�����Ϣ������������������������������

	// ���������ID��ǩԼ�˺ţ���2088��ͷ��16λ��������ɵ��ַ������鿴��ַ��https://b.alipay.com/order/pidAndKey.htm
	public static String partner = "2088221869648653";

	// �տ�֧�����˺ţ���2088��ͷ��16λ��������ɵ��ַ�����һ��������տ��˺ž���ǩԼ�˺�
	public static String seller_id = partner;

	// �̻���˽Կ,��ҪPKCS8��ʽ��RSA��˽Կ���ɣ�https://doc.open.alipay.com/doc2/detail.htm?spm=a219a.7629140.0.0.nBDxfy&treeId=58&articleId=103242&docType=1
	// public static String private_key
	// ="MIICXAIBAAKBgQCkMKiqxCdmE7AspRGYChtaC0wg3kqRvvxoi3O4NKGDxNUUb1ucFTPFuYa1Q91qgtLCihokzG32ZKIsj7zziCFQ7kLbt6XBtFz+3u5K6LhbPYugjPWxVVM294uRjvUEI+jc5ASIleiT448H+HAt5pLbEL7aEut7QDYzeinyYhKQwQIDAQABAoGAFnhWTPCFV9Hv9Vwt2Tng3mTXaVQirmmNz5zuHFKPnCHu61oGFolMSY4HYn1EKxR2VYti4pBbqDHDhAez2zxRGrMW9b1Xslxg8f1T/9zDBtz+9t9DNUB4NlqRy0tdC+NFiTYgCAxylT98y+8ljOw+fBpbcr6g0xhRiauF+/wSp/UCQQDaQfUdbgA9ZdZHorGFAfo8IMF/Fzf7cAnmcoNIqRUkm+mUqIm+wCU2V2lNZbltukmtsTo4oIXxZr8F18Jlko0zAkEAwJUvb1WIxtHBzPwLie8wkV0PdMq8fi44UaD68le27iJhH6hQWEu/l+UBSPQ/KPjBFh1giu19FOhYMf9e0sfiOwJAWL7pBAZuYoi+EHK+6+5Z6YkIJL00LjDoVaPKbgkSZ8hdueyt1bobZ3UzhB0QwOU2gEHIAq6CyB2XHrEUgmiMsQJAe52HJCzALfaoMn66nWBWSYh1il80HL5oUiVFz1b0SejxdNOiNvrwUXyilYSKIi+CKULUHHkSp9/39KfZ4uyAzwJBAMdlthQ2TMeWAn98/2GL9HgzVwHzJikcgVc9d7mlSyJnd7qJkVTN/IdqWHsV2EIEvVJ/wAEOgYSn4d6uHF/E3AA=";
	public static String private_key = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKQwqKrEJ2YTsCylEZgKG1oLTCDeSpG+/GiLc7g0oYPE1RRvW5wVM8W5hrVD3WqC0sKKGiTMbfZkoiyPvPOIIVDuQtu3pcG0XP7e7krouFs9i6CM9bFVUzb3i5GO9QQj6NzkBIiV6JPjjwf4cC3mktsQvtoS63tANjN6KfJiEpDBAgMBAAECgYAWeFZM8IVX0e/1XC3ZOeDeZNdpVCKuaY3PnO4cUo+cIe7rWgYWiUxJjgdifUQrFHZVi2LikFuoMcOEB7PbPFEasxb1vVeyXGDx/VP/3MMG3P7230M1QHg2WpHLS10L40WJNiAIDHKVP3zL7yWM7D58GltyvqDTGFGJq4X7/BKn9QJBANpB9R1uAD1l1keisYUB+jwgwX8XN/twCeZyg0ipFSSb6ZSoib7AJTZXaU1luW26Sa2xOjighfFmvwXXwmWSjTMCQQDAlS9vVYjG0cHM/AuJ7zCRXQ90yrx+LjhRoPryV7buImEfqFBYS7+X5QFI9D8o+MEWHWCK7X0U6Fgx/17Sx+I7AkBYvukEBm5iiL4Qcr7r7lnpiQgkvTQuMOhVo8puCRJnyF257K3VuhtndTOEHRDA5TaAQcgCroLIHZcesRSCaIyxAkB7nYckLMAt9qgyfrqdYFZJiHWKXzQcvmhSJUXPVvRJ6PF006I2+vBRfKKVhIoiL4IpQtQceRKn3/f0p9ni7IDPAkEAx2W2FDZMx5YCf3z/YYv0eDNXAfMmKRyBVz13uaVLImd3uomRVM38h2pYexXYQgS9Un/AAQ6BhKfh3q4cX8TcAA==";
	// public static String private_key =
	// "40Dl6lUDSdOoNfQKZtaKvLq5YfueqS/RFb+JNhZuoGVHLXAb60pnWyOvUqhv3JtUFpB99GSgYWBXt03NmgB+tb+9ZvyyJY6MER3tpN9YEQiWuV+RJ/92aSoH+FqvNKXokTgWP+zahwc5lE4cypNmyzSFIjCWqwP4rK83icNucolG9ktgP1g/IokBj/1pJzu8ZZDwO7TMSgIYdy1x8EDyFFUTCqBmRr+ivoznDivwhyM7vJpFe9G7ZxnNY2K3zKMqSMoGWGhbSmPoZA8F7wx0kqhoE8ACO5uW+aMyfC5pqhkDZwNMz73SmxQ6fc2vvR3fGyyu+VgE8ST4/IZsKpFRO5347AtwFYso7Nz8Lvyoo7Rs0cZEQOjJJTZO7CiWgKs114tWF9btMHrphn9gLUCzWUKRRUrCDrwI7J5RBc6RSZ77uRIeZRQNW9fTIMXH3zO7lcZy688cXuE7HFyHJ8DxUszCWnEmAqww2D3n4UV27uNHFnyJz1+FSxU0RETlQCP5wCVki8MJFB3a1JoiP1F45ywZeNuXKKpko8oUQaMjkZEHpx0rLFuEoKLtwTUddGt4iPpcQFxigcNDJYf5FOaKw628dvHGCZSAnPBjU53BaJn2Bns+ZlG1LNsQNoUkqNol3IHBNJrbYcExrJ+0WW7p9vWfNHgrmw5wlP3XKhSM1nLMgAm7tKRzX3BY161S9QNEvDNidO2fhvjgtd0N975xWGKoz/SOgY6shbTluHPduE/CC924212BpmT1Cm0+VkzBD5yKJI1yZdPppl6a/TWtJmd7iyK/0S9vjDrAvhjmukdzIpZlWJOaZg==";

	// ֧�����Ĺ�Կ,�鿴��ַ��https://b.alipay.com/order/pidAndKey.htm
	public static String alipay_public_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";

	// �������첽֪ͨҳ��·�� ��http://��ʽ������·�������ܼ�?id=123�����Զ����������������������������
	public static String notify_url = "http://localhost:8080/Zis-Pay/notify_url.jsp";

	// ҳ����תͬ��֪ͨҳ��·�� ��http://��ʽ������·�������ܼ�?id=123�����Զ����������������������������
	public static String return_url = "http://localhost:8080/Zis-Pay/return_url.jsp";

	// ǩ����ʽ
	public static String sign_type = "RSA";

	// �����ã�����TXT��־�ļ���·������AlipayCore.java���е�logResult(String sWord)��ӡ������
	public static String log_path = "F:\\alipayLog\\alipayLog.txt";

	// �ַ������ʽ Ŀǰ֧�� gbk �� utf-8
	public static String input_charset = "gbk";

	// ֧������ �������޸�
	public static String payment_type = "1";

	// ���õĽӿ����������޸�
	public static String service = "create_direct_pay_by_user";

	// �����������������������������������Ļ�����Ϣ������������������������������

	// �������������������� �����������÷�������Ϣ�����û��ͨ�����㹦�ܣ�Ϊ�ռ��� ������������������������������

	// ������ʱ��� ��Ҫʹ����������ļ�submit�е�query_timestamp����
	public static String anti_phishing_key = "";

	// �ͻ��˵�IP��ַ �Ǿ�����������IP��ַ���磺221.0.0.1
	public static String exter_invoke_ip = "";

	// �������������������������������÷�������Ϣ�����û��ͨ�����㹦�ܣ�Ϊ�ռ��� ������������������������������

}
