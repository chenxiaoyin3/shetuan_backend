package com.shetuan.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.shetuan.dao.AudioDao;
import com.shetuan.entity.Audio;

@Repository("AudioDaoImpl")
public class AudioDaoImpl extends BaseDaoImpl<Audio,Long> implements AudioDao{

}
