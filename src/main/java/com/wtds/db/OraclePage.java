package com.wtds.db;

/**
 * Oracle分页对象
 * @author wyj
 *
 */
public class OraclePage {
	
	//开始下标
	private int startRow = 0;
	//结束下标
	private int endRow = 10;
	//总数据行数
	private int totalRow = 0;
	//当前页数
	private int currentPage = 1;
	
	public OraclePage() {
	}
	/**
	 * @param currentPage 第几页
	 * @param totalRow 显示多少行
	 */
	public OraclePage(int currentPage,int totalRow) {
		this.totalRow = totalRow;
		this.startRow = (currentPage - 1) * totalRow;
		this.endRow = this.startRow + totalRow;
	}
	
	public int getStartRow() {
		return startRow;
	}
	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}
	public int getEndRow() {
		return endRow;
	}
	public void setEndRow(int endRow) {
		this.endRow = endRow;
	}
	public int getTotalRow() {
		return totalRow;
	}
	public void setTotalRow(int totalRow) {
		this.totalRow = totalRow;
	}
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	
	
}
