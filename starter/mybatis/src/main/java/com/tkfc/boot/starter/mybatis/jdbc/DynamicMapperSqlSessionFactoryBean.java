package com.tkfc.boot.starter.mybatis.jdbc;

import com.tkfc.boot.starter.mybatis.builder.MysqlCRUDBuilder;
import com.tkfc.boot.starter.mybatis.extend.BaseModel;
import com.tkfc.core.toolkit.PropUtil;
import com.tkfc.core.toolkit.ReflectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


/**
 * 继承自SqlSessionFactoryBean类，实现Mapper基本操作模块化功能
 * 在一般系统中，每个Model都会有部分基本操作，这些操作模式基本相同，所以在我们系统内部将其抽象出来，通过动态的生成Mapper的形式注入到Mybatis中。
 * 对于生成的mapper文件，在setMapperLocations(Resource[])方法中加入到Factory map locations中，让iBaits自行处理。
 *
 * @see SqlSessionFactoryBean
 */
@Slf4j
public class DynamicMapperSqlSessionFactoryBean extends SqlSessionFactoryBean {

    @SuppressWarnings("all")
    private static final String modelPathKey = "org.mybatis.jvm.model.class.classpath";


    /**
     * 再此方法中加入了动态mapper 文件插入
     *
     * @param mapperLocations mapper 资源文件列表
     */
    @Override
    public void setMapperLocations(Resource[] mapperLocations) {
        //生成mapper资源文件对象
        Resource[] resources = productModelMapperResources();
        //将原始source和生成的source整合
        resources = addResourcesByArray(mapperLocations, resources);
        //开始加载
        super.setMapperLocations(resources);
    }

    /**
     * 将一个单独的resource mapper资源文件加入到resource资源文件列表中
     *
     * @param sources  资源文件列表
     * @param resource 资源文件
     * @return 添加后的资源
     */
    private Resource[] addResourcesByArray(Resource[] sources, Resource... resource) {
        Resource[] copy = new Resource[sources.length + resource.length];
        log.debug("add resource length " + resource.length);
        int i = 0;
        for (; i < sources.length; i++) {
            copy[i] = sources[i];
        }
        for (; i < copy.length; i++) {
            copy[i] = resource[i - sources.length];
        }
        return copy;
    }

    /**
     * 根据当前model类，自动生成mapper对应的基本文件
     *
     * @return 生成的mapper文件的资源
     */
    private Resource[] productModelMapperResources() {

        List<Class<?>> list = getAllBaseClass();
        log.debug("classes size is " + list.size());
        //根据class生成相应的Mapper文件
        Resource[] resources = new Resource[list.size()];
        //开始生成
        log.info("scanning database model , initializing CRUD API ......");
        for (int i = 0; i < list.size(); i++) {
            resources[i] = productModel(list.get(i));
        }
        return resources;
    }

    /**
     * 获取JVM中所有Model类列表
     *
     * @return Model类列表
     */
    private List<Class<?>> getAllBaseClass() {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(Thread.currentThread().getContextClassLoader());
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resolver);
        List<Class<?>> classList = new ArrayList<>();
        try {
            String modelPath = PropUtil.getInstance().getConfig(modelPathKey);
            Resource[] rs_model = resolver.getResources(modelPath);

            for (Resource resource : rs_model) {
                String className = metadataReaderFactory.getMetadataReader(resource).getClassMetadata().getClassName();
                Class<?> clazz;
                try {
                    clazz = Class.forName(className);
                    //判断当前cl是否是BaseModel的子类，且不是BaseModel
                    if (BaseModel.class.isAssignableFrom(clazz) || ReflectionUtil.hasAnnotation(clazz, "TableName")) {
                        classList.add(clazz);
                    }
                } catch (ClassNotFoundException e) {
                    log.error("实体" + className + "应该有一个无参的构造函数...");
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classList;
    }

    /**
     * 根据ModelClass生成对应的Mapper基本文件
     *
     * @param cl Model class
     * @return Mapper文件对象
     */
    private Resource productModel(Class<?> cl) {
        String data;
        try {
            data = new MysqlCRUDBuilder().buildByClass(cl);
        } catch (Exception e) {
            log.debug("generate sql map  by class " + cl.getName() + "  error ", e);
            throw new IllegalArgumentException(e);
        }
        log.debug(cl.getName() + " is \n" + data);
        return new InputStreamResource(new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8)), cl.getName());
    }


}
