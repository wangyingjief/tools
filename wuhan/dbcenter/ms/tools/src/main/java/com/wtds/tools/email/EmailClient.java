package com.wtds.tools.email;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import com.wtds.tools.ThreadPoolUtil;

/**
 * <b>发送邮件客户端</b><br>
 * 使用阿里云邮箱配置初始化客户端:<br>
 * EmailClient email = new EmailClient(EmailClient.aliyunEmailConfig("登陆名", "密码"));
 * @author wyj
 *
 */
public class EmailClient {

	private String smtp = "";

	private int port = 465;

	private String myEmail = "";

	private String userName = "";

	private String passWord = "";

	// 实例一个线程池，用于处理日志
	private ThreadPoolExecutor pool;

	/**
	 * 阿里云邮发件配置
	 * @param userName
	 * @param password
	 * @return
	 */
	public static Map<String, String> aliyunEmailConfig(String userName, String password) {
		Map<String, String> conf = new HashMap<String, String>();
		conf.put("smtp", "smtp.mxhichina.com");
		conf.put("port", "465");
		conf.put("userName", userName);
		conf.put("password", password);
		return conf;
	}

	/**
	 * 使用阿里云邮发件配置信息初始化客户端
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
	 * @param smtp 发件服务器
	 * @param port 端口
	 * @param userName 登陆名
	 * @param password 密码
	 */
	public EmailClient(String smtp, int port, String userName, String password) {
		this.smtp = smtp;
		this.port = port;
		this.myEmail = userName;
		this.userName = userName;
		this.passWord = password;
	}

	/**
	 * <b>发送简单邮件(同步)</b>
	 * <br><font color="red">不推荐使用</font>
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

	/**
	 * <b>发送简单邮件(异步)</b> <br>
	 * <font color="green">推荐使用</font>
	 * 
	 * @param toEmail
	 *            发送给谁
	 * @param subject
	 *            邮件标题
	 * @param msg
	 *            内容
	 * @param callback
	 *            发送结果<br>
	 *            <b>使用方式:</b> <br>
	 *            email.asynSendSimpleEmail("xx@qq.com", "test", "这是1封测试邮件！",(Result r) -> { <br>
	 *            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.println("返回发送状态：" + r.getType());<br>
	 *            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.println("返回发送消息：" + r.getMsg()); <br>
	 *            });
	 */
	public void asynSendSimpleEmail(String toEmail, String subject, String msg, EmailCallback callback) {
		if (pool == null) {
			pool = ThreadPoolUtil.newThreadPoolExecutor(0, 8, 60);
		}
		pool.execute(() -> {
			try {
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
				callback.result(new Result(SendType.success, "发送 Email 至 (" + toEmail + ") - success!"));
			} catch (Exception e) {
				e.printStackTrace();
				callback.result(new Result(SendType.err, e.toString()));
			}
		});
	}

	public static void main(String[] args) {
		// 调用同步发送邮件
		EmailClient email = new EmailClient(EmailClient.aliyunEmailConfig("wangyingjie@mitnova.com", "Wyj123456"));
//		try {
//			email.sendSimpleEmail("258433574@qq.com", "test", "这是1封测试邮件！");
//		} catch (EmailException e) {
//			e.printStackTrace();
//		}
		// 调用异步发送邮件
		email.asynSendSimpleEmail("258433574@qq.com", "test", "这是1封测试邮件！", (Result r) -> {
			System.out.println("返回发送状态：" + r.getType());
			System.out.println("返回发送消息：" + r.getMsg());
		});
	}
}
