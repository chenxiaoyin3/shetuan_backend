package com.sn.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.sn.dao.PictureBookResourcesDao;
import com.sn.entity.PictureBookResources;

@Repository("PictureBookResourcesDaoImpl")
public class PictureBookResourcesDaoImpl extends BaseDaoImpl<PictureBookResources,Long> implements PictureBookResourcesDao {

}
