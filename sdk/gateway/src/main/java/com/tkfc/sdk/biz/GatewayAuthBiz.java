package com.tkfc.sdk.biz;

import com.alibaba.fastjson2.JSONObject;
import com.tkfc.boot.starter.mybatis.extend.BaseVo;
import com.tkfc.cache.base.interfaces.ICacheService;
import com.tkfc.cache.base.interfaces.ILock;
import com.tkfc.core.enums.OsTypeEnum;
import com.tkfc.core.enums.YesOrNoEnum;
import com.tkfc.core.throwable.base.Assert;
import com.tkfc.core.toolkit.*;
import com.tkfc.sdk.enums.GatewayAuthType;
import com.tkfc.sdk.enums.GatewayTicketLoginVerifyType;
import com.tkfc.sdk.model.GatewayTenant;
import com.tkfc.sdk.model.GatewayTicket;
import com.tkfc.sdk.model.GatewayUser;
import com.tkfc.sdk.pojo.dto.*;
import com.tkfc.sdk.pojo.vo.*;
import com.tkfc.sdk.service.GatewayTenantService;
import com.tkfc.sdk.service.GatewayUserService;
import com.tkfc.sdk.utils.GatewayAuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 网关鉴权领域服务
 *
 * @author 0neBean
 * @since 2023-03-23 23:28:48
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GatewayAuthBiz {


    private final static int SAFE_TIME_LIMIT = 1;
    private final static Integer SEND_SMS_IP_LIMIT = 20;
    private final static Integer SEND_SMS_MOBILE_LIMIT = 5;
    private final static long SMS_CODE_TIME_OUT = 1000 * 60 * 2;
    private final static long ACCESS_TOKEN_EXPIRE = 1000 * 60 * 60 * 2;
    private final static String ALIYUN_SMS_SIGNATURE_AD = "杭州覆筐网络科技有限公司";
    private final static String ALIYUN_SMS_TEMPLATE_CODE = "SMS_278090292";
    private final static String SEND_SMS_CHECK_IP_KEY = "gateway:send-sms-check:ip:%s";
    private final static String SEND_SMS_CHECK_MOBILE_KEY = "gateway:send-sms-check:mobile:%s";
    private final static String SEND_SMS_CHECK_IP_LOCK_KEY = "gateway:send-sms-check:lock-ip:%s";
    private final static String GATEWAY_AUTH_ACCESS_TOKEN_KEY = "gateway:authInfo:access-token:";
    private final static String GATEWAY_AUTH_LOGIN_SMS_CODE_KEY = "gateway:authInfo:login-sms-code";
    private final static String GATEWAY_AUTH_LOGIN_USER_INFO_KEY = "gateway:authInfo:login-user-info";
    private final static String GATEWAY_AUTH_TEMP_LOGIN_INFO_KEY = "gateway:authInfo:temp-login-info";
    private final static String SEND_SMS_CHECK_MOBILE_LOCK_KEY = "gateway:send-sms-check:lock-mobile:%s";
    private final static String GATEWAY_AUTH_DEVICE_MAP_ACCESS_KEY = "gateway:authInfo:device-map-access";
    private final static String GATEWAY_AUTH_LOGIN_DEVICE_INFO_KEY = "gateway:authInfo:login-device-info";

    private final ICacheService cacheService;
    private final GatewayUserService userService;
    private final GatewayTenantService tenantService;

    public GatewayTicket getTicketInfo(String ticketId) {
        String groupKey = String.format("gateway:accessInfo:%s", ticketId);
        String ticketInfoKey = String.format("%s:ticketInfo", groupKey);
        return cacheService.getObject(ticketInfoKey, GatewayTicket.class);
    }


    public AccessTokenVo getAccessToken(GetAccessTokenReqDto req) {
        AccessTokenVo accessTokenVo = new AccessTokenVo();
        //时间戳校验
        boolean legalTimeStamp = DateUtil.isLegalTimeStampString(req.getTimestamp());
        boolean inMinuteTime = DateUtil.isInMinuteTime(req.getTimestamp(), SAFE_TIME_LIMIT);
        log.info("getAccessToken current timestamp = {}", System.currentTimeMillis());
        Assert.isTrue(legalTimeStamp && inMinuteTime, "无效的访问请求");
        //凭证信息校验
        log.info("getAccessToken ticket id = {}", req.getTicketId());
        GatewayTicket ticket = getTicketInfo(req.getTicketId());
        Assert.notNull(ticket, "无效的凭证信息");
        //签名校验
        boolean checkSign = checkSign(req.getSign(), req.getTicketId(), ticket.getSecret(), req.getTimestamp());
        Assert.isTrue(checkSign, "无效的签名");
        //生成oauth授权码
        accessTokenVo.setAccessToken(generateAccessToken(req, ticket));
        //设置令牌失效时间
        accessTokenVo.setAccessTokenExpireTime(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRE);
        //设置令牌ID
        accessTokenVo.setTicketId(req.getTicketId());
        //检查鉴权类型是否需要登录
        boolean needLogin =
                Objects.equals(ticket.getAuthType(), GatewayAuthType.OAUTH_LOGIN_CHECK.getValue()) ||
                        Objects.equals(ticket.getAuthType(), GatewayAuthType.OAUTH_LOGIN_LITE_CHECK.getValue()) ||
                        Objects.equals(ticket.getAuthType(), GatewayAuthType.OAUTH_DEVICE_TOKEN_LOGIN_CHECK.getValue());
        //生成设备令牌
        if (needLogin) {
            String deviceToken = StringUtil.isBlank(req.getDeviceToken()) ? generateDeviceToken(req.getTicketId()) : req.getDeviceToken();
            accessTokenVo.setDeviceToken(deviceToken);
            //删除旧的accessToken
            String oldAccessToken = cacheService.hGet(GATEWAY_AUTH_DEVICE_MAP_ACCESS_KEY, accessTokenVo.getDeviceToken(), String.class);
            if (StringUtil.isNotBlank(oldAccessToken)) {
                String oldAccessTokenCacheKey = StringUtil.concat(GATEWAY_AUTH_ACCESS_TOKEN_KEY, oldAccessToken);
                cacheService.del(oldAccessTokenCacheKey);
            }
            cacheService.hSet(GATEWAY_AUTH_DEVICE_MAP_ACCESS_KEY, accessTokenVo.getDeviceToken(), accessTokenVo.getAccessToken());
        }
        //写入缓存
        String accessTokenCacheKey = StringUtil.concat(GATEWAY_AUTH_ACCESS_TOKEN_KEY, accessTokenVo.getAccessToken());
        cacheService.set(accessTokenCacheKey, accessTokenVo, ACCESS_TOKEN_EXPIRE);
        return accessTokenVo;
    }


    public GatewayLoginVo loginLite(GatewayLoginLiteReqDto req) {
        String siteKey = GatewayAuthUtil.getSiteKey();
        //装载返回结果 提取参数
        GatewayLoginVo result = new GatewayLoginVo();
        String username = req.getUsername();
        String deviceToken = req.getDeviceToken();
        //校验登录账号密码
        GatewayUserVo gatewayUser = userService.findUserByUsername(req.getUsername(), siteKey);
        if (Objects.isNull(gatewayUser)) {
            gatewayUser = userService.createUser(username, siteKey);
        }
        Long userId = gatewayUser.getId();
        //检查用户名是否已登录,如有先删除登录标识
        String oldDeviceToken = cacheService.hGet(GATEWAY_AUTH_LOGIN_USER_INFO_KEY, username, String.class);
        if (StringUtil.isNotEmpty(oldDeviceToken)) {
            cacheService.hDel(GATEWAY_AUTH_LOGIN_USER_INFO_KEY, username);
            cacheService.hDel(GATEWAY_AUTH_LOGIN_DEVICE_INFO_KEY, oldDeviceToken);
        }
        //保存新的登录标识
        JSONObject loginDeviceInfoCache = new JSONObject();
        loginDeviceInfoCache.put("userId", userId.toString());
        loginDeviceInfoCache.put("email", gatewayUser.getEmail());
        loginDeviceInfoCache.put("mobile", gatewayUser.getMobileNumber());
        cacheService.hSet(GATEWAY_AUTH_LOGIN_DEVICE_INFO_KEY, deviceToken, loginDeviceInfoCache);
        cacheService.hSet(GATEWAY_AUTH_LOGIN_USER_INFO_KEY, username, deviceToken);
        return result;
    }


    public GatewayLogoutVo logout() {
        GatewayLogoutVo result = new GatewayLogoutVo();
        String ticketId = GatewayAuthUtil.getTicketId();
        String username = GatewayAuthUtil.getUsername();
        String email = GatewayAuthUtil.getEmail();
        String mobile = GatewayAuthUtil.getMobile();
        String deviceToken = GatewayAuthUtil.getDeviceToken();
        GatewayTicket ticketInfo = getTicketInfo(ticketId);
        Assert.notNull(ticketInfo, "无效的凭证信息");
        //获取单点登录地址
        if (Objects.equals(GatewayAuthType.OAUTH_LOGIN_CHECK.getValue(), ticketInfo.getAuthType())) {
            result.setSsoAddress(ticketInfo.getOauthRedirectUrl());
        }
        Assert.notBlank(deviceToken, "无效的设备信息");
        Assert.isTrue(cacheService.hDel(GATEWAY_AUTH_LOGIN_DEVICE_INFO_KEY, deviceToken), "登出操作失败");
        //删除登录标识
        if (StringUtil.isNotBlank(username)) {
            Assert.isTrue(cacheService.hDel(GATEWAY_AUTH_LOGIN_USER_INFO_KEY, username), "登出操作失败");
        }
        if (StringUtil.isNotBlank(mobile)) {
            Assert.isTrue(cacheService.hDel(GATEWAY_AUTH_LOGIN_USER_INFO_KEY, mobile), "登出操作失败");
        }
        if (StringUtil.isNotBlank(email)) {
            Assert.isTrue(cacheService.hDel(GATEWAY_AUTH_LOGIN_USER_INFO_KEY, email), "登出操作失败");
        }
        return result;
    }


    public GatewayLoginStatusVo loginStatus() {
        String deviceToken = GatewayAuthUtil.getDeviceToken();
        GatewayLoginStatusVo loginStatusVo = cacheService.hGet(GATEWAY_AUTH_LOGIN_DEVICE_INFO_KEY, deviceToken, GatewayLoginStatusVo.class);
        boolean hasLoginInfo = Objects.nonNull(loginStatusVo);
        if (!hasLoginInfo) {
            loginStatusVo = new GatewayLoginStatusVo();
        }
        loginStatusVo.setIsLogin(hasLoginInfo);
        return loginStatusVo;
    }


    public GatewayLoginVo login(GatewayLoginReqDto req) {
        //装载返回结果 提取参数
        GatewayLoginVo result = new GatewayLoginVo();
        String username = req.getUsername();
        String deviceToken = req.getDeviceToken();
        //校验登录账号密码
        String ticketId = GatewayAuthUtil.getTicketId();
        GatewayUserVo gatewayUser = userService.findUserByUsername(req.getUsername(), ticketId);
        Assert.notNull(gatewayUser, "用户信息不存在");
        Long userId = gatewayUser.getId();
        log.info("login req = {}", JsonUtil.toJson(req.getPassword()));
        Boolean matchPassword = userService.matchPassword(req.getPassword(), gatewayUser.getPassword());
        Assert.isTrue(matchPassword, "账号与密码不符");
        //校验凭证信息
        GatewayTicket ticket = getTicketInfo(ticketId);
        Assert.notNull(ticket, "凭证信息失效");
        String loginVerify = ticket.getLoginVerify();
        result.setMobileNumber(gatewayUser.getMobileNumber());
        //如果凭证设置了短信验证 ，先保存登录信息
        Boolean smsCheck = EnumsUtil.equalsValue(GatewayTicketLoginVerifyType.NONE, loginVerify);
        String identityToken = saveTempLoginInfo(username, userId, deviceToken, smsCheck);
        result.setIdentityToken(identityToken);
        result.setLoginVerify(loginVerify);
        return result;
    }


    public Boolean smsCodeCheck(GatewaySmsCodeCheckReqDto req) {
        //获取临时登录信息
        String identityToken = req.getIdentityToken();
        String smsCode = req.getSmsCode();
        String tempLoginInfoCacheKey = geneTempLoginInfoCacheKey(identityToken);
        JSONObject tmpLoginInfoCache = cacheService.getObject(tempLoginInfoCacheKey, JSONObject.class);
        Assert.notNull(tmpLoginInfoCache, "登录状态已失效,请重新登录");
        String username = tmpLoginInfoCache.getString("username");
        //用手机号查找验证码信息
        String smsCodeCacheKey = StringUtil.concat(GATEWAY_AUTH_LOGIN_SMS_CODE_KEY, ":", username);
        String smsCodeRaw = cacheService.getString(smsCodeCacheKey);
        //校验验证码是否正确
        if (Objects.equals(smsCodeRaw, smsCode)) {
            //删除短信缓存
            cacheService.del(smsCodeCacheKey);
            tmpLoginInfoCache.put("smsCheck", Boolean.TRUE);
            cacheService.set(tempLoginInfoCacheKey, tmpLoginInfoCache, ACCESS_TOKEN_EXPIRE);
        } else {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }


    public Boolean sendSmsCode(GatewayLoginCommonReqDto req) {
        //获取临时登录信息
        String identityToken = req.getIdentityToken();
        String tempLoginInfoCacheKey = geneTempLoginInfoCacheKey(identityToken);
        JSONObject tmpLoginInfoCache = cacheService.getObject(tempLoginInfoCacheKey, JSONObject.class);
        Assert.notNull(tmpLoginInfoCache, "登录状态已失效,请重新登录");
        //获取登录信息中的字段
        String username = tmpLoginInfoCache.getString("username");
        String ticketId = GatewayAuthUtil.getTicketId();
        GatewayUserVo user = userService.findUserByUsername(username, ticketId);
        Assert.notNull(user, "无效的账户");
        String smsCode = generateVerifyCode();
        String mobileNumber = user.getMobileNumber();
        //检查短信发送是否过于频繁
        String ipAddress = WebUtil.getIpAddress();
        Assert.isTrue(checkBeforeSmsSend(ipAddress, mobileNumber), "操作频繁,请稍后再试");
        //发送短信
        boolean sendSmsFlag = sendSmsCodeMessage(smsCode, mobileNumber);
        Assert.isTrue(sendSmsFlag, "发送短信失败,请稍后重试");
        //保存验证码缓存
        String smsCodeCacheKey = StringUtil.concat(GATEWAY_AUTH_LOGIN_SMS_CODE_KEY, ":", username);
        cacheService.set(smsCodeCacheKey, smsCode, SMS_CODE_TIME_OUT);
        return Boolean.TRUE;
    }


    public List<GatewayTenantVo> getTenantInfo(GatewayLoginCommonReqDto req) {
        //获取临时登录信息
        String identityToken = req.getIdentityToken();
        //校验短信验证码检查结果
        JSONObject tmpLoginInfoCache = checkSmsVerify(identityToken);
        Long userOpenId = tmpLoginInfoCache.getLong("userId");
        String ticketId = GatewayAuthUtil.getTicketId();
        return tenantService.findBindTenantInfo(TransferQueryDto.builder().mainDataId(userOpenId).build(), ticketId);
    }


    public Boolean selectTenant(GatewaySelectTenantReqDto req) {
        //获取临时登录信息
        String identityToken = req.getIdentityToken();
        Long tenantId = req.getTenantId();
        //校验短信验证码检查结果
        JSONObject tmpLoginInfoCache = checkSmsVerify(identityToken);
        Assert.notNull(tmpLoginInfoCache, "登录状态已失效,请重新登录");
        String username = tmpLoginInfoCache.getString("username");
        String deviceToken = tmpLoginInfoCache.getString("deviceToken");
        String ticketId = GatewayAuthUtil.getTicketId();
        GatewayUserVo user = userService.findUserByUsername(username, ticketId);
        Assert.isTrue(user.getIsActive(), "账户状态异常");
        GatewayTenant tenant = tenantService.findById(tenantId);
        Assert.isTrue(EnumsUtil.equalsValue(YesOrNoEnum.NO, tenant.getIsLock()), "租户状态异常");
        //没有验证直接更新登录信息
        updateUserLoginInfoCache(user, tenant, deviceToken);
        return Boolean.TRUE;
    }


    public Boolean registerCustomer(RegisterCustomerReqDto req) {
        //用手机号查找验证码信息
        String smsCodeCacheKey = StringUtil.concat(GATEWAY_AUTH_LOGIN_SMS_CODE_KEY, ":", req.getMobile());
        String smsCodeRaw = cacheService.getString(smsCodeCacheKey);
        //校验验证码是否正确
        Assert.isTrue(Objects.equals(smsCodeRaw, req.getSmsCode()), "短信验证码不正确");
        cacheService.del(smsCodeCacheKey);
        String ticketId = GatewayAuthUtil.getTicketId();
        GatewayUserVo user = userService.findUserByUsername(req.getUsername(), ticketId);
        Assert.isNull(user, "该用户名已被占用");
        user = userService.findUserByMobile(req.getMobile(), ticketId);
        Assert.isNull(user, "该手机号已注册");
        GatewayUser userForSave = GatewayUser.builder()
                .email(req.getEmail())
                .password(req.getPassword())
                .mobileNumber(req.getMobile())
                .username(req.getUsername())
                .realName(req.getRealName())
                .build();
        if (CollectionUtil.isNotEmpty(req.getInterestedApp())) {
            userForSave.setInterestedApp(CollectionUtil.listToStringWithComma(req.getInterestedApp()));
        }
        Assert.isThrowException(() -> userService.save(userForSave, ticketId), DuplicateKeyException.class, "该手机号已注册,请直接登录!");
//        NewCustomerRegisterMsg msg = JsonUtil.copyObject(req, NewCustomerRegisterMsg.class);
//        mailBox.send("ad-new-customer-register", msg);
        return Boolean.TRUE;
    }


    public Boolean sendRegisterCustomerSms(SendRegisterCustomerSmsReqDto req) {
        //获取登录信息中的字段
        String mobileNumber = req.getMobile();
        //检查短信发送是否过于频繁
        String ipAddress = WebUtil.getIpAddress();
        Assert.isTrue(checkBeforeSmsSend(ipAddress, mobileNumber), "操作频繁,请稍后再试");
        //发送短信
        String smsCode = generateVerifyCode();
        boolean sendSmsFlag = sendSmsCodeMessage(smsCode, mobileNumber);
        Assert.isTrue(sendSmsFlag, "发送短信失败,请稍后重试");
        //保存验证码缓存
        String smsCodeCacheKey = StringUtil.concat(GATEWAY_AUTH_LOGIN_SMS_CODE_KEY, ":", mobileNumber);
        cacheService.set(smsCodeCacheKey, smsCode, SMS_CODE_TIME_OUT);
        return Boolean.TRUE;
    }

    /**
     * 校验短信验证码检查结果
     *
     * @param identityToken 临时身份凭证
     * @return bool
     */
    private JSONObject checkSmsVerify(String identityToken) {
        String tempLoginInfoCacheKey = geneTempLoginInfoCacheKey(identityToken);
        JSONObject tmpLoginInfoCache = cacheService.getObject(tempLoginInfoCacheKey, JSONObject.class);
        Assert.notNull(tmpLoginInfoCache, "登录状态已失效,请重新登录");
        Boolean smsCheck = tmpLoginInfoCache.getBoolean("smsCheck");
        Assert.isTrue(smsCheck, "短信验证已失效,请重新登录");
        return tmpLoginInfoCache;
    }

    /**
     * 校验签名
     *
     * @param sign      签名
     * @param ticketId  应用ID
     * @param secret    密钥
     * @param timestamp 时间戳
     * @return 签名是否通过
     */
    private static boolean checkSign(String sign, String ticketId, String secret, String timestamp) {
        String rawSign = DigestUtils.md5Hex(ticketId + secret + timestamp);
        log.info("checkSign rawSign = {}", rawSign);
        return sign.equalsIgnoreCase(rawSign);
    }

    /**
     * 检查短信是否可以发送
     *
     * @param ipAddress   ip地址
     * @param phoneNumber 手机号码
     * @return bool
     */
    private boolean checkBeforeSmsSend(String ipAddress, String phoneNumber) {
        log.info("check before sms send , ipAddress = {} , phoneNumber = {} ", ipAddress, phoneNumber);
        ILock ipLock = cacheService.getLock(String.format(SEND_SMS_CHECK_IP_LOCK_KEY, ipAddress));
        ILock mobileLock = cacheService.getLock(String.format(SEND_SMS_CHECK_MOBILE_LOCK_KEY, phoneNumber));
        String ipCountKey = String.format(SEND_SMS_CHECK_IP_KEY, ipAddress);
        String mobileCountKey = String.format(SEND_SMS_CHECK_MOBILE_KEY, phoneNumber);
        ipLock.lock();
        try {
            Integer ipCount = cacheService.getInteger(ipCountKey);
            ipCount = Objects.isNull(ipCount) ? 0 : ipCount;
            if (ipCount >= SEND_SMS_IP_LIMIT) {
                return Boolean.FALSE;
            }
            cacheService.set(ipCountKey, ++ipCount, 1000 * 60 * 60 * 24L);
        } finally {
            ipLock.unLock();
        }
        mobileLock.lock();
        try {
            Integer mobileCount = cacheService.getInteger(mobileCountKey);
            mobileCount = Objects.isNull(mobileCount) ? 0 : mobileCount;
            if (mobileCount >= SEND_SMS_MOBILE_LIMIT) {
                return Boolean.FALSE;
            }
            cacheService.set(mobileCountKey, ++mobileCount, 1000 * 60 * 5L);
        } finally {
            mobileLock.unLock();
        }
        return Boolean.TRUE;
    }

    /**
     * 生成 accessToken
     *
     * @param req    请求参数
     * @param ticket 凭证信息
     * @return accessToken
     */
    private String generateAccessToken(GetAccessTokenReqDto req, GatewayTicket ticket) {
        String salt = ParseUtil.toString(SnowflakeIdUtil.generateId());
        byte[] paramBytes = CollectionUtil.byteArrMerge(req.getTicketId(), ticket.getSecret(), req.getSign(), req.getTimestamp(), salt);
        byte[] nowTimeBytes = ParseUtil.toString(System.currentTimeMillis()).getBytes();
        byte[] finalByteArray = CollectionUtil.byteArrMerge(paramBytes, nowTimeBytes);
        return EncryptionUtil.sha1(EncryptionUtil.md5Hex(new String(finalByteArray)));
    }

    /**
     * 生成 deviceToken
     *
     * @param ticketId 凭证Id
     * @return deviceToken
     */
    private static String generateDeviceToken(String ticketId) {
        String deviceToken;
        OsTypeEnum platform = WebUtil.getPlatform();
        String platformCode = platform.getValue();
        deviceToken = platformCode + EncryptionUtil.sha1(platformCode + System.currentTimeMillis() + ticketId);
        return platformCode + deviceToken + ticketId;
    }

    /**
     * 保存临时登录信息
     *
     * @param username    用户名
     * @param userId      用户ID
     * @param deviceToken 设备令牌
     * @return 临时凭证标识
     */
    private String saveTempLoginInfo(String username, Long userId, String deviceToken, Boolean smsCheck) {
        JSONObject tmpLoginInfoCache = new JSONObject();
        tmpLoginInfoCache.put("userId", userId);
        tmpLoginInfoCache.put("username", username);
        tmpLoginInfoCache.put("deviceToken", deviceToken);
        tmpLoginInfoCache.put("smsCheck", smsCheck);
        String identityToken = generateIdentityToken(deviceToken);
        String tempLoginInfoCacheKey = geneTempLoginInfoCacheKey(identityToken);
        cacheService.set(tempLoginInfoCacheKey, tmpLoginInfoCache, ACCESS_TOKEN_EXPIRE);
        return identityToken;
    }

    /**
     * 生成 deviceToken
     *
     * @param deviceToken 设备
     * @return deviceToken
     */
    private String generateIdentityToken(String deviceToken) {
        String salt = ParseUtil.toString(SnowflakeIdUtil.generateId());
        return EncryptionUtil.sha1(deviceToken + System.currentTimeMillis() + salt);
    }

    /**
     * @return 随机4位短信验证码
     */
    private static String generateVerifyCode() {
        return (int) (Math.random() * 9000 + 1000) + "";
    }

    /**
     * 生成临时登录信息缓存的key
     *
     * @param username 用户名
     * @return 缓存key
     */
    private static String geneTempLoginInfoCacheKey(String username) {
        return StringUtil.concat(GATEWAY_AUTH_TEMP_LOGIN_INFO_KEY, ":", username);
    }

    /**
     * 更新用户登录信息缓存
     *
     * @param user        用户
     * @param tenant      租户
     * @param deviceToken 设备标识
     */
    private void updateUserLoginInfoCache(GatewayUserVo user, GatewayTenant tenant, String deviceToken) {
        String username = user.getUsername();
        String realName = user.getRealName();
        String tenantName = tenant.getTenantName();
        Long userId = user.getId();
        Long tenantId = tenant.getId();
        //检查用户名是否已登录,如有先删除登录标识
        String oldDeviceToken = cacheService.hGet(GATEWAY_AUTH_LOGIN_USER_INFO_KEY, username, String.class);
        if (StringUtil.isNotEmpty(oldDeviceToken)) {
            cacheService.hDel(GATEWAY_AUTH_LOGIN_USER_INFO_KEY, username);
            cacheService.hDel(GATEWAY_AUTH_LOGIN_DEVICE_INFO_KEY, oldDeviceToken);
        }
        //保存新的登录标识
        JSONObject loginDeviceInfoCache = new JSONObject();
        loginDeviceInfoCache.put("userId", userId);
        loginDeviceInfoCache.put("username", username);
        loginDeviceInfoCache.put("realName", realName);
        loginDeviceInfoCache.put("tenantName", tenantName);
        loginDeviceInfoCache.put("tenantId", tenantId);
        cacheService.hSet(GATEWAY_AUTH_LOGIN_DEVICE_INFO_KEY, deviceToken, loginDeviceInfoCache);
        cacheService.hSet(GATEWAY_AUTH_LOGIN_USER_INFO_KEY, username, deviceToken);
    }

    /**
     * 发送短信验证码
     *
     * @param smsCode      验证码code
     * @param mobileNumber 手机号码
     * @return bool
     */
    private boolean sendSmsCodeMessage(String smsCode, String mobileNumber) {
        JSONObject param = new JSONObject();
        param.put("code", smsCode);
        try {
//            AliyunSmsUtil.sendSms(ALIYUN_SMS_SIGNATURE_AD, ALIYUN_SMS_TEMPLATE_CODE, param, mobileNumber);
        } catch (Exception e) {
            log.error(String.format("send aliyun sms to %s failure", mobileNumber), e);
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

}
