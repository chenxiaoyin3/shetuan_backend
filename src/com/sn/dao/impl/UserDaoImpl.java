package com.sn.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.sn.dao.UserDao;
import com.sn.entity.User;

@Repository("UserDaoImpl")
public class UserDaoImpl extends BaseDaoImpl<User,Long> implements UserDao  {

}
