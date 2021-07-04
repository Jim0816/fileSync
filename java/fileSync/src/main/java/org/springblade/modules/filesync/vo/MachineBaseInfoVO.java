package org.springblade.modules.filesync.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.filesync.entity.MachineBaseInfo;

@Data
@EqualsAndHashCode(callSuper = true)
public class MachineBaseInfoVO extends MachineBaseInfo {
	private static final long serialVersionUID = 1L;
}
