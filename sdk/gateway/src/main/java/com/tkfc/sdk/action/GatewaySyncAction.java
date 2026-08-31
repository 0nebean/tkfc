package com.tkfc.sdk.action;

import com.tkfc.core.common.annotations.web.action.Action;
import com.tkfc.core.common.annotations.web.method.json.PostJson;
import com.tkfc.core.common.annotations.web.param.BodyParam;
import com.tkfc.core.common.annotations.web.report.IgnoreReportDoc;
import com.tkfc.core.common.pojo.BaseResponse;
import com.tkfc.sdk.biz.GatewaySyncAccessInfoBiz;
import com.tkfc.sdk.pojo.actor.SyncGatewayInfoMsg;
import com.tkfc.sdk.pojo.dto.*;
import com.tkfc.sdk.pojo.vo.*;
import com.tkfc.sdk.service.GatewayCallTimesService;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * 网关访问信息同步
 *
 * @author 0neBean
 * @since 2023-03-23 21:51:24
 */
@RequiredArgsConstructor
@Action(tag = "网关访问信息同步", path = "gateway/sync")
public class GatewaySyncAction {

    private final GatewaySyncAccessInfoBiz  syncAccessInfoBiz;
    private final GatewayCallTimesService callTimesService;

    @IgnoreReportDoc
    @PostJson(authors = {"0neBean"}, tag = "同步站点域名", path = {"siteSyncDomain"})
    public Boolean siteSyncDomain(@BodyParam(tag = "请求参数") SyncGatewayInfoMsg req) {
        return syncAccessInfoBiz.siteSync(req);
    }

    @IgnoreReportDoc
    @PostJson(authors = {"0neBean"}, tag = "同步访问凭证", path = {"syncTicketInfo"})
    public Boolean syncTicketInfo(@BodyParam(tag = "请求参数") SyncGatewayInfoMsg req) {
        return syncAccessInfoBiz.syncTicketInfo(req);
    }

    @IgnoreReportDoc
    @PostJson(authors = {"0neBean"}, tag = "删除站点域名", path = {"siteDelDomain"})
    public Boolean siteDelDomain(@BodyParam(tag = "请求参数") SyncGatewayInfoMsg req) {
        return syncAccessInfoBiz.siteDel(req);
    }

    @IgnoreReportDoc
    @PostJson(authors = {"0neBean"}, tag = "批量查询凭证类型调用统计", path = {"batchFindTicketCallTimes"})
    public BaseResponse<List<GatewayCallTimesVo>> batchFindTicketCallTimes(@BodyParam(tag = "请求参数") BatchQueryCallTimesReqDto req) {
        return BaseResponse.ok(callTimesService.batchFindTicketCallTimes(req));
    }

    @IgnoreReportDoc
    @PostJson(authors = {"0neBean"}, tag = "批量查询接口类型调用统计", path = {"batchFindApiCallTimes"})
    public BaseResponse<List<GatewayCallTimesVo>> batchFindApiCallTimes(@BodyParam(tag = "请求参数") BatchQueryCallTimesReqDto req) {
        return BaseResponse.ok(callTimesService.batchFindApiCallTimes(req));
    }

}
