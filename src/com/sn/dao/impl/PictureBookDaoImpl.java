package com.sn.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.sn.dao.PictureBookDao;
import com.sn.entity.PictureBook;

@Repository("PictureBookDaoImpl")
public class PictureBookDaoImpl extends BaseDaoImpl<PictureBook,Long> implements PictureBookDao {

}
