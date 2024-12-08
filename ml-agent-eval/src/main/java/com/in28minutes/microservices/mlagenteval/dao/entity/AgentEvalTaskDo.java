package com.in28minutes.microservices.mlagenteval.dao.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * UI agent评估任务管理对象 t_agent_eval_task
 *
 * @author ruoyi
 * @date 2024-12-07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_agent_eval_task")
public class AgentEvalTaskDo extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private String id;

    /** 评估任务名称 */
    private String evalTaskName;

    /** 评估任务类型 */
    private String evalTaskType;

    /** 描述 */
    private String description;

    /** 超时时间 */
    private String timeout;

    /** 项目id */
    private String projectId;

    /** 租户id */
    private String tenantId;

    /** 创建人 */
    private String createUser;

    /** 更新人 */
    private String updateUser;


    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("evalTaskName", getEvalTaskName())
                .append("evalTaskType", getEvalTaskType())
                .append("description", getDescription())
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

