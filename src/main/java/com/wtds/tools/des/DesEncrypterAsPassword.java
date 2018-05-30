package com.wtds.tools.des;

import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

import com.wtds.tools.Base64Util;

public class DesEncrypterAsPassword {

	 Key key;  
	  
	    public DesEncrypterAsPassword() {  
	    	 setKey("MRCODE1425");
	    }  
	  
	    public DesEncrypterAsPassword(String str) {  
	        setKey(str); // 生成密匙  
	    }  
	  
	    public Key getKey() {  
	        return key;  
	    }  
	  
	    public void setKey(Key key) {  
	        this.key = key;  
	    }  
	  
	   /** 
	    * 根据参数生成 KEY   
	    */  
	    public void setKey (String strKey) {  
	        try {  
	            KeyGenerator _generator = KeyGenerator.getInstance("DES");  
	            //防止linux下 随机生成key   
	            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG" );  
	            secureRandom.setSeed(strKey.getBytes());  
	              
	            _generator.init(56,secureRandom);  
	            this.key = _generator.generateKey();  
	            _generator = null;  
	        } catch (Exception e) {  
	            throw new RuntimeException(  
	                    "Error initializing SqlMap class. Cause: " + e);  
	        }  
	    }
	  
	  /** 
	     * 根据参数生成 KEY      
	     */  
	    /*public void setKey(String strKey) { 
	        try { 
	            KeyGenerator _generator = KeyGenerator.getInstance("DES"); 
	            _generator.init(new SecureRandom(strKey.getBytes())); 
	            this.key = _generator.generateKey(); 
	            _generator = null; 
	        } catch (Exception e) { 
	            throw new RuntimeException( 
	                    "Error initializing SqlMap class. Cause: " + e); 
	        } 
	    }*/
	  
	    /** 
	     * 加密 String 明文输入 ,String 密文输出 
	     */  
	    public String encryptStr(String strMing) {  
	        byte[] byteMi = null;  
	        byte[] byteMing = null;  
	        String strMi = "";  
	        try {  
	            byteMing = strMing.getBytes("UTF8");  
	            byteMi = this.encryptByte(byteMing);  
	            strMi = Base64Util.encode(byteMi);  
	            
	        } catch (Exception e) {  
	            throw new RuntimeException(  
	                    "Error initializing SqlMap class. Cause: " + e);  
	        } finally {  
	            byteMing = null;  
	            byteMi = null;  
	        }  
	        return strMi;  
	    }  
	  
	    /** 
	     * 解密 以 String 密文输入 ,String 明文输出 
	     * @param strMi
	     * @return 
	     */  
	    public String decryptStr(String strMi) {  
	        byte[] byteMing = null;  
	        byte[] byteMi = null;  
	        String strMing = "";  
	        try {  
	            byteMi = Base64Util.decode(strMi); 
	            byteMing = this.decryptByte(byteMi);  
	            strMing = new String(byteMing, "UTF8");  
	        } catch (Exception e) {  
	            throw new RuntimeException(  
	                    "Error initializing SqlMap class. Cause: " + e);  
	        } finally {  
	            byteMing = null;  
	            byteMi = null;  
	        }  
	        return strMing;  
	    }  
	  
	    /** 
	     * 加密以 byte[] 明文输入 ,byte[] 密文输出 
	     * 
	     * @param byteS 
	     * @return 
	     */  
	    private byte[] encryptByte(byte[] byteS) {  
	        byte[] byteFina = null;  
	        Cipher cipher;  
	        try {  
	            cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");  
	            cipher.init(Cipher.ENCRYPT_MODE, key);  
	            byteFina = cipher.doFinal(byteS);  
	        } catch (Exception e) {  
	            throw new RuntimeException(  
	                    "Error initializing SqlMap class. Cause: " + e);  
	        } finally {  
	            cipher = null;  
	        }  
	        return byteFina;  
	    }  
	  
	    /** 
	     * 解密以 byte[] 密文输入 , 以 byte[] 明文输出 
	     * 
	     * @param byteD 
	     * @return 
	     */  
	    private byte[] decryptByte(byte[] byteD) {  
	        Cipher cipher;  
	        byte[] byteFina = null;  
	        try {  
	            cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");  
	            cipher.init(Cipher.DECRYPT_MODE, key);  
	            byteFina = cipher.doFinal(byteD);  
	        } catch (Exception e) {  
	            throw new RuntimeException(  
	                    "Error initializing SqlMap class. Cause: " + e);  
	        } finally {  
	            cipher = null;  
	        }  
	        return byteFina;  
	    }  
	   
	    public static void main(String[] args) throws Exception {  
	    	DesEncrypterAsPassword des = new DesEncrypterAsPassword("123456");  
	        // DES 加密文件  
	        // des.encryptFile("G:/test.doc", "G:/ 加密 test.doc");  
	        // DES 解密文件  
	        // des.decryptFile("G:/ 加密 test.doc", "G:/ 解密 test.doc");  
	        String str1 = "select good from NyhGoods AS good,NyhStore AS store where good.storeId='<@good.storeId@>' and good.goodsName like '%<@good.goodsName@>%' order by good.addTime desc";  
	        // DES 加密字符串  
	        String str2 = des.encryptStr(str1);  
	        // DES 解密字符串  
	        String deStr = des.decryptStr(str2);  
	        System.out.println(" 加密前： " + str1);  
	        System.out.println(" 加密后： " + str2);  
	        System.out.println(" 加密后长度： " + str2.length());  
	        System.out.println(" 解密后： " + deStr);  
	    }  
}