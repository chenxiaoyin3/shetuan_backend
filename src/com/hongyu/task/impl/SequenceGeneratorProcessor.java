package com.hongyu.task.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.hongyu.entity.CommonSequence;
import com.hongyu.service.CommonSequenceService;
import com.hongyu.task.Processor;
@Component("sequenceGeneratorProcessor")
public class SequenceGeneratorProcessor implements Processor {
	
	 @Resource(name="commonSequenceServiceImp")
	 CommonSequenceService commonSequenceService;

	@Override
	public void process() {
		// TODO Auto-generated method stub
		try{
			List<CommonSequence> commonSequences = commonSequenceService.findAll();
			for(CommonSequence cs : commonSequences) {
				cs.setValue(0L);
				commonSequenceService.update(cs);
			}
		} catch (Exception e) {
		      e.printStackTrace();
		}
	}
}
