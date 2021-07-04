package org.springblade.modules.filesync.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@TableName("net_file")
public class NetFile {
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

	private String fileName;
	@JsonSerialize(
		using = ToStringSerializer.class
	)
	private Long parentId;

	private Integer fileType;

	private Integer fileSize;
	@DateTimeFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@JsonFormat(
		pattern = "yyyy-MM-dd HH:mm:ss"
	)
	private Integer fileUpdateTime;
}
