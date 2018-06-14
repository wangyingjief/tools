package com.wtds.tools;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

public class EmailClient {

	private String smtp = "";

	private int port = 465;

	private String myEmail = "";

	private String userName = "";

	private String passWord = "";

	public static Map<String, String> aliyunEmailConfig(String userName, String password) {
		Map<String, String> conf = new HashMap<String, String>();
		conf.put("smtp", "smtp.mxhichina.com");
		conf.put("port", "465");
		conf.put("userName", userName);
		conf.put("password", password);
		return conf;
	}

	/**
	 * 更具初始化配置初始化邮件客户端
	 * 
	 * @param aliyunEmailConfig
	 */
	public EmailClient(Map<String, String> aliyunEmailConfig) {
		this.smtp = aliyunEmailConfig.get("smtp");
		this.port = Integer.parseInt(aliyunEmailConfig.get("port"));
		this.myEmail = aliyunEmailConfig.get("userName");
		this.userName = aliyunEmailConfig.get("userName");
		this.passWord = aliyunEmailConfig.get("password");
	}

	/**
	 * 初始化邮件客户端
	 * 
	 * @param host
	 * @param port
	 * @param myEmail
	 * @param userName
	 * @param password
	 */
	public EmailClient(String smtp, int port, String userName, String password) {
		this.smtp = smtp;
		this.port = port;
		this.myEmail = userName;
		this.userName = userName;
		this.passWord = password;
	}

	/**
	 * 发送简单邮件
	 * 
	 * @param toEmail
	 *            发送给谁
	 * @param subject
	 *            邮件标题
	 * @param msg
	 *            内容
	 * @throws EmailException
	 */
	public void sendSimpleEmail(String toEmail, String subject, String msg) throws EmailException {
		Email email = new SimpleEmail();
		email.setHostName(smtp);
		email.setSmtpPort(port);
		email.setAuthenticator(new DefaultAuthenticator(userName, passWord));
		email.setSSLOnConnect(true);
		email.setFrom(myEmail);
		email.setSubject(subject);
		email.setMsg(msg);
		email.addTo(toEmail);
		email.send();
		System.out.println("发送 Email 至 (" + toEmail + ") - success!");
	}

	public static void main(String[] args) {
		EmailClient email = new EmailClient(EmailClient.aliyunEmailConfig("wangyingjie@mitnova.com", "Wyj123456"));
		try {
			email.sendSimpleEmail("258433574@qq.com", "test", "这是1封测试邮件！");
		} catch (EmailException e) {
			e.printStackTrace();
		}
	}
}
