package com.in28minutes.microservices.mlagenteval.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("t_agent_device_reference")
public class AgentDeviceReferenceDo extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private String id;
    private String taskId;
    private String deviceName;

    private String deviceUdid;

    private String deviceOccupyStatus;

    private String deviceRunning;
    private String occupyUser;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime occupyTime;

    private String timeout;

    private String projectId;
    private String tenantId;
    private String createUser;
    private String updateUser;


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("taskId", getTaskId())
                .append("deviceName", getDeviceName())
                .append("deviceUdid", getDeviceUdid())
                .append("deviceOccupyStatus", getDeviceOccupyStatus())
                .append("deviceRunning", getDeviceRunning())
                .append("occupyUser", getOccupyUser())
                .append("occupyTime", getOccupyTime())
                .append("timeout", getTimeout())
                .append("projectId", getProjectId())
                .append("tenantId", getTenantId())
                .append("createUser", getCreateUser())
                .append("createTime", getCreateTime())
                .append("updateUser", getUpdateUser())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}

