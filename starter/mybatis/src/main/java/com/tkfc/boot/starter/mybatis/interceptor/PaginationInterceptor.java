package com.tkfc.boot.starter.mybatis.interceptor;

import com.tkfc.core.common.pojo.Pagination;
import com.tkfc.core.toolkit.StringUtil;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.*;
import java.util.concurrent.locks.Condition;


/**
 * 分页查询。 通过 BoundSqlWrapper 实现SQL语句的动态替换。通过Executor调用Mybatis的查询，不直接操纵JDBC
 */
//只拦截select部分
@Intercepts({@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})})
@SuppressWarnings({"rawtypes"})
public class PaginationInterceptor implements Interceptor {

    private final static String COUNT_SQL_ID = "countSqlId";

    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parameter = invocation.getArgs()[1];

        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        if (boundSql == null || StringUtil.isEmpty(boundSql.getSql()))
            return null;

        Object parameterObject = boundSql.getParameterObject();

        Pagination pagination = null;
        // 通过参数传递Pagination
        if (parameterObject != null) {
            pagination = getParameterByType(parameterObject, Pagination.class);
            List<?> conditions = getParameterByType(parameterObject, List.class);
            if (conditions != null && conditions.size() > 0) {
                if (Condition.class.isAssignableFrom(conditions.get(0).getClass())) {
                    boundSql.setAdditionalParameter("conditions", conditions);
                }
            }
        }

        if (pagination != null) {
            String originalSql = boundSql.getSql().trim();
            int totalCount = 0;
            Integer pageSize = pagination.getPageSize();
            Integer currentPage = pagination.getCurrentPage();
            boolean hasPageSize = Objects.nonNull(pageSize) && pageSize >= 0;
            boolean hasCurrentPage = Objects.nonNull(currentPage) && currentPage >= 0;
            
            // 判断是否为仅 LIMIT 模式（只设置 pageSize，不设置 currentPage）
            boolean limitOnly = hasPageSize && !hasCurrentPage;
            
            if (limitOnly) {
                // 仅 LIMIT 模式：只设置 LIMIT，不查询总数，不计算分页
                String pageSql = getPagingString(originalSql, 0, pageSize);
                invocation.getArgs()[2] = new RowBounds(RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
                if (boundSql.getParameterMappings().size() == 0){
                    boundSql = new BoundSql(mappedStatement.getConfiguration(), pageSql, new ArrayList<>(), boundSql.getParameterObject());
                }
                BoundSqlWrapper newBoundSql = new BoundSqlWrapper(boundSql, pageSql, mappedStatement.getConfiguration());
                MappedStatement newMs = copyFromMappedStatement(mappedStatement, new BoundSqlSqlSource(newBoundSql));
                invocation.getArgs()[0] = newMs;
            } else {
                // 标准分页模式：查询总数并计算分页
                // 得到总记录数
                if (hasPageSize && hasCurrentPage) {
                    //查询记录总数。
                    StringBuilder countSql = new StringBuilder();
                    String customSql = getCustomCountSql(mappedStatement, parameter, parameterObject);
                    countSql.append("select count(1) as count from (").append(Objects.requireNonNullElse(customSql, originalSql)).append(") t");
                    BoundSqlWrapper newBoundSql = new BoundSqlWrapper(boundSql, countSql.toString(), mappedStatement.getConfiguration());
                    ResultMap map = new ResultMap.Builder(mappedStatement.getConfiguration(), "qq", Integer.class, new ArrayList<>()).build();
                    List<ResultMap> mapList = new ArrayList<>();
                    mapList.add(map);
                    MappedStatement newMs = copyFromMappedStatement(mappedStatement, new BoundSqlSqlSource(newBoundSql), mapList);
                    totalCount = (Integer) ((Executor) invocation.getTarget()).query(newMs, parameterObject, (RowBounds) invocation.getArgs()[2], null).get(0);
                }

                // 分页计算
                pagination.init(totalCount, pagination.getPageSize(), pagination.getCurrentPage());


                // 分页查询 本地化对象 修改数据库注意修改实现
                String pageSql = getPagingString(originalSql, pagination.getPageSize() * (pagination.getCurrentPage() - 1), pagination.getPageSize());
                invocation.getArgs()[2] = new RowBounds(RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
                if (boundSql.getParameterMappings().size() == 0){
                    boundSql = new BoundSql(mappedStatement.getConfiguration(), pageSql, new ArrayList<>(), boundSql.getParameterObject());
                }
                BoundSqlWrapper newBoundSql = new BoundSqlWrapper(boundSql, pageSql, mappedStatement.getConfiguration());
                MappedStatement newMs = copyFromMappedStatement(mappedStatement, new BoundSqlSqlSource(newBoundSql));
                invocation.getArgs()[0] = newMs;
            }

        }
        return invocation.proceed();

    }

    /**
     * 分頁查詢時，用countSqlId參數值指向的statement來執行count統計總條數
     *
     * @param mappedStatement sql Statement
     * @param parameter       查询参数
     * @param parameterObject 参数对象
     * @return sql
     */
    @SuppressWarnings("unchecked")
    private String getCustomCountSql(MappedStatement mappedStatement, Object parameter, Object parameterObject) {
        if (parameterObject == null)
            return null;
        if (
                parameter.getClass().equals(Pagination.class) &&
                        parameterObject.getClass().equals(Pagination.class)
        ) {
            Pagination page = (Pagination) parameter;
            parameter = new HashMap<>();
            ((HashMap) parameter).put("arg0", page);
            ((HashMap) parameter).put("param0", page);
            parameterObject = parameter;
        }
        Map map = (Map) parameterObject;
        if (!map.containsKey(COUNT_SQL_ID))
            return null;
        String sqlId = (String) map.get(COUNT_SQL_ID);
        if (sqlId == null)
            return null;
        String id = mappedStatement.getId();
        String namespaceSqlId = id.substring(0, id.lastIndexOf(".")) + "." + sqlId;
        if (!mappedStatement.getConfiguration().hasStatement(namespaceSqlId, true))
            return null;
        MappedStatement countMappedStatement = mappedStatement.getConfiguration().getMappedStatement(namespaceSqlId);
        BoundSql countBoundSql = countMappedStatement.getBoundSql(parameter);
        return countBoundSql.getSql();
    }

    public static class BoundSqlWrapper extends BoundSql {
        private final BoundSql sourceBoundSql;
        private final String sql;

        public BoundSqlWrapper(BoundSql sourceBoundSql, String sql, Configuration configuration) {
            super(configuration, null, null, null);
            this.sql = sql;
            this.sourceBoundSql = sourceBoundSql;

        }

        @Override
        public String getSql() {
            return this.sql;
        }

        @Override
        public List<ParameterMapping> getParameterMappings() {
            return this.sourceBoundSql.getParameterMappings();
        }

        @Override
        public Object getParameterObject() {
            return this.sourceBoundSql.getParameterObject();
        }

        @Override
        public boolean hasAdditionalParameter(String name) {
            return this.sourceBoundSql.hasAdditionalParameter(name);
        }

        @Override
        public void setAdditionalParameter(String name, Object value) {
            this.sourceBoundSql.setAdditionalParameter(name, value);
        }

        @Override
        public Object getAdditionalParameter(String name) {
            return this.sourceBoundSql.getAdditionalParameter(name);
        }
    }

    public static class BoundSqlSqlSource implements SqlSource {
        BoundSql boundSql;

        public BoundSqlSqlSource(BoundSql boundSql) {

            this.boundSql = boundSql;
        }

        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }
    }

    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    public void setProperties(Properties properties) {

    }


    /**
     * 取参数中的类型
     *
     * @param parameterObject 参数对象
     * @param parameterType   参数类型
     * @return 参数的类型
     */
    @SuppressWarnings("unchecked")
    private <T> T getParameterByType(Object parameterObject, Class<T> parameterType) {
        if (parameterObject instanceof Map) {
            Map map = (Map) parameterObject;
            for (Object parameter : map.values()) {
                if (parameter == null) continue;
                if (parameterType.isAssignableFrom(parameter.getClass())) {
                    return (T) parameter;
                }
            }

        } else if (parameterType.isAssignableFrom(parameterObject.getClass())) {
            return (T) parameterObject;
        }
        return null;
    }

    /**
     * 得到分页的SQL
     *
     * @param offset 偏移量
     * @param limit  位置
     * @return 分页SQL
     */
    private String getPagingString(String querySelect, int offset, int limit) {
        if (StringUtil.isEmpty(querySelect)) {
            return querySelect;
        }
        querySelect = getLineSql(querySelect);
        return "SELECT * FROM (" + querySelect + ")A LIMIT " + limit + " OFFSET " + offset;

    }


    /**
     * 将SQL语句变成一条语句，并且每个单词的间隔都是1个空格
     *
     * @param sql SQL语句
     * @return 如果sql是NULL返回空，否则返回转化后的SQL
     */
    private String getLineSql(String sql) {
        return sql.replaceAll("[\r\n]", " ").replaceAll("\\s{2,}", " ");
    }

    public static MappedStatement copyFromMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
        return copyFromMappedStatement(ms, newSqlSource, ms.getResultMaps());
    }


    public static MappedStatement copyFromMappedStatement(MappedStatement ms, SqlSource newSqlSource, List<ResultMap> maps) {
        MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), newSqlSource, ms
                .getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(maps);
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());

        return builder.build();
    }
}