package org.springblade.modules.filesync.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springblade.modules.filesync.entity.NetBaseInfo;

public interface NetBaseInfoMapper extends BaseMapper<NetBaseInfo> {
	@Update("update net_baseinfo set is_delete = 1 where id = #{netId} and create_machine_id = #{machineId}")
	Integer logicalDel(@Param("netId") Long netId, @Param("machineId") Long machineId);

	@Update("update net_baseinfo as tb set tb.connect_nums = tb.connect_nums + #{num} where id = #{netId}")
	Integer updateConnNums(@Param("netId") Long netId, @Param("num")Integer num);

	@Update("update net_baseinfo set work_state = #{state} where id = #{netId}")
	Integer updateState(@Param("netId") Long netId, @Param("state") Integer state);

}
