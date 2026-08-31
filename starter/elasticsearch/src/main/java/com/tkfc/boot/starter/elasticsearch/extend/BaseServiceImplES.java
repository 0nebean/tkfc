package com.tkfc.boot.starter.elasticsearch.extend;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.SearchRequest.Builder;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.json.JsonData;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.tkfc.core.common.annotations.elasticsearch.ESField;
import com.tkfc.core.enums.elasticsearch.ESFieldType;
import com.tkfc.core.common.pojo.Pagination;
import com.tkfc.core.common.pojo.Sort;
import com.tkfc.core.toolkit.JsonUtil;
import com.tkfc.core.toolkit.ParseUtil;
import com.tkfc.core.toolkit.ReflectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
public class BaseServiceImplES<T extends BaseModelES, K extends BaseMapperES<T>> implements BaseServiceES<T> {

    /**
     * dao 原型属性
     */
    @Autowired
    @SuppressWarnings("all")
    protected K baseMapper;

    @Override
    public Boolean existIndex() {
        return baseMapper.existIndex();
    }

    @Override
    public void createIndex(int shards, int replicas) {
        baseMapper.createIndex(shards, replicas);
    }

    @Override
    public void createIndex() {
        baseMapper.createIndex(1, 0);
    }

    @Override
    public void createIndexIfNotExist() {
        if (!existIndex()) {
            baseMapper.createIndex(1, 0);
        }
    }

    @Override
    public void putMappingRequest() {
        baseMapper.putMappingRequest();
    }

    @Override
    public void deleteIndex() {
        baseMapper.deleteIndex();
    }

    @Override
    public DeleteResponse deleteById(String id) {
        return baseMapper.deleteById(id);
    }

    @Override
    public BulkResponse deleteByIds(List<String> ids) {
        return baseMapper.deleteByIds(ids);
    }

    @Override
    public IndexResponse index(T document) {
        return baseMapper.index(document);
    }

    @Override
    public BulkResponse indexBatch(List<T> documents) {
        return baseMapper.indexBatch(documents);
    }

    @Override
    public UpdateResponse<JsonData> update(T document) {
        return baseMapper.update(document);
    }

    @Override
    public BulkResponse updateBatch(List<T> documents) {
        return baseMapper.updateBatch(documents);
    }

    @Override
    public T findById(String id) {
        return findOne(s -> s.query(q -> q.ids(i -> i.values(id))), null, null);
    }

    @Override
    public List<T> findByIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }

        return find(s -> {
            s.query(q -> q.ids(i -> i.values(ids)));
            s.size(ids.size());
        }, null, null);
    }

    @Override
    public List<T> find(Consumer<Builder> kql, Pagination pagination, Sort sort) {
        SearchResponse<JsonData> searchResponse = baseMapper.findWithResponse(s -> {
            kql.accept(s);
            if (pagination != null) {
                s.trackTotalHits(t -> t.enabled(true));
                if (pagination.getPageSize() != null) {
                    s.size(pagination.getPageSize());
                }

                if (pagination.getSearchAfter() != null && pagination.getSearchAfter().length > 0) {
                    if (sort == null || sort.orders.isEmpty()) {
                        throw new IllegalStateException("使用 searchAfter 分页时必须提供排序参数");
                    }
                    validateSearchAfterLength(pagination, sort);
                    List<FieldValue> after = new ArrayList<>();
                    for (Object o : pagination.getSearchAfter()) {
                        after.add(fieldValueFromObject(o));
                    }
                    s.searchAfter(after);
                }
            }
            if (sort != null && !sort.orders.isEmpty()) {
                for (Sort.ESOrder order : sort.orders) {
                    s.sort(so -> so.field(f -> f.field(order.getOrderBy()).order(sortOrderFromString(order.getSort()))));
                }
            }
        });

        if (pagination != null) {
            pagination.setTotalCount(ParseUtil.toInt(count(kql)));
        }

        List<T> results = new ArrayList<>();
        List<Hit<JsonData>> hits = searchResponse.hits().hits();

        for (Hit<JsonData> hit : hits) {
            try {
                Class<?> clazz = ReflectionUtil.findParameterizedType(this.getClass(), 0);
                if (clazz == null) {
                    throw new IllegalStateException("无法获取泛型类型，请检查类定义");
                }
                JsonData source = hit.source();
                if (source == null) {
                    continue;
                }
                String sourceJson = source.toJson().toString();
                sourceJson = normalizeTextFieldsForRead(sourceJson, clazz);
                T document = (T) JsonUtil.toBean(sourceJson, clazz);
                results.add(document);
            } catch (Exception e) {
                log.error("解析搜索结果失败，hit ID: " + hit.id(), e);
            }
        }

        if (pagination != null && !hits.isEmpty()) {
            List<FieldValue> lastSort = hits.get(hits.size() - 1).sort();
            if (lastSort != null && !lastSort.isEmpty()) {
                Object[] arr = new Object[lastSort.size()];
                for (int i = 0; i < lastSort.size(); i++) {
                    arr[i] = fieldValueToObject(lastSort.get(i));
                }
                pagination.setSearchAfter(arr);
            }
        }

        return results;
    }

    /** 业务侧仅传 {@code ASC} / {@code DESC}，映射为 ES 的 {@link SortOrder#Asc} / {@link SortOrder#Desc}。 */
    private static SortOrder sortOrderFromString(String sort) {
        if (sort == null || sort.isBlank()) {
            return SortOrder.Desc;
        }
        String s = sort.trim();
        if ("ASC".equalsIgnoreCase(s)) {
            return SortOrder.Asc;
        }
        if ("DESC".equalsIgnoreCase(s)) {
            return SortOrder.Desc;
        }
        throw new IllegalArgumentException("sort 仅支持 ASC / DESC: " + sort);
    }

    private static void validateSearchAfterLength(Pagination pagination, Sort sort) {
        Object[] searchAfter = pagination.getSearchAfter();
        if (searchAfter == null || searchAfter.length == 0) {
            return;
        }
        int sortSize = sort.orders.size();
        if (searchAfter.length != sortSize) {
            throw new IllegalArgumentException(
                    "searchAfter 长度与排序字段数量不一致: searchAfterLength=" + searchAfter.length + ", sortSize=" + sortSize);
        }
    }

    private static FieldValue fieldValueFromObject(Object o) {
        return switch (o) {
            case null -> throw new IllegalArgumentException("searchAfter 元素不能为 null");
            case String s -> FieldValue.of(s);
            case Long l -> FieldValue.of(l);
            case Integer i -> FieldValue.of(i.longValue());
            case Double d -> FieldValue.of(d);
            case Float f -> FieldValue.of(f.doubleValue());
            case Boolean b -> FieldValue.of(b);
            default -> FieldValue.of(String.valueOf(o));
        };
    }

    private static Object fieldValueToObject(FieldValue fv) {
        if (fv.isLong()) {
            return fv.longValue();
        }
        if (fv.isString()) {
            return fv.stringValue();
        }
        if (fv.isDouble()) {
            return fv.doubleValue();
        }
        if (fv.isBoolean()) {
            return fv.booleanValue();
        }
        if (fv.isNull()) {
            return null;
        }
        throw new IllegalStateException("不支持的 sort 值类型: " + fv._kind());
    }

    private static String normalizeTextFieldsForRead(String sourceJson, Class<?> clazz) {
        if (sourceJson == null || sourceJson.isBlank() || clazz == null) {
            return sourceJson;
        }
        JSONObject root = JSONObject.parseObject(sourceJson);
        normalizeJsonTextFieldsForRead(root, clazz);
        return root.toJSONString();
    }

    private static void normalizeJsonTextFieldsForRead(JSONObject json, Class<?> clazz) {
        if (json == null || clazz == null) {
            return;
        }
        for (Field field : clazz.getDeclaredFields()) {
            var esField = field.getAnnotation(ESField.class);
            if (esField == null) {
                continue;
            }
            String name = esField.name();
            if (name == null || name.isBlank()) {
                name = field.getName();
            }
            if (!json.containsKey(name)) {
                continue;
            }
            Object value = json.get(name);
            if (value == null) {
                continue;
            }
            ESFieldType type = esField.type();

            if (type == ESFieldType.Text && value instanceof String s) {
                Class<?> ft = field.getType();
                try {
                    if (JSONArray.class.isAssignableFrom(ft) && s.startsWith("[")) {
                        json.put(name, JSONArray.parseArray(s));
                    } else if (JSONObject.class.isAssignableFrom(ft) && s.startsWith("{")) {
                        json.put(name, JSONObject.parseObject(s));
                    }
                } catch (Exception ignore) {
                }
                continue;
            }

            if ((type == ESFieldType.Object || type == ESFieldType.NESTED) && shouldWalkNestedType(field.getType())) {
                if (value instanceof JSONObject childObj) {
                    normalizeJsonTextFieldsForRead(childObj, field.getType());
                } else if (value instanceof JSONArray arr) {
                    for (Object item : arr) {
                        if (item instanceof JSONObject childItem) {
                            normalizeJsonTextFieldsForRead(childItem, field.getType());
                        }
                    }
                }
            }
        }
    }

    private static boolean shouldWalkNestedType(Class<?> type) {
        if (type == null || type.isPrimitive() || type.isEnum()) {
            return false;
        }
        Package p = type.getPackage();
        if (p == null) {
            return false;
        }
        String pkg = p.getName();
        return pkg.startsWith("com.tkfc.") && !type.isInterface();
    }

    @Override
    public List<T> find(Consumer<Builder> kql) {
        return find(kql, null, null);
    }

    @Override
    public List<T> find(Consumer<Builder> kql, Sort sort) {
        return find(kql, null, sort);
    }

    @Override
    public T findOne(Consumer<Builder> kql) {
        List<T> results = find(kql, null, null);
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public T findOne(Consumer<Builder> kql, Sort sort) {
        List<T> results = find(kql, null, sort);
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public T findOne(Consumer<Builder> kql, Pagination pagination, Sort sort) {
        List<T> results = find(kql, pagination, sort);
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public Long count() {
        if (!existIndex()) {
            return 0L;
        }

        return findWithTotal(s -> s.query(q -> q.matchAll(m -> m)));
    }

    @Override
    public Long count(Consumer<Builder> kql) {
        if (!existIndex()) {
            return 0L;
        }

        return findWithTotal(kql);
    }

    private long findWithTotal(Consumer<Builder> kql) {
        SearchResponse<JsonData> searchResponse = baseMapper.findWithResponse(s -> {
            kql.accept(s);
            s.size(0);
            s.trackTotalHits(t -> t.enabled(true));
        });
        return Optional.ofNullable(searchResponse.hits().total()).map(TotalHits::value).orElse(0L);
    }

}
