package org.springblade.modules.filesync.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@TableName("net_machine_relation")
public class NetMachineRelation {
	@JsonSerialize(
		using = ToStringSerializer.class
	)
	@ApiModelProperty("主键id")
	@TableId(
		value = "id",
		type = IdType.ID_WORKER
	)
	private Long id;

	@JsonSerialize(
		using = ToStringSerializer.class
	)
	private Long netId;

	@JsonSerialize(
		using = ToStringSerializer.class
	)
	private Long machineId;

	private Integer isDelete;
}
