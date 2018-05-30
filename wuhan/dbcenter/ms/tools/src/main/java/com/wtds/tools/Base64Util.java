package com.wtds.tools;
import org.apache.commons.codec.binary.Base64;  

public class Base64Util {  
	
	public static String encode(String data) {
        return Base64.encodeBase64String(data.getBytes());  
    }  
	
    public static String encode(String data,String charset) { 
    	byte [] b = null;
    	try {
    		b = data.getBytes(charset);
		} catch (Exception e) {
			e.printStackTrace();
		}
        return Base64.encodeBase64String(b);  
    }
  
    public static String encode(byte [] data) {
        return Base64.encodeBase64String(data);  
    }  
    
    public static byte[] decode(String data) {
    	return Base64.decodeBase64(data);
    }
    
    public static String decode(String data,String charset) {
    	if(charset == null){
    		return new String(Base64.decodeBase64(data));
    	}else{
	    	byte [] b = Base64.decodeBase64(data);
	    	String r = null;
	    	try {
	    		r = new String(b,charset);
			} catch (Exception e) {
				e.printStackTrace();
			}
	        return r;
    	}
    }
  
    public static void main(String[] args) {  
       
    }  
  
}  