package org.springblade.modules.filesync.controller;

import lombok.AllArgsConstructor;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.tool.api.R;
import org.springblade.modules.filesync.entity.MachineBaseInfo;
import org.springblade.modules.filesync.entity.NetBaseInfo;
import org.springblade.modules.filesync.entity.NetMachineRelation;
import org.springblade.modules.filesync.mapper.MachineBaseInfoMapper;
import org.springblade.modules.filesync.mapper.NetBaseInfoMapper;
import org.springblade.modules.filesync.mapper.NetMachineRelationMapper;
import org.springblade.modules.filesync.service.IMachineBaseInfoService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@AllArgsConstructor
@RequestMapping("/file-sync")
public class FileSyncController {

	private MachineBaseInfoMapper machineBaseInfoMapper;
	private NetBaseInfoMapper netBaseInfoMapper;
	private NetMachineRelationMapper netMachineRelationMapper;
	/**
	 * 给客户机分配唯一账号 (服务器替客户端去申请)
	 */
	@GetMapping("/apply")
	public R applyId(String macAddr, String openIp){
		Integer num = machineBaseInfoMapper.selectMachineBaseInfoCount(macAddr, openIp);
		if(num == 0){
			//当前系统不存在本机器信息
			MachineBaseInfo machineInfo = new MachineBaseInfo();
			machineInfo.setMacAddr(macAddr);
			machineInfo.setIp(openIp);
			machineBaseInfoMapper.insert(machineInfo);
		}
		String machineId = machineBaseInfoMapper.selectMachineBaseInfoId(macAddr, openIp);
		return R.data(machineId);
	}

	/**
	 * 创建同步网络
	 * 参数：
	 * 返回:
	 */
	@GetMapping("/create")
	@Transactional
	public R createSyncNetWork(String machineId, String joinPwd){
		Map<String,Object> result = new HashMap<>();
		NetBaseInfo oldNetBaseInfo = netBaseInfoMapper.selectOne(Condition.getQueryWrapper(new NetBaseInfo()).eq("create_machine_id", Long.valueOf(machineId)).eq("is_delete", 0));
		if(oldNetBaseInfo != null){
			//当前主机创建的网络存在
			result.put("status", 501); // 501表示网络已经存在，不需要再建立
			result.put("desc", "网络已经存在");
		}else{
			try{
				//1.创建新网络
				NetBaseInfo netBaseInfo = new NetBaseInfo();
				netBaseInfo.setCreateMachineId(Long.valueOf(machineId));
				netBaseInfo.setPassword(joinPwd);
				netBaseInfoMapper.insert(netBaseInfo);
				oldNetBaseInfo = netBaseInfoMapper.selectOne(Condition.getQueryWrapper(new NetBaseInfo()).eq("create_machine_id", Long.valueOf(machineId)).eq("is_delete", 0));
				//2.默认将创建者加入网络中
				NetMachineRelation netMachineRelation = new NetMachineRelation();
				netMachineRelation.setNetId(oldNetBaseInfo.getId());
				netMachineRelation.setMachineId(Long.valueOf(machineId));
				netMachineRelationMapper.insert(netMachineRelation);
				result.put("status", 500); // 500表示网络创建成功
				result.put("desc", "网络创建成功");
			}catch (Exception e){
				result.put("status", 502);// 502表示网络创建失败
				result.put("desc", "网络创建失败");
			}
		}
		result.put("network_info", oldNetBaseInfo);
		return R.data(result);
	}

	/**
	 * 查询网络使用情况(没有被逻辑删除的网络)
	 */
	@GetMapping("/net_info_1")
	public R getNetInfoByNetId(String netId){
		return R.data(netBaseInfoMapper.selectOne(Condition.getQueryWrapper(new NetBaseInfo()).eq("id",netId).eq("is_delete", 0)));
	}

	/**
	 * 查询本机创建的网络(没有被逻辑删除的网络)
	 */
	@GetMapping("/net_info_2")
	public R getNetInfoByMachineId(String machineId){
		return R.data(netBaseInfoMapper.selectOne(Condition.getQueryWrapper(new NetBaseInfo()).eq("create_machine_id",machineId).eq("is_delete", 0)));
	}

	/**
	 * 查询所有网络信息
	 */
	@GetMapping("/net_list")
	public R getAllNetInfo(){
		List<Map> result = new ArrayList();
		List<NetBaseInfo> netBaseInfoList = netBaseInfoMapper.selectList(Condition.getQueryWrapper(new NetBaseInfo()).eq("is_delete", 0));
		for (NetBaseInfo netBaseInfo : netBaseInfoList) {
			Map<String, Object> map = new HashMap<>();
			map.put("basic_info", netBaseInfo);
			List<NetMachineRelation> members = netMachineRelationMapper.selectList(Condition.getQueryWrapper(new NetMachineRelation()).eq("net_id", netBaseInfo.getId()).eq("is_delete", 0));
			map.put("members", members);
			result.add(map);
		}
		return R.data(result);
	}

	/**
	 * 销毁指定网络
	 */
	@GetMapping("/destroy")
	@Transactional
	public R delNetWork(String netId, String machineId){
		try{
			//1.删除指定网络
			netBaseInfoMapper.logicalDel(Long.valueOf(netId), Long.valueOf(machineId));
			//2.删除加入该网络的所有主机
			netMachineRelationMapper.logicalDel(Long.valueOf(netId));
		}catch (Exception e){
			return R.fail("网络销毁失败");
		}
		return R.success("网络销毁成功");
	}

	/**
	 * 加入指定网络
	 */
	@GetMapping("/join")
	@Transactional
	public R joinNetWork(String netId, String machineId, String joinPwd){
		try{
			NetBaseInfo join_net = netBaseInfoMapper.selectOne(Condition.getQueryWrapper(new NetBaseInfo()).eq("id",netId).eq("password", joinPwd));
			if(join_net == null){
				return R.fail("当前网络号或者密码错误");
			}
			//1.主机加入指定网络
			NetMachineRelation netMachineRelation = new NetMachineRelation();
			netMachineRelation.setNetId(Long.valueOf(netId));
			netMachineRelation.setMachineId(Long.valueOf(machineId));
			netMachineRelationMapper.insert(netMachineRelation);
			//2.修改该网络当前加入数量
			netBaseInfoMapper.updateConnNums(Long.valueOf(netId), 1);
		}catch (Exception e){
			return R.fail("加入失败");
		}
		return R.success("加入成功");
	}

	/**
	 * 退出指定网络
	 */
	@GetMapping("/exit")
	@Transactional
	public R exitNetWork(String netId, String machineId){
		try{
			netMachineRelationMapper.logicalDelByTwo(Long.valueOf(netId), Long.valueOf(machineId));
			netBaseInfoMapper.updateConnNums(Long.valueOf(netId), -1);
		}catch (Exception e){
			return R.fail("退出失败");
		}
		return R.success("退出成功");
	}

	/**
	 * 修改网络工作状态
	 */
	@GetMapping("/set")
	public synchronized R setNetWork(String netId, String state){
		try{
			netBaseInfoMapper.updateState(Long.valueOf(netId), Integer.valueOf(state));
		}catch (Exception e){
			return R.fail("修改失败");
		}
		return R.success("修改成功");
	}
}
