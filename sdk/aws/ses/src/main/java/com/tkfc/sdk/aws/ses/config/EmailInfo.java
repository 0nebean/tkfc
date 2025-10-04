package com.tkfc.sdk.aws.ses.config;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailInfo {

    /**
     * 发件人邮箱
     */
    private String sender;

    /**
     * 收件人邮箱
     */
    private String recipient;

    /**
     * 主题名称
     */
    private String subject;

    /**
     * Html文本内容
     */
    private String bodyHTML;

}

