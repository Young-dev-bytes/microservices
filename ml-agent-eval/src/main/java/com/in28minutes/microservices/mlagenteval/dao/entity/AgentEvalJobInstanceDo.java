package com.in28minutes.microservices.mlagenteval.dao.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("t_agent_eval_job_instance")
public class AgentEvalJobInstanceDo {
    private static final long serialVersionUID = 1L;

    /** Primary Key */
    private String id;

    /** Job ID */
    private String jobId;

    /** Job Status: Running, Success, Failed, Stopped */
    private String jobStatus;

    /** Job Instance Task Information: {"AGRO01":"AGRO02"} */
    private String trackInfo;

    /** Current Execution Round */
    private Integer turn;

    /** Project ID */
    private String projectId;

    /** Tenant ID */
    private String tenantId;

    /** Creator */
    private String createUser;

    /** Updater */
    private String updateUser;
}
