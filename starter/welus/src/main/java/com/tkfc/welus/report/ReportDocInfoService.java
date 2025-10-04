package com.tkfc.welus.report;

import com.tkfc.core.common.pojo.BaseResponse;
import com.tkfc.core.constants.StringPool;
import com.tkfc.core.throwable.base.Assert;
import com.tkfc.core.toolkit.CollectionUtil;
import com.tkfc.core.toolkit.PropUtil;
import com.tkfc.core.toolkit.RestUtil;
import com.tkfc.core.toolkit.StringUtil;
import com.tkfc.welus.report.dto.doc.GatewayDocDto;
import com.tkfc.welus.report.dto.doc.GatewayDocParamDto;
import com.tkfc.welus.report.dto.doc.GatewayDocParamType;
import com.tkfc.welus.report.dto.doc.GatewayDocReportDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 上报文档服务
 *
 * @author 0neBean
 * @since 2022-06-21 14:31:57
 */
@Slf4j
public class ReportDocInfoService {

    @SuppressWarnings("all")
    private final static String ACTION_RESOURCE_TYPE = "0";
    private final static String REPORT_API_URL_default_PREFIX = "http://";
    private final static String REPORT_API_URL_HTTPS_PREFIX = "https://";
    private static final String DOC_REPORT_API_BASE = "/gatewayDoc/report";
    private final static String WELUS_REPORT_DOC_SERVER_HOST = "welus.report.doc.server.host";

    /**
     * 上报文档
     *
     * @param documents 文档信息
     * @param types     接口参数类型
     * @return bool 上报结果
     */
    public static Boolean uploadRecordResult(List<GatewayDocDto> documents, Map<String, GatewayDocParamType> types) {
        String requestUrl = handleReportUrl();
        GatewayDocReportDto request = buildReportRequestParam(documents, types);
        BaseResponse<Boolean> response = RestUtil.getInstance().doPostForRef(requestUrl, request, new ParameterizedTypeReference<>() {
        });
        return response.getData();
    }

    private static String handleReportUrl() {
        String configUrl = PropUtil.getInstance().getConfig(WELUS_REPORT_DOC_SERVER_HOST);
        Assert.notBlank(configUrl, "report api url config can not be empty");
        if (!configUrl.startsWith("http")) {
            if (StringUtil.isIpAddress(configUrl)) {
                configUrl = REPORT_API_URL_default_PREFIX + configUrl;
            } else {
                configUrl = REPORT_API_URL_HTTPS_PREFIX + configUrl;
            }
        }
        return configUrl + DOC_REPORT_API_BASE;
    }

    /**
     * 构建上报参数
     *
     * @param documents 文档信息
     * @param types     参数类型
     * @return 上报参数
     */
    private static GatewayDocReportDto buildReportRequestParam(List<GatewayDocDto> documents, Map<String, GatewayDocParamType> types) {
        String appKey = documents.stream().findFirst().map(GatewayDocDto::getAppKey).orElse(StringPool.EMPTY);
        documents = distinctResources(documents);
        List<GatewayDocParamDto> params = new ArrayList<>();
        List<GatewayDocParamDto> childParams = new ArrayList<>();
        for (GatewayDocDto document : documents) {
            List<GatewayDocParamDto> eachParam = document.getParams();
            if (Objects.nonNull(document.getReturnResult())) {
                params.add(document.getReturnResult());
            }
            if (CollectionUtil.isNotEmpty(eachParam)) {
                params.addAll(eachParam.stream().filter(Objects::nonNull).collect(Collectors.toList()));
            }
        }
        for (GatewayDocParamDto param : params) {
            List<GatewayDocParamDto> childField = param.getBodyFieldList();
            expandAllChildParamField(childParams, childField);
        }
        if (CollectionUtil.isNotEmpty(childParams)) {
            params.addAll(childParams);
        }
        for (int i = 0; i < params.size(); i++) {
            params.get(i).setSort(i);
        }
        List<GatewayDocParamDto> paramsResult = new ArrayList<>();
        Map<String, GatewayDocParamDto> defKeyMapField = new HashMap<>();
        params.forEach(p -> defKeyMapField.put(p.getFieldDefKey(), p));
        defKeyMapField.forEach((k, v) -> paramsResult.add(v));
        return GatewayDocReportDto.builder().appKey(appKey).resources(documents).params(paramsResult).types(types).build();
    }

    /**
     * 资源去重
     *
     * @param documents 文档信息
     * @return 文档信息
     */
    private static List<GatewayDocDto> distinctResources(List<GatewayDocDto> documents) {
        List<GatewayDocDto> result = new ArrayList<>();
        Set<String> resourceDefKeys = new HashSet<>();
        for (GatewayDocDto document : documents) {
            String resourceDefKey = document.getResourceDefKey();
            boolean isActionInfo = Objects.equals(document.getResourceType(), ACTION_RESOURCE_TYPE);
            boolean repeatAction = resourceDefKeys.contains(resourceDefKey);
            //如果是action
            if (isActionInfo && repeatAction) {
                continue;
            }
            result.add(document);
            resourceDefKeys.add(resourceDefKey);
        }
        return result;
    }

    /**
     * 递归拆解所哟参数对象
     *
     * @param params      参数列表
     * @param childFields 子参数
     */
    private static void expandAllChildParamField(List<GatewayDocParamDto> params, List<GatewayDocParamDto> childFields) {
        if (CollectionUtil.isEmpty(childFields)) {
            return;
        }
        params.addAll(childFields);
        for (GatewayDocParamDto param : childFields) {
            List<GatewayDocParamDto> childField = param.getBodyFieldList();
            if (CollectionUtil.isEmpty(childField)) {
                continue;
            }
            expandAllChildParamField(params, childField.stream().filter(Objects::nonNull).collect(Collectors.toList()));
        }
    }

}
