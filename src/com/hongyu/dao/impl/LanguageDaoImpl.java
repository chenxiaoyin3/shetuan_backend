package com.hongyu.dao.impl;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.LanguageDao;
import com.hongyu.entity.Language;

@Repository("languageDaoImpl")
public class LanguageDaoImpl extends BaseDaoImpl<Language, Long> implements LanguageDao {

	@Override
	public Language findByName(String name) {
		// TODO Auto-generated method stub
		if (name == null) {
			return null;
		}
		try {
			String jpql = "select language from Language language where language.name = :name";
			return entityManager.createQuery(jpql, Language.class).setFlushMode(FlushModeType.COMMIT).setParameter("name", name).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

}
