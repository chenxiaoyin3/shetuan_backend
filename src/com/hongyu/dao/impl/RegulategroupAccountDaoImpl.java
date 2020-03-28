package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.RegulategroupAccountDao;
import com.hongyu.entity.RegulategroupAccount;
@Repository("regulategroupAccountDaoImpl")
public class RegulategroupAccountDaoImpl extends BaseDaoImpl<RegulategroupAccount, Long>
		implements RegulategroupAccountDao {

}
