package com.hongyu.dao.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.FlushModeType;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyGroupDao;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyLine;
@Repository("hyGroupDaoImpl")
public class HyGroupDaoImpl extends BaseDaoImpl<HyGroup, Long> implements HyGroupDao {

	@Override
	public List<Date> groupDateExist(Long line, Long id,Boolean teamType) {
		// TODO Auto-generated method stub
		String jpql = null;
//		if(id != null) {
//			jpql = "select g.startDay from com.hongyu.entity.HyGroup g where g.line.id = ?1 and g.id != ?2";
//		} else {
//			jpql = "select g.startDay from com.hongyu.entity.HyGroup g where g.line.id = ?1 and g.teamType = ?3";
//		}
		jpql = "select g.startDay from com.hongyu.entity.HyGroup g where g.line.id = ?1 and g.teamType = ?3 and g.isCancel = ?4";
		
		TypedQuery<Date> result = entityManager.createQuery(jpql, Date.class).setFlushMode(FlushModeType.COMMIT);
		result.setParameter(1, line);
		result.setParameter(3, teamType);
		result.setParameter(4, false);
//		if(id != null) {
//			result.setParameter(2, id);
//		}
		return result.getResultList();
	}
	
}
