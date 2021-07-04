package org.springblade.modules.filesync.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.modules.filesync.entity.MachineBaseInfo;
import org.springblade.modules.filesync.mapper.MachineBaseInfoMapper;
import org.springblade.modules.filesync.service.IMachineBaseInfoService;
import org.springblade.modules.filesync.vo.MachineBaseInfoVO;
import org.springblade.modules.system.entity.User;
import org.springblade.modules.system.mapper.UserMapper;
import org.springblade.modules.system.service.IUserService;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotEmpty;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
@AllArgsConstructor
public class MachineBaseInfoServiceImpl implements IMachineBaseInfoService {

	@Override
	public IPage<MachineBaseInfoVO> selectSubsystemPage(IPage<MachineBaseInfoVO> page, MachineBaseInfoVO machineBaseInfoVO) {
		return null;
	}

	@Override
	public boolean deleteLogic(@NotEmpty List<Long> ids) {
		return false;
	}

	@Override
	public boolean changeStatus(@NotEmpty List<Long> ids, Integer status) {
		return false;
	}

	@Override
	public boolean saveBatch(Collection<MachineBaseInfo> entityList, int batchSize) {
		return false;
	}

	@Override
	public boolean saveOrUpdateBatch(Collection<MachineBaseInfo> entityList, int batchSize) {
		return false;
	}

	@Override
	public boolean updateBatchById(Collection<MachineBaseInfo> entityList, int batchSize) {
		return false;
	}

	@Override
	public boolean saveOrUpdate(MachineBaseInfo entity) {
		return false;
	}

	@Override
	public MachineBaseInfo getOne(Wrapper<MachineBaseInfo> queryWrapper, boolean throwEx) {
		return null;
	}

	@Override
	public Map<String, Object> getMap(Wrapper<MachineBaseInfo> queryWrapper) {
		return null;
	}

	@Override
	public <V> V getObj(Wrapper<MachineBaseInfo> queryWrapper, Function<? super Object, V> mapper) {
		return null;
	}

	@Override
	public BaseMapper<MachineBaseInfo> getBaseMapper() {
		return null;
	}
}
