package com.tkfc.boot.starter.mybatis.scanner;

import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.Set;

/**
 * 自定义 Mapper 扫描器，解决 Provider 注解导致的 factoryBeanObjectType 类型错误
 *
 * @author 0neBean
 */
public class CustomClassPathMapperScanner extends ClassPathMapperScanner {

    public CustomClassPathMapperScanner(BeanDefinitionRegistry registry) {
        super(registry);
    }

    @Override
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);

        if (beanDefinitions.isEmpty()) {
            logger.warn("No MyBatis mapper was found in '" + String.join(", ", basePackages)
                    + "' package. Please check your configuration.");
        } else {
            processBeanDefinitions(beanDefinitions);
        }

        return beanDefinitions;
    }

    private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) {
        for (BeanDefinitionHolder holder : beanDefinitions) {
            GenericBeanDefinition definition = (GenericBeanDefinition) holder.getBeanDefinition();

            // 移除 factoryBeanObjectType 属性，让 Spring 在运行时动态解析
            definition.setAttribute("factoryBeanObjectType", null);
        }
    }
}
