package com.hongyu.dao.impl;import org.springframework.stereotype.Repository;import com.grain.dao.impl.BaseDaoImpl;import com.hongyu.dao.AddedServiceAndServiceTransferDao;import com.hongyu.entity.AddedServiceAndServiceTransfer;@Repository("addedServiceAndServiceTransferDaoImpl")public class AddedServiceAndServiceTransferDaoImpl extends BaseDaoImpl<AddedServiceAndServiceTransfer, Long> implements AddedServiceAndServiceTransferDao {}