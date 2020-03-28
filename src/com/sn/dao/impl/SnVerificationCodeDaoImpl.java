package com.sn.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.sn.dao.SnVerificationCodeDao;
import com.sn.entity.SnVerificationCode;

@Repository("SnVerificationCodeDaoImpl")
public class SnVerificationCodeDaoImpl extends BaseDaoImpl<SnVerificationCode, Long> implements SnVerificationCodeDao {

}
