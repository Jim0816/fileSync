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
package org.springblade.modules.subsystem.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import lombok.AllArgsConstructor;
import javax.validation.Valid;

import org.springblade.common.cache.UserCache;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.modules.auth.utils.HttpUtil;
import org.springblade.modules.subsystem.utils.RSAUtils;
import org.springblade.modules.system.entity.Menu;
import org.springblade.modules.system.service.IMenuService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.modules.subsystem.entity.Subsystem;
import org.springblade.modules.subsystem.vo.SubsystemVO;
import org.springblade.modules.subsystem.service.ISubsystemService;
import org.springblade.core.boot.ctrl.BladeController;

import java.util.*;

/**
 *  控制器
 *
 * @author BladeX
 * @since 2020-03-04
 */
@RestController
@AllArgsConstructor
@RequestMapping("blade-subsystem/subsystem")
@Api(value = "", tags = "接口")
public class SubsystemController extends BladeController {

	private ISubsystemService subsystemService;
	private IMenuService menuService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入subsystem")
	public R<Subsystem> detail(Subsystem subsystem) {
		Subsystem detail = subsystemService.getOne(Condition.getQueryWrapper(subsystem));
		return R.data(detail);
	}

	/**
	 * 分页
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入subsystem")
	public R<IPage<Subsystem>> list(Subsystem subsystem, Query query) {
		IPage<Subsystem> pages = subsystemService.page(Condition.getPage(query), Condition.getQueryWrapper(subsystem));
		return R.data(pages);
	}

	/**
	 * 自定义分页
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入subsystem")
	public R<IPage<SubsystemVO>> page(SubsystemVO subsystem, Query query) {
		IPage<SubsystemVO> pages = subsystemService.selectSubsystemPage(Condition.getPage(query), subsystem);
		return R.data(pages);
	}

	/**
	 * 新增
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入subsystem")
	public R save(@Valid @RequestBody Subsystem subsystem) throws InterruptedException {
        if(subsystemService.save(subsystem)){
			Subsystem s = new Subsystem();
			s.setName(subsystem.getName());
			s.setUrl(subsystem.getUrl());
			Subsystem subsystem1 = subsystemService.getOne(Wrappers.query(s));
			Menu menu = new Menu();
			menu.setParentId(0L);
			menu.setCode(subsystem.getName());
			menu.setName(subsystem.getName());
			menu.setAlias("system");
			menu.setSort(0);
			menu.setCategory(0);
			Long pid = subsystem1.getId();
			menu.setSysParentId(pid);
			menu.setIsSon(subsystem1.getId());
			return R.status(menuService.save(menu));
		}
        return R.status(false);

	}

	/**
	 * 修改
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入subsystem")
	public R update(@Valid @RequestBody Subsystem subsystem) {
		Menu menu = new Menu();
		menu.setCategory(0);
		menu.setParentId(0L);
		menu.setCode(subsystem.getName());
		menu.setName(subsystem.getName());
		menu.setAlias("system");
		menu.setSort(0);
		menu.setIsSon(subsystem.getId());
		return R.status(subsystemService.updateById(subsystem)&&menuService.updateByIsSon(menu));
	}

	/**
	 * 新增或修改
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入subsystem")
	public R submit(@Valid @RequestBody Subsystem subsystem) {
		return R.status(subsystemService.saveOrUpdate(subsystem));
	}


	/**
	 * 删除
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		List<Long> list = Func.toLongList(ids);
		System.out.println(list);

		List<Subsystem> l = subsystemService.listByIds(list);
		l.stream().forEach(e->{
			Menu menu = new Menu();
			menu.setIsSon(e.getId());
			Menu menu1 = menuService.getOne(Condition.getQueryWrapper(menu));
			menuService.deleteBySystemId(menu1.getId());
		});
		return R.status(subsystemService.deleteLogic(list)&&menuService.deleteByIsSon(list));
	}

	/**
	 * 获取密钥
	 */
	@GetMapping("/keypair")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "获取密钥", notes = "传入keypair")
	public R keypair() throws Exception {
		Map map = RSAUtils.genKeyPair();
		HashMap hashmap = new HashMap();
		hashmap.put("privateKey",RSAUtils.getPrivateKey(map));
		hashmap.put("publicKey",RSAUtils.getPublicKey(map));
		return R.data(hashmap);
	}

	@PostMapping("/SSOLogin")
	@ApiOperationSupport(order = 9)
	@ApiOperation(value = "单点登录", notes = "传入url")
	public R SSOLogin(@RequestParam("username")String username,@RequestParam("systemname")String systemname) throws Exception{
		Subsystem subsystem = new Subsystem();
		subsystem.setName(systemname);
		String key = subsystemService.getOne(Condition.getQueryWrapper(subsystem)).getPublicKey();
		String data = RSAUtils.encryptDataOnJavaByPublicKey(username,key);
		return R.data(data);
	}

	//systemname子系統名稱  data為用戶電話號碼
	@PostMapping("/encrypt")
	public R encrypt(@RequestParam("systemname")String systemname,@RequestParam("data")String data){
		Subsystem subsystem = new Subsystem();
		subsystem.setName(systemname);
		String key = subsystemService.getOne(Condition.getQueryWrapper(subsystem)).getPublicKey();
		String result = RSAUtils.encryptDataOnJavaByPublicKey(data,key);
		return R.data(result);
	}

	@GetMapping("/untreated/corn")
	public R corn(BladeUser user,@RequestParam Long id){
		String phone = UserCache.getUser(user.getUserId()).getPhone();
		Subsystem subsystem = subsystemService.getOne(Wrappers.<Subsystem>query().eq("id",id));
		try{
			String subsystemName = subsystem.getName().trim();
			String untreatedUrl = "";
			switch(subsystemName){
				case "火灾调查系统" :
					untreatedUrl = subsystem.getUrl()+"/api/blade-firereason/accidentInfoDeal/untreated";
					break;
				default:
					break;
			}
			String result = HttpUtil.sendGet(untreatedUrl, "phone="+phone);
			JSONObject data = JSONObject.parseObject(result);
			data.put("id",subsystem.getId().toString());
			data.put("name",subsystem.getName());
			data.put("num",data.get("num"));
			data.put("url",data.get("url"));
			return R.data(data);
		}catch(Exception e){
			e.printStackTrace();
		}
		//查询出现异常后返回以下默认值
		JSONObject json = new JSONObject();
		json.put("id",subsystem.getId().toString());
		json.put("name",subsystem.getName());
		json.put("num",0);
		json.put("url","#");
		return R.data(json);
	}

	@GetMapping("/untreated/list")
	public R untreated(BladeUser user){
		String phone = UserCache.getUser(user.getUserId()).getPhone();
		List<Subsystem> list = subsystemService.list();
		List<JSONObject> untreated = new ArrayList();
		list.stream().forEach(e->{
			try {
				String result = HttpUtil.sendGet(e.getUrl()+"/untreated", "phone="+phone);
				JSONObject data = JSONObject.parseObject(result);
				data.put("name",e.getName());
				untreated.add(data);
			}catch(Exception ex){
				JSONObject data = new JSONObject();
				data.put("name",e.getName());
				data.put("url","#");
				data.put("num","0");
				untreated.add(data);
				ex.printStackTrace();
			}

		});
		return R.data(untreated);
	}
}
