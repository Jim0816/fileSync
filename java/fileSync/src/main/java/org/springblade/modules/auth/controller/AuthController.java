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
package org.springblade.modules.auth.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.sun.jersey.json.impl.provider.entity.JSONObjectProvider;
import com.wf.captcha.SpecCaptcha;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springblade.common.cache.CacheNames;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.tool.api.R;
import org.springblade.core.launch.constant.AppConstant;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.support.Kv;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.WebUtil;
import org.springblade.modules.auth.granter.ITokenGranter;
import org.springblade.modules.auth.granter.TokenGranterBuilder;
import org.springblade.modules.auth.granter.TokenParameter;
import org.springblade.modules.auth.utils.HttpUtil;
import org.springblade.modules.auth.utils.TokenUtil;
import org.springblade.modules.subsystem.entity.Subsystem;
import org.springblade.modules.subsystem.service.ISubsystemService;
import org.springblade.modules.system.entity.Menu;
import org.springblade.modules.system.entity.UserInfo;
import org.springblade.modules.system.service.IMenuService;
import org.springblade.modules.system.service.IRoleMenuService;
import org.springblade.modules.system.utils.AuthUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 认证模块
 *
 * @author Chill
 */
@RestController
@AllArgsConstructor
@RequestMapping(AppConstant.APPLICATION_AUTH_NAME)
@ApiSort(1)
@Api(value = "用户授权认证", tags = "授权接口")
public class AuthController {

	private BladeRedisCache redisCache;

	private IMenuService menuService;
	private ISubsystemService subsystemService;
	private IRoleMenuService roleMenuService;

	/**
	 * 定时同步权限信息
	 */
//	@Scheduled(cron = "0 0 03 ? * *")
//	@Scheduled(cron = "0 */1 * * * ?")
	@PostMapping("/pullAuth")
	public R pullAuth(){
		//遍历系统获取接口地址
		List<Subsystem> list = subsystemService.list();
		for(Subsystem subsystem : list){
			String url = subsystem.getUrl()+"/yhauth";
			String data = HttpUtil.sendGet(url,"");
			JSONObject json = JSONObject.parseObject(data);
			Menu m1 = new Menu();
			m1.setName(subsystem.getName());
			Menu parent = menuService.getOne(Condition.getQueryWrapper(m1));

			menuService.deleteBySystemId(parent.getId());

//			if(json.get("code").toString().equals("999")){
			try{
				JSONArray ja = JSONArray.parseArray(json.get("data").toString());
				List<Menu> menuList = new ArrayList<>();
				ja.stream().forEach(e->{
					Menu m3 = new Menu();
					JSONObject js = JSONObject.parseObject(e.toString());
					m3.setName(js.get("name").toString());
					m3.setCode(js.get("name").toString());
					m3.setParentId(js.get("pid").toString().equals("0")?parent.getId():parent.getSysParentId()+Long.parseLong(js.get("pid").toString()));
					m3.setId(parent.getSysParentId()+Long.parseLong(js.get("id").toString()));
					m3.setAlias("auth");
					m3.setSystemId(parent.getId());
					menuList.add(m3);
				});
				menuService.saveBatch(menuList);
			}catch(Exception e){
				e.printStackTrace();
				System.out.println(subsystem.getName()+":未获取到数据");
				continue;
			}

		}

		return R.success("权限更新成功");
//		//获取原权限信息
//		Menu m2 = new Menu();
//		m2.setParentId(parent);
//
//		List<Menu> old = menuService.list(Wrappers.query(m2));
//		//获取新权限信息
//		ArrayList news = new ArrayList();
//		news.add(new JSONObject().put("id",1).put("name","人员增加"));
//		news.add(new JSONObject().put("id",2).put("name","人员删除"));
//		news.add(new JSONObject().put("id",3).put("name","人员修改"));
//		news.add(new JSONObject().put("id",4).put("name","人员查看"));
//		//整理
//		HashMap map = AuthUtils.getAuth(old,news);
//		List<JSONObject> addJSON = (List) map.get("add");
//		List<Menu> update = (List) map.get("update");
//		List<Long> del = (List) map.get("del");
//		//新增
//		List<Menu> add = new ArrayList<>();
//		for(JSONObject json: addJSON){
//			Long id = Long.valueOf(json.get("id").toString());
//			String name = json.get("name").toString();
//			Menu menu = new Menu();
//			menu.setParentId(parent);
//			menu.setName(name);
//			menu.setCode(name);
//			menu.setAlias("auth");
//			menu.setSort(0);
//			menu.setCategory(0);
//			menu.setIsSon(id);
//			add.add(menu);
//		}
//		return R.status(menuService.saveBatch(add)&&menuService.updateBatchById(update)&&menuService.removeByIds(del));
	}

	/**
	 * 各系统查询权限信息
	 */
	@GetMapping("pushAuth")
	public R selectAuth(@RequestParam("name")String name){
		List<HashMap> list = roleMenuService.selectRoleBySystemName(name);
		Menu menu = new Menu();
		menu.setName(name);
		HashMap map = new HashMap();
		Long base = menuService.getOne(Condition.getQueryWrapper(menu)).getSysParentId();
		List<HashMap> data = list.stream().map(e->{
			e.put("aid",Long.parseLong(e.get("aid").toString())-base);
			return e;
		}).collect(Collectors.toList());
		return R.data(data);
	}

	@ApiLog("登录用户验证")
	@PostMapping("/oauth/token")
	@ApiOperation(value = "获取认证token", notes = "传入租户ID:tenantId,账号:account,密码:password")
	public Kv token(@ApiParam(value = "租户ID", required = true) @RequestParam String tenantId,
					@ApiParam(value = "账号", required = true) @RequestParam(required = false) String username,
					@ApiParam(value = "密码", required = true) @RequestParam(required = false) String password) {

		Kv authInfo = Kv.create();

		String grantType = WebUtil.getRequest().getParameter("grant_type");
		String refreshToken = WebUtil.getRequest().getParameter("refresh_token");

		String userType = Func.toStr(WebUtil.getRequest().getHeader(TokenUtil.USER_TYPE_HEADER_KEY), TokenUtil.DEFAULT_USER_TYPE);

		TokenParameter tokenParameter = new TokenParameter();
		tokenParameter.getArgs().set("tenantId", tenantId).set("username", username).set("password", password).set("grantType", grantType).set("refreshToken", refreshToken).set("userType", userType);

		ITokenGranter granter = TokenGranterBuilder.getGranter(grantType);
		UserInfo userInfo = granter.grant(tokenParameter);

		if (userInfo == null || userInfo.getUser() == null) {
			return authInfo.set("error_code", HttpServletResponse.SC_BAD_REQUEST).set("error_description", "用户名或密码不正确");
		}

		if (Func.isEmpty(userInfo.getRoles())) {
			return authInfo.set("error_code", HttpServletResponse.SC_BAD_REQUEST).set("error_description", "未获得用户的角色信息");
		}

		return TokenUtil.createAuthInfo(userInfo);
	}

	@GetMapping("/oauth/captcha")
	public Kv captcha() {
		SpecCaptcha specCaptcha = new SpecCaptcha(130, 48, 5);
		String verCode = specCaptcha.text().toLowerCase();
		String key = UUID.randomUUID().toString();
		// 存入redis并设置过期时间为30分钟
		redisCache.setEx(CacheNames.CAPTCHA_KEY + key, verCode, Duration.ofMinutes(30));
		// 将key和base64返回给前端
		return Kv.create().set("key", key).set("image", specCaptcha.toBase64());
	}

}
