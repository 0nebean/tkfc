package com.tkfc.sdk.service.impl;

import com.tkfc.boot.starter.mybatis.extend.BaseServiceImpl;
import com.tkfc.sdk.mapper.GatewaySiteDomainMapper;
import com.tkfc.sdk.model.GatewaySiteDomain;
import com.tkfc.sdk.pojo.vo.GatewaySiteDomainVo;
import com.tkfc.sdk.service.GatewaySiteDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 站点域名 serviceImpl
 *
 * @author 0neBean
 * @since 2023-10-29 12:24:53
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GatewaySiteDomainServiceImpl extends BaseServiceImpl<GatewaySiteDomain, GatewaySiteDomainVo, GatewaySiteDomainMapper> implements GatewaySiteDomainService {

}
