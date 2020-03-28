package com.hongyu.dao.impl;

import java.util.List;
import java.util.Map;

import javax.persistence.LockModeType;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.Filter;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.dao.GroupPlaceholderDao;
import com.hongyu.entity.Expose;
import com.hongyu.entity.GroupPlaceholder;

@Repository("GroupPlaceholderDaoImpl")
public class GroupPlaceholderDaoImpl extends BaseDaoImpl<GroupPlaceholder, Long> implements GroupPlaceholderDao {

	

}
