package com.wtds.tools;

/**
 * 用于返回json数据实体类
 */
public class ReturnJsonBean {
	public ReturnJsonBean(String type,String msg,Object data){
		this.code = type;
		this.msg = msg;
		this.dataset = data;
	}
	
	public String code;
	public String msg;
	public Object dataset;
	
//	public String getMSG() {
//		return MSG;
//	}
//	public void setMSG(String MSG) {
//		this.MSG = MSG;
//	}
//	public String getCODE() {
//		return CODE;
//	}
//	public void setCODE(String CODE) {
//		this.CODE = CODE;
//	}
//	public Object getDATASET() {
//		return DATASET;
//	}
//	public void setDATASET(Object DATASET) {
//		this.DATASET = DATASET;
//	}
}
