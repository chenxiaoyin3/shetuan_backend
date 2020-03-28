package com.shetuan.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.shetuan.dao.SupplementDao;
import com.shetuan.entity.Supplement;

@Repository("SupplementDaoImpl")
public class SupplementDaoImpl extends BaseDaoImpl<Supplement,Long> implements SupplementDao{

}
