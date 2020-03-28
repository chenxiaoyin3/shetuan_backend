package com.hongyu.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArrayHandler {
	public static Map<String, Object> toMap(String[] keys,Object[] arr){
		if(keys.length!=arr.length){
			return null;
		}
		Map<String, Object> map = new HashMap<>();
		for(int i=0;i<keys.length;i++){
			map.put(keys[i], arr[i]);
		}
		return map;
	}
	
	public static Map<String, Object> toSpecialtyBaseInfoMap(Object[] object){
		Map<String,Object> map = new HashMap<>();
		Map<String, Object> specialty = new HashMap<>();
		Map<String, Object> specification = new HashMap<>();
		Map<String, Object> iconURL = new HashMap<>();
		specialty.put("id", object[0]);
		specialty.put("name", object[1]);
		map.put("specialty", specialty);
		specification.put("id", object[2]);
		specification.put("specification", object[3]);
		map.put("specification", specification);
		map.put("pPrice", object[4]);
		map.put("hasSold", object[5]);
		iconURL.put("mediumPath", object[6]);
		map.put("iconURL", iconURL);
		return map;
	}

}
