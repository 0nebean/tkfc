package com.tkfc.sdk.service;

import com.tkfc.boot.starter.mybatis.extend.BaseSplitService;
import com.tkfc.sdk.model.GatewayUser;
import com.tkfc.sdk.pojo.vo.GatewayUserVo;


/**
 * 网关用户 service
 *
 * @author 0neBean
 * @since 2024-06-03 18:11:07
 */
public interface GatewayUserService extends BaseSplitService<GatewayUser, GatewayUserVo> {

    /**
     * 查询用户
     *
     * @param username 用户登录名
     * @param ticketId 凭证id
     * @return 用户
     */
    GatewayUserVo findUserByUsername(String username, String ticketId);

    /**
     * 创建用户
     *
     * @param username 用户登录名
     * @param ticketId 凭证id
     * @return 用户
     */
    GatewayUserVo createUser(String username, String ticketId);

    /**
     * 根据手机号查询用户
     *
     * @param mobile 手机号
     * @return 用户信息
     */
    GatewayUserVo findUserByMobile(String mobile, String ticketId);

    /**
     * 匹配密码
     *
     * @param rawPassword 加密密码
     * @param password    登录参数密码
     * @return bool
     */
    Boolean matchPassword(String rawPassword, String password);

    /**
     * 添加网关用户
     *
     * @param ticketId   凭证ID
     * @param userOpenId 用户OPEN ID
     * @param username   用户名
     * @param password   用户密码
     */
    void addGatewayUser(String ticketId, Long userOpenId, String username, String password);

    /**
     * 重置用户密码
     *
     * @param ticketId   凭证ID
     * @param userOpenId 用户OPEN ID
     * @param password   用户密码
     */
    void resetPassword(String ticketId, String userOpenId, String password);

    /**
     * 更新用户信息
     *
     * @param ticketId     凭证ID
     * @param userOpenId   用户开放ID
     * @param isActive     是否启用
     * @param email        邮箱
     * @param mobileNumber 手机号
     * @param realName     真实姓名
     */
    void updateBizUserInfo(String ticketId, String userOpenId, Boolean isActive, String email, String mobileNumber, String realName);
}
