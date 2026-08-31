package com.tkfc.boot.starter.mybatis.config;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import com.tkfc.boot.starter.mybatis.extend.SqlMapper;
import com.tkfc.boot.starter.mybatis.interceptor.LogSQLExecutionTimeInterceptor;
import com.tkfc.boot.starter.mybatis.interceptor.PaginationInterceptor;
import com.tkfc.boot.starter.mybatis.jdbc.DynamicMapperSqlSessionFactoryBean;
import com.tkfc.core.toolkit.ParseUtil;
import com.tkfc.core.toolkit.PropUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.ibatis.plugin.Interceptor;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
@ConditionalOnProperty(value = "spring.datasource.databaseName")
public class JdbcDataSourceConfig {

    /**
     * 数据库连接池
     *
     * @return 返回数据库连接池实例
     */
    @Bean(name = "dataSource")
    public DruidDataSource druidDataSource() {
        log.info("druid data source initializing...");
        DruidDataSource datasource = new DruidDataSource();
        datasource.setUrl(PropUtil.getInstance().getConfig("spring.datasource.url"));
        datasource.setUsername(PropUtil.getInstance().getConfig("spring.datasource.username"));
        datasource.setPassword(PropUtil.getInstance().getConfig("spring.datasource.password"));
        datasource.setDriverClassName(PropUtil.getInstance().getConfig("spring.datasource.driver-class-name"));
        //初始化大小，最小，最大
        datasource.setInitialSize(ParseUtil.toInt(PropUtil.getInstance().getConfig("spring.datasource.initialSize")));
        datasource.setMinIdle(ParseUtil.toInt(PropUtil.getInstance().getConfig("spring.datasource.minIdle")));
        datasource.setMaxActive(ParseUtil.toInt(PropUtil.getInstance().getConfig("spring.datasource.maxActive")));
        //配置获取连接等待超时的时间
        datasource.setMaxWait(ParseUtil.toLong(PropUtil.getInstance().getConfig("spring.datasource.maxWait")));
        //配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
        datasource.setTimeBetweenEvictionRunsMillis(ParseUtil.toLong(PropUtil.getInstance().getConfig("spring.datasource.timeBetweenEvictionRunsMillis")));
        //配置一个连接在池中最小生存的时间，单位是毫秒
        datasource.setMinEvictableIdleTimeMillis(ParseUtil.toLong(PropUtil.getInstance().getConfig("spring.datasource.minEvictableIdleTimeMillis")));
        datasource.setValidationQuery(PropUtil.getInstance().getConfig("spring.datasource.validationQuery"));
        datasource.setTestWhileIdle(ParseUtil.toBoolean(PropUtil.getInstance().getConfig("spring.datasource.testWhileIdle")));
        datasource.setTestOnBorrow(ParseUtil.toBoolean(PropUtil.getInstance().getConfig("spring.datasource.testOnBorrow")));
        datasource.setTestOnReturn(ParseUtil.toBoolean(PropUtil.getInstance().getConfig("spring.datasource.testOnReturn")));
        //打开PSCache，并且指定每个连接上PSCache的大小
        datasource.setPoolPreparedStatements(ParseUtil.toBoolean(PropUtil.getInstance().getConfig("spring.datasource.poolPreparedStatements")));
        datasource.setMaxPoolPreparedStatementPerConnectionSize(ParseUtil.toInt(PropUtil.getInstance().getConfig("spring.datasource.maxPoolPreparedStatementPerConnectionSize")));

        WallConfig wallConfig = new WallConfig();
        wallConfig.setMultiStatementAllow(true);
        wallConfig.setSetAllow(true);
        WallFilter wallFilter = new WallFilter();
        wallFilter.setConfig(wallConfig);

        boolean isExits = false;
        List<Filter> proxyFilters = datasource.getProxyFilters();
        for (Filter proxyFilter : proxyFilters) {
            if (proxyFilter instanceof WallFilter) {
                ((WallFilter) proxyFilter).setConfig(wallConfig);
                isExits = true;
            }
        }
        if (!isExits){
            List<Filter> filters = new ArrayList<>();
            filters.add(wallFilter);
            datasource.setProxyFilters(filters);
        }

        try {
            //配置监控统计拦截的filters，去掉后监控界面sql无法统计，'wall'用于防火墙
            datasource.setFilters(PropUtil.getInstance().getConfig("spring.datasource.filters"));
        } catch (SQLException e) {
            log.error("druid configuration initialization filter", e);
        }
        return datasource;
    }


//    /**
//     * 配置监控服务器
//     *
//     * 注意：Druid 1.2.x 版本的 StatViewServlet 基于 javax.servlet，与 Spring Boot 3.x 的 jakarta.servlet 不兼容
//     * 暂时注释掉此配置，等待 Druid 发布完全支持 Jakarta EE 的版本
//     *
//     * @return 返回监控注册的servlet对象
//     */
//     @Bean
//     @SuppressWarnings({"rawtypes", "unchecked"})
//     public ServletRegistrationBean statViewServlet() {
//         log.info("druid stat view servlet initializing...");
//         ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
//         // 添加IP白名单
//         servletRegistrationBean.addInitParameter("allow", PropUtil.getInstance().getConfig("com.alibaba.druid.ip.allow"));
//         // 添加IP黑名单，当白名单和黑名单重复时，黑名单优先级更高
//         servletRegistrationBean.addInitParameter("deny", PropUtil.getInstance().getConfig("com.alibaba.druid.ip.deny"));
//         // 添加控制台管理用户
//         servletRegistrationBean.addInitParameter("loginUsername", PropUtil.getInstance().getConfig("com.alibaba.druid.username"));
//         servletRegistrationBean.addInitParameter("loginPassword", PropUtil.getInstance().getConfig("com.alibaba.druid.password"));
//         // 是否能够重置数据
//         servletRegistrationBean.addInitParameter("resetEnable", "false");
//         //慢sql日志
//         servletRegistrationBean.addInitParameter("logSlowSql", PropUtil.getInstance().getConfig("com.alibaba.druid.logSlowSql"));
//         return servletRegistrationBean;
//     }
//
//    /**
//     * 配置服务过滤器
//     *
//     * 注意：Druid 1.2.x 版本的 WebStatFilter 基于 javax.servlet，与 Spring Boot 3.x 的 jakarta.servlet 不兼容
//     * 暂时注释掉此配置，等待 Druid 发布完全支持 Jakarta EE 的版本
//     *
//     * @return 返回过滤器配置对象
//     */
//     @Bean
//     @SuppressWarnings({"rawtypes", "unchecked"})
//     public FilterRegistrationBean statFilter() {
//         FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new WebStatFilter());
//         // 添加过滤规则
//         filterRegistrationBean.addUrlPatterns("/*");
//         // 忽略过滤格式
//         filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*,");
//         return filterRegistrationBean;
//     }


    /**
     * mybatis的扫描配置实例
     *
     * @param sqlSessionFactory DynamicMapperSqlSessionFactoryBean
     * @return 返回一个mybatis扫描配置实例
     */
    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer(@Qualifier("com.tkfc.boot.starter.mybatis.jdbc.DynamicMapperSqlSessionFactoryBean") DynamicMapperSqlSessionFactoryBean sqlSessionFactory) {
        log.info("mybatis mapper xml file scanner initializing...");
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        //设置mybatis接口的扫描路径
        log.info("scanning mybatis mapper xml file  from path = {}", PropUtil.getInstance().getConfig("org.mybatis.base.package"));
        mapperScannerConfigurer.setBasePackage(PropUtil.getInstance().getConfig("org.mybatis.base.package"));
        //设置mybatis接口的抽象接口
        mapperScannerConfigurer.setMarkerInterface(SqlMapper.class);
        //指定sqlSessionFactory
        mapperScannerConfigurer.setSqlSessionFactoryBeanName(sqlSessionFactory.getClass().getName());
        return mapperScannerConfigurer;
    }


    /**
     * 实例化自定义的sqlSessionFactory
     *
     * @param dataSource 数据源
     * @return 返回一个sqlSessionFactory实例
     * @throws Exception 抛出异常
     */
    @Bean(name = "com.tkfc.boot.starter.mybatis.jdbc.DynamicMapperSqlSessionFactoryBean")
    public DynamicMapperSqlSessionFactoryBean sqlSessionFactory(DruidDataSource dataSource) throws Exception {
        log.info("dynamic mapper sql session factory initializing...");
        DynamicMapperSqlSessionFactoryBean sqlSessionFactory = new DynamicMapperSqlSessionFactoryBean();
        //设置数据源
        sqlSessionFactory.setDataSource(dataSource);
        sqlSessionFactory.setConfigLocation(new PathMatchingResourcePatternResolver().getResources(PropUtil.getInstance().getConfig("org.mybatis.config.path"))[0]);
        //设置扫描业务Mapper和抽象Mapper的路径
        Resource[] mls1 = new PathMatchingResourcePatternResolver().getResources(PropUtil.getInstance().getConfig("org.mybatis.base.mapper.path"));
        Resource[] mls2 = new PathMatchingResourcePatternResolver().getResources(PropUtil.getInstance().getConfig("org.mybatis.business.mapper.path"));
        sqlSessionFactory.setMapperLocations(ArrayUtils.addAll(mls1, mls2));
        //设置自定的的自定义插件
        Interceptor[] plugins = new Interceptor[2];
        //增加的sql执行施加统计插件
        Interceptor plg1 = new LogSQLExecutionTimeInterceptor();
        //增加的分页插件
        Interceptor plg2 = new PaginationInterceptor();
        plugins[0] = plg1;
        plugins[1] = plg2;
        sqlSessionFactory.setPlugins(plugins);
        return sqlSessionFactory;
    }

    /**
     * 指定自定义的数据源事务管理者
     *
     * @param dataSource 数据源
     * @return 返回一个数据源事务管理者
     */
    @Bean(name = "transactionManager")
    public DataSourceTransactionManager transactionManager(DruidDataSource dataSource) {
        log.info("customized transaction manager initializing...");
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(dataSource);
        return dataSourceTransactionManager;
    }

    private static final String AOP_POINTCUT_EXPRESSION = "execution(public * net.tkfc.*.*.service..*.*(..))";

    @Bean
    public Advisor txAdviceAdvisor(@Qualifier("transactionManager") DataSourceTransactionManager transactionManager) {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(AOP_POINTCUT_EXPRESSION);
        return new DefaultPointcutAdvisor(pointcut, txAdvice(transactionManager));
    }

    @Bean
    public TransactionInterceptor txAdvice(TransactionManager transactionManager) {
        DefaultTransactionAttribute txAttr_REQUIRED = new DefaultTransactionAttribute();
        txAttr_REQUIRED.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        DefaultTransactionAttribute txAttr_REQUIRED_READONLY = new DefaultTransactionAttribute();
        txAttr_REQUIRED_READONLY.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        txAttr_REQUIRED_READONLY.setReadOnly(true);
        NameMatchTransactionAttributeSource source = new NameMatchTransactionAttributeSource();
        source.addTransactionalMethod("add*", txAttr_REQUIRED);
        source.addTransactionalMethod("save*", txAttr_REQUIRED);
        source.addTransactionalMethod("delete*", txAttr_REQUIRED);
        source.addTransactionalMethod("update*", txAttr_REQUIRED);
        source.addTransactionalMethod("exec*", txAttr_REQUIRED);
        source.addTransactionalMethod("set*", txAttr_REQUIRED);
        source.addTransactionalMethod("get*", txAttr_REQUIRED_READONLY);
        source.addTransactionalMethod("query*", txAttr_REQUIRED_READONLY);
        source.addTransactionalMethod("find*", txAttr_REQUIRED_READONLY);
        source.addTransactionalMethod("list*", txAttr_REQUIRED_READONLY);
        source.addTransactionalMethod("count*", txAttr_REQUIRED_READONLY);
        source.addTransactionalMethod("is*", txAttr_REQUIRED_READONLY);
        return new TransactionInterceptor(transactionManager, source);
    }


}
