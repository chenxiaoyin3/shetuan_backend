package com.grain.service.impl.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grain.dao.user.AdminDao;
import com.grain.entity.user.OpRegister;
import com.grain.entity.user.Role;
import com.grain.service.user.MenuService;
import com.hongyu.EasyUItreeNode;
import com.hongyu.MenuNode;

@Service("menuServiceImpl")
public class MenuServiceImp implements MenuService {

	@Resource(name = "adminDaoImpl")
	private AdminDao dao;
	
	@PersistenceContext
	protected EntityManager entityManager;
	
	// 根节点pid
	public static Long pid4root = 0l;
	// url target 为主页面iframe名称
	public static String target = "mainFrame";

	public static String empty = "";

	@Override
	@Transactional(readOnly = true)
	public List<MenuNode> getMenuNode(Integer sid) {
		// TODO Auto-generated method stub
		List<MenuNode> nodes = new LinkedList<MenuNode>();

/*		Role role = dao.getById(sid).getRole();

		for (OpRegister op : role.getOps()) {

			MenuNode node = Op2Mnode(op);
			if (node != null) {
				nodes.add(node);
			}

		}
*/
		return nodes;
	}

	/**
	 * 根据OpRegister构造MenuNode对象
	 * 
	 * @param op
	 * @return
	 */
	public MenuNode Op2Mnode(OpRegister op) {

		int hasDisplay = op.getHasDisplay();
		// 该操作不需要作为菜单显示
		if (hasDisplay == 0) {
			return null;
		} else {
			MenuNode node = new MenuNode();
			node.setId(op.getId());
			node.setName(op.getOpname());
			node.setTarget(target);

			if (op.getIcon() == null) {
				node.setIcon(empty);
			} else {
				node.setIcon(op.getIcon());
			}

			if (op.getUrl() == null) {
				node.setUrl(empty);
			} else {
				node.setUrl(op.getUrl());
			}
			// 根节点
			if (op.getParent() == null) {
				node.setpId(pid4root);

			} else {
				node.setpId(op.getParent().getId());
			}
			// 默认展开
			node.setOpen(true);
			return node;
		}

	}
	
	public Role userRole(String username){
		Map<String, Object> params = new HashMap<String, Object>();
		String jpql = "select role from v_user where username=:username" ;
		params.put("username", username);	// 设置查询条件
		String name = new String();
		if (dao.findBysql(jpql, params) != null){
			@SuppressWarnings("rawtypes")
			List lst = dao.findBysql(jpql, params);
			if (lst.get(0)!=null){
				name = (String)lst.get(0);
//				return rolename;
			}
		}
		if (name == null) {
			return null;
		}
		try {
			String jpql2 = "select role from Role role where lower(role.name) = lower(:name)";
			return entityManager.createQuery(jpql2, Role.class).setFlushMode(FlushModeType.COMMIT).setParameter("name", name).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
		
//		return null;
	}
	
	
	@Transactional
	public List<EasyUItreeNode> getEasyNode(String  username,String menuid){
//		Role role = dao.findByUsername(username).getRole();
		Role role = userRole(username);
		
		List<EasyUItreeNode> nodes = new ArrayList<EasyUItreeNode>();
		
		for (OpRegister op : role.getOps()) {
			EasyUItreeNode node=null;
			if (menuid!=null){ //只取menuid下一层菜单
				if (op.getParent()!=null && op.getParent().getId().toString().equalsIgnoreCase(menuid))
					node = Op2Easynode(op);
			}else{ //只取一级菜单
				if (op.getParent()==null)
					node = Op2Easynode(op);
			}
			if (node != null) {
				nodes.add(node);
			}
		}
		Collections.sort(nodes);
		return nodes;
	}
	
	/**
	 * 根据OpRegister构造MenuNode对象
	 * 
	 * @param op
	 * @return
	 */
	private EasyUItreeNode Op2Easynode(OpRegister op) {

		int hasDisplay = op.getHasDisplay();
		// 该操作不需要作为菜单显示
		if (hasDisplay == 0) {
			return null;
		} else {
			EasyUItreeNode node = new EasyUItreeNode();
			node.setId(op.getId().toString());
			node.setText(op.getOpname());
			Map<String,String> attributes = new HashMap<String,String>();
			if (op.getUrl() != null){
				attributes.put("url", op.getUrl());
				node.setUrl(op.getUrl());
				node.setAttributes(attributes);
			}
            if(op.getIcon()==null){
            	node.setIconCls(empty);
            }else{
            	node.setIconCls(op.getIcon());
            }
            
			// 默认展开
            node.setState( "open");
            for (OpRegister opp : op.getChildren()){
            	if (opp.getHasDisplay()!=0){
            		node.setState( "closed");
            		break;
            	}
            }
			return node;
		}
	}
}
