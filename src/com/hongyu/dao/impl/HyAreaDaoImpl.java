package com.hongyu.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.FlushModeType;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyAreaDao;
import com.hongyu.entity.HyArea;
@Repository("hyAreaDaoImpl")
public class HyAreaDaoImpl extends BaseDaoImpl<HyArea, Long> 
implements HyAreaDao{


}
