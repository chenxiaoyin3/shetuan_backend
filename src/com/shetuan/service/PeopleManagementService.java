package com.shetuan.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.hongyu.Json;
import com.hongyu.Pageable;
import com.shetuan.entity.People;

public interface PeopleManagementService {
	public Json list(Pageable pageable, String peopleName, boolean state);

	public Json detailById(Long id);

	public Json add(People people);

	public Json editById(People people);

	public Json invalidById(Long id);
	
	public Json validById(Long id);

	public Json getAllPeople();
}
