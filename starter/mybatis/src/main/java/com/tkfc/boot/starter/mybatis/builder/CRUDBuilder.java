package com.tkfc.boot.starter.mybatis.builder;

import freemarker.template.TemplateException;

import java.io.IOException;

/**
 * 生成CRUD的 抽象接口
 *
 * @author 0neBean
 */
public abstract class CRUDBuilder {

    public abstract <T> String buildByClass(Class<T> clazz) throws TemplateException, IOException;

}
