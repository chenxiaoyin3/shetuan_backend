package com.shetuan.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.shetuan.dao.ImageDao;
import com.shetuan.entity.Image;

@Repository("ImageDaoImpl")
public class ImageDaoImpl extends BaseDaoImpl<Image,Long> implements ImageDao{

}
