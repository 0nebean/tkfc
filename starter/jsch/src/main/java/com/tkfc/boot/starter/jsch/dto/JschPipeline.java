package com.tkfc.boot.starter.jsch.dto;

import com.tkfc.core.common.annotations.orm.FiledName;
import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import lombok.*;

/**
 * jsch pipeline 对象
 *
 * @author 0neBean
 * @since 2022-08-09 11:13:14
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Body(tag = "jsch pipeline 对象")
public class JschPipeline {


    @BodyProperty(tag = "pipeline 类型 0: exec 1: scp")
    private String pipelineType;

    @BodyProperty(tag = "pipeline 名称")
    private String pipelineName;

    @BodyProperty(tag = "pipeline 命令")
    private String pipelineCmd;

    @BodyProperty(tag = "pipeline 执行状态")
    private Integer pipelineExecStatus;

    @BodyProperty(tag = "scp 源文件路径")
    private String scpSourcePath;

    @BodyProperty(tag = "scp 目标文件路径")
    private String scpTargetPath;

    @BodyProperty(tag = "模板唯一标识")
    private String templateKey;

    @BodyProperty(tag = "模板内容")
    private String templateContent;

    @BodyProperty(tag = "模板ID")
    private Long templateId;

    @BodyProperty(tag = "项目中资源路径")
    private String sourceClassPath;

    @BodyProperty(tag = "静态资源目录")
    private String staticResourcePath;

    @BodyProperty(tag = "目标路径")
    private String cdnTargetPath;

    @BodyProperty(tag = "oss bucket名称")
    private String ossBucket;

    @BodyProperty(tag = "生成文件名称")
    private String geneFileName;

    @BodyProperty(tag = "排序")
    private String sort;

    @BodyProperty(tag = "执行日志")
    private String execLog;

    @BodyProperty(tag = "是否压缩静态资源")
    private String zipStatic;
}
