package com.shetuan.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.shetuan.dao.LiteratureDao;
import com.shetuan.entity.Literature;

@Repository("LiteratureDaoImpl")
public class LiteratureDaoImpl extends BaseDaoImpl<Literature,Long> implements LiteratureDao{

}

