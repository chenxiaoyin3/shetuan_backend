package com.hongyu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 分页
 * 
 */
public class Page<T> implements Serializable {

	private static final long serialVersionUID = -2053800594583879853L;
  
	/** 内容 */
	private List <T> rows = new ArrayList<T>();
	private List<Object[]>  lstObj = new ArrayList<Object[]>  ();
	
	private List <T> footer = new ArrayList<T>();
	/** 总记录数 */
	private /*final*/ long total;
	/** 分页信息 */
	private final Pageable pageable;
	/**
	 * 初始化一个新创建的Page对象
	 */
	public Page() {
		this.total = 0L;
		this.pageable = new Pageable();
	}

	/**
	 * @param content
	 *            内容
	 * @param total
	 *            总记录数
	 * @param pageable
	 *            分页信息
	 */
	public Page(List<T> rows, long total, Pageable pageable) {
		this.rows.addAll(rows);
		this.total = total;
		this.pageable = pageable;
	}

	public Page(List<Object[]> lst, long total, Pageable pageable,int type) {
		// TODO Auto-generated constructor stub
		this.lstObj=lst;
		this.total = total;
		this.pageable = pageable;
	}

	public List<Object[]> getLstObj() {
		return lstObj;
	}

	public void setLstObj(List<Object[]> lstObj) {
		this.lstObj = lstObj;
	}

	/**
	 * 获取页码
	 * 
	 * @return 页码
	 */
	public int getPageNumber() {
		return pageable.getPage();
	}
	
	/**
	 * 获取每页记录数
	 * 
	 * @return 每页记录数
	 */
	public int getPageSize() {
		return pageable.getRows();
	}
	
	/**
	 * 获取搜索属性
	 * 
	 * @return 搜索属性
	 */
	public String getSearchProperty() {
		return pageable.getSearchProperty();
	}

	/**
	 * 获取搜索值
	 * 
	 * @return 搜索值
	 */
	public String getSearchValue() {
		return pageable.getSearchValue();
	}

	/**
	 * 获取排序属性
	 * 
	 * @return 排序属性
	 */
	public String getOrderProperty() {
		return pageable.getSort();
	}
	/**
	 * 获取排序方向
	 * 
	 * @return 排序方向
	 */
	public String getOrderDirection() {
		return pageable.getOrder();
	}

	/**
	 * 获取排序
	 * 
	 * @return 排序
	 */
	public List<Order> getOrders() {
		return pageable.getOrders();
	}

	/**
	 * 获取筛选
	 * 
	 * @return 筛选
	 */
	@JsonIgnore
	public List<Filter> getFilters() {
		return pageable.getFilters();
	}

	/**
	 * 获取总页数
	 * 
	 * @return 总页数
	 */
	public int getTotalPages() {
		return (int) Math.ceil((double) getTotal() / (double) getPageSize());
	}
	/**
	 * 获取内容
	 * 
	 * @return 内容
	 */
	public List<T> getRows() {
		return rows;
	}
	public void setRows(List<T> rows) {
		this.rows = rows;
	}
	/**
	 * 获取总记录数
	 * 
	 * @return 总记录数
	 */
	public long getTotal() {
		return total;
	}
	
	public void setTotal(long total){
		this.total = total;
	}

	@JsonIgnore
	public List<T> getFooter() {
		return footer;
	}

	public void setFooter(List<T> footer) {
		this.footer = footer;
	}
}
