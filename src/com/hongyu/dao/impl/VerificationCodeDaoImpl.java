package com.hongyu.dao.impl;


import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.VerificationCodeDao;
import com.hongyu.entity.VerificationCode;

@Repository("verificationCodeDaoImpl")
public class VerificationCodeDaoImpl extends BaseDaoImpl<VerificationCode, Long> implements VerificationCodeDao {

}
