package com.tkfc.boot.starter.mybatis.factory;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;

/**
 * 自定义 MapperFactoryBean，解决 Provider 注解导致的类型推断问题
 *
 * @author 0neBean
 */
public class CustomMapperFactoryBean<T> extends MapperFactoryBean<T> {

    public CustomMapperFactoryBean() {
        super();
    }

    public CustomMapperFactoryBean(Class<T> mapperInterface) {
        super(mapperInterface);
    }

    @Override
    public Class<T> getObjectType() {
        return getMapperInterface();
    }
}
