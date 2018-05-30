package com.wtds.tools;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.alibaba.fastjson.JSON;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtUtil {

	/**
	 * 由字符串生成加密key
	 * 
	 * @return
	 */
	public static SecretKey generalKey() {
		String stringKey = "WTDS_WEB_TOKEN:3323";
		byte[] encodedKey = stringKey.getBytes();
		SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
		return key;
	}

	private static String decodeToken(String jwt) {
		String md5 = MD5Util.getMd5(jwt);
		String token = jwt + md5;
		return token.replaceAll("=", "_");
	}

	/**
	 * @param id
	 * @param subject
	 * @param outTime
	 * @return token
	 */
	public static String createToken(String id, String subject, long outTime) {
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);
		SecretKey key = generalKey();

		/**
		 * iss: 该JWT的签发者，是否使用是可选的； sub: 该JWT所面向的用户，是否使用是可选的； aud:
		 * 接收该JWT的一方，是否使用是可选的； exp(expires): 什么时候过期，这里是一个Unix时间戳，是否使用是可选的；
		 * iat(issuedat):在什么时候签发的(UNIX时间)，是否使用是可选的；
		 * nbf(NotBefore)：如果当前时间在nbf里的时间之前，则Token不被接受；一般都会留一些余地，比如几分钟；，是否使用是可选的；
		 */
		JwtBuilder builder = Jwts.builder().setId(id).setIssuedAt(now).setSubject(subject).signWith(signatureAlgorithm,
				key);

		if (outTime > 0) {
			long expMillis = nowMillis + outTime;
			Date exp = new Date(expMillis);
			builder.setExpiration(exp);
		}

		String jwt = Base64Util.encode(builder.compact(), "UTF-8");
		return decodeToken(jwt);
	}

	/**
	 * 增加失效时间
	 * 
	 * @param token
	 * @return
	 */
	public static String increaseOutTime(String token) {
		Claims claims = parseToken(token);

		SecretKey key = generalKey();
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

		Date createTime = claims.getIssuedAt();
		Date expTime = claims.getExpiration();
		long c = expTime.getTime() - createTime.getTime();
		long now = System.currentTimeMillis();
		claims.setIssuedAt(new Date(now));
		claims.setExpiration(new Date(now + c));
		JwtBuilder builder = Jwts.builder().setClaims(claims).signWith(signatureAlgorithm, key);

		String jwt = Base64Util.encode(builder.compact(), "UTF-8");
		return decodeToken(jwt);
	}

	/**
	 * 解密token
	 * 
	 * @param token
	 * @return
	 */
	public static Claims parseToken(String token) {
		token = token.replaceAll("_", "=");
		String jwt = token.substring(0, token.length() - 32);
		String demd5 = token.substring(token.length() - 32);
		String enmd5 = MD5Util.getMd5(jwt);
		// 如果对不上则说明此令牌非法
		if (!demd5.equals(enmd5)) {
			return null;
		}
		jwt = Base64Util.decode(jwt, "UTF-8");
		SecretKey key = generalKey();
		Claims claims = null;
		try {
			claims = Jwts.parser().setSigningKey(key).parseClaimsJws(jwt).getBody();
		} catch (Exception e) {
			// 解密失败
			return null;
		}
		return claims;
	}

	@SuppressWarnings("deprecation")
	public static String getUserInfo(String address,String userName) {
		
		//ip =172.20.104.136:8085 
		String url = "http://"+address+"/api/37175afb964c14421407bbfe1ee70502?token=46028F32D07C6B5C2CF5856C6BCFDB32&login_name="+userName;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost post = new HttpPost(url);
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		String responseJson = null;
		try {			
			responseJson = httpClient.execute(post, responseHandler);
			System.out.println("HttpClient POST请求结果：" + responseJson);
			
			return responseJson;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			System.out.println("HttpClient POST请求异常：" + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			httpClient.getConnectionManager().closeExpiredConnections();
			httpClient.getConnectionManager().closeIdleConnections(30, TimeUnit.SECONDS);
		}
		return null;

	}

	public static void main(String[] args) throws Exception {
		String t = createToken("1", "ha这有一个中文ha", 1000 * 10);
		// String t =
		// "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxIiwiaWF0IjoxNTA1ODM4NTc3LCJzdWIiOiJoYWhhIiwiZXhwIjoxNTA1ODM4NjM3fQ.XK5f_IDMItIAI7cnKltuncIVF5RNTxxL1stiKzV2A0Q";

		System.out.println(parseToken(t));

		while (true) {
			String a = increaseOutTime(t);
			System.out.println(JSON.toJSONString(parseToken(a)));
			Thread.sleep(1000);
		}
	}	
}
