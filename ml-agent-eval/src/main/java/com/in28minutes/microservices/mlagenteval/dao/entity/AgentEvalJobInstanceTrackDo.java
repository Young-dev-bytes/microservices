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
@TableName("t_agent_eval_job_instance_track")
public class AgentEvalJobInstanceTrackDo {
    private static final long serialVersionUID = 1L;

    /** Primary Key */
    private String id;

    /** Job Instance ID */
    private String jobInstanceId;

    /** Instruction Task Identifier, e.g., 1.2.3 */
    private String instructionId;

    /** Track Information: {"scene":"Data Center Inspection","app_version":"1.0","instruction":{"step":1,"action":"move","value":"forward"},"timestamp":"2023-01-01 12:00:00"} */
    private String trackInfo;

    /** Track Detail: {"step":1,"thought":"xxx","observation":"xxx","action":"xxx","image_path":"xxx"} */
    private String trackDetail;

    /** Project ID */
    private String projectId;

    /** Tenant ID */
    private String tenantId;

    /** Creator */
    private String createUser;

    /** Updater */
    private String updateUser;
}
