package com.hongyu.dao.impl;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.PositionDao;
import com.hongyu.entity.Position;

@Repository("positionDaoImpl")
public class PositionDaoImpl extends BaseDaoImpl<Position, Long> implements PositionDao {

	@Override
	public Position findByName(String name) {
		// TODO Auto-generated method stub
		try{
		String jpql="select position from Position position where position.positionName=:name";
		return entityManager.createQuery(jpql,Position.class).setFlushMode(FlushModeType.COMMIT).setParameter("name", name).getSingleResult();
		}catch(NoResultException exception){
			return null;
		}
	}

}
