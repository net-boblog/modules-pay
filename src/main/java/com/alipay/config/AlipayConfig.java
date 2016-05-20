package com.alipay.config;

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *版本：3.4
 *修改日期：2016-03-08
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */

public class AlipayConfig {

	// ↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

	// 合作身份者ID，签约账号，以2088开头由16位纯数字组成的字符串，查看地址：https://b.alipay.com/order/pidAndKey.htm
	public static String partner = "2088221869648653";

	// 收款支付宝账号，以2088开头由16位纯数字组成的字符串，一般情况下收款账号就是签约账号
	public static String seller_id = partner;

	// 商户的私钥,需要PKCS8格式，RSA公私钥生成：https://doc.open.alipay.com/doc2/detail.htm?spm=a219a.7629140.0.0.nBDxfy&treeId=58&articleId=103242&docType=1
	// public static String private_key
	// ="MIICXAIBAAKBgQCkMKiqxCdmE7AspRGYChtaC0wg3kqRvvxoi3O4NKGDxNUUb1ucFTPFuYa1Q91qgtLCihokzG32ZKIsj7zziCFQ7kLbt6XBtFz+3u5K6LhbPYugjPWxVVM294uRjvUEI+jc5ASIleiT448H+HAt5pLbEL7aEut7QDYzeinyYhKQwQIDAQABAoGAFnhWTPCFV9Hv9Vwt2Tng3mTXaVQirmmNz5zuHFKPnCHu61oGFolMSY4HYn1EKxR2VYti4pBbqDHDhAez2zxRGrMW9b1Xslxg8f1T/9zDBtz+9t9DNUB4NlqRy0tdC+NFiTYgCAxylT98y+8ljOw+fBpbcr6g0xhRiauF+/wSp/UCQQDaQfUdbgA9ZdZHorGFAfo8IMF/Fzf7cAnmcoNIqRUkm+mUqIm+wCU2V2lNZbltukmtsTo4oIXxZr8F18Jlko0zAkEAwJUvb1WIxtHBzPwLie8wkV0PdMq8fi44UaD68le27iJhH6hQWEu/l+UBSPQ/KPjBFh1giu19FOhYMf9e0sfiOwJAWL7pBAZuYoi+EHK+6+5Z6YkIJL00LjDoVaPKbgkSZ8hdueyt1bobZ3UzhB0QwOU2gEHIAq6CyB2XHrEUgmiMsQJAe52HJCzALfaoMn66nWBWSYh1il80HL5oUiVFz1b0SejxdNOiNvrwUXyilYSKIi+CKULUHHkSp9/39KfZ4uyAzwJBAMdlthQ2TMeWAn98/2GL9HgzVwHzJikcgVc9d7mlSyJnd7qJkVTN/IdqWHsV2EIEvVJ/wAEOgYSn4d6uHF/E3AA=";
	public static String private_key = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKQwqKrEJ2YTsCylEZgKG1oLTCDeSpG+/GiLc7g0oYPE1RRvW5wVM8W5hrVD3WqC0sKKGiTMbfZkoiyPvPOIIVDuQtu3pcG0XP7e7krouFs9i6CM9bFVUzb3i5GO9QQj6NzkBIiV6JPjjwf4cC3mktsQvtoS63tANjN6KfJiEpDBAgMBAAECgYAWeFZM8IVX0e/1XC3ZOeDeZNdpVCKuaY3PnO4cUo+cIe7rWgYWiUxJjgdifUQrFHZVi2LikFuoMcOEB7PbPFEasxb1vVeyXGDx/VP/3MMG3P7230M1QHg2WpHLS10L40WJNiAIDHKVP3zL7yWM7D58GltyvqDTGFGJq4X7/BKn9QJBANpB9R1uAD1l1keisYUB+jwgwX8XN/twCeZyg0ipFSSb6ZSoib7AJTZXaU1luW26Sa2xOjighfFmvwXXwmWSjTMCQQDAlS9vVYjG0cHM/AuJ7zCRXQ90yrx+LjhRoPryV7buImEfqFBYS7+X5QFI9D8o+MEWHWCK7X0U6Fgx/17Sx+I7AkBYvukEBm5iiL4Qcr7r7lnpiQgkvTQuMOhVo8puCRJnyF257K3VuhtndTOEHRDA5TaAQcgCroLIHZcesRSCaIyxAkB7nYckLMAt9qgyfrqdYFZJiHWKXzQcvmhSJUXPVvRJ6PF006I2+vBRfKKVhIoiL4IpQtQceRKn3/f0p9ni7IDPAkEAx2W2FDZMx5YCf3z/YYv0eDNXAfMmKRyBVz13uaVLImd3uomRVM38h2pYexXYQgS9Un/AAQ6BhKfh3q4cX8TcAA==";
	// public static String private_key =
	// "40Dl6lUDSdOoNfQKZtaKvLq5YfueqS/RFb+JNhZuoGVHLXAb60pnWyOvUqhv3JtUFpB99GSgYWBXt03NmgB+tb+9ZvyyJY6MER3tpN9YEQiWuV+RJ/92aSoH+FqvNKXokTgWP+zahwc5lE4cypNmyzSFIjCWqwP4rK83icNucolG9ktgP1g/IokBj/1pJzu8ZZDwO7TMSgIYdy1x8EDyFFUTCqBmRr+ivoznDivwhyM7vJpFe9G7ZxnNY2K3zKMqSMoGWGhbSmPoZA8F7wx0kqhoE8ACO5uW+aMyfC5pqhkDZwNMz73SmxQ6fc2vvR3fGyyu+VgE8ST4/IZsKpFRO5347AtwFYso7Nz8Lvyoo7Rs0cZEQOjJJTZO7CiWgKs114tWF9btMHrphn9gLUCzWUKRRUrCDrwI7J5RBc6RSZ77uRIeZRQNW9fTIMXH3zO7lcZy688cXuE7HFyHJ8DxUszCWnEmAqww2D3n4UV27uNHFnyJz1+FSxU0RETlQCP5wCVki8MJFB3a1JoiP1F45ywZeNuXKKpko8oUQaMjkZEHpx0rLFuEoKLtwTUddGt4iPpcQFxigcNDJYf5FOaKw628dvHGCZSAnPBjU53BaJn2Bns+ZlG1LNsQNoUkqNol3IHBNJrbYcExrJ+0WW7p9vWfNHgrmw5wlP3XKhSM1nLMgAm7tKRzX3BY161S9QNEvDNidO2fhvjgtd0N975xWGKoz/SOgY6shbTluHPduE/CC924212BpmT1Cm0+VkzBD5yKJI1yZdPppl6a/TWtJmd7iyK/0S9vjDrAvhjmukdzIpZlWJOaZg==";

	// 支付宝的公钥,查看地址：https://b.alipay.com/order/pidAndKey.htm
	public static String alipay_public_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";

	// 服务器异步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
	public static String notify_url = "http://localhost:8080/Zis-Pay/notify_url.jsp";

	// 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
	public static String return_url = "http://localhost:8080/Zis-Pay/return_url.jsp";

	// 签名方式
	public static String sign_type = "RSA";

	// 调试用，创建TXT日志文件夹路径，见AlipayCore.java类中的logResult(String sWord)打印方法。
	public static String log_path = "F:\\alipayLog\\alipayLog.txt";

	// 字符编码格式 目前支持 gbk 或 utf-8
	public static String input_charset = "gbk";

	// 支付类型 ，无需修改
	public static String payment_type = "1";

	// 调用的接口名，无需修改
	public static String service = "create_direct_pay_by_user";

	// ↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

	// ↓↓↓↓↓↓↓↓↓↓ 请在这里配置防钓鱼信息，如果没开通防钓鱼功能，为空即可 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

	// 防钓鱼时间戳 若要使用请调用类文件submit中的query_timestamp函数
	public static String anti_phishing_key = "";

	// 客户端的IP地址 非局域网的外网IP地址，如：221.0.0.1
	public static String exter_invoke_ip = "";

	// ↑↑↑↑↑↑↑↑↑↑请在这里配置防钓鱼信息，如果没开通防钓鱼功能，为空即可 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

}
