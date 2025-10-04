package com.tkfc.dictionary.builder;

import com.tkfc.core.constants.StringPool;
import com.tkfc.core.enums.base.BaseEnums;
import com.tkfc.core.toolkit.EnumsUtil;
import com.tkfc.core.toolkit.PropUtil;
import com.tkfc.core.toolkit.ReflectionUtil;
import com.tkfc.dictionary.annotation.EnableDictionary;
import com.tkfc.dictionary.dto.DictionaryDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 字典构建工厂
 *
 * @author 0neBean
 * @version 1.0
 * @since 2024-07-09 12:40:56
 */
@Slf4j
public class DictionaryBuildFactory {

    /**
     * 构建字典内存对象
     *
     * @return 字典对象列表
     */
    public static List<DictionaryDto> buildDictionary() {
        List<DictionaryDto> dictionaryList = new ArrayList<>();
        //获取字典工具注解
        EnableDictionary enableDictionary = ReflectionUtil.getSpringBootMainClass().getAnnotation(EnableDictionary.class);
        if (Objects.isNull(enableDictionary)) {
            return dictionaryList;
        }
        String[] enumPath = enableDictionary.enumPath();
        if (enumPath.length == 0) {
            return dictionaryList;
        }
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(Thread.currentThread().getContextClassLoader());
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resolver);
        for (String path : enumPath) {
            try {
                Resource[] rs_model = resolver.getResources(covertClassPath(path));
                for (Resource resource : rs_model) {
                    String className = metadataReaderFactory.getMetadataReader(resource).getClassMetadata().getClassName();
                    Class<?> clazz = Class.forName(className);
                    BaseEnums<?>[] enums = EnumsUtil.getValues(clazz);
                    for (BaseEnums<?> enumItem : enums) {
                        DictionaryDto dictionary = DictionaryDto.builder()
                                .dic(enumItem.getDescription())
                                .val(enumItem.getValue().toString())
                                .sort(enumItem.getSort())
                                .groupVal(clazz.getSimpleName()).build();
                        dictionaryList.add(dictionary);
                    }
                }
            } catch (Exception e) {
                log.error("buildDictionary failure , got an error = ", e);
                System.exit(0);
            }
        }
        return dictionaryList;
    }

    /**
     * 包装类路径
     *
     * @param path 注解路径
     * @return class path 路径
     */
    private static String covertClassPath(String path) {
        return String.format("classpath*:%s/*.class", path.replace(StringPool.DOT, StringPool.SLASH));
    }

}
