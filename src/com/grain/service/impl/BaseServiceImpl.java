package com.grain.service.impl;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.grain.dao.BaseDao;
import com.grain.entity.BaseEntity;
import com.grain.service.BaseService;
import com.hongyu.Filter;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.jhlabs.image.HSBAdjustFilter;
import com.shetuan.entity.HistoricalDataIndex;
import com.shetuan.entity.Organization;

/**
 * Service - 基类
 * 
 */
@Transactional
public class BaseServiceImpl<T, ID extends Serializable> implements BaseService<T, ID> {

	/** 更新忽略属性 */
	private static final String[] UPDATE_IGNORE_PROPERTIES = new String[] { BaseEntity.ID_PROPERTY_NAME, BaseEntity.CREATE_DATE_PROPERTY_NAME, BaseEntity.MODIFY_DATE_PROPERTY_NAME };

	/** baseDao */
	private BaseDao<T, ID> baseDao;

	public void setBaseDao(BaseDao<T, ID> baseDao) {
		this.baseDao = baseDao;
	}

	@Transactional(readOnly = true)
	public T find(ID id) {
		return baseDao.find(id);
	}

	@Transactional(readOnly = true)
	public List<T> findAll() {
		return findList(null, null, null, null);
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<T> findList(ID... ids) {
		List<T> result = new ArrayList<T>();
		if (ids != null) {
			for (ID id : ids) {
				T entity = find(id);
				if (entity != null) {
					result.add(entity);
				}
			}
		}
		return result;
	}

	@Transactional(readOnly = true)
	public List<T> findList(Integer count, List<Filter> filters, List<Order> orders) {
		return findList(null, count, filters, orders);
	}

	@Transactional(readOnly = true)
	public List<T> findList(Integer first, Integer count, List<Filter> filters, List<Order> orders) {
		return baseDao.findList(first, count, filters, orders);
	}

	@Transactional(readOnly = true)
	public Page<T> findPage(Pageable pageable) {
		return baseDao.findPage(pageable);
	}

	@Transactional(readOnly = true)
	public long count() {
		return count(new Filter[] {});
	}

	@Transactional(readOnly = true)
	public long count(Filter... filters) {
		return baseDao.count(filters);
	}

	@Transactional(readOnly = true)
	public boolean exists(ID id) {
		return baseDao.find(id) != null;
	}

	@Transactional(readOnly = true)
	public boolean exists(Filter... filters) {
		return baseDao.count(filters) > 0;
	}

	@Transactional
	public void save(T entity) {
		baseDao.persist(entity);
	}

	@Transactional
	public T update(T entity) {
		return baseDao.merge(entity);
	}

	@Transactional
	public T update(T entity, String... ignoreProperties) {
		Assert.notNull(entity);
		if (baseDao.isManaged(entity)) {
			throw new IllegalArgumentException("Entity must not be managed");
		}
		T persistant = baseDao.find(baseDao.getIdentifier(entity));
		if (persistant != null) {
			copyProperties(entity, persistant, (String[]) ArrayUtils.addAll(ignoreProperties, UPDATE_IGNORE_PROPERTIES));
			return update(persistant);
		} else {
			return update(entity);
		}
	}

	@Transactional
	public void delete(ID id) {
		delete(baseDao.find(id));
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public void delete(ID... ids) {
		if (ids != null) {
			for (ID id : ids) {
				delete(baseDao.find(id));
			}
		}
	}

	@Transactional
	public void delete(T entity) {
		baseDao.remove(entity);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void copyProperties(Object source, Object target, String[] ignoreProperties) throws BeansException {
		Assert.notNull(source, "Source must not be null");
		Assert.notNull(target, "Target must not be null");

		PropertyDescriptor[] targetPds = BeanUtils.getPropertyDescriptors(target.getClass());
		List<String> ignoreList = (ignoreProperties != null) ? Arrays.asList(ignoreProperties) : null;
		for (PropertyDescriptor targetPd : targetPds) {
			if (targetPd.getWriteMethod() != null && (ignoreProperties == null || (!ignoreList.contains(targetPd.getName())))) {
				PropertyDescriptor sourcePd = BeanUtils.getPropertyDescriptor(source.getClass(), targetPd.getName());
				if (sourcePd != null && sourcePd.getReadMethod() != null) {
					try {
						Method readMethod = sourcePd.getReadMethod();
						if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
							readMethod.setAccessible(true);
						}
						Object sourceValue = readMethod.invoke(source);
						Object targetValue = readMethod.invoke(target);
						if (sourceValue != null && targetValue != null && targetValue instanceof Collection) {
							Collection collection = (Collection) targetValue;
							collection.clear();
							collection.addAll((Collection) sourceValue);
						} else {
							Method writeMethod = targetPd.getWriteMethod();
							if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
								writeMethod.setAccessible(true);
							}
							writeMethod.invoke(target, sourceValue);
						}
					} catch (Throwable ex) {
						throw new FatalBeanException("Could not copy properties from source to target", ex);
					}
				}
			}
		}
	}

	@Override
	public Page<T> findPage(Pageable pageable, T t) {
		return baseDao.findPage(pageable,t);
	}

	@Override
	public boolean existUserName(String username) {
		Map<String, Object> params = new HashMap<String, Object>();
		String jpql = "select username from v_user where username=:username" ;
		params.put("username", username);	// 设置查询条件
		if (baseDao.findBysql(jpql, params) != null)
			return baseDao.findBysql(jpql, params).size() > 0;
		else
			return false;
	}
	
	public String userRole(String username){
		Map<String, Object> params = new HashMap<String, Object>();
		String jpql = "select role from v_user where username=:username" ;
		params.put("username", username);	// 设置查询条件
		if (baseDao.findBysql(jpql, params) != null){
			@SuppressWarnings("rawtypes")
			List lst = baseDao.findBysql(jpql, params);
			if (lst.get(0)!=null){
				String role = (String)lst.get(0);
				return role;
			}
		}
		return null;
	}
	
	public List ExpertInsectInvestigation(String inquire){
		if (baseDao.findBysql(inquire) != null){
			@SuppressWarnings("rawtypes")
			List lst = baseDao.findBysql(inquire);
			return lst;
		}
		return null;
	}
	
	/**
	 * added by xyy 20171221 
	 * */
	public Page<List<Object[]>> findPageBySqlAndParam(String jpql, Map<String, Object> params, Pageable pageable) {
		return baseDao.findPageBySqlAndParam( jpql,  params,  pageable);
	}
	
	//xyy add on 20171221
	public Page<List<Object[]>> findPageBysql(String jpql, Pageable pageable){
		return baseDao.findPageBysql(jpql, pageable);
	}
	
	@Override
	public Object getSingleResultByNativeQuery(String jpql){
		return baseDao.getSingleResultByNativeQuery(jpql);
	}
	
	@Override
	public List<Object[]> statis(String jpql){
		return baseDao.statis(jpql);
	}
	
	/**added by wj 2019/1/2 对数据库进行删除操作*/
	public Object deleteBySql(String jpql){
		return baseDao.deleteBySql(jpql);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<HashMap<String, Object>> getResultByObjectMapper(List<T> objects, String filterName,
			Set<String> properties) {
		
		List<HashMap<String,Object>> result=new ArrayList<>();
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			FilterProvider myFilter = new SimpleFilterProvider().addFilter(filterName,
					SimpleBeanPropertyFilter.filterOutAllExcept(properties));
			mapper.setFilters(myFilter);
			for(T object : objects) {
				String jsonString = mapper.writeValueAsString(object);
				HashMap<String, Object> m = JSON.parseObject(jsonString, HashMap.class);  
				result.add(m);
			}
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public List<Filter> setFilterEqsAsMap(Map<String, Object> map) {
		List<Filter> filters = new ArrayList<>();
		for (String key : map.keySet()) {
			Object value = map.get(key);
			if(value != null) {
				filters.add(Filter.eq(key, value));
			}
		}
		return filters;
	}
	
	@Override
	public List<Filter> setFilterGeMap(Map<String, Object> params) {
		List<Filter> filters = new ArrayList<>();
		for (String key : params.keySet()) {
			Object value = params.get(key);
			if(value != null) {
				filters.add(Filter.ge(key, value));
			}
		}
		return filters;
	}
	
	@Override
	public List<Filter> addFilterGeMap(Map<String, Object> params, List<Filter> filters) {
		for (String key : params.keySet()) {
			Object value = params.get(key);
			if(value != null) {
				filters.add(Filter.ge(key, value));
			}
		}
		return filters;
	}

	@Override
	public List<Filter> setFilterLeMap(Map<String, Object> params) {
		List<Filter> filters = new ArrayList<>();
		for (String key : params.keySet()) {
			Object value = params.get(key);
			if(value != null) {
				filters.add(Filter.le(key, value));
			}
		}
		return filters;
	}

	@Override
	public List<Filter> addFilterLeMap(Map<String, Object> params, List<Filter> filters) {
		for (String key : params.keySet()) {
			Object value = params.get(key);
			if(value != null) {
				filters.add(Filter.le(key, value));
			}
		}
		return filters;
	}
	
	@Override
	public HashMap<String, Object> transferToHashMap(ObjectMapper mapper, T object) {
		HashMap<String, Object> m = new HashMap<String, Object>();
		try {
			String json = mapper.writeValueAsString(object);
			m = JSON.parseObject(json, HashMap.class); 
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return m;
	}

	@Override
	public List<HashMap<String, Object>> transferToHashMapList(ObjectMapper mapper, List<T> objects) {
		List<HashMap<String, Object>> result=new ArrayList<>();
		for(T object : objects) {
			HashMap<String, Object> m = transferToHashMap(mapper, object);
			result.add(m);
		}
		return result;
	}
}
