package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.ServicePromiseDao;
import com.hongyu.entity.ServicePromise;

@Repository("servicePromiseDaoImpl")
public class ServicePromiseDaoImpl extends BaseDaoImpl<ServicePromise, Long> implements ServicePromiseDao {

}
