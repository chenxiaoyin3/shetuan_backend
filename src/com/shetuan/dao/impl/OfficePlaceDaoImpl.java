package com.shetuan.dao.impl;
import org.springframework.stereotype.Repository;
import com.grain.dao.impl.BaseDaoImpl;
import com.shetuan.entity.OfficePlace;
import com.shetuan.dao.OfficePlaceDao;
@Repository("OfficePlaceDaoImpl")
public class OfficePlaceDaoImpl extends BaseDaoImpl<OfficePlace,Long> implements OfficePlaceDao{

}