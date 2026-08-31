package com.tkfc.boot.starter.elasticsearch.extend;

import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest.Builder;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import co.elastic.clients.json.JsonData;
import com.tkfc.core.common.pojo.Pagination;
import com.tkfc.core.common.pojo.Sort;

import java.util.List;
import java.util.function.Consumer;

public interface BaseServiceES<T extends BaseModelES> {

    /**
     * 检查索引是否存在。
     *
     * @return 如果索引存在则返回 true，否则返回 false
     */
    Boolean existIndex();

    /**
     * 创建一个新的 Elasticsearch 索引请求。
     *
     * @param shards   索引的主分片数量
     * @param replicas 每个主分片的副本数量
     */
    void createIndex(int shards, int replicas);

    /**
     * 创建一个新的 Elasticsearch 索引请求。
     */
    void createIndex();

    /**
     * 创建一个新的 Elasticsearch 索引请求 如果不存在。
     */
    void createIndexIfNotExist();

    /**
     * 发送一个 PUT 请求来更新指定索引的映射。
     */
    void putMappingRequest();

    /**
     * 删除 Elasticsearch 索引。
     */
    void deleteIndex();

    /**
     * 根据 ID 删除文档
     *
     * @param id 文档 ID
     * @return 删除响应
     */
    DeleteResponse deleteById(String id);

    /**
     * 批量删除文档
     *
     * @param ids 文档 ID 列表
     * @return 批量删除响应
     */
    BulkResponse deleteByIds(List<String> ids);

    /**
     * 保存文档到索引
     *
     * @param document 要保存的文档
     * @return 索引响应
     */
    IndexResponse index(T document);

    /**
     * 批量保存文档到索引
     *
     * @param documents 要保存的文档列表
     * @return 批量索引响应
     */
    BulkResponse indexBatch(List<T> documents);

    /**
     * 更新文档
     *
     * @param document 要更新的文档
     * @return 更新响应
     */
    UpdateResponse<JsonData> update(T document);

    /**
     * 批量更新文档
     *
     * @param documents 要更新的文档列表，需要包含 ID
     * @return 批量更新响应
     */
    BulkResponse updateBatch(List<T> documents);

    /**
     * 根据 ID 查询单条数据
     *
     * @param id 文档 ID
     * @return 单条搜索结果，如果没有找到则返回 null
     */
    T findById(String id);

    /**
     * 根据 ID 列表批量查询数据
     *
     * @param ids 文档 ID 列表
     * @return 搜索结果列表
     */
    List<T> findByIds(List<String> ids);

    /**
     * 使用 Elasticsearch Java API Client 构建查询
     *
     * @param kql 搜索条件
     * @param pagination       分页参数
     * @param sort               排序参数
     * @return 搜索结果列表
     */
    List<T> find(Consumer<Builder> kql,
                 Pagination pagination, Sort sort);

    /**
     * 根据搜索条件进行查询（简化版本）
     *
     * @param kql 搜索条件
     * @return 搜索结果列表
     */
    List<T> find(Consumer<Builder> kql);

    /**
     * 根据搜索条件进行查询（带排序）
     *
     * @param kql 搜索条件
     * @param sort               排序参数
     * @return 搜索结果列表
     */
    List<T> find(Consumer<Builder> kql, Sort sort);

    /**
     * 根据搜索条件进行查询，只返回一条数据
     *
     * @param kql 搜索条件
     * @return 单条搜索结果，如果没有找到则返回 null
     */
    T findOne(Consumer<Builder> kql);

    /**
     * 根据搜索条件进行查询，只返回一条数据（带排序）
     *
     * @param kql 搜索条件
     * @param sort               排序参数
     * @return 单条搜索结果，如果没有找到则返回 null
     */
    T findOne(Consumer<Builder> kql, Sort sort);

    /**
     * 根据搜索条件进行查询，只返回一条数据（带分页和排序）
     *
     * @param kql 搜索条件
     * @param pagination       分页参数
     * @param sort               排序参数
     * @return 单条搜索结果，如果没有找到则返回 null
     */
    T findOne(Consumer<Builder> kql, Pagination pagination, Sort sort);

    /**
     * 统计当前索引下的数据总数
     *
     * @return 数据总数
     */
    Long count();

    /**
     * 根据搜索条件统计符合条件的文档数量
     *
     * @param kql 搜索条件
     * @return 符合条件的文档数量
     */
    Long count(Consumer<Builder> kql);
}
