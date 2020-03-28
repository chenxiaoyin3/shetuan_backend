package com.grain.dao.impl;

import javax.persistence.FlushModeType;

import org.springframework.stereotype.Repository;

import com.grain.dao.LogDao;
import com.grain.entity.Log;

/**
 * Dao - 日志
 * 
 */
@Repository("logDaoImpl")
public class LogDaoImpl extends BaseDaoImpl<Log, Long> implements LogDao {

	public void removeAll() {
		String jpql = "delete from Log log";
		entityManager.createQuery(jpql).setFlushMode(FlushModeType.COMMIT).executeUpdate();
	}

}