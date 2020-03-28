package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.BranchPreSaveDao;
import com.hongyu.entity.BranchPreSave;

@Repository("branchPreSaveDaoImpl")
public class BranchPreSaveDaoImpl extends BaseDaoImpl<BranchPreSave, Long> implements BranchPreSaveDao {

}
