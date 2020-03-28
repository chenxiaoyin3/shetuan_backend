package com.grain.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hongyu.Filter;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;

/**
 * Service - 基类
 * 
 */
/**
 * @author root
 *
 * @param <T>
 * @param <ID>
 */
public interface BaseService<T, ID extends Serializable> {

	/**
	 * 查找实体对象
	 * 
	 * @param id
	 *            ID
	 * @return 实体对象，若不存在则返回null
	 */
	T find(ID id);

	/**
	 * 查找所有实体对象集合
	 * 
	 * @return 所有实体对象集合
	 */
	List<T> findAll();

	/**
	 * 查找实体对象集合
	 * 
	 * @param ids
	 *            ID
	 * @return 实体对象集合
	 */
	@SuppressWarnings("unchecked")
	List<T> findList(ID... ids);

	/**
	 * 查找实体对象集合
	 * 
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 实体对象集合
	 */
	List<T> findList(Integer count, List<Filter> filters, List<Order> orders);

	/**
	 * 查找实体对象集合
	 * 
	 * @param first
	 *            起始记录
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 实体对象集合
	 */
	List<T> findList(Integer first, Integer count, List<Filter> filters, List<Order> orders);

	/**
	 * 查找实体对象分页
	 * 
	 * @param pageable
	 *            分页信息
	 * @return 实体对象分页
	 */
	Page<T> findPage(Pageable pageable);

	/**
	 * 查询实体对象总数
	 * 
	 * @return 实体对象总数
	 */
	long count();

	/**
	 * 查询实体对象数量
	 * 
	 * @param filters
	 *            筛选
	 * @return 实体对象数量
	 */
	long count(Filter... filters);

	/**
	 * 判断实体对象是否存在
	 * 
	 * @param id
	 *            ID
	 * @return 实体对象是否存在
	 */
	boolean exists(ID id);

	/**
	 * 判断实体对象是否存在
	 * 
	 * @param filters
	 *            筛选
	 * @return 实体对象是否存在
	 */
	boolean exists(Filter... filters);

	/**
	 * 保存实体对象
	 * 
	 * @param entity
	 *            实体对象
	 */
	void save(T entity);

	/**
	 * 更新实体对象
	 * 
	 * @param entity
	 *            实体对象
	 * @return 实体对象
	 */
	T update(T entity);

	/**
	 * 更新实体对象
	 * 
	 * @param entity
	 *            实体对象
	 * @param ignoreProperties
	 *            忽略属性
	 * @return 实体对象
	 */
	T update(T entity, String... ignoreProperties);

	/**
	 * 删除实体对象
	 * 
	 * @param id
	 *            ID
	 */
	void delete(ID id);

	/**
	 * 删除实体对象
	 * 
	 * @param ids
	 *            ID
	 */
	@SuppressWarnings("unchecked")
	void delete(ID... ids);

	/**
	 * 删除实体对象
	 * 
	 * @param entity
	 *            实体对象
	 */
	void delete(T entity);

	// szy add on 20160407 便于eayui的datagrid使用
	public Page<T> findPage(Pageable pageable,T t) ;
	//szy add on 20160711 便于所有用户检测同名用
	boolean existUserName(String username);
	//szy add on 20160711 便于所有用户检测角色
	public String userRole(String username);
	//yl add on 20161031便于专家虫调查询
	public List ExpertInsectInvestigation(String inquire);
	
	//xyy add on 20171221
	public Page<List<Object[]>> findPageBysql(String jpql, Pageable pageable);
	
	//xyy add on 20180130  统计某一列的数值和
	public Object getSingleResultByNativeQuery(String jpql);
	
	//xyy add on 20180131 只用于电子券的统计
	public List<Object[]> statis(String jpql);
	
	/**added by wj 2019/1/2 对数据库进行删除操作*/
	public Object deleteBySql(String jpql);
	
	/** xyy add on 20171221 用于CollectedController 收款记录的ReceiptOther的查询 */
	public Page<List<Object[]>> findPageBySqlAndParam(String jpql, Map<String, Object> params, Pageable pageable);

	/**
	 * Oct 29, 2019 by cxy
	 * 根据map<属性名称，对象> 添加filter
	 * @param params
	 * @return
	 */
	public List<Filter> setFilterEqsAsMap(Map<String, Object> params);
	
	/**
	 * Nov 8, 2019 by cxy
	 * 根据map<属性名称，对象> 添加Filter.ge
	 * @param params
	 * @return
	 */
	public List<Filter> setFilterGeMap(Map<String, Object> params);
	
	/**
	 * Nov 8, 2019 by cxy
	 * 根据map<属性名称，对象> 添加Filter.le
	 * @param params
	 * @return
	 */
	public List<Filter> setFilterLeMap(Map<String, Object> params);
	
	
	/**
	 * Oct 31, 2019 by cxy
	 * 根据字段名称获取实体集合中的部分字段，放入hashmap中返回
	 * @param properties
	 * @return
	 */
	public List<HashMap<String,Object>> getResultByObjectMapper(List<T> objects, String filterName,
			Set<String> properties);

	public List<Filter> addFilterGeMap(Map<String, Object> params, List<Filter> filters);

	public List<Filter> addFilterLeMap(Map<String, Object> params, List<Filter> filters);

	/**
	 * Nov 15, 2019 by cxy
	 * transfer entity into hashmap
	 * @param object
	 * @return
	 */
	public HashMap<String, Object> transferToHashMap(ObjectMapper mapper, T object);
	
	public List<HashMap<String, Object>> transferToHashMapList(ObjectMapper mapper, List<T> objects);
}
