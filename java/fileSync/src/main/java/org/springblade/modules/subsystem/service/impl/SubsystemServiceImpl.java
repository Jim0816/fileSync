/*
 *      Copyright (c) 2018-2028, Chill Zhuang All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: Chill 庄骞 (smallchill@163.com)
 */
package org.springblade.modules.subsystem.service.impl;

import org.springblade.modules.subsystem.entity.Subsystem;
import org.springblade.modules.subsystem.vo.SubsystemVO;
import org.springblade.modules.subsystem.mapper.SubsystemMapper;
import org.springblade.modules.subsystem.service.ISubsystemService;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 *  服务实现类
 *
 * @author BladeX
 * @since 2020-03-04
 */
@Service
public class SubsystemServiceImpl extends BaseServiceImpl<SubsystemMapper, Subsystem> implements ISubsystemService {

	private SubsystemMapper subsystemMapper;

	@Override
	public IPage<SubsystemVO> selectSubsystemPage(IPage<SubsystemVO> page, SubsystemVO subsystem) {
		return page.setRecords(baseMapper.selectSubsystemPage(page, subsystem));
	}


}
