package com.tkfc.boot.starter.elasticsearch.extend;

import com.tkfc.core.common.annotations.elasticsearch.ESDocument;
import com.tkfc.core.common.annotations.elasticsearch.ESField;
import com.tkfc.core.common.annotations.elasticsearch.ESId;
import com.tkfc.core.enums.elasticsearch.ESFieldType;
import com.tkfc.core.toolkit.CollectionUtil;
import com.tkfc.core.toolkit.ReflectionUtil;
import com.tkfc.core.toolkit.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.PutMappingRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

/**
 * 顶级es mapper
 * @param <T>
 * @author 0neBean
 */
@Slf4j
public abstract class BaseMapperES<T extends BaseModelES> {

    @Resource
    protected RestHighLevelClient client;

    /**
     * 获取索引名称
     * 从泛型T的ESDocument注解中获取indexName
     *
     * @return 索引名称
     */
    protected String getIndexName() {
        Class<?> clazz = ReflectionUtil.findParameterizedType(this.getClass(), 0);
        if (clazz == null) {
            throw new ElasticsearchException("无法获取泛型类型，请检查类定义");
        }

        ESDocument esDocument = clazz.getAnnotation(ESDocument.class);
        if (esDocument == null) {
            throw new ElasticsearchException("类 " + clazz.getName() + " 缺少 @ESDocument 注解");
        }

        String indexName = esDocument.indexName();
        if (StringUtil.isBlank(indexName)) {
            throw new ElasticsearchException("ESDocument注解中的indexName不能为空");
        }

        return indexName;
    }

    /**
     * 获取文档ID
     * 通过反射获取带有@ESId注解的字段值
     *
     * @param document 文档对象
     * @return 文档ID
     */
    protected String getDocumentId(T document) {
        if (document == null) {
            return null;
        }

        Class<?> clazz = document.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            ESId esId = field.getAnnotation(com.tkfc.core.common.annotations.elasticsearch.ESId.class);
            if (esId != null) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(document);
                    return value != null ? value.toString() : null;
                } catch (IllegalAccessException e) {
                    log.error("获取文档ID失败", e);
                    return null;
                }
            }
        }

        return null;
    }

    /**
     * 检查索引是否存在。
     *
     * @return 如果索引存在则返回true，否则返回false
     * @throws ElasticsearchException 如果在检查索引是否存在时发生IO异常
     */
    protected Boolean existIndex() {
        String indexName = getIndexName();
        boolean exists;
        try {
            GetIndexRequest request = new GetIndexRequest(indexName);
            exists = client.indices().exists(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new ElasticsearchException("判断索引 {" + indexName + "} 是否存在失败");
        }
        return exists;
    }

    /**
     * 创建一个新的Elasticsearch索引请求。
     *
     * @param shards    索引的主分片数量
     * @param replicas  每个主分片的副本数量
     * @throws ElasticsearchException 如果创建索引时发生IO异常或者索引已存在
     */
    protected void createIndex(int shards, int replicas) {
        String indexName = getIndexName();
        if (existIndex()) {
            return;
        }
        try {
            CreateIndexRequest request = new CreateIndexRequest(indexName);
            // Settings for this index
            request.settings(Settings.builder()
                    .put("index.number_of_shards", shards)
                    .put("index.number_of_replicas", replicas)
            );
            client.indices().createAsync(request, RequestOptions.DEFAULT, new ActionListener<>() {
                @Override
                public void onResponse(CreateIndexResponse createIndexResponse) {
                    // 处理成功响应
                    System.out.println("Index created successfully: " + createIndexResponse.index());
                }

                @Override
                public void onFailure(Exception e) {
                    // 处理失败响应
                    System.err.println("Failed to create index: " + e.getMessage());
                }
            });
            CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
            log.info(" acknowledged : {}", createIndexResponse.isAcknowledged());
            log.info(" shardsAcknowledged :{}", createIndexResponse.isShardsAcknowledged());
        } catch (IOException e) {
            throw new ElasticsearchException("创建索引 {" + indexName + "} 失败");
        }
    }

    /**
     * 发送一个PUT请求来更新指定索引的映射。
     *
     * @throws ElasticsearchException 如果在处理ESField注解时发现name或type属性未指定，或者在发送请求时发生IO异常
     */
    protected void putMappingRequest() {
        Class<?> clazz = ReflectionUtil.findParameterizedType(this.getClass(), 0);
        String indexName = getIndexName();
        Field[] fields = null;
        if (clazz != null) {
            fields = clazz.getDeclaredFields();
        }
        if (CollectionUtil.isEmpty(fields)) {
            return;
        }

        try {
            PutMappingRequest request = new PutMappingRequest(indexName);
            XContentBuilder builder = XContentFactory.jsonBuilder();
            builder.startObject();
            builder.startObject("properties");
            for (Field field : fields) {
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
                    throw new ElasticsearchException("注解ESField的type属性未指定");
                }
                builder.startObject(name);
                {
                    builder.field("type", esFieldType.typeName);
                    // 分词器
                    String analyzer = esField.getString("analyzer");
                    if (StringUtil.isNotBlank(analyzer)) {
                        builder.field("analyzer", analyzer);
                    }
                }
                builder.endObject();
            }
            builder.endObject();
            builder.endObject();
            request.source(builder);

            AcknowledgedResponse putMappingResponse = client.indices().putMapping(request, RequestOptions.DEFAULT);
            log.info("acknowledged : :{}", putMappingResponse.isAcknowledged());

        } catch (IOException e) {
            log.error("putMappingRequest , error", e);
        }
    }

    /**
     * 删除Elasticsearch索引。
     *
     * @throws ElasticsearchException 如果删除索引时发生IO异常
     */
    protected void deleteIndex() {
        String indexName = getIndexName();
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(indexName);
        try {
            client.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new ElasticsearchException("删除索引 {" + indexName + "} 失败");
        }
    }

    /**
     * 根据ID删除文档
     *
     * @param id 文档ID
     * @return 删除响应
     * @throws ElasticsearchException 如果删除失败
     */
    protected DeleteResponse deleteById(String id) {
        try {
            DeleteRequest deleteRequest = new DeleteRequest(getIndexName(), id);
            return client.delete(deleteRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new ElasticsearchException("删除文档失败，ID: " + id, e);
        }
    }

    /**
     * 批量删除文档
     *
     * @param ids 文档ID列表
     * @return 批量删除响应
     * @throws ElasticsearchException 如果批量删除失败
     */
    protected BulkResponse deleteByIds(List<String> ids) {
        try {
            BulkRequest bulkRequest = new BulkRequest();
            for (String id : ids) {
                DeleteRequest deleteRequest = new DeleteRequest(getIndexName(), id);
                bulkRequest.add(deleteRequest);
            }
            return client.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new ElasticsearchException("批量删除文档失败", e);
        }
    }

    /**
     * 保存文档到索引
     *
     * @param document 要保存的文档
     * @return 索引响应
     * @throws ElasticsearchException 如果保存失败
     */
    protected IndexResponse index(T document) {
        try {
            String id = getDocumentId(document);
            IndexRequest indexRequest = new IndexRequest(getIndexName());
            if (StringUtil.isNotBlank(id)) {
                indexRequest.id(id);
            }
            indexRequest.source(document.toJson(), XContentType.JSON);
            return client.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new ElasticsearchException("保存文档失败", e);
        }
    }

    /**
     * 批量保存文档到索引
     *
     * @param documents 要保存的文档列表
     * @return 批量索引响应
     * @throws ElasticsearchException 如果批量保存失败
     */
    protected BulkResponse indexBatch(List<T> documents) {
        try {
            BulkRequest bulkRequest = new BulkRequest();
            for (T document : documents) {
                String id = getDocumentId(document);
                IndexRequest indexRequest = new IndexRequest(getIndexName());
                if (StringUtil.isNotBlank(id)) {
                    indexRequest.id(id);
                }
                indexRequest.source(document.toJson(), XContentType.JSON);
                bulkRequest.add(indexRequest);
            }
            return client.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new ElasticsearchException("批量保存文档失败", e);
        }
    }

    /**
     * 更新文档
     *
     * @param document 要更新的文档
     * @return 更新响应
     * @throws ElasticsearchException 如果更新失败
     */
    protected UpdateResponse update(T document) {
        String id = getDocumentId(document);
        try {
            UpdateRequest updateRequest = new UpdateRequest(getIndexName(), id);
            updateRequest.doc(document.toJson(), XContentType.JSON);
            return client.update(updateRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new ElasticsearchException("更新文档失败，ID: " + id, e);
        }
    }

    /**
     * 批量更新文档
     *
     * @param documents 要更新的文档列表，需要包含ID
     * @return 批量更新响应
     * @throws ElasticsearchException 如果批量更新失败
     */
    protected BulkResponse updateBatch(List<T> documents) {
        try {
            BulkRequest bulkRequest = new BulkRequest();
            for (T document : documents) {
                String id = getDocumentId(document);
                if (StringUtil.isBlank(id)) {
                    throw new ElasticsearchException("文档缺少ID，无法进行更新操作");
                }
                UpdateRequest updateRequest = new UpdateRequest(getIndexName(), id);
                updateRequest.doc(document.toJson(), XContentType.JSON);
                bulkRequest.add(updateRequest);
            }
            return client.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new ElasticsearchException("批量更新文档失败", e);
        }
    }

    /**
     * 根据SearchSourceBuilder进行查询，返回SearchResponse
     *
     * @param searchSourceBuilder 搜索源构建器
     * @return SearchResponse对象
     * @throws ElasticsearchException 如果查询失败
     */
    protected SearchResponse findWithResponse(SearchSourceBuilder searchSourceBuilder) {
        try {
            SearchRequest searchRequest = new SearchRequest(getIndexName());
            searchRequest.source(searchSourceBuilder);
            return client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new ElasticsearchException("执行搜索查询失败", e);
        }
    }

}
