package com.wtds.tools.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

public class HttpUtil {

	private static final int HttpParamBean = 0;
	static boolean showLog = false;

	public static String sendGet(String url) {
		return sendGet(url, null, null);
	}

	public static String sendGet(String url, String param) {
		return sendGet(url, param, null);
	}

	/**
	 * 向指定URL发送GET方法的请求
	 * 
	 * @param url
	 *            发送请求的URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return URL 所代表远程资源的响应结果
	 */
	public static String sendGet(String url, String param, HttpClient client) {
		return sendGet(url, param, client, "UTF-8");
	}

	public static String sendGet(String url, String param, HttpClient client, String charset) {
		HttpClient oldclient = client;
		if (oldclient == null)
			oldclient = new HttpClient();
		GetMethod get = null;
		String result = "";
		try {
			get = new GetMethod(url);
			get.setFollowRedirects(false);
			int statusCode = oldclient.executeMethod(get);
			if (isRedirect(statusCode)) {
				Header header = get.getResponseHeader("location");
				if (header != null) {
					String location = header.getValue();
					if (location == null || location.equals("")) {
						location = "/";
					}
					get.releaseConnection();
					get = new GetMethod(location);
					oldclient.executeMethod(get);
				}
			}
			InputStream responseBody = get.getResponseBodyAsStream();
			// 打印结果
			BufferedReader in = new BufferedReader(new InputStreamReader(responseBody, charset));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (get != null)
				get.releaseConnection();
		}
		return result;
	}

	public static boolean isEmpty(String str) {
		return (str == null) || str.trim().equals("") || str.trim().equals("null");
	}

	public static String sendPost(String url) {
		return sendPost(url, null, null);
	}

	public static String sendPost(String url, List<HttpParamBean> param) {
		return sendPost(url, param, null);
	}

	public static String sendPost(String url, List<HttpParamBean> param, HttpClient client) {
		return sendPost(url, param, client, "UTF-8");
	}
	
	/**
	 * Post发送json（默认UTF-8）
	 * @param url
	 * @param json
	 * @return
	 * @throws Exception
	 */
	public static String sendPostJson(String url, String json) throws Exception {
		return sendPostJson(url,json,null,"UTF-8");
	}
	
	/**
	 * Post发送json
	 * @param url
	 * @param json
	 * @param client
	 * @param charset
	 * @return
	 * @throws Exception
	 */
	public static String sendPostJson(String url, String json, HttpClient client, String charset) throws Exception {
		String result = "";
		try {
			HttpClient oldclient = client;
			if (oldclient == null)
				oldclient = new HttpClient();
			PostMethod postMethod = new PostMethod(url);
			HttpClientParams params = oldclient.getParams();
			params.setContentCharset(charset);
			oldclient.setParams(params);
			// 设置参数编码
			postMethod.setRequestEntity(new StringRequestEntity(json, "application/json", charset));

			InputStream responseBody = null;

			// 执行 获取状态
			int statusCode = oldclient.executeMethod(postMethod);
			if (isRedirect(statusCode)) {
				Header header = postMethod.getResponseHeader("location");
				String location = header.getValue();
				if (location == null || location.equals("")) {
					location = "/";
				}
				GetMethod method2 = new GetMethod(location);
				oldclient.executeMethod(method2);
				responseBody = method2.getResponseBodyAsStream();
			} else if (statusCode == HttpStatus.SC_OK) {
				responseBody = postMethod.getResponseBodyAsStream();
			} else {
				throw new Exception(statusCode + "");
			}

			// 打印结果
			BufferedReader in = new BufferedReader(new InputStreamReader(responseBody, charset));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
			postMethod.getResponseCharSet();
			postMethod.getResponseBodyAsString();
			// 释放连接
			postMethod.releaseConnection();
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String sendPost(String url, List<HttpParamBean> param, HttpClient client, String charset) {
		String result = "";
		try {
			HttpClient oldclient = client;
			if (oldclient == null)
				oldclient = new HttpClient();
			PostMethod postMethod = new PostMethod(url);
			HttpClientParams params = oldclient.getParams();
			params.setContentCharset(charset);
			oldclient.setParams(params);
			// 设置参数编码
			postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, charset);

			// postMethod.getRequestHeader("Cookie");
			// 构造键值对参数
			if (param != null) {
				NameValuePair[] data = new NameValuePair[param.size()];
				for (int i = 0; i < param.size(); i++) {
					NameValuePair nvp = new NameValuePair(param.get(i).getKey(), param.get(i).getValue());
					data[i] = nvp;
				}
				postMethod.setRequestBody(data);
			}

			InputStream responseBody = null;
			// 执行 获取状态
			int statusCode = oldclient.executeMethod(postMethod);
			if (isRedirect(statusCode)) {
				Header header = postMethod.getResponseHeader("location");
				String location = header.getValue();
				if (location == null || location.equals("")) {
					location = "/";
				}
				GetMethod method2 = new GetMethod(location);
				oldclient.executeMethod(method2);
				responseBody = method2.getResponseBodyAsStream();
			} else {
				responseBody = postMethod.getResponseBodyAsStream();
			}

			// 打印结果
			BufferedReader in = new BufferedReader(new InputStreamReader(responseBody, charset));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
			postMethod.getResponseCharSet();
			postMethod.getResponseBodyAsString();
			// 释放连接
			postMethod.releaseConnection();
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	private static boolean isRedirect(int statusCode) {
		if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY || statusCode == HttpStatus.SC_MOVED_TEMPORARILY
				|| statusCode == HttpStatus.SC_SEE_OTHER || statusCode == HttpStatus.SC_TEMPORARY_REDIRECT)
			return true;
		return false;
	}

	@SuppressWarnings("deprecation")
	public static void put(String url, String data) throws Exception {
		MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
		connectionManager.setMaxTotalConnections(100);
		connectionManager.setMaxConnectionsPerHost(100);
		HttpClient client = new HttpClient(connectionManager);
		PutMethod put = new PutMethod(url);
		put.setRequestBody(data);
		client.executeMethod(put);
	}

	@SuppressWarnings("deprecation")
	public static void delete(String url) throws Exception {
		MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
		connectionManager.setMaxTotalConnections(100);
		connectionManager.setMaxConnectionsPerHost(100);
		HttpClient client = new HttpClient(connectionManager);
		DeleteMethod del = new DeleteMethod(url);
		client.executeMethod(del);
	}

	public static void main(String[] args) {
		// List<HttpParamBean> list = new ArrayList<HttpParamBean>();
		// HttpParamBean gpb = new HttpParamBean("a", "页面");
		// HttpParamBean gpb2 = new HttpParamBean("url",
		// "http://www.123.com?u1=1&u2=2&u3=zhognwen");
		// list.add(gpb);
		// list.add(gpb2);
		// String result = sendPost("http://localhost/resource/list.do",list);
		// System.out.println(result);

		String result = "";
		try {
			result = sendPostJson("http://localhost:8080/demo", "{a:2}", null, "UTF-8");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(result);
	}
}
