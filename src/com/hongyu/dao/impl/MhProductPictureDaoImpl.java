package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.MhProductPictureDao;
import com.hongyu.entity.MhProductPicture;

@Repository("mhProductPictureDaoImpl")
public class MhProductPictureDaoImpl extends BaseDaoImpl<MhProductPicture,Long> implements MhProductPictureDao {

}
