package com.tkfc.boot.starter.elasticsearch.extend;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.json.JsonData;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.tkfc.core.common.annotations.elasticsearch.ESDocument;
import com.tkfc.core.common.annotations.elasticsearch.ESField;
import com.tkfc.core.common.annotations.elasticsearch.ESId;
import com.tkfc.core.enums.elasticsearch.ESFieldType;
import com.tkfc.core.toolkit.JsonUtil;
import com.tkfc.core.toolkit.ReflectionUtil;
import com.tkfc.core.toolkit.StringUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;

/**
 * 顶级 es mapper
 *
 * @param <T>
 * @author 0neBean
 */
@Slf4j
public abstract class BaseMapperES<T extends BaseModelES> {

    @Resource
    protected ElasticsearchClient client;

    /**
     * 获取索引名称
     * 从泛型 T 的 ESDocument 注解中获取 indexName
     *
     * @return 索引名称
     */
    protected String getIndexName() {
        Class<?> clazz = ReflectionUtil.findParameterizedType(this.getClass(), 0);
        if (clazz == null) {
            throw new IllegalStateException("无法获取泛型类型，请检查类定义");
        }

        ESDocument esDocument = clazz.getAnnotation(ESDocument.class);
        if (esDocument == null) {
            throw new IllegalStateException("类 " + clazz.getName() + " 缺少 @ESDocument 注解");
        }

        String indexName = esDocument.indexName();
        if (StringUtil.isBlank(indexName)) {
            throw new IllegalStateException("ESDocument 注解中的 indexName 不能为空");
        }

        return indexName;
    }

    /**
     * 获取文档 ID
     * 通过反射获取带有 @ESId 注解的字段值
     *
     * @param document 文档对象
     * @return 文档 ID
     */
    protected String getDocumentId(T document) {
        if (document == null) {
            return null;
        }

        Class<?> clazz = document.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            ESId esId = field.getAnnotation(ESId.class);
            if (esId != null) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(document);
                    return value != null ? value.toString() : null;
                } catch (IllegalAccessException e) {
                    log.error("获取文档 ID 失败", e);
                    return null;
                }
            }
        }

        return null;
    }

    /**
     * 检查索引是否存在。
     *
     * @return 如果索引存在则返回 true，否则返回 false
     */
    protected Boolean existIndex() {
        String indexName = getIndexName();
        try {
            return client.indices().exists(e -> e.index(indexName)).value();
        } catch (IOException e) {
            throw new IllegalStateException("判断索引 {" + indexName + "} 是否存在失败", e);
        }
    }

    /**
     * 创建一个新的 Elasticsearch 索引。
     *
     * @param shards   索引的主分片数量
     * @param replicas 每个主分片的副本数量
     */
    protected void createIndex(int shards, int replicas) {
        String indexName = getIndexName();
        if (Boolean.TRUE.equals(existIndex())) {
            return;
        }
        try {
            Class<?> clazz = ReflectionUtil.findParameterizedType(this.getClass(), 0);
            Map<String, Property> properties = collectEsProperties(clazz);

            CreateIndexResponse createIndexResponse = client.indices().create(c -> {
                c.index(indexName)
                        .settings(s -> s
                                .numberOfShards(String.valueOf(shards))
                                .numberOfReplicas(String.valueOf(replicas))
                        );
                if (!properties.isEmpty()) {
                    c.mappings(m -> m.properties(properties));
                }
                return c;
            });
            log.info(" acknowledged : {}", createIndexResponse.acknowledged());
            log.info(" shardsAcknowledged :{}", createIndexResponse.shardsAcknowledged());
        } catch (IOException e) {
            throw new IllegalStateException("创建索引 {" + indexName + "} 失败", e);
        }
    }

    /**
     * 发送一个 PUT 请求来更新指定索引的映射。
     */
    protected void putMappingRequest() {
        Class<?> clazz = ReflectionUtil.findParameterizedType(this.getClass(), 0);
        String indexName = getIndexName();
        if (clazz == null) {
            return;
        }

        try {
            Map<String, Property> properties = collectEsProperties(clazz);
            if (properties.isEmpty()) {
                return;
            }

            var putMappingResponse = client.indices().putMapping(p -> p
                    .index(indexName)
                    .properties(properties)
            );
            log.info("acknowledged : :{}", putMappingResponse.acknowledged());

        } catch (IOException e) {
            throw new IllegalStateException("更新索引 {" + indexName + "} 映射失败", e);
        }
    }

    /**
     * 收集某类型上所有带 {@link ESField} 的字段映射；{@link ESFieldType#Object} 且为业务 VO 类型时会递归收集子属性。
     */
    private Map<String, Property> collectEsProperties(Class<?> clazz) {
        Map<String, Property> properties = new HashMap<>();
        if (clazz == null) {
            return properties;
        }
        for (Field field : clazz.getDeclaredFields()) {
            AnnotationAttributes esField = AnnotatedElementUtils.getMergedAnnotationAttributes(field, ESField.class);
            if (esField == null) {
                continue;
            }
            String name = esField.getString("name");
            if (StringUtil.isBlank(name)) {
                name = field.getName();
            }
            ESFieldType esFieldType = (ESFieldType) esField.get("type");
            if (esFieldType == null) {
                throw new IllegalStateException("注解 ESField 的 type 属性未指定: " + clazz.getName() + "#" + field.getName());
            }
            String analyzer = esField.getString("analyzer");
            properties.put(name, buildProperty(field, esFieldType, analyzer));
        }
        return properties;
    }

    private Property buildProperty(Field field, ESFieldType esFieldType, String analyzer) {
        if (esFieldType == ESFieldType.Object && shouldCollectNestedObjectProperties(field.getType())) {
            Map<String, Property> nested = collectEsProperties(field.getType());
            return Property.of(pr -> pr.object(o -> o.properties(nested)));
        }
        return toProperty(esFieldType, analyzer);
    }

    /**
     * 仅对 com.tkfc 包下的自定义类型递归生成 object.properties，避免误入 JDK / JSON 库等类型。
     */
    private static boolean shouldCollectNestedObjectProperties(Class<?> type) {
        if (type == null || type.isPrimitive() || type.isEnum()) {
            return false;
        }
        if (Collection.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type)) {
            return false;
        }
        Package p = type.getPackage();
        if (p == null) {
            return false;
        }
        String pkg = p.getName();
        if (!pkg.startsWith("com.tkfc.")) {
            return false;
        }
        return !type.isInterface();
    }

    private static Property toProperty(ESFieldType esFieldType, String analyzer) {
        return switch (esFieldType) {
            case Text -> Property.of(pr -> pr.text(tp -> {
                if (StringUtil.isNotBlank(analyzer)) {
                    tp.analyzer(analyzer);
                }
                return tp;
            }));
            case Keyword -> Property.of(pr -> pr.keyword(k -> k));
            case Byte -> Property.of(pr -> pr.byte_(b -> b));
            case Short -> Property.of(pr -> pr.short_(s -> s));
            case Integer -> Property.of(pr -> pr.integer(i -> i));
            case Long -> Property.of(pr -> pr.long_(l -> l));
            case Float -> Property.of(pr -> pr.float_(f -> f));
            case Double -> Property.of(pr -> pr.double_(d -> d));
            case Boolean -> Property.of(pr -> pr.boolean_(b -> b));
            case Date -> Property.of(pr -> pr.date(d -> d));
            case Object -> Property.of(pr -> pr.object(o -> o));
            case NESTED -> Property.of(pr -> pr.nested(n -> n));
        };
    }

    private String normalizeDocumentJsonForEs(T document) {
        if (document == null) {
            return null;
        }
        JSONObject root = JSONObject.parseObject(document.toJson());
        normalizeJsonByEsField(root, document.getClass());
        return root.toJSONString();
    }

    private void normalizeJsonByEsField(JSONObject json, Class<?> clazz) {
        if (json == null || clazz == null) {
            return;
        }
        for (Field field : clazz.getDeclaredFields()) {
            AnnotationAttributes esField = AnnotatedElementUtils.getMergedAnnotationAttributes(field, ESField.class);
            if (esField == null) {
                continue;
            }
            String name = esField.getString("name");
            if (StringUtil.isBlank(name)) {
                name = field.getName();
            }
            if (!json.containsKey(name)) {
                continue;
            }
            ESFieldType type = (ESFieldType) esField.get("type");
            Object value = json.get(name);
            if (value == null) {
                continue;
            }
            if (type == ESFieldType.Text && !(value instanceof String)) {
                json.put(name, JsonUtil.toJson(value));
                continue;
            }
            if ((type == ESFieldType.Object || type == ESFieldType.NESTED) && shouldCollectNestedObjectProperties(field.getType())) {
                if (value instanceof JSONObject childObj) {
                    normalizeJsonByEsField(childObj, field.getType());
                } else if (value instanceof JSONArray arr) {
                    for (Object item : arr) {
                        if (item instanceof JSONObject childItem) {
                            normalizeJsonByEsField(childItem, field.getType());
                        }
                    }
                }
            }
        }
    }

    /**
     * 删除 Elasticsearch 索引。
     */
    protected void deleteIndex() {
        String indexName = getIndexName();
        try {
            client.indices().delete(d -> d.index(indexName));
        } catch (IOException e) {
            throw new IllegalStateException("删除索引 {" + indexName + "} 失败", e);
        }
    }

    /**
     * 根据 ID 删除文档
     *
     * @param id 文档 ID
     * @return 删除响应
     */
    protected DeleteResponse deleteById(String id) {
        try {
            return client.delete(d -> d.index(getIndexName()).id(id));
        } catch (IOException e) {
            throw new IllegalStateException("删除文档失败，ID: " + id, e);
        }
    }

    /**
     * 批量删除文档
     *
     * @param ids 文档 ID 列表
     * @return 批量删除响应
     */
    protected BulkResponse deleteByIds(List<String> ids) {
        try {
            List<BulkOperation> ops = new ArrayList<>();
            for (String id : ids) {
                ops.add(BulkOperation.of(o -> o.delete(d -> d.index(getIndexName()).id(id))));
            }
            return client.bulk(b -> b.operations(ops));
        } catch (IOException e) {
            throw new IllegalStateException("批量删除文档失败", e);
        }
    }

    /**
     * 保存文档到索引
     *
     * @param document 要保存的文档
     * @return 索引响应
     */
    protected IndexResponse index(T document) {
        try {
            String id = getDocumentId(document);
            String json = normalizeDocumentJsonForEs(document);
            IndexResponse response = client.index(i -> {
                i.index(getIndexName());
                if (StringUtil.isNotBlank(id)) {
                    i.id(id);
                }
                return i.document(JsonData.fromJson(json));
            });
            if (response.result() == null) {
                throw new IllegalStateException("保存文档失败：未返回写入结果, index=" + getIndexName() + ", id=" + id);
            }
            return response;
        } catch (IOException e) {
            throw new IllegalStateException("保存文档失败", e);
        }
    }

    /**
     * 批量保存文档到索引
     *
     * @param documents 要保存的文档列表
     * @return 批量索引响应
     */
    protected BulkResponse indexBatch(List<T> documents) {
        try {
            List<BulkOperation> ops = new ArrayList<>();
            for (T document : documents) {
                String id = getDocumentId(document);
                String json = normalizeDocumentJsonForEs(document);
                ops.add(BulkOperation.of(o -> o.index(idx -> {
                    idx.index(getIndexName());
                    if (StringUtil.isNotBlank(id)) {
                        idx.id(id);
                    }
                    return idx.document(JsonData.fromJson(json));
                })));
            }
            BulkResponse response = client.bulk(b -> b.operations(ops));
            if (response.errors()) {
                StringBuilder sb = new StringBuilder();
                int limit = Math.min(response.items().size(), 10);
                for (int i = 0; i < limit; i++) {
                    var item = response.items().get(i);
                    if (item.error() != null) {
                        sb.append("[item=").append(i)
                                .append(", id=").append(item.id())
                                .append(", type=").append(item.error().type())
                                .append(", reason=").append(item.error().reason())
                                .append("] ");
                    }
                }
                throw new IllegalStateException("批量保存文档失败，存在写入错误: " + sb);
            }
            return response;
        } catch (IOException e) {
            throw new IllegalStateException("批量保存文档失败", e);
        }
    }

    /**
     * 更新文档
     *
     * @param document 要更新的文档
     * @return 更新响应
     */
    protected UpdateResponse<JsonData> update(T document) {
        String id = getDocumentId(document);
        try {
            String json = normalizeDocumentJsonForEs(document);
            return client.update(u -> u
                            .index(getIndexName())
                            .id(id)
                            .doc(JsonData.fromJson(json)),
                    JsonData.class
            );
        } catch (IOException e) {
            throw new IllegalStateException("更新文档失败，ID: " + id, e);
        }
    }

    /**
     * 批量更新文档
     *
     * @param documents 要更新的文档列表，需要包含 ID
     * @return 批量更新响应
     */
    protected BulkResponse updateBatch(List<T> documents) {
        try {
            List<BulkOperation> ops = new ArrayList<>();
            for (T document : documents) {
                String id = getDocumentId(document);
                if (StringUtil.isBlank(id)) {
                    throw new IllegalStateException("文档缺少 ID，无法进行更新操作");
                }
                String json = normalizeDocumentJsonForEs(document);
                ops.add(BulkOperation.of(o -> o.update(u -> u
                        .index(getIndexName())
                        .id(id)
                        .action(a -> a.doc(JsonData.fromJson(json)))
                )));
            }
            return client.bulk(b -> b.operations(ops));
        } catch (IOException e) {
            throw new IllegalStateException("批量更新文档失败", e);
        }
    }

    /**
     * 根据查询条件执行搜索，返回 SearchResponse
     *
     * @param kql 在已设置 index 的 {@link co.elastic.clients.elasticsearch.core.SearchRequest.Builder} 上补充 query、高亮等
     */
    protected SearchResponse<JsonData> findWithResponse(Consumer<SearchRequest.Builder> kql) {
        try {
            SearchRequest.Builder builder = new SearchRequest.Builder().index(getIndexName());
            kql.accept(builder);
            SearchRequest request = sanitizeSearchAfter(builder.build());
            return client.search(request, JsonData.class);
        } catch (IOException e) {
            throw new IllegalStateException("执行搜索查询失败", e);
        }
    }

    /**
     * 兼容历史分页游标异常：当 search_after 的元素个数大于 sort 个数时，自动截断，避免 ES 报错。
     */
    private static SearchRequest sanitizeSearchAfter(SearchRequest request) {
        if (request == null) {
            return null;
        }
        List<FieldValue> searchAfter = request.searchAfter();
        List<co.elastic.clients.elasticsearch._types.SortOptions> sortOptions = request.sort();
        if (searchAfter == null || searchAfter.isEmpty() || sortOptions == null || sortOptions.isEmpty()) {
            return request;
        }

        int sortSize = sortOptions.size();
        if (searchAfter.size() <= sortSize) {
            return request;
        }

        List<FieldValue> trimmed = new ArrayList<>(searchAfter.subList(0, sortSize));
        log.warn("search_after 长度({})大于 sort 长度({})，已自动截断。index={}", searchAfter.size(), sortSize, request.index());

        SearchRequest.Builder rebuilt = new SearchRequest.Builder();
        rebuilt.index(request.index());
        if (request.query() != null) {
            rebuilt.query(request.query());
        }
        if (request.size() != null) {
            rebuilt.size(request.size());
        }
        if (request.from() != null) {
            rebuilt.from(request.from());
        }
        if (request.sort() != null && !request.sort().isEmpty()) {
            rebuilt.sort(request.sort());
        }
        if (request.trackTotalHits() != null) {
            rebuilt.trackTotalHits(request.trackTotalHits());
        }
        rebuilt.searchAfter(trimmed);
        return rebuilt.build();
    }

}
