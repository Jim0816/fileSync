package org.springblade.modules.filesync.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springblade.modules.filesync.entity.NetMachineRelation;

public interface NetMachineRelationMapper extends BaseMapper<NetMachineRelation> {
	@Update("update net_machine_relation set is_delete = 1 where net_id = #{netId}")
	Integer logicalDel(@Param("netId") Long netId);

	@Update("update net_machine_relation set is_delete = 1 where net_id = #{netId} and machine_id = #{machineId}")
	Integer logicalDelByTwo(@Param("netId") Long netId, @Param("machineId") Long machineId);
}
