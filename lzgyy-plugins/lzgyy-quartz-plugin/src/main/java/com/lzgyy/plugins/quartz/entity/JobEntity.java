package com.lzgyy.plugins.quartz.entity;
import java.io.Serializable;

import lombok.Data;

@Data
public class JobEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/* job主键 */
    private Long id;
    /* job名称 */
    private String name;
    /* job组名 */
    private String group;
    /* 执行的cron */
    private String cron;
    /* job的参数 */
    private String parameter;
    /* job描述信息 */
    private String description;
    /* vm参数 */
    private String vmParam;
    /* job的jar路径 */
    private String jarPath;
    /* job的执行状态,这里我设置为OPEN/CLOSE且只有该值为OPEN才会执行该Job */
    private String status;
}