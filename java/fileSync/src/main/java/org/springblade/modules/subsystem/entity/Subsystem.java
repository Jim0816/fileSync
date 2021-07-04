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
package org.springblade.modules.subsystem.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import org.springblade.core.mp.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 实体类
 *
 * @author BladeX
 * @since 2020-03-04
 */
@Data
@TableName("tb_subsystem")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Subsystem对象", description = "Subsystem对象")
public class Subsystem extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	* 子系统名称
	*/
		@ApiModelProperty(value = "子系统名称")
		private String name;
	/**
	 * 子系统地址
	 */
		@ApiModelProperty(value = "子系统地址")
		private String url;
	/**
	* 私钥
	*/
		@ApiModelProperty(value = "私钥")
		private String privateKey;
	/**
	* 公钥
	*/
		@ApiModelProperty(value = "公钥")
		private String publicKey;

	/**
	 * 网络类型
	 */
		@ApiModelProperty(value = "网络类型")
		private String netType;

}
