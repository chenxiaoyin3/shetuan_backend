package com.grain.service.impl.user;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grain.dao.RoleDao;
import com.grain.dao.impl.RoleDaoImpl;
import com.grain.dao.impl.user.AdminDaoImpl;
import com.grain.dao.impl.user.OpRegisterDaoImpl;
import com.grain.entity.user.OpRegister;
import com.grain.entity.user.Role;
import com.grain.service.impl.BaseServiceImpl;
import com.grain.service.user.RoleService;
import com.hongyu.EasyUItreeNode;
import com.hongyu.Principal;

/**
 * Service - 角色
 * 
 */
@Service("roleServiceImpl")
public class RoleServiceImpl extends BaseServiceImpl<Role, Long> implements RoleService {
	@Resource(name = "opRegisterDaoImpl")
	OpRegisterDaoImpl opdao;
	
	@Resource(name = "adminDaoImpl")
	AdminDaoImpl admindao;
	
	@Resource(name = "roleDaoImpl")
	RoleDaoImpl roledao;
	
	@Resource(name = "roleDaoImpl")
	public void setBaseDao(RoleDao roleDao) {
		super.setBaseDao(roleDao);
	}

	@Override
	@Transactional
	@CacheEvict(value = "authorization", allEntries = true)
	public void save(Role role) {
		super.save(role);
	}

	@Override
	@Transactional
	@CacheEvict(value = "authorization", allEntries = true)
	public Role update(Role role) {
		Role r=roledao.find(role.getId());
		r.setName(role.getName());
		r.setDescription(role.getDescription());
		//return roledao.merge(r);
		return super.update(r); 
	}

	@Override
	@Transactional
	@CacheEvict(value = "authorization", allEntries = true)
	public Role update(Role role, String... ignoreProperties) {
		Role r=roledao.find(role.getId());
		r.setName(role.getName());
		r.setDescription(role.getDescription());
		return super.update(r, ignoreProperties);
	}

	@Override
	@Transactional
	@CacheEvict(value = "authorization", allEntries = true)
	public void delete(Long id) {
		super.delete(id);
	}

	@Override
	@Transactional
	@CacheEvict(value = "authorization", allEntries = true)
	public void delete(Long... ids) {
		super.delete(ids);
	}

	@Override
	@Transactional
	@CacheEvict(value = "authorization", allEntries = true)
	public void delete(Role role) {
		super.delete(role);
	}

	@Override
	public List<EasyUItreeNode> tree(Principal p, String id) {
		List <OpRegister> l =new ArrayList<OpRegister>();
		List <EasyUItreeNode> lt = new ArrayList<EasyUItreeNode>();
		
		if (p==null){
			l = opdao.findList(null, null, null, null);
		}else
			l.addAll( admindao.findByUsername(p.getUsername()).getRole().getOps());

		if (l != null && l.size() > 0) {
			for (OpRegister op: l){
				EasyUItreeNode n=new EasyUItreeNode();
				n.setId(op.getId().toString());
				n.setText(op.getOpname());
				n.setIconCls(op.getIcon()==null?"":op.getIcon());
				n.setUrl(op.getUrl()==null?"":op.getUrl());
				
				if (op.getParent()!=null){
					n.setPid(op.getParent().getId().toString());
				}
				lt.add(n);
			}
		}
		return lt;
	}

	@Override
	@Transactional(readOnly = false)
	public void grant(Role role,Long... ids) {
		Role r=roledao.find(role.getId());
		
		Set<OpRegister> ops = new HashSet<OpRegister>();
		for (Long id : ids) {
			OpRegister op = opdao.find( id);
			ops.add(op);
		}
		r.setOps(ops);
		save(r);
	}
}