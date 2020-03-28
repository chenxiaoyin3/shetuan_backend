package com.shetuan.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.shetuan.dao.SupplementFileDao;
import com.shetuan.entity.SupplementFile;

@Repository("SupplementFileDaoImpl")
public class SupplementFileDaoImpl extends BaseDaoImpl<SupplementFile,Long> implements SupplementFileDao{

}

