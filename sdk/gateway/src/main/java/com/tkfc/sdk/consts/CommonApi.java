package com.tkfc.sdk.consts;

import com.alibaba.fastjson2.JSONArray;
import com.tkfc.cache.base.interfaces.ICacheService;
import com.tkfc.cache.base.interfaces.ILock;
import com.tkfc.core.constants.StringPool;
import com.tkfc.core.function.SerializableCiConsumer;
import com.tkfc.core.throwable.base.Assert;
import com.tkfc.core.toolkit.*;

import java.util.Objects;

/**
 * The CommonApi class provides a method to fetch the current application's report in JSON format.
 */
public class CommonApi {

    /**
     * Retrieves the current application's report in JSON format and passes it to the provided callback.
     *
     * @param callBack A callback that accepts a JSONArray, used to process the retrieved report data.
     */
    public static void getCurrentAppReportJson(SerializableCiConsumer<JSONArray, String, String> callBack) {
        String version = EnvUtil.getEnvProps("version");
        String appKey = EnvUtil.getEnvProps("appKey");
        String fileName = String.format("%s_%s", appKey, version.replace(StringPool.DOT, StringPool.UNDERSCORE));
        String remotePath = "devops/appInst";
        String cdnType = PropUtil.getInstance().getConfig("cdn.type");
        String cdnCommonPath;
        String cdnAccessHost;
        if (Objects.equals(cdnType, "s3")) {
            cdnCommonPath = PropUtil.getInstance().getConfig("cdn.s3.common.path");
            cdnAccessHost = PropUtil.getInstance().getConfig("cdn.s3.access.host");
        } else if (Objects.equals(cdnType, "cos")) {
            cdnCommonPath = PropUtil.getInstance().getConfig("cdn.cos.common.path");
            cdnAccessHost = PropUtil.getInstance().getConfig("cdn.cos.access.host");
        } else {
            cdnCommonPath = PropUtil.getInstance().getConfig("cdn.oss.common.path");
            cdnAccessHost = PropUtil.getInstance().getConfig("cdn.oss.access.host");
        }
        String accessUrl = buildReportAccessUrl(cdnAccessHost, cdnCommonPath, remotePath, fileName);
        ICacheService cacheService = SpringUtil.getBean(ICacheService.class);
        String lockKey = String.format("devops:reportHealthInfo:lock:%s", fileName);
        ILock lock = cacheService.getLock(lockKey);
        lock.lock();
        try {
            JSONArray reportHealthJsonArr = JsonUtil.jsonStringToJsonArray(new String(IoUtil.getFileFromUrl(accessUrl)));
            Assert.notNull(reportHealthJsonArr, "un know app version");
            callBack.accept(reportHealthJsonArr, fileName, remotePath);
        } catch (Exception e) {
            throw new RuntimeException(e);

        } finally {
            lock.unLock();
        }
    }

    /**
     * 拼出与 devops 上传一致的健康 JSON 访问地址。
     * 例：https://host/public/devops/appInst/{appKey}_{version}.json
     */
    private static String buildReportAccessUrl(String host, String commonPath, String remotePath, String fileName) {
        String h = host == null ? StringPool.EMPTY : host.replaceAll("/+$", "");
        String c = commonPath == null ? StringPool.EMPTY : commonPath;
        if (StringUtil.isNotBlank(c) && !c.endsWith(StringPool.SLASH)) {
            c = c + StringPool.SLASH;
        }
        while (c.startsWith(StringPool.SLASH)) {
            c = c.substring(1);
        }
        String r = remotePath == null ? StringPool.EMPTY : remotePath.replaceAll("^/+", "").replaceAll("/+$", "");
        return String.format("%s/%s%s/%s.json", h, c, r, fileName);
    }
}
