package org.springblade.modules.filesync.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springblade.modules.filesync.entity.MachineBaseInfo;
import org.springblade.modules.filesync.vo.MachineBaseInfoVO;


import java.util.List;

public interface MachineBaseInfoMapper extends BaseMapper<MachineBaseInfo> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param machineBaseInfoVO
	 * @return
	 */
	List<MachineBaseInfoVO> selectMachineBaseInfoPage(IPage page, MachineBaseInfoVO machineBaseInfoVO);

	@Select("select count(*) from machine_baseinfo where mac_addr=#{macAddr} and ip=#{openIp}")
	Integer selectMachineBaseInfoCount(@Param("macAddr") String macAddr, @Param("openIp") String openIp);

	@Select("select id from machine_baseinfo where mac_addr=#{macAddr} and ip=#{openIp}")
	String selectMachineBaseInfoId(@Param("macAddr") String macAddr, @Param("openIp") String openIp);

}
