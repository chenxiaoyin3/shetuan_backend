package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.AdministrativeUploadDao;
import com.hongyu.entity.AdministrativeUpload;
@Repository("administrativeUploadDaoImpl")
public class AdministrativeUploadDaoImpl extends BaseDaoImpl<AdministrativeUpload, Long> implements AdministrativeUploadDao {

}

