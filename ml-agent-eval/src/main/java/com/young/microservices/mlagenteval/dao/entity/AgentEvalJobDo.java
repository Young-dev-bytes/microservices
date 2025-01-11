package com.young.microservices.mlagenteval.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_agent_eval_job")
public class AgentEvalJobDo extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private String id;

    private String taskId;

    private String jobName;

    private String datasetId;

    private Integer executeTurn;

    private String modelName;

    private String inferUrl;

    private String dag;

    private String promptTemplate;

    private Integer isHistoryActions;

    private Integer isStepEval;

    private Integer isJobEval;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("taskId", getTaskId())
                .append("jobName", getJobName())
                .append("datasetId", getDatasetId())
                .append("executeTurn", getExecuteTurn())
                .append("modelName", getModelName())
                .append("inferUrl", getInferUrl())
                .append("dag", getDag())
                .append("promptTemplate", getPromptTemplate())
                .append("isHistoryActions", getIsHistoryActions())
                .append("isStepEval", getIsStepEval())
                .append("isJobEval", getIsJobEval())
                .append("projectId", getProjectId())
                .append("tenantId", getTenantId())
                .append("createUser", getCreateUser())
                .append("createTime", getCreateTime())
                .append("updateUser", getUpdateUser())
                .append("updateTime", getUpdateTime()).toString();
    }
}
