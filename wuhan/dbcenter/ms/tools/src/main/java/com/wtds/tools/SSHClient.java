package com.wtds.tools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

public class SSHClient {
	// 远程主机的ip地址
	private String ip;
	// 远程主机登录用户名
	private String username;
	// 远程主机的登录密码
	private String password;
	// 设置ssh连接的远程端口
	public int DEFAULT_SSH_PORT = 22;

	JSch jsch;

	SSHUserInfo userInfo;

	Session session;

	Channel channel;

	ChannelExec channelExec;
	
	public boolean isConnected() {
		return session.isConnected();
	}

	public void connection(String ip, int port, String username, String password) {
		this.ip = ip;
		this.username = username;
		this.password = password;
		this.DEFAULT_SSH_PORT = port;
		
		jsch = new JSch();
		userInfo = new SSHUserInfo();

		try {
			// 创建session并且打开连接，因为创建session之后要主动打开连接
			session = jsch.getSession(username, ip, DEFAULT_SSH_PORT);
			session.setPassword(password);
			session.setUserInfo(userInfo);
			session.connect();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 执行shell命令
	 * 
	 * @param command
	 * @return
	 */
	public String execute(final String command) {
		String result = "";
		// 保存输出内容的容器
		ArrayList<String> stdout = new ArrayList<String>();
		try {
			// 打开通道，设置通道类型，和执行的命令
			channel = session.openChannel("exec");
			channelExec = (ChannelExec) channel;

			channelExec.setCommand(command);

			channelExec.setInputStream(null);

			BufferedReader input = new BufferedReader(new InputStreamReader(channelExec.getInputStream()));

			channelExec.connect();

			// 接收远程服务器执行命令的结果
			String line;
			while ((line = input.readLine()) != null) {
				stdout.add(line+"\r\n");
			}
			input.close();

			// 得到returnCode
			// if (channelExec.isClosed()) {
			// returnCode = channelExec.getExitStatus();
			// }

			for (String str : stdout) {
				result += str;
			}

			// 关闭通道
			channelExec.disconnect();
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public void close() {

		// 关闭session
		session.disconnect();
	}

	public static void main(final String[] args) {
		SSHClient shell = new SSHClient();
		shell.connection("114.55.136.158", 22, "root", "pass12#$");
		String s1 = shell.execute("cd /opt ;pwd;");
		// String s3 = shell.execute("pwd");
		System.out.println(s1);
		// System.out.println(s3);
		shell.close();
	}
}

class SSHUserInfo implements UserInfo {

	@Override
	public String getPassphrase() {
		// TODO Auto-generated method stub
//		System.out.println("SSHUserInfo.getPassphrase()");
		return null;
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
//		System.out.println("SSHUserInfo.getPassword()");
		return null;
	}

	@Override
	public boolean promptPassphrase(String arg0) {
		// TODO Auto-generated method stub
//		System.out.println("SSHUserInfo.promptPassphrase()");
//		System.out.println(arg0);
		return false;
	}

	@Override
	public boolean promptPassword(String arg0) {
		// TODO Auto-generated method stub
//		System.out.println("SSHUserInfo.promptPassword()");
//		System.out.println(arg0);
		return false;
	}

	@Override
	public boolean promptYesNo(String arg0) {
		// TODO Auto-generated method stub'
//		System.out.println("SSHUserInfo.promptYesNo()");
//		System.out.println(arg0);
		if (arg0.contains("The authenticity of host")) {
			return true;
		}
		return true;
	}

	@Override
	public void showMessage(String arg0) {
		// TODO Auto-generated method stub
//		System.out.println("SSHUserInfo.showMessage()");
	}

}