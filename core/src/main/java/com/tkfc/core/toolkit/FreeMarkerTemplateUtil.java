package com.tkfc.core.toolkit;

import com.tkfc.core.enums.SystemTypeEnum;
import com.tkfc.core.enums.base.BaseEnums;
import com.tkfc.core.throwable.base.Assert;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.NullCacheStorage;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

/**
 * @author 0neBean
 * FreeMarker 操作工具类
 */
@Slf4j
public class FreeMarkerTemplateUtil {

    //枚举项
    public enum LoaderType implements BaseEnums<String> {
        FILE("file", "文件", 0),
        STRING("string", "字符串", 1),
        CLASSPATH("classpath", "类上下文", 2),
        ;

        private final String description;
        private final String value;
        private final Integer sort;

        LoaderType(String value, String description, Integer sort) {
            this.value = value;
            this.description = description;
            this.sort = sort;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public Integer getSort() {
            return sort;
        }

        @Override
        public BaseEnums<String>[] getValues() {
            return values();
        }
    }

    private static Class<?> resourceLoaderClass = null;
    private static final Configuration STRING_LOADER;
    private static final Configuration CLASSPATH_LOADER;
    private static final Configuration FILE_LOADER;
    private static final StringTemplateLoader STRING_TEMPLATE_LOADER = new StringTemplateLoader();


    static {
        //模板基础路径
        //文件模板
        FILE_LOADER = new Configuration(Configuration.VERSION_2_3_22);
        FILE_LOADER.setDefaultEncoding(String.valueOf(StandardCharsets.UTF_8));
        FILE_LOADER.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        FILE_LOADER.setCacheStorage(NullCacheStorage.INSTANCE);
        String BASE_FILE_PATH;
        if (Objects.equals(EnvUtil.getOsType(), SystemTypeEnum.WINDOWS)) {
            BASE_FILE_PATH = PropUtil.getInstance().getConfig("freemarker.base.file.path.windows");
        } else {
            BASE_FILE_PATH = PropUtil.getInstance().getConfig("freemarker.base.file.path.unix");
        }
        try {
            FILE_LOADER.setTemplateLoader(new FileTemplateLoader(new File(BASE_FILE_PATH)));
        } catch (IOException ignored) {
        }
        //字符串模板
        STRING_LOADER = new Configuration(Configuration.VERSION_2_3_22);
        STRING_LOADER.setDefaultEncoding(String.valueOf(StandardCharsets.UTF_8));
        STRING_LOADER.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        STRING_LOADER.setCacheStorage(NullCacheStorage.INSTANCE);
        STRING_LOADER.setTemplateLoader(STRING_TEMPLATE_LOADER);
        //类上下文模板
        CLASSPATH_LOADER = new Configuration(Configuration.VERSION_2_3_22);
        CLASSPATH_LOADER.setDefaultEncoding(String.valueOf(StandardCharsets.UTF_8));
        CLASSPATH_LOADER.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        CLASSPATH_LOADER.setCacheStorage(NullCacheStorage.INSTANCE);
        //初始化加载类
        resourceLoaderClass = (resourceLoaderClass == null) ? ReflectionUtil.getSpringBootMainClass() : resourceLoaderClass;
        CLASSPATH_LOADER.setTemplateLoader(new ClassTemplateLoader(resourceLoaderClass, System.getProperty("file.separator")));
    }

    /**
     * 初始化加载类
     *
     * @param resourceLoader 加载类
     */
    public static void initResourceLoader(Class<?> resourceLoader) {
        resourceLoaderClass = resourceLoader;
    }

    /**
     * 获取模板
     *
     * @param templateName 模板路径
     * @return Template
     */
    private static Template getTemplate(String templateName, LoaderType loaderType) {
        try {
            switch (loaderType) {
                case STRING:
                    return STRING_LOADER.getTemplate(templateName);
                case CLASSPATH:
                    return CLASSPATH_LOADER.getTemplate(templateName);
                case FILE:
                    return FILE_LOADER.getTemplate(templateName);
                default:
                    return null;
            }
        } catch (IOException e) {
            log.error("getTemplate get an err , e = ", e);
            return null;
        }
    }

    /**
     * 设置string 模板
     *
     * @param templateName    模板名称
     * @param templateContent 模板内容
     */
    public static void putStringTemplate(String templateName, String templateContent) {
        STRING_TEMPLATE_LOADER.putTemplate(templateName, templateContent);
    }

    /**
     * 根据模板生成string
     *
     * @param param        参数
     * @param templateName 模板名字
     * @param loaderType   加载器类型
     * @return string
     */
    public static String generateString(Map<String, Object> param, String templateName, LoaderType loaderType) throws TemplateException, IOException {
        Template template = getTemplate(templateName, loaderType);
        StringWriter out = new StringWriter();
        assert template != null;
        template.process(param, out);
        return out.getBuffer().toString();
    }


    /**
     * 根据模板生成string
     *
     * @param param          参数
     * @param templateString 模板
     * @return string
     */
    public static String generateString(Map<String, Object> param, String templateString) throws TemplateException, IOException {
        Template template = new Template(SnowflakeIdUtil.generateId().toString(), templateString, STRING_LOADER);
        StringWriter out = new StringWriter();
        template.process(param, out);
        return out.getBuffer().toString();
    }

    /**
     * 生成文件
     *
     * @param param        参数
     * @param templateName 模板名称
     * @param file         文件
     * @param loaderType   加载器类型
     */
    public static void generateFile(Map<String, Object> param, String templateName, File file, LoaderType loaderType) throws TemplateException, IOException {
        Template template = getTemplate(templateName, loaderType);
        Assert.notNull(template);
        FileOutputStream fos = new FileOutputStream(file.getAbsoluteFile());
        Writer out = new BufferedWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8), 10240);
        template.process(param, out);
        fos.close();
        out.close();
    }


}
