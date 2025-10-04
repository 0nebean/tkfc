package com.tkfc.boot.starter.notice.sender;

import com.alibaba.fastjson2.JSONObject;
import com.tkfc.boot.starter.notice.pojo.wechat.WechatSendResp;
import com.tkfc.core.toolkit.CollectionUtil;
import com.tkfc.core.toolkit.RestUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class WeChatRobotSender {

    private final static String CALL_URL = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=%s";
    private final static JSONObject HEADER = new JSONObject() {{
        put("Content-Type", "application/json");
    }};

    public static WechatSendResp sendMarkdown(String robotId, String markdown) {
        String url = String.format(CALL_URL, robotId);
        JSONObject msg = new JSONObject();
        msg.put("content", markdown);
        JSONObject param = new JSONObject();
        param.put("msgtype", "markdown");
        param.put("markdown", msg);
        return RestUtil.getInstance().doPostForObj(url, param, HEADER, WechatSendResp.class);
    }

    public static WechatSendResp sendText(String robotId, String content, List<String> atUsername, List<String> atPhoneNumber, Boolean atAll) {
        List<String> atAllList;
        String url = String.format(CALL_URL, robotId);
        JSONObject msg = new JSONObject();
        if (atAll) {
            atAllList = new ArrayList<>() {{
                add("@all");
            }};
            msg.put("mentioned_list", atAllList);
        } else {
            atUsername = CollectionUtil.isEmpty(atUsername) ? new ArrayList<>() : atUsername;
            atPhoneNumber = CollectionUtil.isEmpty(atPhoneNumber) ? new ArrayList<>() : atPhoneNumber;
            msg.put("mentioned_list", atUsername);
            msg.put("mentioned_mobile_list", atPhoneNumber);
        }
        msg.put("content", content);
        JSONObject param = new JSONObject();
        param.put("msgtype", "text");
        param.put("text", msg);
        return RestUtil.getInstance().doPostForObj(url, param, HEADER, WechatSendResp.class);
    }

    /**
     * 发送图片和文本
     * @param robotId 机器人ID
     * @param title 比哦啊题
     * @param description 注释
     * @param jumpUrl 跳转链接
     * @param picUrl 图片链接
     * @return 发送标识
     */
    public static WechatSendResp sendPicAndText(String robotId, String title, String description, String jumpUrl, String picUrl) {
        try {
            jumpUrl = Objects.isNull(jumpUrl) ? "https://qq.com" : jumpUrl;
            String url = String.format(CALL_URL, robotId);
            JSONObject param = new JSONObject();
            JSONObject news = new JSONObject();
            List<JSONObject> articles = new ArrayList<>();
            JSONObject msg = new JSONObject();
            msg.put("title", title);
            msg.put("description", description);
            msg.put("url", jumpUrl);
            msg.put("picurl", picUrl);
            articles.add(msg);
            news.put("articles", articles);
            param.put("msgtype", "news");
            param.put("news", news);
            return RestUtil.getInstance().doPostForObj(url, param, HEADER, WechatSendResp.class);
        } catch (Exception e) {
            log.error("sendPicAndText got an error  = ", e);
            return WechatSendResp.builder().errcode(999).errmsg(e.getMessage()).build();
        }
    }
}