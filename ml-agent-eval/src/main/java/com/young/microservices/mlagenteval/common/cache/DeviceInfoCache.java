package com.young.microservices.mlagenteval.common.cache;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.young.microservices.mlagenteval.dao.entity.AgentDeviceReferenceDo;
import com.young.microservices.mlagenteval.dao.mapper.AgentDeviceReferenceMapper;
import com.young.microservices.mlagenteval.dto.DeviceInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class DeviceInfoCache extends GuavaCache<String, DeviceInfo>{

    private static final long DEFAULT_MAXIMUM_SIZE = 100L;

    private static final long DEFAULT_EXPIRE = 60 * 60 * 4L;

    private final AgentDeviceReferenceMapper agentDeviceReferenceMapper;

    public DeviceInfoCache(AgentDeviceReferenceMapper agentDeviceReferenceMapper) {
        super(DEFAULT_MAXIMUM_SIZE, DEFAULT_EXPIRE, GuavaRefreshEnum.EXPIRE_AFTER_WRITE);
        this.agentDeviceReferenceMapper = agentDeviceReferenceMapper;
    }

    @Override
    protected DeviceInfo fetchData(String deviceUdId) {
        DeviceInfo deviceInfo = new DeviceInfo();
        LambdaQueryWrapper<AgentDeviceReferenceDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentDeviceReferenceDo::getDeviceUdid, deviceUdId);
        AgentDeviceReferenceDo agentDeviceReferenceDo = agentDeviceReferenceMapper.selectList(queryWrapper).get(0);
        BeanUtils.copyProperties(agentDeviceReferenceDo, deviceInfo);
        deviceInfo.setDeviceFullName(deviceInfo.getDeviceName().concat("(").concat(deviceUdId).concat(")"));
        return deviceInfo;
    }
}
