package com.grain.dao.impl.user;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.grain.dao.user.OpRegisterDao;
import com.grain.entity.user.OpRegister;

@Repository("opRegisterDaoImpl")
public class OpRegisterDaoImpl extends BaseDaoImpl <OpRegister,Long>implements OpRegisterDao {


}
