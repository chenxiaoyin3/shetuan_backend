package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.CommonUploadFileDao;
import com.hongyu.entity.CommonUploadFileEntity;
@Repository("commonUploadFileDaoImpl")
public class CommonUploadFileDaoImpl extends BaseDaoImpl<CommonUploadFileEntity, Long> implements CommonUploadFileDao {

}
