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
package org.springblade.modules.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.launch.constant.AppConstant;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.annotation.PreAuth;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.constant.RoleConstant;
import org.springblade.core.tool.utils.Func;
import org.springblade.modules.system.entity.TopMenu;
import org.springblade.modules.system.service.ITopMenuService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springblade.core.cache.constant.CacheConstant.MENU_CACHE;
import static org.springblade.core.cache.constant.CacheConstant.SYS_CACHE;

/**
 * 顶部菜单表 控制器
 *
 * @author BladeX
 * @since 2019-07-14
 */
@RestController
@AllArgsConstructor
@RequestMapping(AppConstant.APPLICATION_SYSTEM_NAME + "/topmenu")
@Api(value = "顶部菜单表", tags = "顶部菜单")
@PreAuth(RoleConstant.HAS_ROLE_ADMIN)
public class TopMenuController extends BladeController {

	private ITopMenuService topMenuService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入topMenu")
	public R<TopMenu> detail(TopMenu topMenu) {
		TopMenu detail = topMenuService.getOne(Condition.getQueryWrapper(topMenu));
		return R.data(detail);
	}

	/**
	 * 分页 顶部菜单表
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入topMenu")
	public R<IPage<TopMenu>> list(TopMenu topMenu, Query query) {
		IPage<TopMenu> pages = topMenuService.page(Condition.getPage(query), Condition.getQueryWrapper(topMenu));
		return R.data(pages);
	}

	/**
	 * 新增 顶部菜单表
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入topMenu")
	public R save(@Valid @RequestBody TopMenu topMenu) {
		return R.status(topMenuService.save(topMenu));
	}

	/**
	 * 修改 顶部菜单表
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入topMenu")
	public R update(@Valid @RequestBody TopMenu topMenu) {
		return R.status(topMenuService.updateById(topMenu));
	}

	/**
	 * 新增或修改 顶部菜单表
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入topMenu")
	public R submit(@Valid @RequestBody TopMenu topMenu) {
		return R.status(topMenuService.saveOrUpdate(topMenu));
	}


	/**
	 * 删除 顶部菜单表
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(topMenuService.deleteLogic(Func.toLongList(ids)));
	}

	/**
	 * 设置顶部菜单
	 */
	@PostMapping("/grant")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "顶部菜单配置", notes = "传入topMenuId集合以及menuId集合")
	@CacheEvict(cacheNames = {SYS_CACHE, MENU_CACHE}, allEntries = true)
	public R grant(@ApiParam(value = "topMenuId集合", required = true) @RequestParam String topMenuIds,
				   @ApiParam(value = "menuId集合", required = true) @RequestParam String menuIds) {
		boolean temp = topMenuService.grant(Func.toLongList(topMenuIds), Func.toLongList(menuIds));
		return R.status(temp);
	}

}
