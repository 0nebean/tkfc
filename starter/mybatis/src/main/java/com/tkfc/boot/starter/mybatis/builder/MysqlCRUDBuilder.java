package com.tkfc.boot.starter.mybatis.builder;

import com.alibaba.fastjson2.JSONObject;
import com.tkfc.boot.starter.mybatis.builder.metadata.BeanInfo;
import com.tkfc.boot.starter.mybatis.builder.metadata.MetaDataMappingManager;
import com.tkfc.core.constants.StringPool;
import com.tkfc.core.toolkit.FreeMarkerTemplateUtil;
import com.tkfc.core.toolkit.FreeMarkerTemplateUtil.LoaderType;
import com.tkfc.core.toolkit.JsonUtil;
import com.tkfc.core.toolkit.PropUtil;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * mysql 增删改查方法 生成器
 *
 * @author 0neBean
 */
@Slf4j
public class MysqlCRUDBuilder extends CRUDBuilder {

    @Override
    public <T> String buildByClass(Class<T> clazz) throws TemplateException, IOException {
        BeanInfo beanInfo = MetaDataMappingManager.getBeanInfo(clazz);
        String templatePath = PropUtil.getInstance().getConfig("org.mybatis.create.sql.vm.file.path");
        JSONObject param = JsonUtil.toJsonObject(beanInfo);
        String buildSql = FreeMarkerTemplateUtil.generateString(param, templatePath, LoaderType.CLASSPATH);
        log.debug("MysqlCRUDBuilder generate table = {} buildSql = {}", beanInfo.getTableName(), StringPool.NEWLINE + buildSql);
        return buildSql;
    }

}
