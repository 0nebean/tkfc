package com.tkfc.boot.starter.notice.pojo.wechat;

import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import lombok.*;

/**
 * 微信发送返回
 *
 * @author 0neBean
 * @since 2022-07-31 22:24:59
 */
@Body(tag = "微信发送返回")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WechatSendResp {

    @BodyProperty(tag = "错误编码")
    private Integer errcode;

    @BodyProperty(tag = "错误消息")
    private String errmsg;

}
