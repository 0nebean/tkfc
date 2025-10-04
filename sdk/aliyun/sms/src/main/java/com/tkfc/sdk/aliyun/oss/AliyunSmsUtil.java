package com.tkfc.sdk.aliyun.oss;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendBatchSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendBatchSmsResponse;
import com.aliyun.dysmsapi20170525.models.SendBatchSmsResponseBody;
import com.aliyun.teaopenapi.models.Config;
import com.tkfc.core.constants.StringPool;
import com.tkfc.core.throwable.base.Assert;
import com.tkfc.core.toolkit.JsonUtil;
import com.tkfc.core.toolkit.PropUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 操作sms SDK
 *
 * @author 0neBean
 * @since 2023-04-09 15:48:04
 */
@Slf4j
public class AliyunSmsUtil {

    /**
     * 批量发送短信
     *
     * @param signName       签名
     * @param templateCode   模板code
     * @param param          参数
     * @param toPhoneNumbers 手机号
     * @throws Exception 异常
     */
    public static void sendSms(String signName, String templateCode, JSONObject param, List<String> toPhoneNumbers) throws Exception {
        Client client = initSmsClient();
        SendBatchSmsRequest sendBatchSmsRequest = new SendBatchSmsRequest();
        sendBatchSmsRequest.setTemplateCode(templateCode);
        sendBatchSmsRequest.setPhoneNumberJson(JsonUtil.toJson(toPhoneNumbers));
        sendBatchSmsRequest.setSignNameJson(JsonUtil.toJson(Collections.singletonList(signName)));
        List<JSONObject> params = new ArrayList<>();
        for (int i = 0; i < toPhoneNumbers.size(); i++) {
            params.add(new JSONObject(param));
        }
        sendBatchSmsRequest.setTemplateParamJson(JsonUtil.toJson(params));
        SendBatchSmsResponse response = client.sendBatchSms(sendBatchSmsRequest);
        String code = Optional.ofNullable(response).map(SendBatchSmsResponse::getBody).map(SendBatchSmsResponseBody::getCode).orElse("FAILURE");
        String message = Optional.ofNullable(response).map(SendBatchSmsResponse::getBody).map(SendBatchSmsResponseBody::getMessage).orElse(StringPool.EMPTY);
        Assert.isTrue(Objects.equals(code, "OK"), String.format("send aliyun sms failure , error = %S", message));
    }

    /**
     * 发送短信
     *
     * @param signName      签名
     * @param templateCode  模板code
     * @param param         参数
     * @param toPhoneNumber 手机号
     * @throws Exception 异常
     */
    public static void sendSms(String signName, String templateCode, JSONObject param, String toPhoneNumber) throws Exception {
        sendSms(signName, templateCode, param, Collections.singletonList(toPhoneNumber));
    }


    /**
     * 初始化 oss 链接
     *
     * @return oss 操作实例
     */
    private static Client initSmsClient() throws Exception {
        String accessId = PropUtil.getInstance().getConfig("aliyun.sdk.access.id", PropUtil.DEFAULT_NAME_SPACE);
        String accessKey = PropUtil.getInstance().getConfig("aliyun.sdk.access.key", PropUtil.DEFAULT_NAME_SPACE);
        Config config = new Config().setAccessKeyId(accessId).setAccessKeySecret(accessKey);
        return new Client(config);
    }

}
