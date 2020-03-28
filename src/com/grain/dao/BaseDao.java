package com.grain.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.persistence.LockModeType;

import com.hongyu.Filter;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;

/**
 * Dao - 基类
 * 
 */
public interface BaseDao<T, ID extends Serializable> {

	/**
	 * 查找实体对象
	 * 
	 * @param id
	 *            ID
	 * @return 实体对象，若不存在则返回null
	 */
	T find(ID id);

	/**
	 * 查找实体对象
	 * 
	 * @param id
	 *            ID
	 * @param lockModeType
	 *            锁定方式
	 * @return 实体对象，若不存在则返回null
	 */
	T find(ID id, LockModeType lockModeType);
	
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
	 * 查询实体对象数量
	 * 
	 * @param filters
	 *            筛选
	 * @return 实体对象数量
	 */
	long count(Filter... filters);

	/**
	 * 持久化实体对象
	 * 
	 * @param entity
	 *            实体对象
	 */
	void persist(T entity);

	/**
	 * 合并实体对象
	 * 
	 * @param entity
	 *            实体对象
	 * @return 实体对象
	 */
	T merge(T entity);

	/**
	 * 移除实体对象
	 * 
	 * @param entity
	 *            实体对象
	 */
	void remove(T entity);

	/**
	 * 刷新实体对象
	 * 
	 * @param entity
	 *            实体对象
	 */
	void refresh(T entity);

	/**
	 * 刷新实体对象
	 * 
	 * @param entity
	 *            实体对象
	 * @param lockModeType
	 *            锁定方式
	 */
	void refresh(T entity, LockModeType lockModeType);

	/**
	 * 获取实体对象ID
	 * 
	 * @param entity
	 *            实体对象
	 * @return 实体对象ID
	 */
	ID getIdentifier(T entity);

	/**
	 * 判断是否为托管状态
	 * 
	 * @param entity
	 *            实体对象
	 * @return 是否为托管状态
	 */
	boolean isManaged(T entity);

	/**
	 * 设置为游离状态
	 * 
	 * @param entity
	 *            实体对象
	 */
	void detach(T entity);

	/**
	 * 锁定实体对象
	 * 
	 * @param entity
	 *            实体对象
	 * @param lockModeType
	 *            锁定方式
	 */
	void lock(T entity, LockModeType lockModeType);

	/**
	 * 清除缓存
	 */
	void clear();

	/**
	 * 同步数据
	 */
	void flush();

	// addd by szy on 20160407 for easyui datagrid
	public Page<T> findPage(Pageable pageable,T t);
	
	public List<Object[]> findBysql(String jpql,Map<String, Object> params);
	public List<Object[]> findBysql(String jpql);

	/**
	 * 
	 * @param jpql  sql语句
	 * @param pageable	
	 * @return
	 */
	Page<List<Object[]>> findPageBysql(String jpql, Pageable pageable);
	
	/**
	 * added by xyy  20171221
	 * 在CollectedController中首先被使用
	 * */
	Page<List<Object[]>> findPageBySqlAndParam(String jpql, Map<String, Object> params, Pageable pageable);

	/**added by xyy 20180130  统计某一列的数值和*/
	Object getSingleResultByNativeQuery(String jpql);
	
	/**added by xyy 20180131 只用于电子券的统计*/
	List<Object[]> statis(String jpql);

	/**added by wj 2019/1/2 对数据库进行删除操作*/
	Object deleteBySql(String jpql);
}
