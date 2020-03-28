package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.MemberDao;
import com.hongyu.entity.Member;
import com.hongyu.service.MemberService;

@Service("memberServiceImpl")
public class MemberServiceImpl extends BaseServiceImpl<Member,Long> implements MemberService {
	@Resource(name="memberDaoImpl")
	MemberDao memberDao;
	
	@Resource(name="memberDaoImpl")
	@Override
	public void setBaseDao(BaseDao<Member, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}

	
	
}
