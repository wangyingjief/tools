package com.wtds.db;

import java.util.Date;

import com.wtds.db.annotation.DBTable;

@DBTable(name="SYS_APIDOC_DOCUMENT_INFO")
public class TestBean {
	
	private String uuid;
    // 项目名称
    private String projectName;
    // 接口分组（一般填写模块名称）
    private String docGroup;
    // 接口URL
    private String docUrl;
	// 接口详细说明
    private String docDescription;
	// 接口参数
    private String docParameter;
	// 测试数据
    private String docTestData;
	// 返回值样例
    private String docSuccessExample;
	// ip地址，需要获取服务运行的服务器ip
    private String address;
	// api在class中的路径 如果字段中字符开头为"-custom"则说明此次API信息不是来源于系统接口，而是来源于API中心自定义接口
    private String apiPathForJava;
    //添加时间
    private Date createTime;
    
    
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getDocGroup() {
		return docGroup;
	}
	public void setDocGroup(String docGroup) {
		this.docGroup = docGroup;
	}
	public String getDocUrl() {
		return docUrl;
	}
	public void setDocUrl(String docUrl) {
		this.docUrl = docUrl;
	}
	public String getDocDescription() {
		return docDescription;
	}
	public void setDocDescription(String docDescription) {
		this.docDescription = docDescription;
	}
	public String getDocParameter() {
		return docParameter;
	}
	public void setDocParameter(String docParameter) {
		this.docParameter = docParameter;
	}
	public String getDocTestData() {
		return docTestData;
	}
	public void setDocTestData(String docTestData) {
		this.docTestData = docTestData;
	}
	public String getDocSuccessExample() {
		return docSuccessExample;
	}
	public void setDocSuccessExample(String docSuccessExample) {
		this.docSuccessExample = docSuccessExample;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getApiPathForJava() {
		return apiPathForJava;
	}
	public void setApiPathForJava(String apiPathForJava) {
		this.apiPathForJava = apiPathForJava;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
    
}
