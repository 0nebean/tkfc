// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.tkfc.sdk.aws.ses;

import com.tkfc.sdk.aws.ses.config.EmailInfo;
import com.tkfc.sdk.aws.ses.config.SESConfig;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sesv2.model.Body;
import software.amazon.awssdk.services.sesv2.model.Content;
import software.amazon.awssdk.services.sesv2.model.Destination;
import software.amazon.awssdk.services.sesv2.model.EmailContent;
import software.amazon.awssdk.services.sesv2.model.Message;
import software.amazon.awssdk.services.sesv2.model.SendEmailRequest;
import software.amazon.awssdk.services.sesv2.SesV2Client;

/**
 * 操作aws ses
 *
 * @author xumingpeng
 * @since 2024-07-04 10:50:04
 */
public class AwsSESUtil {


    public static void main(String[] args) {
        sendMail(SESConfig.builder()
                .accessKey("填写ak")
                .secretKey("填写sk")
                .region(Region.EU_CENTRAL_1).build()
                , EmailInfo.builder()
                        .sender("service@lodivina.com")
                        .recipient("mpxu88@126.com")
                        .subject("测试发送地址")
                        .bodyHTML("<html>" + "<head></head>" + "<body>" + "<h1>Hello!</h1>"
                                + "</body>" + "</html>")
                        .build());
    }


    /**
     * 初始化s3 client
     *
     * @param config    ses配置
     * @param emailInfo 邮件信息
     */
    public static void sendMail(SESConfig config, EmailInfo emailInfo) {
        send(initSesV2Client(config), emailInfo);
    }


    /**
     * 发送邮件
     */
    private static void send(SesV2Client client,
                             EmailInfo emailInfo) {
        Destination destination = Destination.builder()
                .toAddresses(emailInfo.getRecipient())
                .build();
        Content content = Content.builder()
                .data(emailInfo.getBodyHTML())
                .build();
        Content sub = Content.builder()
                .data(emailInfo.getSubject())
                .build();
        Body body = Body.builder()
                .html(content)
                .build();
        Message msg = Message.builder()
                .subject(sub)
                .body(body)
                .build();
        EmailContent emailContent = EmailContent.builder()
                .simple(msg)
                .build();
        SendEmailRequest emailRequest = SendEmailRequest.builder()
                .destination(destination)
                .content(emailContent)
                .fromEmailAddress(emailInfo.getSender())
                .build();
        client.sendEmail(emailRequest);
    }

    /**
     * 初始化s3 client
     *
     * @param config s3配置
     * @return S3Client
     */
    private static SesV2Client initSesV2Client(SESConfig config) {
        return SesV2Client.builder()
                .credentialsProvider(StaticCredentialsProvider
                        .create(AwsBasicCredentials.
                                create(config.getAccessKey(),
                                        config.getSecretKey())))
                .region(config.getRegion())
                .build();
    }

}